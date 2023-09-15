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
 * This file is created by fankes on 2023/8/25.
 */
@file:Suppress("unused")

package com.highcapable.sweetproperty.plugin

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.utils.debug.SError
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware

/**
 * [SweetProperty] 插件定义类
 */
class SweetPropertyPlugin<T : ExtensionAware> internal constructor() : Plugin<T> {

    /** 当前扩展实例 */
    private val extension = SweetPropertyExtension()

    override fun apply(target: T) = when (target) {
        is Settings -> {
            extension.onSettingsLoaded(target)
            target.gradle.settingsEvaluated { extension.onSettingsEvaluate(target) }
            target.gradle.projectsLoaded {
                extension.onProjectLoaded(rootProject)
                rootProject.afterEvaluate { extension.onProjectEvaluate(rootProject) }
            }
        }
        else -> SError.make("${SweetProperty.TAG} can only applied in settings.gradle/settings.gradle.kts, but current is $target")
    }
}