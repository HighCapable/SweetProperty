/*
 * SweetProperty -  An easy get project properties anywhere Gradle plugin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is created by fankes on 2023/8/27.
 */
package com.highcapable.sweetproperty.plugin.generator

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.generated.SweetPropertyProperties
import com.highcapable.sweetproperty.plugin.config.proxy.ISweetPropertyConfigs
import com.highcapable.sweetproperty.plugin.extension.accessors.proxy.IExtensionAccessors
import com.highcapable.sweetproperty.plugin.generator.factory.PropertyMap
import com.highcapable.sweetproperty.plugin.generator.factory.parseTypedValue
import com.highcapable.sweetproperty.utils.capitalize
import com.highcapable.sweetproperty.utils.debug.SError
import com.highcapable.sweetproperty.utils.firstNumberToLetter
import com.highcapable.sweetproperty.utils.toNonJavaName
import com.highcapable.sweetproperty.utils.uncapitalize
import com.highcapable.sweetproperty.utils.uppercamelcase
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.text.SimpleDateFormat
import java.util.*
import javax.annotation.Nonnull
import javax.lang.model.element.Modifier
import kotlin.properties.Delegates

/**
 * 属性键值可访问 [Class] 生成实现类
 */
internal class PropertiesAccessorsGenerator {

    private companion object {

        /** 生成的 [Class] 所在包名 */
        private const val ACCESSORS_PACKAGE_NAME = "${SweetPropertyProperties.PROJECT_GROUP_NAME}.plugin.extension.accessors.generated"

        /** 生成的 [Class] 后缀名 */
        private const val CLASS_SUFFIX_NAME = "Accessors"

        /** 生成的首位 [Class] 后缀名 */
        private const val TOP_CLASS_SUFFIX_NAME = "Properties$CLASS_SUFFIX_NAME"

        /** 标识首位生成的 [Class] TAG */
        private const val TOP_SUCCESSIVE_NAME = "_top_successive_name"
    }

    /** 当前配置实例 */
    private var configs by Delegates.notNull<ISweetPropertyConfigs.IBuildScriptGenerateConfigs>()

    /** 生成的属性键值 [Class] 构建器数组 */
    private val classSpecs = mutableMapOf<String, TypeSpec.Builder>()

    /** 生成的属性键值构造方法构建器数组 */
    private val constructorSpecs = mutableMapOf<String, MethodSpec.Builder>()

    /** 生成的属性键值预添加的构造方法名称数组 */
    private val preAddConstructorSpecNames = mutableListOf<Pair<String, String>>()

    /** 生成的属性键值 [Class] 扩展类名数组 */
    private val memoryExtensionClasses = mutableMapOf<String, String>()

    /** 生成的属性键值连续名称记录数组 */
    private val grandSuccessiveNames = mutableListOf<String>()

    /** 生成的属性键值连续名称重复次数数组 */
    private val grandSuccessiveDuplicateIndexs = mutableMapOf<String, Int>()

    /** 生成的属性键值不重复 TAG 数组 */
    private val usedSuccessiveTags = mutableSetOf<String>()

    /**
     * 不重复调用
     * @param tags 当前 TAG 数组
     * @param block 执行的方法块
     */
    private inline fun noRepeated(vararg tags: String, block: () -> Unit) {
        val allTag = tags.joinToString("-")
        if (usedSuccessiveTags.contains(allTag).not()) block()
        usedSuccessiveTags.add(allTag)
    }

    /**
     * 字符串首字母大写并添加 [CLASS_SUFFIX_NAME] 后缀
     * @return [String]
     */
    private fun String.capitalized() = "${capitalize()}$CLASS_SUFFIX_NAME"

    /**
     * 字符串首字母小写并添加 [CLASS_SUFFIX_NAME] 后缀
     * @return [String]
     */
    private fun String.uncapitalized() = "${uncapitalize()}$CLASS_SUFFIX_NAME"

    /**
     * 字符串类名转换为 [ClassName]
     * @param packageName 包名 - 默认空
     * @return [ClassName]
     */
    private fun String.asClassType(packageName: String = "") = ClassName.get(packageName, this)

    /**
     * 通过 [TypeSpec] 创建 [JavaFile]
     * @return [JavaFile]
     */
    private fun TypeSpec.createJavaFile(packageName: String) = JavaFile.builder(packageName, this).build()

    /**
     * 创建通用构建器描述类
     * @param name 名称
     * @param accessorsName 接续名 - 默认空
     * @param isInner 是否为内部类 - 默认是
     * @return [TypeSpec.Builder]
     */
    private fun createClassSpec(name: String, accessorsName: String = "", isInner: Boolean = true) =
        TypeSpec.classBuilder(if (isInner) name.capitalized() else name).apply {
            if (isInner) {
                addJavadoc("The \"$accessorsName\" accessors")
                addSuperinterface(IExtensionAccessors::class.java)
                addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            } else {
                addJavadoc(
                    """
                      This class is generated by ${SweetProperty.TAG} at ${SimpleDateFormat.getDateTimeInstance().format(Date())}
                      <br/>
                      The content here is automatically generated according to the properties of your projects
                      <br/>
                      You can visit <a href="${SweetProperty.PROJECT_URL}">here</a> for more help
                    """.trimIndent()
                )
                addModifiers(Modifier.PUBLIC)
            }
        }

    /**
     * 创建通用构造方法构建器描述类
     * @return [MethodSpec.Builder]
     */
    private fun createConstructorSpec() = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)

    /**
     * 向通用构建器描述类添加变量
     * @param accessorsName 接续名
     * @param className 类名
     * @return [TypeSpec.Builder]
     */
    private fun TypeSpec.Builder.addSuccessiveField(accessorsName: String, className: String) = addField(
        FieldSpec.builder(className.capitalized().asClassType(), className.uncapitalized(), Modifier.PRIVATE, Modifier.FINAL)
            .addJavadoc("Create the \"$accessorsName\" accessors")
            .build()
    )

    /**
     * 向通用构建器描述类添加方法
     * @param accessorsName 接续名
     * @param methodName 方法名
     * @param className 类名
     * @return [TypeSpec.Builder]
     */
    private fun TypeSpec.Builder.addSuccessiveMethod(accessorsName: String, methodName: String, className: String) =
        addMethod(
            MethodSpec.methodBuilder("get${methodName.capitalize()}")
                .addJavadoc("Resolve the \"$accessorsName\" accessors")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(Nonnull::class.java)
                .returns(className.capitalized().asClassType())
                .addStatement("return ${className.uncapitalized()}")
                .build()
        )

    /**
     * 向通用构建器描述类添加最终键值方法
     * @param accessorsName 接续名
     * @param methodName 方法名
     * @param value 键值内容
     * @return [TypeSpec.Builder]
     */
    private fun TypeSpec.Builder.addFinalValueMethod(accessorsName: String, methodName: String, value: Any) =
        addMethod(
            MethodSpec.methodBuilder("get${methodName.capitalize()}").apply {
                val typedValue = value.parseTypedValue(configs.isEnableTypeAutoConversion)
                addJavadoc("Resolve the \"$accessorsName\" value ${typedValue.second}")
                addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Nonnull::class.java)
                    .returns(typedValue.first.java)
                    .addStatement("return ${typedValue.second}")
            }.build()
        )

    /**
     * 向通用构造方法构建器描述类添加变量实例化语句
     * @param className 类名
     * @return [MethodSpec.Builder]
     */
    private fun MethodSpec.Builder.addSuccessiveStatement(className: String) =
        addStatement("${className.uncapitalized()} = new ${className.capitalized()}()")

    /**
     * 获取、创建通用构建器描述类
     * @param name 名称
     * @param accessorsName 接续名 - 默认空
     * @return [TypeSpec.Builder]
     */
    private fun getOrCreateClassSpec(name: String, accessorsName: String = "") =
        classSpecs[name] ?: createClassSpec(name, accessorsName).also { classSpecs[name] = it }

    /**
     * 获取、创建通用构造方法构建器描述类
     * @param name 名称
     * @return [MethodSpec.Builder]
     */
    private fun getOrCreateConstructorSpec(name: String) = constructorSpecs[name] ?: createConstructorSpec().also { constructorSpecs[name] = it }

    /**
     * 解析并生成所有类的构建器 (核心方法)
     *
     * 解析开始前需要确保已调用 [createTopClassSpec] 并调用一次 [clearGeneratedData] 防止数据混淆
     *
     * 解析完成后需要调用 [releaseParseTypeSpec] 完成解析
     * @param successiveName 连续的名称
     * @param value 键值内容
     */
    private fun parseTypeSpec(successiveName: String, value: Any) {
        /**
         * 获取生成的属性键值连续名称重复次数
         * @return [Int]
         */
        fun String.duplicateGrandSuccessiveIndex() = lowercase().let { name ->
            if (grandSuccessiveDuplicateIndexs.contains(name)) {
                grandSuccessiveDuplicateIndexs[name] = (grandSuccessiveDuplicateIndexs[name] ?: 1) + 1
                grandSuccessiveDuplicateIndexs[name] ?: 2
            } else 2.also { grandSuccessiveDuplicateIndexs[name] = it }
        }

        /**
         * 解析 (拆分) 名称到数组
         *
         * 形如 "com.mytest" → "ComMytest" → "mytest"
         * @return [List]<[Triple]<[String], [String], [String]>>
         */
        fun String.parseSuccessiveNames(): List<Triple<String, String, String>> {
            var grandAcccessorsName = ""
            var grandSuccessiveName = ""
            val successiveNames = mutableListOf<Triple<String, String, String>>()
            val splitNames = replace(".", "|").replace("-", "|")
                .replace("_", "|").replace(" ", "_")
                .split("|").dropWhile { it.isBlank() }
                .ifEmpty { listOf(this) }
            splitNames.forEach { eachName ->
                val name = eachName.capitalize().toNonJavaName().firstNumberToLetter()
                grandAcccessorsName += if (grandAcccessorsName.isNotBlank()) ".$eachName" else eachName
                grandSuccessiveName += name
                if (grandSuccessiveNames.any { it != grandSuccessiveName && it.lowercase() == grandSuccessiveName.lowercase() })
                    grandSuccessiveName += duplicateGrandSuccessiveIndex().toString()
                grandSuccessiveNames.add(grandSuccessiveName)
                successiveNames.add(Triple(grandAcccessorsName, grandSuccessiveName, name))
            }; return successiveNames.distinct()
        }
        val successiveNames = successiveName.parseSuccessiveNames()
        successiveNames.forEachIndexed { index, (accessorsName, className, methodName) ->
            val nextItem = successiveNames.getOrNull(index + 1)
            val lastItem = successiveNames.getOrNull(successiveNames.lastIndex)
            val nextAccessorsName = nextItem?.first ?: ""
            val nextClassName = nextItem?.second ?: ""
            val nextMethodName = nextItem?.third ?: ""
            val lastMethodName = lastItem?.third ?: ""
            val isPreLastIndex = index == successiveNames.lastIndex - 1
            if (successiveNames.size == 1) getOrCreateClassSpec(TOP_SUCCESSIVE_NAME).addFinalValueMethod(successiveName, methodName, value)
            if (index == successiveNames.lastIndex) return@forEachIndexed
            if (index == 0) noRepeated(TOP_SUCCESSIVE_NAME, methodName, className) {
                getOrCreateClassSpec(TOP_SUCCESSIVE_NAME, accessorsName)
                    .addSuccessiveField(accessorsName, className)
                    .addSuccessiveMethod(accessorsName, methodName, className)
                getOrCreateConstructorSpec(TOP_SUCCESSIVE_NAME).addSuccessiveStatement(className)
            }
            noRepeated(className, nextMethodName, nextClassName) {
                getOrCreateClassSpec(className, accessorsName).apply {
                    if (isPreLastIndex.not()) {
                        addSuccessiveField(nextAccessorsName, nextClassName)
                        addSuccessiveMethod(nextAccessorsName, nextMethodName, nextClassName)
                    } else addFinalValueMethod(successiveName, lastMethodName, value)
                }
                if (isPreLastIndex.not()) preAddConstructorSpecNames.add(className to nextClassName)
            }
        }
    }

    /** 完成生成所有类的构建器 (释放) */
    private fun releaseParseTypeSpec() =
        preAddConstructorSpecNames.onEach { (topClassName, innerClassName) ->
            getOrCreateConstructorSpec(topClassName)?.addSuccessiveStatement(innerClassName)
        }.clear()

    /**
     * 解析并生成所有类的构建器
     * @return [TypeSpec]
     */
    private fun buildTypeSpec(): TypeSpec {
        classSpecs.forEach { (name, typeSpec) ->
            constructorSpecs[name]?.build()?.let { typeSpec.addMethod(it) }
            if (name != TOP_SUCCESSIVE_NAME) classSpecs[TOP_SUCCESSIVE_NAME]?.addType(typeSpec.build())
        }; return classSpecs[TOP_SUCCESSIVE_NAME]?.build() ?: SError.make("Merge accessors classes failed")
    }

    /**
     * 创建首位构建器
     * @param configs 当前配置
     * @throws IllegalStateException 如果名称为空
     */
    private fun createTopClassSpec(configs: ISweetPropertyConfigs.IBuildScriptGenerateConfigs) {
        if (configs.name.isBlank()) SError.make("Class name cannot be empty or blank")
        this.configs = configs
        val topClassName = "${configs.name.replace(":", "_").uppercamelcase()}$TOP_CLASS_SUFFIX_NAME"
        memoryExtensionClasses[configs.name] = "$ACCESSORS_PACKAGE_NAME.$topClassName"
        classSpecs[TOP_SUCCESSIVE_NAME] = createClassSpec(topClassName, isInner = false)
        constructorSpecs[TOP_SUCCESSIVE_NAME] = createConstructorSpec()
    }

    /**
     * 清空所有已生成的数据
     * @param isClearAll 是否全部清空 - 包括添加的 [memoryExtensionClasses] - 默认否
     */
    private fun clearGeneratedData(isClearAll: Boolean = false) {
        classSpecs.clear()
        constructorSpecs.clear()
        preAddConstructorSpecNames.clear()
        grandSuccessiveNames.clear()
        grandSuccessiveDuplicateIndexs.clear()
        usedSuccessiveTags.clear()
        if (isClearAll) memoryExtensionClasses.clear()
    }

    /**
     * 生成 [JavaFile] 数组
     *
     * - 注意：[allConfigs] 与 [allKeyValues] 数量必须相等
     * @param allConfigs 全部配置实例
     * @param allKeyValues 全部键值数组
     * @return [MutableList]<[JavaFile]>
     * @throws IllegalStateException 如果生成失败
     */
    internal fun build(
        allConfigs: MutableList<ISweetPropertyConfigs.IBuildScriptGenerateConfigs>,
        allKeyValues: MutableList<PropertyMap>
    ) = runCatching {
        val files = mutableListOf<JavaFile>()
        if (allConfigs.size != allKeyValues.size) SError.make("Invalid build arguments")
        if (allConfigs.isEmpty()) return@runCatching files
        clearGeneratedData(isClearAll = true)
        allConfigs.forEachIndexed { index, configs ->
            val keyValues = allKeyValues[index]
            clearGeneratedData()
            createTopClassSpec(configs)
            keyValues.forEach { (key, value) ->
                parseTypeSpec(key, value)
                releaseParseTypeSpec()
            }; files.add(buildTypeSpec().createJavaFile(ACCESSORS_PACKAGE_NAME))
        }; files
    }.getOrElse { SError.make("Failed to generated accessors classes\n$it") }

    /**
     * 获取参与编译的 Stub [JavaFile] 数组
     * @return [List]<[JavaFile]>
     */
    internal val compileStubFiles get(): List<JavaFile> {
        val stubFiles = mutableListOf<JavaFile>()
        val nonnullFile =
            TypeSpec.annotationBuilder(Nonnull::class.java.simpleName)
                .addModifiers(Modifier.PUBLIC)
                .build().createJavaFile(Nonnull::class.java.packageName)
        val iExtensionAccessorsFile =
            TypeSpec.interfaceBuilder(IExtensionAccessors::class.java.simpleName)
                .addModifiers(Modifier.PUBLIC)
                .build().createJavaFile(IExtensionAccessors::class.java.packageName)
        stubFiles.add(nonnullFile)
        stubFiles.add(iExtensionAccessorsFile)
        return stubFiles
    }

    /**
     * 获取扩展功能预置 [Class]
     * @param name 名称
     * @return [String]
     * @throws IllegalStateException 如果 [Class] 不存在
     */
    internal fun propertiesClass(name: String) = memoryExtensionClasses[name] ?: SError.make("Could not found class \"$name\"")
}