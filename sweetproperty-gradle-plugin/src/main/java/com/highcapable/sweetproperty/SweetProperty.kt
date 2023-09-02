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
 * This file is Created by fankes on 2023/8/25.
 */
package com.highcapable.sweetproperty

import com.highcapable.sweetproperty.generated.SweetPropertyProperties

/**
 * [SweetProperty] 的装载调用类
 */
object SweetProperty {

    /** 标签名称 */
    const val TAG = SweetPropertyProperties.PROJECT_NAME

    /** 版本 */
    const val VERSION = SweetPropertyProperties.PROJECT_VERSION

    /** 项目地址 */
    const val PROJECT_URL = SweetPropertyProperties.PROJECT_URL
}