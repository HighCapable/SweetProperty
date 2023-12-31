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
 * This file is created by fankes on 2023/8/28.
 */
package com.highcapable.sweetproperty.plugin.config.default

import com.highcapable.sweetproperty.plugin.config.proxy.ISweetPropertyConfigs
import com.highcapable.sweetproperty.plugin.extension.dsl.configure.SweetPropertyConfigureExtension
import com.highcapable.sweetproperty.plugin.generator.factory.PropertyValueRule

/**
 * 默认配置类实现类
 */
internal object DefaultConfigs {

    /**
     * 获取默认子配置类
     * @param name 名称
     * @param base 继承配置 - 默认 null
     * @return [ISweetPropertyConfigs.ISubConfigs]
     */
    internal fun subConfigs(name: String, base: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null) =
        object : ISweetPropertyConfigs.ISubConfigs {
            override val sourcesCode get() = sourcesCodeGenerateConfigs(name, base)
            override val buildScript get() = buildScriptGenerateConfigs(name, base)
        }

    /**
     * 获取默认项目生成代码配置类
     * @param name 名称
     * @param selfBase 自身继承配置 - 默认 null
     * @param globalBase 全局继承配置 - 默认 null
     * @return [ISweetPropertyConfigs.ISourcesCodeGenerateConfigs]
     */
    internal fun sourcesCodeGenerateConfigs(
        name: String,
        selfBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null,
        globalBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null
    ) = object : ISweetPropertyConfigs.ISourcesCodeGenerateConfigs {
        override val name get() = name
        override val generateDirPath get() = ISweetPropertyConfigs.DEFAULT_GENERATE_DIR_PATH
        override val packageName get() = ""
        override val className get() = ""
        override val isEnableRestrictedAccess get() = false
        override val isEnable
            get() = selfBase?.isEnable
                ?: globalBase?.isEnable
                ?: baseGenerateConfigs(name).isEnable
        override val propertiesFileNames
            get() = selfBase?.propertiesFileNames
                ?: globalBase?.propertiesFileNames
                ?: baseGenerateConfigs(name).propertiesFileNames
        override val permanentKeyValues
            get() = selfBase?.permanentKeyValues
                ?: globalBase?.permanentKeyValues
                ?: baseGenerateConfigs(name).permanentKeyValues
        override val excludeKeys
            get() = selfBase?.excludeKeys
                ?: globalBase?.excludeKeys
                ?: baseGenerateConfigs(name).excludeKeys
        override val includeKeys
            get() = selfBase?.includeKeys
                ?: globalBase?.includeKeys
                ?: baseGenerateConfigs(name).includeKeys
        override val keyValuesRules
            get() = selfBase?.keyValuesRules
                ?: globalBase?.keyValuesRules
                ?: baseGenerateConfigs(name).keyValuesRules
        override val isEnableExcludeNonStringValue
            get() = selfBase?.isEnableExcludeNonStringValue
                ?: globalBase?.isEnableExcludeNonStringValue
                ?: baseGenerateConfigs(name).isEnableExcludeNonStringValue
        override val isEnableTypeAutoConversion
            get() = selfBase?.isEnableTypeAutoConversion
                ?: globalBase?.isEnableTypeAutoConversion
                ?: baseGenerateConfigs(name).isEnableTypeAutoConversion
        override val isEnableValueInterpolation
            get() = selfBase?.isEnableValueInterpolation
                ?: globalBase?.isEnableValueInterpolation
                ?: baseGenerateConfigs(name).isEnableValueInterpolation
        override val generateLocationTypes
            get() = selfBase?.generateLocationTypes
                ?: globalBase?.generateLocationTypes
                ?: baseGenerateConfigs(name).generateLocationTypes
    }

    /**
     * 获取默认构建脚本生成代码配置类
     * @param name 名称
     * @param selfBase 自身继承配置 - 默认 null
     * @param globalBase 全局继承配置 - 默认 null
     * @return [ISweetPropertyConfigs.IBuildScriptGenerateConfigs]
     */
    internal fun buildScriptGenerateConfigs(
        name: String,
        selfBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null,
        globalBase: SweetPropertyConfigureExtension.BaseGenerateConfigureExtension? = null
    ) = object : ISweetPropertyConfigs.IBuildScriptGenerateConfigs {
        override val name get() = name
        override val extensionName get() = ISweetPropertyConfigs.DEFAULT_EXTENSION_NAME
        override val isEnable
            get() = selfBase?.isEnable
                ?: globalBase?.isEnable
                ?: baseGenerateConfigs(name).isEnable
        override val propertiesFileNames
            get() = selfBase?.propertiesFileNames
                ?: globalBase?.propertiesFileNames
                ?: baseGenerateConfigs(name).propertiesFileNames
        override val permanentKeyValues
            get() = selfBase?.permanentKeyValues
                ?: globalBase?.permanentKeyValues
                ?: baseGenerateConfigs(name).permanentKeyValues
        override val excludeKeys
            get() = selfBase?.excludeKeys
                ?: globalBase?.excludeKeys
                ?: baseGenerateConfigs(name).excludeKeys
        override val includeKeys
            get() = selfBase?.includeKeys
                ?: globalBase?.includeKeys
                ?: baseGenerateConfigs(name).includeKeys
        override val keyValuesRules
            get() = selfBase?.keyValuesRules
                ?: globalBase?.keyValuesRules
                ?: baseGenerateConfigs(name).keyValuesRules
        override val isEnableExcludeNonStringValue
            get() = selfBase?.isEnableExcludeNonStringValue
                ?: globalBase?.isEnableExcludeNonStringValue
                ?: baseGenerateConfigs(name).isEnableExcludeNonStringValue
        override val isEnableTypeAutoConversion
            get() = selfBase?.isEnableTypeAutoConversion
                ?: globalBase?.isEnableTypeAutoConversion
                ?: baseGenerateConfigs(name).isEnableTypeAutoConversion
        override val isEnableValueInterpolation
            get() = selfBase?.isEnableValueInterpolation
                ?: globalBase?.isEnableValueInterpolation
                ?: baseGenerateConfigs(name).isEnableValueInterpolation
        override val generateLocationTypes
            get() = selfBase?.generateLocationTypes
                ?: globalBase?.generateLocationTypes
                ?: baseGenerateConfigs(name).generateLocationTypes
    }

    /**
     * 获取默认通用生成代码配置类
     * @param name 名称
     * @return [ISweetPropertyConfigs.IBaseGenerateConfigs]
     */
    private fun baseGenerateConfigs(name: String) = object : ISweetPropertyConfigs.IBaseGenerateConfigs {
        override val name get() = name
        override val isEnable get() = true
        override val propertiesFileNames get() = mutableListOf(ISweetPropertyConfigs.DEFAULT_PROPERTIES_FILE_NAME)
        override val permanentKeyValues get() = mutableMapOf<String, Any>()
        override val excludeKeys get() = mutableListOf<Any>()
        override val includeKeys get() = mutableListOf<Any>()
        override val keyValuesRules get() = mutableMapOf<String, PropertyValueRule>()
        override val isEnableExcludeNonStringValue get() = true
        override val isEnableTypeAutoConversion get() = true
        override val isEnableValueInterpolation get() = true
        override val generateLocationTypes get() = ISweetPropertyConfigs.defaultGenerateLocationTypes
    }
}