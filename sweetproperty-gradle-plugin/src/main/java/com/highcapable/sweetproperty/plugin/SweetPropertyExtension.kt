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
package com.highcapable.sweetproperty.plugin

import com.highcapable.sweetproperty.SweetProperty
import com.highcapable.sweetproperty.gradle.factory.getOrCreate
import com.highcapable.sweetproperty.gradle.proxy.IGradleLifecycle
import com.highcapable.sweetproperty.plugin.extension.dsl.configure.SweetPropertyConfigureExtension
import com.highcapable.sweetproperty.plugin.helper.PluginUpdateHelper
import com.highcapable.sweetproperty.plugin.helper.PropertiesDeployHelper
import com.highcapable.sweetproperty.utils.debug.SError
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

/**
 * [SweetProperty] 插件扩展类
 */
internal class SweetPropertyExtension internal constructor() : IGradleLifecycle {

    /** 当前配置方法体实例 */
    private var configure: SweetPropertyConfigureExtension? = null

    override fun onSettingsLoaded(settings: Settings) {
        configure = settings.getOrCreate<SweetPropertyConfigureExtension>(SweetPropertyConfigureExtension.NAME)
    }

    override fun onSettingsEvaluate(settings: Settings) {
        val configs = configure?.build(settings) ?: SError.make("Extension \"${SweetPropertyConfigureExtension.NAME}\" create failed")
        PluginUpdateHelper.checkingForUpdate(settings)
        PropertiesDeployHelper.initialize(settings, configs)
    }

    override fun onProjectLoaded(rootProject: Project) {
        PropertiesDeployHelper.resolve(rootProject)
    }

    override fun onProjectEvaluate(rootProject: Project) {
        PropertiesDeployHelper.deploy(rootProject)
    }
}