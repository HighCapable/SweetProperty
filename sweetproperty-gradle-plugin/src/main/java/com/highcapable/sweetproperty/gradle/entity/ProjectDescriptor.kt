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
 * This file is created by fankes on 2023/8/28.
 */
package com.highcapable.sweetproperty.gradle.entity

import com.highcapable.sweetproperty.gradle.factory.fullName
import com.highcapable.sweetproperty.utils.debug.SError
import com.highcapable.sweetproperty.utils.noBlank
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import java.io.File
import kotlin.properties.Delegates

/**
 * 项目描述实现类
 */
internal class ProjectDescriptor private constructor() {

    internal companion object {

        /**
         * 创建 [ProjectDescriptor]
         * @param settings 当前设置
         * @param name 当前名称 (项目) - 默认空
         * @return [ProjectDescriptor]
         */
        internal fun create(settings: Settings, name: String = "") = ProjectDescriptor().also {
            val isRootProject = name.isBlank() || name == settings.rootProject.name
            it.type = Type.SETTINGS
            it.name = name.noBlank() ?: settings.rootProject.name
            it.currentDir = (if (isRootProject) settings.rootProject else settings.findProject(":$name"))?.projectDir
                ?: SError.make("Project \"$name\" not found")
            it.rootDir = settings.rootDir
            it.homeDir = settings.gradle.gradleUserHomeDir
        }

        /**
         * 创建 [ProjectDescriptor]
         * @param project 当前项目
         * @return [ProjectDescriptor]
         */
        internal fun create(project: Project) = ProjectDescriptor().also {
            it.type = Type.PROJECT
            it.name = project.fullName
            it.currentDir = project.projectDir
            it.rootDir = project.rootDir
            it.homeDir = project.gradle.gradleUserHomeDir
        }
    }

    /** 当前项目类型 */
    internal var type by Delegates.notNull<Type>()

    /** 当前项目名称 */
    internal var name = ""

    /** 当前项目目录 */
    internal var currentDir by Delegates.notNull<File>()

    /** 根项目目录 */
    internal var rootDir by Delegates.notNull<File>()

    /** 用户目录 */
    internal var homeDir by Delegates.notNull<File>()

    /**
     * 项目类型定义类
     */
    internal enum class Type {
        /** 设置 */
        SETTINGS,

        /** 项目 */
        PROJECT
    }

    override fun toString() = "ProjectDescriptor(type: $type, name: $name)"
}