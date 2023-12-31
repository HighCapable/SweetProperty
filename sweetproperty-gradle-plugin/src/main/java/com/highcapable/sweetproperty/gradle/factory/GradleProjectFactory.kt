/*
 * SweetProperty -  An easy get project properties anywhere Gradle plugin.
 * Copyright (C) 2019-2024 HighCapable
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
package com.highcapable.sweetproperty.gradle.factory

import com.highcapable.sweetproperty.utils.code.entity.MavenPomData
import com.highcapable.sweetproperty.utils.toFile
import org.gradle.api.Project
import org.gradle.kotlin.dsl.buildscript
import org.gradle.kotlin.dsl.repositories

/**
 * 获取指定项目的完整名称
 * @param isUseColon 是否在子项目前使用冒号 - 默认是
 * @return [String]
 */
internal fun Project.fullName(isUseColon: Boolean = true): String {
    val isRoot = this == rootProject
    val baseNames = mutableListOf<String>()

    /**
     * 递归子项目
     * @param project 当前项目
     */
    fun fetchChild(project: Project) {
        project.parent?.also { if (it != it.rootProject) fetchChild(it) }
        baseNames.add(project.name)
    }; fetchChild(project = this)
    return buildString { baseNames.onEach { append(":$it") }.clear() }.let { if (isUseColon && !isRoot) it else it.drop(1) }
}

/**
 * 向构建脚本添加自定义依赖
 * @param repositoryPath 存储库路径
 * @param pomData Maven POM 实体
 */
internal fun Project.addDependencyToBuildScript(repositoryPath: String, pomData: MavenPomData) =
    buildscript {
        repositories {
            maven {
                url = repositoryPath.toFile().toURI()
                mavenContent { includeGroup(pomData.groupId) }
            }
        }; dependencies { classpath("${pomData.groupId}:${pomData.artifactId}:${pomData.version}") }
    }

/**
 * 装载构建脚本的 [Class]
 * @param name [Class] 完整名称
 * @return [Class]
 */
internal fun Project.loadBuildScriptClass(name: String) = runCatching { buildscript.classLoader.loadClass(name) }.getOrNull()