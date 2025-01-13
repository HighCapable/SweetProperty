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
 * This file is created by fankes on 2023/8/27.
 */
package com.highcapable.sweetproperty.plugin.generator.factory

import com.highcapable.sweetproperty.utils.underscore
import kotlin.reflect.KClass

/** 属性键值数组类型定义 */
internal typealias PropertyMap = MutableMap<String, Any>

/** 属性键值优化数组类型定义 */
internal typealias PropertyOptimizeMap = MutableMap<String, Pair<String, Any>>

/** 属性键值规则类型定义 */
internal typealias PropertyValueRule = (value: String) -> String

/**
 * 移除键值内容自动转换类型的引号
 * @return [String]
 */
internal fun String.removeAutoConversion() = removeSurrounding("\"").removeSurrounding("'")

/**
 * 解析到键值内容类型
 * @param isAutoConversion 是否自动转换类型
 * @return [Pair]<[KClass], [String]>
 */
internal fun Any.parseTypedValue(isAutoConversion: Boolean): Pair<KClass<*>, String> {
    var isStringType = false
    val valueString = toString()
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\\", "\\\\")
        .let {
            if (isAutoConversion && (it.startsWith("\"") && it.endsWith("\"") || it.startsWith("'") && it.endsWith("'"))) {
                isStringType = true
                it.drop(1).dropLast(1)
            } else it.replace("\"", "\\\"")
        }
    if (!isAutoConversion) return Pair(String::class, "\"$valueString\"")
    val typeSpec = when {
        isStringType -> String::class
        valueString.trim().toIntOrNull() != null -> Int::class
        valueString.trim().toLongOrNull() != null -> Long::class
        valueString.trim().toDoubleOrNull() != null -> Double::class
        valueString.trim().toFloatOrNull() != null -> Float::class
        valueString.trim() == "true" || valueString.trim() == "false" -> Boolean::class
        else -> String::class
    }; return Pair(typeSpec, if (typeSpec == String::class) "\"$valueString\"" else valueString.let {
        if (typeSpec == Long::class && !it.endsWith("L")) "${it}L" else it
    })
}

/**
 * [PropertyMap] 转换到 [PropertyOptimizeMap]
 *
 * 替换可能的键值名称特殊字符内容并保留原始键值名称
 * @return [PropertyOptimizeMap]
 */
internal fun PropertyMap.toOptimize(): PropertyOptimizeMap {
    val newMap: PropertyOptimizeMap = mutableMapOf()
    var uniqueNumber = 1
    forEach { (key, value) ->
        var newKey = key.replace("\\W".toRegex(), "_")
        while (newMap.containsKey(newKey)) newKey = "$newKey${++uniqueNumber}"
        newMap[newKey] = key to value
    }; return newMap
}

/**
 * [PropertyOptimizeMap] 转换为大写下划线命名
 * @return [PropertyOptimizeMap]
 */
internal fun PropertyOptimizeMap.toUnderscores(): PropertyOptimizeMap {
    val newMap: PropertyOptimizeMap = mutableMapOf()
    var uniqueNumber = 1
    forEach { (key, value) ->
        var newKey = key.underscore()
        while (newMap.containsKey(newKey)) newKey = "$newKey${++uniqueNumber}"
        newMap[newKey] = value.first to value.second
    }; return newMap
}