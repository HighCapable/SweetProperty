/*
 * SweetProperty -  An easy get project properties anywhere Gradle plugin
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
 * This file is Created by fankes on 2023/8/28.
 */
package com.highcapable.sweetproperty.plugin.config.factory

import com.highcapable.sweetproperty.gradle.factory.fullName
import com.highcapable.sweetproperty.plugin.config.default.DefaultConfigs
import com.highcapable.sweetproperty.plugin.config.proxy.ISweetPropertyConfigs
import com.highcapable.sweetproperty.plugin.extension.dsl.configure.SweetPropertyConfigureExtension
import com.highcapable.sweetproperty.utils.noBlank
import org.gradle.api.Project

/**
 * 获取当前配置
 * @param project 当前项目
 * @return [ISweetPropertyConfigs.ISubConfigs]
 */
internal fun ISweetPropertyConfigs.with(project: Project) = projects[project.fullName] ?: global

/**
 * 创建 [ISweetPropertyConfigs.ISubConfigs] 实例
 * @param name 名称 - 默认 "Global"
 * @param global 全局配置 - 默认为自身
 * @return [ISweetPropertyConfigs.ISubConfigs]
 */
internal fun SweetPropertyConfigureExtension.SubConfigureExtension.create(
    name: String = "Global",
    global: SweetPropertyConfigureExtension.SubConfigureExtension = this
) = object : ISweetPropertyConfigs.ISubConfigs {
    override val sourcesCode
        get() = this@create.sourcesCodeConfigure?.create(name, global.sourcesCodeConfigure, this@create.allConfigure, global.allConfigure)
            ?: global.sourcesCodeConfigure?.create(name, globalBase = global.allConfigure)
            ?: DefaultConfigs.subConfigs(name, global.allConfigure).sourcesCode
    override val buildScript
        get() = this@create.buildScriptConfigure?.create(name, global.buildScriptConfigure, this@create.allConfigure, global.allConfigure)
            ?: global.buildScriptConfigure?.create(name, globalBase = global.allConfigure)
            ?: DefaultConfigs.subConfigs(name, global.allConfigure).buildScript
}

/**
 * 创建 [ISweetPropertyConfigs.ISourcesCodeGenerateConfigs] 实例
 * @param name 名称
 * @param global 全局配置 - 默认 null
 * @param selfBase 自身继承配置 - 默认 null
 * @param globalBase 全局继承配置 - 默认 null
 * @return [ISweetPropertyConfigs.ISourcesCodeGenerateConfigs]
 */
private fun SweetPropertyConfigureExtension.SourcesCodeGenerateConfigureExtension.create(
    name: String,
    global: SweetPropertyConfigureExtension.SourcesCodeGenerateConfigureExtension? = null,
    selfBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null,
    globalBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null
) = object : ISweetPropertyConfigs.ISourcesCodeGenerateConfigs {
    override val name get() = name
    override val generateDirPath
        get() = this@create.generateDirPath.noBlank()
            ?: global?.generateDirPath?.noBlank()
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).generateDirPath
    override val packageName
        get() = this@create.packageName.noBlank()
            ?: global?.packageName?.noBlank()
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).packageName
    override val className
        get() = this@create.className.noBlank()
            ?: global?.className?.noBlank()
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).className
    override val isEnableRestrictedAccess
        get() = this@create.isEnableRestrictedAccess
            ?: global?.isEnableRestrictedAccess
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).isEnableRestrictedAccess
    override val isEnable
        get() = this@create.isEnable
            ?: selfBase?.isEnable
            ?: global?.isEnable
            ?: globalBase?.isEnable
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).isEnable
    override val propertiesFileNames
        get() = this@create.propertiesFileNames
            ?: global?.propertiesFileNames
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).propertiesFileNames
    override val permanentKeyValues
        get() = this@create.permanentKeyValues
            ?: global?.permanentKeyValues
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).permanentKeyValues
    override val excludeKeys
        get() = this@create.excludeKeys
            ?: global?.excludeKeys
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).excludeKeys
    override val includeKeys
        get() = this@create.includeKeys
            ?: global?.includeKeys
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).includeKeys
    override val isEnableExcludeNonStringValue
        get() = this@create.isEnableExcludeNonStringValue
            ?: selfBase?.isEnableExcludeNonStringValue
            ?: global?.isEnableExcludeNonStringValue
            ?: globalBase?.isEnableExcludeNonStringValue
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).isEnableExcludeNonStringValue
    override val isEnableTypeAutoConversion
        get() = this@create.isEnableTypeAutoConversion
            ?: selfBase?.isEnableTypeAutoConversion
            ?: global?.isEnableTypeAutoConversion
            ?: globalBase?.isEnableTypeAutoConversion
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).isEnableTypeAutoConversion
    override val isEnableValueInterpolation
        get() = this@create.isEnableValueInterpolation
            ?: selfBase?.isEnableValueInterpolation
            ?: global?.isEnableValueInterpolation
            ?: globalBase?.isEnableValueInterpolation
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).isEnableValueInterpolation
    override val generateLocationTypes
        get() = this@create.generateLocationTypes
            ?: selfBase?.generateLocationTypes
            ?: global?.generateLocationTypes
            ?: globalBase?.generateLocationTypes
            ?: DefaultConfigs.sourcesCodeGenerateConfigs(name, selfBase, globalBase).generateLocationTypes
}

/**
 * 创建 [ISweetPropertyConfigs.IBuildScriptGenerateConfigs] 实例
 * @param name 名称
 * @param global 全局配置 - 默认 null
 * @param selfBase 自身继承配置 - 默认 null
 * @param globalBase 全局继承配置 - 默认 null
 * @return [ISweetPropertyConfigs.IBuildScriptGenerateConfigs]
 */
private fun SweetPropertyConfigureExtension.BuildScriptGenerateConfigureExtension.create(
    name: String,
    global: SweetPropertyConfigureExtension.BuildScriptGenerateConfigureExtension? = null,
    selfBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null,
    globalBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null
) = object : ISweetPropertyConfigs.IBuildScriptGenerateConfigs {
    override val name get() = name
    override val extensionName
        get() = this@create.extensionName.noBlank()
            ?: global?.extensionName?.noBlank()
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).extensionName
    override val isEnable
        get() = this@create.isEnable
            ?: selfBase?.isEnable
            ?: global?.isEnable
            ?: globalBase?.isEnable
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).isEnable
    override val propertiesFileNames
        get() = this@create.propertiesFileNames
            ?: global?.propertiesFileNames
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).propertiesFileNames
    override val permanentKeyValues
        get() = this@create.permanentKeyValues
            ?: global?.permanentKeyValues
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).permanentKeyValues
    override val excludeKeys
        get() = this@create.excludeKeys
            ?: global?.excludeKeys
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).excludeKeys
    override val includeKeys
        get() = this@create.includeKeys
            ?: global?.includeKeys
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).includeKeys
    override val isEnableExcludeNonStringValue
        get() = this@create.isEnableExcludeNonStringValue
            ?: selfBase?.isEnableExcludeNonStringValue
            ?: global?.isEnableExcludeNonStringValue
            ?: globalBase?.isEnableExcludeNonStringValue
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).isEnableExcludeNonStringValue
    override val isEnableTypeAutoConversion
        get() = this@create.isEnableTypeAutoConversion
            ?: selfBase?.isEnableTypeAutoConversion
            ?: global?.isEnableTypeAutoConversion
            ?: globalBase?.isEnableTypeAutoConversion
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).isEnableTypeAutoConversion
    override val isEnableValueInterpolation
        get() = this@create.isEnableValueInterpolation
            ?: selfBase?.isEnableValueInterpolation
            ?: global?.isEnableValueInterpolation
            ?: globalBase?.isEnableValueInterpolation
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).isEnableValueInterpolation
    override val generateLocationTypes
        get() = this@create.generateLocationTypes
            ?: selfBase?.generateLocationTypes
            ?: global?.generateLocationTypes
            ?: globalBase?.generateLocationTypes
            ?: DefaultConfigs.buildScriptGenerateConfigs(name, selfBase, globalBase).generateLocationTypes
}