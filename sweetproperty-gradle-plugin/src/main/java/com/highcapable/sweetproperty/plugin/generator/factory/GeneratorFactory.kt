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
 * This file is Created by fankes on 2023/8/27.
 */
package com.highcapable.sweetproperty.plugin.generator.factory

import kotlin.reflect.KClass

/** 属性键值数组类型定义 */
internal typealias PropertyMap = MutableMap<String, Any>

/**
 * 解析到键值内容类型
 * @param isAutoConversion 是否自动转换类型
 * @return [Pair]<[KClass], [String]>
 */
internal fun Any.parseTypedValue(isAutoConversion: Boolean): Pair<KClass<*>, String> {
    val valueString = toString()
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
    if (isAutoConversion.not()) return Pair(String::class, "\"$valueString\"")
    val typeSpec = when {
        valueString.trim().toIntOrNull() != null -> Int::class
        valueString.trim().toLongOrNull() != null -> Long::class
        valueString.trim().toDoubleOrNull() != null -> Double::class
        valueString.trim().toFloatOrNull() != null -> Float::class
        valueString.trim() == "true" || valueString.trim() == "false" -> Boolean::class
        else -> String::class
    }; return Pair(typeSpec, if (typeSpec == String::class) "\"$valueString\"" else valueString.let {
        if (typeSpec == Long::class && it.endsWith("L").not()) "${it}L" else it
    })
}