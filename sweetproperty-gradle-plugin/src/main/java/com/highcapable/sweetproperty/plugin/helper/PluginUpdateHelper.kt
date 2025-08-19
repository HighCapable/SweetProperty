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
 * This file is created by fankes on 2023/9/26.
 */
package com.highcapable.sweetproperty.plugin.helper

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.generated.SweetPropertyProperties
import com.highcapable.sweetproperty.utils.debug.SLog
import com.highcapable.sweetproperty.utils.executeUrlBody
import org.gradle.api.initialization.Settings
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 插件自身检查更新工具类
 */
internal object PluginUpdateHelper {

    /** Maven Central Release URL 地址 */
    private const val SONATYPE_OSS_RELEASES_URL = "https://repo1.maven.org/maven2"

    /** 依赖配置文件名 */
    private const val METADATA_FILE_NAME = "maven-metadata.xml"

    /** 插件自身依赖 URL 名称 */
    private val groupUrlNotation =
        "${SweetPropertyProperties.PROJECT_GROUP_NAME.replace(".","/")}/${SweetPropertyProperties.GRADLE_PLUGIN_MODULE_NAME}"

    /** 检查更新 URL 地址 */
    private val releaseUrl = "$SONATYPE_OSS_RELEASES_URL/$groupUrlNotation/$METADATA_FILE_NAME"

    /**
     * 检查更新
     * @param settings 当前设置
     */
    internal fun checkingForUpdate(settings: Settings) {
        if (settings.gradle.startParameter.isOffline) return
        val latestVersion = releaseUrl.executeUrlBody(isShowFailure = false).trim().findLatest()
        if (latestVersion.isNotBlank() && latestVersion != SweetProperty.VERSION) SLog.note(
            """
              Plugin update is available, the current version is ${SweetProperty.VERSION}, please update to $latestVersion
              You can modify your plugin version in your project's settings.gradle or settings.gradle.kts
              plugins {
                  id("${SweetPropertyProperties.PROJECT_GROUP_NAME}") version "$latestVersion"
                  ...
              }
              For more information, you can visit ${SweetProperty.PROJECT_URL}
            """.trimIndent(), SLog.UP
        )
    }

    /**
     * 解析 [METADATA_FILE_NAME] 内容并获取 "latest"
     * @return [String]
     */
    private fun String.findLatest() = runCatching {
        if (!(contains("<metadata ") || contains("<metadata>")) || !endsWith("</metadata>")) return@runCatching ""
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(this))).let { document ->
            document.getElementsByTagName("latest")?.let { if (it.length > 0) it.item(0)?.textContent ?: "" else "" }
        }
    }.getOrNull() ?: ""
}