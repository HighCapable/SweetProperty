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
 * This file is created by fankes on 2023/8/27.
 */
@file:Suppress("unused")

package com.highcapable.sweetproperty.utils

import java.io.File

/**
 * 字符串路径转换为文件
 *
 * 自动调用 [parseFileSeparator]
 * @return [File]
 */
internal fun String.toFile() = File(parseFileSeparator())

/**
 * 格式化到当前操作系统的文件分隔符
 * @return [String]
 */
internal fun String.parseFileSeparator() = replace("/", File.separator).replace("\\", File.separator)

/**
 * 格式化到 Unix 操作系统的文件分隔符
 * @return [String]
 */
internal fun String.parseUnixFileSeparator() = replace("\\", "/")

/**
 * 检查目录是否为空
 *
 * - 如果不是目录 (可能是文件) - 返回 true
 * - 如果文件不存在 - 返回 true
 * @return [Boolean]
 */
internal fun File.isEmpty() = exists().not() || isDirectory.not() || listFiles().isNullOrEmpty()

/** 删除目录下的空子目录 */
internal fun File.deleteEmptyRecursively() {
    listFiles { file -> file.isDirectory }?.forEach { subDir ->
        subDir.deleteEmptyRecursively()
        if (subDir.listFiles()?.isEmpty() == true) subDir.delete()
    }
}