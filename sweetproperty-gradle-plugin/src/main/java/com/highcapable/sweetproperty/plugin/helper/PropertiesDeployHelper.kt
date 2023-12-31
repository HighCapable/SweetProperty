/*
 * SweetProperty -  An easy get project properties anywhere Gradle plugin.
 * Copyright (C) 2019-2024 HighCapable
 * https://github.com/HighCapable/SweetProperty
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2023/8/30.
 */
package com.highcapable.sweetproperty.plugin.helper

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.generated.SweetPropertyProperties
import com.highcapable.sweetproperty.gradle.entity.ProjectDescriptor
import com.highcapable.sweetproperty.gradle.factory.addDependencyToBuildScript
import com.highcapable.sweetproperty.gradle.factory.fullName
import com.highcapable.sweetproperty.gradle.factory.get
import com.highcapable.sweetproperty.gradle.factory.getOrCreate
import com.highcapable.sweetproperty.gradle.factory.hasExtension
import com.highcapable.sweetproperty.gradle.factory.loadBuildScriptClass
import com.highcapable.sweetproperty.plugin.config.factory.with
import com.highcapable.sweetproperty.plugin.config.proxy.ISweetPropertyConfigs
import com.highcapable.sweetproperty.plugin.config.type.GenerateLocationType
import com.highcapable.sweetproperty.plugin.generator.PropertiesAccessorsGenerator
import com.highcapable.sweetproperty.plugin.generator.PropertiesSourcesGenerator
import com.highcapable.sweetproperty.plugin.generator.factory.PropertyMap
import com.highcapable.sweetproperty.plugin.generator.factory.removeAutoConversion
import com.highcapable.sweetproperty.utils.camelcase
import com.highcapable.sweetproperty.utils.code.entity.MavenPomData
import com.highcapable.sweetproperty.utils.code.factory.compile
import com.highcapable.sweetproperty.utils.debug.SError
import com.highcapable.sweetproperty.utils.flatted
import com.highcapable.sweetproperty.utils.hasInterpolation
import com.highcapable.sweetproperty.utils.isEmpty
import com.highcapable.sweetproperty.utils.noBlank
import com.highcapable.sweetproperty.utils.noEmpty
import com.highcapable.sweetproperty.utils.replaceInterpolation
import com.highcapable.sweetproperty.utils.toStringMap
import com.highcapable.sweetproperty.utils.uppercamelcase
import org.gradle.api.DomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.properties.Delegates

/**
 * 属性键值部署工具类
 */
internal object PropertiesDeployHelper {

    /** 属性键值可访问 [Class] 标识名称 */
    private const val ACCESSORS_NAME = "properties-accessors"

    /** 属性键值默认生成包名 */
    private const val DEFAULT_PACKAGE_NAME = "${SweetPropertyProperties.PROJECT_GROUP_NAME}.defaultproperties"

    /** 当前配置实例 */
    private var configs by Delegates.notNull<ISweetPropertyConfigs>()

    /** 当前缓存的属性键值数组 (初始化装载) */
    private var cachedSettingsProperties = mutableListOf<PropertyMap>()

    /** 当前缓存的属性键值数组 (每个项目) */
    private var cachedProjectProperties = mutableMapOf<String, PropertyMap>()

    /** 上次修改的 Hash Code */
    private var lastModifiedHashCode = 0

    /** 配置是否已被修改 */
    private var isConfigsModified = true

    /** 属性键值可访问 [Class] 生成目录 */
    private var accessorsDir by Delegates.notNull<File>()

    /** 属性键值可访问 [Class] 虚拟依赖数据 */
    private val accessorsPomData = MavenPomData(SweetPropertyProperties.PROJECT_GROUP_NAME, ACCESSORS_NAME, SweetProperty.VERSION)

    /** 属性键值可访问 [Class] 生成实例 */
    private val accessorsGenerator = PropertiesAccessorsGenerator()

    /** 属性键值代码生成实例 */
    private val sourcesGenerator = PropertiesSourcesGenerator()

    /**
     * 装载并初始化
     * @param settings 当前设置
     * @param configs 当前配置
     */
    internal fun initialize(settings: Settings, configs: ISweetPropertyConfigs) {
        this.configs = configs
        checkingConfigsModified(settings)
        if (!configs.isEnable) return
        generatedAccessors(settings)
    }

    /**
     * 开始处理
     * @param rootProject 当前根项目
     */
    internal fun resolve(rootProject: Project) {
        if (!configs.isEnable) return
        resolveAccessors(rootProject)
    }

    /**
     * 开始部署
     * @param rootProject 当前根项目
     */
    internal fun deploy(rootProject: Project) {
        if (!configs.isEnable) return
        deployAccessors(rootProject)
        deploySourcesCode(rootProject)
    }

    /**
     * 检查配置是否已被修改
     * @param settings 当前设置
     */
    private fun checkingConfigsModified(settings: Settings) {
        settings.settingsDir.also { dir ->
            val groovyHashCode = dir.resolve("settings.gradle").takeIf { it.exists() }?.readText()?.hashCode()
            val kotlinHashCode = dir.resolve("settings.gradle.kts").takeIf { it.exists() }?.readText()?.hashCode()
            val gradleHashCode = groovyHashCode ?: kotlinHashCode ?: -1
            isConfigsModified = gradleHashCode == -1 || lastModifiedHashCode != gradleHashCode
            lastModifiedHashCode = gradleHashCode
        }
    }

    /**
     * 生成构建脚本代码
     * @param settings 当前设置
     */
    private fun generatedAccessors(settings: Settings) {
        accessorsDir = generatedAccessorsDir(settings)
        val allConfigs = mutableListOf<ISweetPropertyConfigs.IBuildScriptGenerateConfigs>()
        val allProperties = mutableListOf<PropertyMap>()
        if (configs.global.buildScript.isEnable) {
            allProperties.add(generatedProperties(configs.global.buildScript, ProjectDescriptor.create(settings)))
            allConfigs.add(configs.global.buildScript)
        }
        configs.projects.forEach { (name, subConfigs) ->
            if (!subConfigs.buildScript.isEnable) return@forEach
            allProperties.add(generatedProperties(subConfigs.buildScript, ProjectDescriptor.create(settings, name)))
            allConfigs.add(subConfigs.buildScript)
        }
        if (!isConfigsModified &&
            allProperties == cachedSettingsProperties &&
            !accessorsDir.resolve(accessorsPomData.relativePomPath).isEmpty()
        ) return
        cachedSettingsProperties = allProperties
        accessorsGenerator.build(allConfigs, allProperties).compile(accessorsPomData, accessorsDir.absolutePath, accessorsGenerator.compileStubFiles)
    }

    /**
     * 处理构建脚本代码
     * @param rootProject 当前根项目
     */
    private fun resolveAccessors(rootProject: Project) {
        if (!accessorsDir.resolve(accessorsPomData.relativePomPath).isEmpty())
            rootProject.addDependencyToBuildScript(accessorsDir.absolutePath, accessorsPomData)
    }

    /**
     * 部署构建脚本代码
     * @param rootProject 当前根项目
     */
    private fun deployAccessors(rootProject: Project) {
        /** 部署扩展方法 */
        fun Project.deploy() {
            val configs = configs.with(this).buildScript
            if (!configs.isEnable) return
            val className = accessorsGenerator.propertiesClass(configs.name)
            val accessorsClass = loadBuildScriptClass(className) ?: SError.make(
                """
                  Generated class "$className" not found, stop loading $this
                  Please check whether the initialization process is interrupted and re-run Gradle Sync
                  If this doesn't work, please manually delete the entire "${accessorsDir.absolutePath}" directory
                """.trimIndent()
            )
            getOrCreate(configs.extensionName.camelcase(), accessorsClass)
        }
        rootProject.deploy()
        rootProject.subprojects.forEach { it.deploy() }
    }

    /**
     * 部署项目代码
     * @param rootProject 当前根项目
     */
    private fun deploySourcesCode(rootProject: Project) {
        /** 生成代码 */
        fun Project.generate() {
            val configs = configs.with(this).sourcesCode
            val outputDir = file(configs.generateDirPath)
            if (!configs.isEnable) return
            val properties = generatedProperties(configs, ProjectDescriptor.create(project = this))
            if (!isConfigsModified && properties == cachedProjectProperties[fullName()] && !outputDir.isEmpty()) {
                if (configs.isEnable) configureSourceSets(project = this)
                return
            }; outputDir.apply { if (exists()) deleteRecursively() }
            cachedProjectProperties[fullName()] = properties
            val packageName = generatedPackageName(configs, project = this)
            val className = generatedClassName(configs, project = this)
            sourcesGenerator.build(configs, properties, packageName, className).writeTo(outputDir)
            configureSourceSets(project = this)
        }
        rootProject.generate()
        rootProject.subprojects.forEach { it.afterEvaluate { generate() } }
    }

    /**
     * 配置项目 SourceSets
     * @param project 当前项目
     */
    private fun configureSourceSets(project: Project) {
        fun Project.deploySourceSets(name: String) = runCatching {
            val extension = get(name)
            val collection = extension.javaClass.getMethod("getSourceSets").invoke(extension) as DomainObjectCollection<*>
            collection.configureEach {
                val kotlin = javaClass.getMethod("getKotlin").invoke(this)
                kotlin.javaClass.getMethod("srcDir", Any::class.java).invoke(kotlin, configs.with(project).sourcesCode.generateDirPath)
            }
        }
        if (project.hasExtension("kotlin")) project.deploySourceSets(name = "kotlin")
        if (project.hasExtension("android")) project.deploySourceSets(name = "android")
    }

    /**
     * 获取属性键值可访问 [Class] 生成目录
     * @param settings 当前设置
     * @return [File]
     */
    private fun generatedAccessorsDir(settings: Settings) =
        settings.rootDir.resolve(".gradle").resolve(SweetPropertyProperties.PROJECT_MODULE_NAME).resolve(ACCESSORS_NAME).apply { mkdirs() }

    /**
     * 获取生成的属性键值数组
     * @param configs 当前配置
     * @param descriptor 当前描述
     * @return [PropertyMap]
     */
    private fun generatedProperties(configs: ISweetPropertyConfigs.IBaseGenerateConfigs, descriptor: ProjectDescriptor): PropertyMap {
        val propteries = mutableMapOf<String, Any>()
        configs.permanentKeyValues.forEach { (key, value) -> propteries[key] = value }
        configs.generateLocationTypes.forEach {
            mutableMapOf<Any?, Any?>().apply {
                when (it) {
                    GenerateLocationType.CURRENT_PROJECT -> createProperties(configs, descriptor.currentDir).forEach { putAll(it) }
                    GenerateLocationType.ROOT_PROJECT -> createProperties(configs, descriptor.rootDir).forEach { putAll(it) }
                    GenerateLocationType.GLOBAL -> createProperties(configs, descriptor.homeDir).forEach { putAll(it) }
                    GenerateLocationType.SYSTEM -> putAll(System.getProperties())
                    GenerateLocationType.SYSTEM_ENV -> putAll(System.getenv())
                }
            }.filter { (key, value) ->
                if (configs.isEnableExcludeNonStringValue)
                    key is CharSequence && key.isNotBlank() && value is CharSequence
                else key.toString().isNotBlank() && value != null
            }.toStringMap().filter { (key, _) ->
                configs.includeKeys.noEmpty()?.any { content ->
                    when (content) {
                        is Regex -> content.matches(key)
                        else -> content.toString() == key
                    }
                } ?: true
            }.filter { (key, _) ->
                configs.excludeKeys.noEmpty()?.none { content ->
                    when (content) {
                        is Regex -> content.matches(key)
                        else -> content.toString() == key
                    }
                } ?: true
            }.toMutableMap().also { resolveKeyValues ->
                resolveKeyValues.onEach { (key, value) ->
                    val resolveKeys = mutableListOf<String>()

                    /**
                     * 处理键值内容
                     * @return [String]
                     */
                    fun String.resolveValue(): String = replaceInterpolation { matchKey ->
                        if (resolveKeys.size > 5) SError.make("Key \"$key\" has been called recursively multiple times of those $resolveKeys")
                        resolveKeys.add(matchKey)
                        var resolveValue = if (configs.isEnableValueInterpolation) resolveKeyValues[matchKey] ?: "" else matchKey
                        resolveValue = resolveValue.removeAutoConversion()
                        if (resolveValue.hasInterpolation()) resolveValue.resolveValue()
                        else resolveValue
                    }
                    if (value.hasInterpolation()) resolveKeyValues[key] = value.resolveValue()
                }.takeIf { configs.keyValuesRules.isNotEmpty() }?.forEach { (key, value) ->
                    configs.keyValuesRules[key]?.also { resolveKeyValues[key] = it(value) }
                }; propteries.putAll(resolveKeyValues)
            }
        }; return propteries
    }

    /**
     * 获取生成的包名
     * @param configs 当前配置
     * @param project 当前项目
     * @return [String]
     */
    private fun generatedPackageName(configs: ISweetPropertyConfigs.ISourcesCodeGenerateConfigs, project: Project): String {
        /**
         * 获取 Android 项目的 "namespace"
         * @return [String] or null
         */
        fun Project.namespace() = runCatching {
            val extension = get("android")
            extension.javaClass.getMethod("getNamespace").invoke(extension) as String
        }.getOrNull()
        val packageName = configs.packageName.noBlank()
            ?: project.namespace()
            ?: project.group.toString().noBlank()
            ?: "$DEFAULT_PACKAGE_NAME.${project.fullName(isUseColon = false).replace(":", "").flatted()}"
        return "$packageName.generated"
    }

    /**
     * 获取生成的类名
     * @param configs 当前配置
     * @param project 当前项目
     * @return [String]
     */
    private fun generatedClassName(configs: ISweetPropertyConfigs.ISourcesCodeGenerateConfigs, project: Project): String {
        val className = configs.className.noBlank()
            ?: project.fullName(isUseColon = false).replace(":", "_").uppercamelcase().noBlank()
            ?: "Undefined"
        return "${className.uppercamelcase()}Properties"
    }

    /**
     * 创建新的 [Properties] 数组
     * @param configs 当前配置
     * @param dir 当前目录
     * @return [MutableList]<[Properties]>
     */
    private fun createProperties(configs: ISweetPropertyConfigs.IBaseGenerateConfigs, dir: File?) = runCatching {
        mutableListOf<Properties>().apply {
            configs.propertiesFileNames.forEach {
                val propertiesFile = dir?.resolve(it)
                if (propertiesFile?.exists() == true) add(Properties().apply { load(FileReader(propertiesFile.absolutePath)) })
            }
        }
    }.getOrNull() ?: mutableListOf()
}