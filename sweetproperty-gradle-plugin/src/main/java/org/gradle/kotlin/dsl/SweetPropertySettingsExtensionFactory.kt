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
@file:Suppress("unused")

package org.gradle.kotlin.dsl

import com.highcapable.sweetproperty.gradle.factory.configure
import com.highcapable.sweetproperty.gradle.factory.get
import com.highcapable.sweetproperty.plugin.extension.dsl.configure.SweetPropertyConfigureExtension
import org.gradle.api.Action
import org.gradle.api.initialization.Settings

/**
 * WORKAROUND: for some reason a type-safe accessor is not generated for the extension,
 * even though it is present in the extension container where the plugin is applied.
 * This seems to work fine, and the extension methods are only available when the plugin
 * is actually applied.
 *
 * See related link [here](https://stackoverflow.com/questions/72627792/gradle-settings-plugin-extension)
 */

/**
 * Retrieves the [SweetPropertyConfigureExtension] extension.
 * @return [SweetPropertyConfigureExtension]
 */
val Settings.sweetProperty get() = get<SweetPropertyConfigureExtension>(SweetPropertyConfigureExtension.NAME)

/**
 * Configures the [SweetPropertyConfigureExtension] extension.
 * @param configure
 */
fun Settings.sweetProperty(configure: Action<SweetPropertyConfigureExtension>) = configure(SweetPropertyConfigureExtension.NAME, configure)