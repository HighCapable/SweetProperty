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
 * This file is created by fankes on 2023/8/26.
 */
package com.highcapable.sweetproperty.utils

/**
 * 转换当前 [Map] 键值到字符串类型
 * @return [Map]<[String], [String]>
 */
internal inline fun <reified K, V> Map<K, V>.toStringMap() = mapKeys { e -> e.key.toString() }.mapValues { e -> e.value.toString() }

/**
 * 当数组不为空时返回非空
 * @return [T] or null
 */
internal inline fun <reified T : Collection<*>> T.noEmpty() = takeIf { it.isNotEmpty() }

/**
 * 当字符串不为空白时返回非空
 * @return [T] or null
 */
internal inline fun <reified T : CharSequence> T.noBlank() = takeIf { it.isNotBlank() }

/**
 * 扁平化字符串处理
 *
 * 移除所有空格并转换为小写字母
 * @return [String]
 */
internal fun String.flatted() = replace(" ", "").lowercase()

/**
 * 驼峰、"-"、"." 转大写下划线命名
 * @return [String]
 */
internal fun String.underscore() = replace(".", "_").replace("-", "_").replace(" ", "_").replace("([a-z])([A-Z]+)".toRegex(), "$1_$2").uppercase()

/**
 * 下划线、分隔线、点、空格命名字符串转小驼峰命名字符串
 * @return [String]
 */
internal fun String.camelcase() = runCatching {
    split("_", ".", "-", " ").map { it.replaceFirstChar { e -> e.titlecase() } }.let { words ->
        words.first().replaceFirstChar { it.lowercase() } + words.drop(1).joinToString("")
    }
}.getOrNull() ?: this

/**
 * 下划线、分隔线、点、空格命名字符串转大驼峰命名字符串
 * @return [String]
 */
internal fun String.uppercamelcase() = camelcase().capitalize()

/**
 * 字符串首字母大写
 * @return [String]
 */
internal fun String.capitalize() = replaceFirstChar { it.uppercaseChar() }

/**
 * 字符串首字母小写
 * @return [String]
 */
internal fun String.uncapitalize() = replaceFirstChar { it.lowercaseChar() }

/**
 * 转换字符串第一位数字到外观近似大写字母
 * @return [String]
 */
internal fun String.firstNumberToLetter() =
    if (isNotBlank()) (mapOf(
        '0' to 'O', '1' to 'I',
        '2' to 'Z', '3' to 'E',
        '4' to 'A', '5' to 'S',
        '6' to 'G', '7' to 'T',
        '8' to 'B', '9' to 'P'
    )[first()] ?: first()) + substring(1)
    else this

/**
 * 转换字符串为非 Java 关键方法引用名称
 * @return [String]
 */
internal fun String.toNonJavaName() = if (lowercase() == "class") replace("lass", "lazz") else this

/**
 * 字符串中是否存在插值符号 ${...}
 * @return [Boolean]
 */
internal fun String.hasInterpolation() = contains("\${") && contains("}")

/**
 * 替换字符串中的插值符号 ${...}
 * @param result 回调结果
 * @return [String]
 */
internal fun String.replaceInterpolation(result: (groupValue: String) -> CharSequence) =
    "\\$\\{(.+?)}".toRegex().replace(this) { result(it.groupValues[1]) }