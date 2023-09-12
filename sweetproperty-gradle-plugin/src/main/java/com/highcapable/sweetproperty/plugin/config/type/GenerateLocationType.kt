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
 * This file is created by fankes on 2023/8/25.
 */
@file:Suppress("unused")

package com.highcapable.sweetproperty.plugin.config.type

/**
 * 生成位置类型定义类
 */
internal enum class GenerateLocationType {
    /** 当前项目 */
    CURRENT_PROJECT,

    /** 根项目 */
    ROOT_PROJECT,

    /** 全局 */
    GLOBAL,

    /** 系统 */
    SYSTEM,

    /** 系统环境变量 */
    SYSTEM_ENV
}