/*
 * SweetProperty -  An easy get project properties anywhere Gradle plugin.
 * Copyright (C) 2019 HighCapable
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
package com.highcapable.sweetproperty.plugin.config.proxy

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.generated.SweetPropertyProperties
import com.highcapable.sweetproperty.plugin.config.type.GenerateLocationType
import com.highcapable.sweetproperty.plugin.generator.factory.PropertyValueRule

/**
 * [SweetProperty] 配置类接口类
 */
internal interface ISweetPropertyConfigs {

    companion object {

        /**
         * 默认的生成目录路径
         *
         * "build/generated/[SweetPropertyProperties.PROJECT_MODULE_NAME]"
         */
        internal const val DEFAULT_GENERATE_DIR_PATH = "build/generated/${SweetPropertyProperties.PROJECT_MODULE_NAME}"

        /**
         * 默认的部署 `sourceSet` 名称
         */
        internal const val DEFAULT_SOURCE_SET_NAME = "main"

        /**
         * 默认的属性配置文件名称
         *
         * "gradle.properties"
         */
        internal const val DEFAULT_PROPERTIES_FILE_NAME = "gradle.properties"

        /**
         * 默认的构建脚本扩展方法名称
         *
         * "property"
         */
        internal const val DEFAULT_EXTENSION_NAME = "property"

        /**
         * 默认的生成位置类型数组
         *
         * arrayOf([GenerateLocationType.CURRENT_PROJECT], [GenerateLocationType.ROOT_PROJECT])
         */
        internal val defaultGenerateLocationTypes = arrayOf(GenerateLocationType.CURRENT_PROJECT, GenerateLocationType.ROOT_PROJECT)
    }

    /** 是否启用插件 */
    val isEnable: Boolean

    /** 配置全部 */
    val global: ISubConfigs

    /** 配置每个项目数组 */
    val projects: MutableMap<String, ISubConfigs>

    /**
     * 子配置类接口类
     */
    interface ISubConfigs {

        /** 项目生成代码配置类接口 */
        val sourcesCode: ISourcesCodeGenerateConfigs

        /** 构建脚本生成代码配置类接口 */
        val buildScript: IBuildScriptGenerateConfigs
    }

    /**
     * 项目生成代码配置类接口类
     */
    interface ISourcesCodeGenerateConfigs : IBaseGenerateConfigs {

        /** 自定义生成的目录路径 */
        val generateDirPath: String

        /** 自定义部署的 `sourceSet` 名称 */
        val sourceSetName: String

        /** 自定义生成的包名 */
        val packageName: String

        /** 自定义生成的类名 */
        val className: String

        /** 是否启用受限访问功能 */
        val isEnableRestrictedAccess: Boolean
    }

    /**
     * 构建脚本生成代码配置类接口类
     */
    interface IBuildScriptGenerateConfigs : IBaseGenerateConfigs {

        /** 自定义构建脚本扩展方法名称 */
        val extensionName: String
    }

    /**
     * 通用生成代码配置类接口类
     */
    interface IBaseGenerateConfigs {

        /** 名称 */
        val name: String

        /** 是否为当前功能生成代码 */
        val isEnable: Boolean

        /** 属性配置文件名称数组 */
        val propertiesFileNames: MutableList<String>

        /** 固定存在的属性键值数组 */
        val permanentKeyValues: MutableMap<String, Any>

        /** 被排除的属性键值名称数组 */
        val excludeKeys: MutableList<Any>

        /** 被包含的属性键值名称数组 */
        val includeKeys: MutableList<Any>

        /** 属性键值规则数组 */
        val keyValuesRules: MutableMap<String, PropertyValueRule>

        /** 是否启用排除非字符串类型键值内容 */
        val isEnableExcludeNonStringValue: Boolean

        /** 是否启用类型自动转换功能 */
        val isEnableTypeAutoConversion: Boolean

        /** 是否启用键值内容插值功能 */
        val isEnableValueInterpolation: Boolean

        /** 生成的位置类型数组 */
        val generateLocationTypes: Array<GenerateLocationType>
    }
}