# Sweet Property

[![GitHub license](https://img.shields.io/github/license/HighCapable/SweetProperty?color=blue&style=flat-square)](https://github.com/HighCapable/SweetProperty/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/HighCapable/SweetProperty?display_name=release&logo=github&color=green&style=flat-square)](https://github.com/HighCapable/SweetProperty/releases)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram&style=flat-square)](https://t.me/HighCapable_Dev)
[![QQ](https://img.shields.io/badge/discussion-QQ-blue.svg?logo=tencent-qq&logoColor=red&style=flat-square)](https://qm.qq.com/cgi-bin/qm/qr?k=Pnsc5RY6N2mBKFjOLPiYldbAbprAU3V7&jump_from=webapi&authKey=X5EsOVzLXt1dRunge8ryTxDRrh9/IiW1Pua75eDLh9RE3KXE+bwXIYF5cWri/9lf)

<img src="img-src/icon.png" width = "100" height = "100" alt="LOGO"/>

一个轻松在任意地方获取项目属性的 Gradle 插件。

[English](README.md) | 简体中文

| <img src="https://github.com/HighCapable/.github/blob/main/img-src/logo.jpg?raw=true" width = "30" height = "30" alt="LOGO"/> | [HighCapable](https://github.com/HighCapable) |
|-------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------|

这个项目属于上述组织，**点击上方链接关注这个组织**，发现更多好项目。

## 🚨 此项目已停止维护

此项目由于很多未能解决的黑盒问题已被弃用，我们不再推荐任何人使用它，现在请迁移到全新设计的 [Gropify](https://github.com/HighCapable/Gropify)。

## 这是什么

这是一个用来轻松获取 Gradle 项目属性配置文件 `gradle.properties` 中键值的 Gradle 插件。

在使用 Kotlin DSL 作为构建脚本后，无法直接使用 Groovy 弱类型语言获取 `gradle.properties` 中键值的功能。

这个时候我们只能使用类似 `properties["custom_key"]` 的方式来进行获取，看起来很麻烦，而且键值名称一旦疏忽造成错误，就会引发问题。

这就是这个项目诞生的原因，它的作用是根据指定的属性配置文件生成键值实体类，在构建脚本以及项目中畅通无阻地访问你设置的属性。

## 兼容性

理论支持不是很旧的 Gradle，建议版本为 `7.x.x` 及以上。

支持包含 Kotlin 插件的 Java 项目和 Android 项目，其它类型的项目暂不支持。

> 构建脚本语言

- Kotlin DSL

推荐优先使用此语言作为构建脚本语言，这也是目前 Gradle 推荐的语言。

- Groovy DSL

部分功能可能无法兼容，在后期会逐渐放弃支持，且部分功能会无法使用。

## 开始使用

- [点击这里](docs/guide-zh-CN.md) 查看使用文档

## 更新日志

- [点击这里](docs/changelog-zh-CN.md) 查看历史更新日志

## 项目推广

如果你正在寻找一个可以自动管理 Gradle 项目依赖的 Gradle 插件，你可以了解一下 [SweetDependency](https://github.com/HighCapable/SweetDependency) 项目。

本项目同样使用了 **SweetDependency**。

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <h2>嘿，还请君留步！👋</h2>
    <h3>这里有 Android 开发工具、UI 设计、Gradle 插件、Xposed 模块和实用软件等相关项目。</h3>
    <h3>如果下方的项目能为你提供帮助，不妨为我点个 star 吧！</h3>
    <h3>所有项目免费、开源，遵循对应开源许可协议。</h3>
    <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README-zh-CN.md">→ 查看更多关于我的项目，请点击这里 ←</a></h1>
</div>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=HighCapable/SweetProperty&type=Date)

## 许可证

- [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)

```
Apache License Version 2.0

Copyright (C) 2019 HighCapable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

版权所有 © 2019 HighCapable