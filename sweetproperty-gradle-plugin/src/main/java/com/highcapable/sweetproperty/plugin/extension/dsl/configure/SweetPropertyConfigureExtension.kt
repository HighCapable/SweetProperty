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
 * This file is created by fankes on 2023/8/25.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "PropertyName", "DeprecatedCallableAddReplaceWith")

package com.highcapable.sweetproperty.plugin.extension.dsl.configure

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.gradle.factory.isUnSafeExtName
import com.highcapable.sweetproperty.plugin.config.factory.create
import com.highcapable.sweetproperty.plugin.config.proxy.ISweetPropertyConfigs
import com.highcapable.sweetproperty.plugin.config.type.GenerateLocationType
import com.highcapable.sweetproperty.plugin.generator.factory.PropertyValueRule
import com.highcapable.sweetproperty.utils.debug.SError
import com.highcapable.sweetproperty.utils.noEmpty
import org.gradle.api.Action
import org.gradle.api.initialization.Settings

/**
 * [SweetProperty] 配置方法体实现类
 */
open class SweetPropertyConfigureExtension internal constructor() {

    internal companion object {

        /** [SweetPropertyConfigureExtension] 扩展名称 */
        internal const val NAME = "sweetProperty"

        /** 根项目标识名称 */
        private const val ROOT_PROJECT_TAG = "<ROOT_PROJECT>"
    }

    /** 当前全局配置实例 */
    internal val globalConfigure = SubConfigureExtension()

    /** 当前每个项目配置实例数组 */
    internal val projectConfigures = mutableMapOf<String, SubConfigureExtension>()

    /** 当前项目 */
    @JvmField
    val CURRENT_PROJECT = "CURRENT_PROJECT"

    /** 根项目 */
    @JvmField
    val ROOT_PROJECT = "ROOT_PROJECT"

    /** 全局 (用户目录) */
    @JvmField
    val GLOBAL = "GLOBAL"

    /** 系统 */
    @JvmField
    val SYSTEM = "SYSTEM"

    /** 系统环境变量 */
    @JvmField
    val SYSTEM_ENV = "SYSTEM_ENV"

    /**
     * 是否启用插件
     *
     * 默认启用 - 如果你想关闭插件 - 在这里设置就可以了
     */
    var isEnable = true
        @JvmName("enable") set

    /**
     * 配置全局
     * @param action 配置方法体
     */
    fun global(action: Action<SubConfigureExtension>) = action.execute(globalConfigure)

    /**
     * 配置根项目
     * @param action 配置方法体
     */
    fun rootProject(action: Action<SubConfigureExtension>) = configureProject(ROOT_PROJECT_TAG, action)

    /**
     * 配置指定项目 (数组)
     * @param names 项目完整名称 (数组)
     * @param action 配置方法体
     */
    fun project(vararg names: String, action: Action<SubConfigureExtension>) = names.forEach { configureProject(it, action) }

    /**
     * 配置项目
     * @param name 项目完整名称
     * @param action 配置方法体
     */
    private fun configureProject(name: String, action: Action<SubConfigureExtension>) =
        action.execute(SubConfigureExtension().also { projectConfigures[name] = it })

    /**
     * 子配置方法体实现类
     */
    open inner class SubConfigureExtension internal constructor() {

        /** 当前通用生成代码功能配置实例 */
        internal var allConfigure: BaseGenerateConfigureExtension? = null

        /** 当前项目生成代码功能配置实例 */
        internal var sourcesCodeConfigure: SourcesCodeGenerateConfigureExtension? = null

        /** 当前构建脚本生成代码功能配置实例 */
        internal var buildScriptConfigure: BuildScriptGenerateConfigureExtension? = null

        /**
         * 错误的调用会导致关闭整个插件的功能
         *
         * 请使用 [all]、[sourcesCode]、[buildScript]
         * @throws [IllegalStateException]
         */
        @Deprecated(message = "Do not use", level = DeprecationLevel.ERROR)
        val isEnable: Boolean
            get() = SError.make("Please called all { isEnable = ... }, sourcesCode { isEnable = ... }, buildScript { isEnable = ... }")

        /**
         * 配置通用生成代码功能
         * @param action 配置方法体
         */
        fun all(action: Action<BaseGenerateConfigureExtension>) {
            allConfigure = BaseGenerateConfigureExtension().also { action.execute(it) }
        }

        /**
         * 配置项目生成代码功能
         * @param action 配置方法体
         */
        fun sourcesCode(action: Action<SourcesCodeGenerateConfigureExtension>) {
            sourcesCodeConfigure = SourcesCodeGenerateConfigureExtension().also { action.execute(it) }
        }

        /**
         * 配置构建脚本生成代码功能
         * @param action 配置方法体
         */
        fun buildScript(action: Action<BuildScriptGenerateConfigureExtension>) {
            buildScriptConfigure = BuildScriptGenerateConfigureExtension().also { action.execute(it) }
        }
    }

    /**
     * 项目生成代码配置方法体实现类
     */
    open inner class SourcesCodeGenerateConfigureExtension internal constructor() : BaseGenerateConfigureExtension() {

        /**
         * 自定义生成的目录路径
         *
         * 你可以填写相对于当前项目的路径
         *
         * 默认为 [ISweetPropertyConfigs.DEFAULT_GENERATE_DIR_PATH]
         */
        var generateDirPath = ""
            @JvmName("generateDirPath") set

        /**
         * 自定义生成的包名
         *
         * Android 项目默认使用 "android" 配置方法块中的 "namespace"
         *
         * 普通的 Kotlin on Jvm 项目默认使用项目设置的 "project.group"
         */
        var packageName = ""
            @JvmName("packageName") set

        /**
         * 自定义生成的类名
         *
         * 默认使用当前项目的名称 + "Properties"
         */
        var className = ""
            @JvmName("className") set

        /**
         * 是否启用受限访问功能
         *
         * 默认不启用 - 启用后将为生成的类和方法添加 "internal" 修饰符
         */
        var isEnableRestrictedAccess: Boolean? = null
            @JvmName("enableRestrictedAccess") set
    }

    /**
     * 构建脚本生成代码配置方法体实现类
     */
    open inner class BuildScriptGenerateConfigureExtension internal constructor() : BaseGenerateConfigureExtension() {

        /**
         * 自定义构建脚本扩展方法名称
         *
         * 默认为 [ISweetPropertyConfigs.DEFAULT_EXTENSION_NAME]
         */
        var extensionName = ""
            @JvmName("extensionName") set
    }

    /**
     * 通用生成代码配置方法体实现类
     */
    open inner class BaseGenerateConfigureExtension internal constructor() {

        /** 当前属性配置文件路径数组 */
        internal var propertiesFileNames: MutableList<String>? = null

        /** 当前固定存在的属性键值数组 */
        internal var permanentKeyValues: MutableMap<String, Any>? = null

        /** 当前被排除的属性键值名称数组 */
        internal var excludeKeys: MutableList<Any>? = null

        /** 当前被包含的属性键值名称数组 */
        internal var includeKeys: MutableList<Any>? = null

        /** 当前属性键值规则数组 */
        internal var keyValuesRules: MutableMap<String, PropertyValueRule>? = null

        /** 当前生成位置类型数组 */
        internal var generateLocationTypes: Array<GenerateLocationType>? = null

        /**
         * 是否为当前功能生成代码
         *
         * 默认启用
         *
         * [SourcesCodeGenerateConfigureExtension] 启用后将会为当前项目生成代码并添加到当前项目的 sourceSets 中
         *
         * [BuildScriptGenerateConfigureExtension] 启用后将会为构建脚本生成代码并为构建脚本生成扩展方法
         *
         * 在 [BuildScriptGenerateConfigureExtension] 中你可以使用 [BuildScriptGenerateConfigureExtension.extensionName] 来自定义构建脚本扩展方法名称
         */
        var isEnable: Boolean? = null
            @JvmName("enable") set

        /**
         * 设置属性配置文件名称
         *
         * - 此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - 请现在迁移到 [propertiesFileNames]
         */
        @Deprecated(message = "Migrate to propertiesFileNames(...)")
        var propertiesFileName = ""
            @JvmName("propertiesFileName") set

        /**
         * 是否启用排除非字符串类型键值内容
         *
         * 默认启用 - 启用后将从属性键值中排除不是字符串类型的键值及内容
         */
        var isEnableExcludeNonStringValue: Boolean? = null
            @JvmName("enableExcludeNonStringValue") set

        /**
         * 是否启用类型自动转换功能
         *
         * 默认启用 - 启用后将自动识别属性键值中的类型并转换为对应的类型
         *
         * 在启用后如果你想要强制设置一个键值内容为字符串类型 - 你可以使用单引号或双引号包裹整个字符串
         *
         * 注意：在关闭此功能后如上所述的功能也将同时失效
         */
        var isEnableTypeAutoConversion: Boolean? = null
            @JvmName("enableTypeAutoConversion") set

        /**
         * 是否启用键值内容插值功能
         *
         * 默认启用 - 启用后将自动识别属性键值内容中的 ${...} 内容并进行替换
         *
         * 注意：插值的内容仅会从当前 (当前配置文件) 属性键值列表进行查找
         */
        var isEnableValueInterpolation: Boolean? = null
            @JvmName("enableValueInterpolation") set

        /**
         * 设置属性配置文件名称数组
         *
         * 属性配置文件将根据你设置的文件名称自动从当前根项目、子项目以及用户目录的根目录进行获取
         *
         * 默认为 [ISweetPropertyConfigs.DEFAULT_PROPERTIES_FILE_NAME]
         *
         * 你可以添加多组属性配置文件名称 - 将按照顺序依次进行读取
         *
         * - 注意：一般情况下不需要修改此设置 - 错误的文件名称将导致获取到空键值内容
         * @param names 要添加的属性配置文件名称数组
         * @param isAddDefault 是否添加默认的属性配置文件名称 - 默认是
         */
        @JvmOverloads
        fun propertiesFileNames(vararg names: String, isAddDefault: Boolean = true) {
            if (names.isEmpty()) SError.make("Properties file names must not be empty")
            if (names.any { it.isBlank() }) SError.make("Properties file names must not have blank contents")
            propertiesFileNames = names.distinct().toMutableList()
            if (isAddDefault) propertiesFileNames?.add(0, ISweetPropertyConfigs.DEFAULT_PROPERTIES_FILE_NAME)
        }

        /**
         * 设置固定存在的属性键值数组
         *
         * 在这里可以设置一些一定存在的键值 - 这些键值无论能否从属性键值中得到都会进行生成
         *
         * 这些键值在属性键值存在时使用属性键值的内容 - 不存在时使用这里设置的内容
         *
         * - 注意：属性键值名称不能存在特殊符号以及空格 - 否则可能会生成失败
         * @param pairs 键值数组
         */
        @JvmName("-kotlin-dsl-only-permanentKeyValues-")
        fun permanentKeyValues(vararg pairs: Pair<String, Any>) {
            if (pairs.isEmpty()) SError.make("Permanent key-values must not be empty")
            if (pairs.any { it.first.isBlank() }) SError.make("Permanent key-values must not have blank contents")
            permanentKeyValues = mutableMapOf(*pairs)
        }

        /**
         * 设置固定存在的属性键值数组 (Groovy 兼容方法)
         *
         * 在这里可以设置一些一定存在的键值 - 这些键值无论能否从属性键值中得到都会进行生成
         *
         * 这些键值在属性键值存在时使用属性键值的内容 - 不存在时使用这里设置的内容
         *
         * - 注意：属性键值名称不能存在特殊符号以及空格 - 否则可能会生成失败
         * @param keyValues 键值数组
         */
        fun permanentKeyValues(keyValues: Map<String, Any>) {
            if (keyValues.isEmpty()) SError.make("Permanent key-values must not be empty")
            if (keyValues.any { it.key.isBlank() }) SError.make("Permanent key-values must not have blank contents")
            permanentKeyValues = keyValues.toMutableMap()
        }

        /**
         * 设置需要排除的属性键值名称数组
         *
         * 在这里可以设置一些你希望从已知的属性键值中排除的键值名称
         *
         * 这些键值在属性键值存在它们时被排除 - 不会出现在生成的代码中
         *
         * - 注意：如果你排除了 [permanentKeyValues] 中设置的键值 -
         *        那么它们只会变为你设置的初始键值内容并继续保持存在
         * @param keys 键值名称数组 - 你可以传入 [Regex] 或使用 [String.toRegex] 以使用正则功能
         */
        fun excludeKeys(vararg keys: Any) {
            if (keys.isEmpty()) SError.make("Exclude keys must not be empty")
            if (keys.any { it.toString().isBlank() }) SError.make("Exclude keys must not have blank contents")
            excludeKeys = keys.distinct().toMutableList()
        }

        /**
         * 设置需要包含的属性键值名称数组
         *
         * 在这里可以设置一些你希望从已知的属性键值中包含的键值名称
         *
         * 这些键值在属性键值存在它们时被包含 - 未被包含的键值不会出现在生成的代码中
         * @param keys 键值名称数组 - 你可以传入 [Regex] 或使用 [String.toRegex] 以使用正则功能
         */
        fun includeKeys(vararg keys: Any) {
            if (keys.isEmpty()) SError.make("Include keys must not be empty")
            if (keys.any { it.toString().isBlank() }) SError.make("Include keys must not have blank contents")
            includeKeys = keys.distinct().toMutableList()
        }

        /**
         * 设置属性键值规则数组
         *
         * 你可以设置一组键值规则 - 使用 [createValueRule] 创建新的规则 - 用于解析得到的键值内容
         *
         * 示例如下 ↓
         *
         * ```kotlin
         * keyValuesRules(
         *     "some.key1" to createValueRule { if (it.contains("_")) it.replace("_", "-") else it },
         *     "some.key2" to createValueRule { "$it-value" }
         * )
         * ```
         *
         * 这些键值规则在属性键值存在它们时被应用
         * @param pairs 属性键值规则数组
         */
        @JvmName("-kotlin-dsl-only-keyValuesRules-")
        fun keyValuesRules(vararg pairs: Pair<String, PropertyValueRule>) {
            if (pairs.isEmpty()) SError.make("Key-values rules must not be empty")
            if (pairs.any { it.first.isBlank() }) SError.make("Key-values rules must not have blank contents")
            keyValuesRules = mutableMapOf(*pairs)
        }

        /**
         * 设置属性键值规则数组 (Groovy 兼容方法)
         *
         * 你可以设置一组键值规则 - 使用 [createValueRule] 创建新的规则 - 用于解析得到的键值内容
         *
         * 这些键值规则在属性键值存在它们时被应用
         * @param rules 属性键值规则数组
         */
        fun keyValuesRules(rules: Map<String, PropertyValueRule>) {
            if (rules.isEmpty()) SError.make("Key-values rules must not be empty")
            if (rules.any { it.key.isBlank() }) SError.make("Key-values rules must not have blank contents")
            keyValuesRules = rules.toMutableMap()
        }

        /**
         * 创建新的属性键值规则
         * @param rule 回调当前规则
         * @return [PropertyValueRule]
         */
        fun createValueRule(rule: PropertyValueRule) = rule

        /**
         * 设置从何处生成属性键值
         *
         * 默认为 [ISweetPropertyConfigs.defaultGenerateLocationTypes]
         *
         * 你可以使用以下类型来进行设置
         *
         * - [CURRENT_PROJECT]
         * - [ROOT_PROJECT]
         * - [GLOBAL]
         * - [SYSTEM]
         * - [SYSTEM_ENV]
         *
         * [SweetProperty] 将从你设置的生成位置依次生成属性键值 - 生成位置的顺序跟随你设置的顺序决定
         *
         * - 风险提示：[GLOBAL]、[SYSTEM]、[SYSTEM_ENV] 可能存在密钥和证书 - 请小心管理生成的代码
         * @param types 位置类型数组
         */
        fun generateFrom(vararg types: String) {
            generateLocationTypes = mutableListOf<GenerateLocationType>().apply {
                types.toList().noEmpty()?.forEach {
                    add(when (it) {
                        CURRENT_PROJECT -> GenerateLocationType.CURRENT_PROJECT
                        ROOT_PROJECT -> GenerateLocationType.ROOT_PROJECT
                        GLOBAL -> GenerateLocationType.GLOBAL
                        SYSTEM -> GenerateLocationType.SYSTEM
                        SYSTEM_ENV -> GenerateLocationType.SYSTEM_ENV
                        else -> SError.make("Invalid generate location type \"$it\"")
                    })
                } ?: SError.make("Generate location types must not be empty")
            }.toTypedArray()
        }
    }

    /**
     * 构造 [ISweetPropertyConfigs]
     * @param settings 当前设置
     * @return [ISweetPropertyConfigs]
     */
    internal fun build(settings: Settings): ISweetPropertyConfigs {
        /**
         * 检查是否以字母开头
         * @param description 描述
         */
        fun String.checkingStartWithLetter(description: String) {
            firstOrNull()?.also {
                if (it !in 'A'..'Z' && it !in 'a'..'z') SError.make("$description name \"$this\" must start with a letter")
            }
        }

        /** 检查合法包名 */
        fun String.checkingValidPackageName() {
            if (isNotBlank() && !matches("^[a-zA-Z_][a-zA-Z0-9_]*(\\.[a-zA-Z_][a-zA-Z0-9_]*)*$".toRegex()))
                SError.make("Invalid package name \"$this\"")
        }

        /** 检查合法类名 */
        fun String.checkingValidClassName() {
            if (isNotBlank() && !matches("^[a-zA-Z][a-zA-Z0-9_]*$".toRegex()))
                SError.make("Invalid class name \"$this\"")
        }

        /** 检查合法扩展方法名称 */
        fun String.checkingValidExtensionName() {
            checkingStartWithLetter(description = "Extension")
            if (isNotBlank() && isUnSafeExtName()) SError.make("This name \"$this\" is a Gradle built-in extension")
        }

        /** 检查名称是否合法 */
        fun SweetPropertyConfigureExtension.SubConfigureExtension.checkingNames() {
            sourcesCodeConfigure?.packageName?.checkingValidPackageName()
            sourcesCodeConfigure?.className?.checkingValidClassName()
            buildScriptConfigure?.extensionName?.checkingValidExtensionName()
        }
        val currentEnable = isEnable
        globalConfigure.checkingNames()
        val currentGlobal = globalConfigure.create()
        val currentProjects = mutableMapOf<String, ISweetPropertyConfigs.ISubConfigs>()
        val rootName = settings.rootProject.name
        if (projectConfigures.any { (name, _) -> name.lowercase() == rootName.lowercase() })
            SError.make("This name \"$rootName\" is a root project, please use rootProject function to configure it, not project(\"$rootName\")")
        if (projectConfigures.containsKey(ROOT_PROJECT_TAG)) {
            projectConfigures[rootName] = projectConfigures[ROOT_PROJECT_TAG] ?: SError.make("Internal error")
            projectConfigures.remove(ROOT_PROJECT_TAG)
        }
        projectConfigures.forEach { (name, subConfigure) ->
            name.replaceFirst(":", "").checkingStartWithLetter(description = "Project")
            subConfigure.checkingNames()
            currentProjects[name] = subConfigure.create(name, globalConfigure)
        }; return object : ISweetPropertyConfigs {
            override val isEnable get() = currentEnable
            override val global get() = currentGlobal
            override val projects get() = currentProjects
        }
    }
}