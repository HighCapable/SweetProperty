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
package com.highcapable.sweetproperty.utils.debug

import com.highcapable.sweetproperty.SweetProperty

/**
 * 全局异常管理类
 */
internal object SError {

    /**
     * 抛出异常
     * @param msg 消息内容
     * @throws IllegalStateException
     */
    internal fun make(msg: String): Nothing = error("[${SweetProperty.TAG}] $msg")
}