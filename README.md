# Sweet Property

[![GitHub license](https://img.shields.io/github/license/HighCapable/SweetProperty?color=blue)](https://github.com/HighCapable/SweetProperty/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/HighCapable/SweetProperty?display_name=release&logo=github&color=green)](https://github.com/HighCapable/SweetProperty/releases)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram)](https://t.me/HighCapable_Dev)

<img src="img-src/icon.png" width = "100" height = "100" alt="LOGO"/>

An easy get project properties anywhere Gradle plugin.

English | [简体中文](README-zh-CN.md)

| <img src="https://github.com/HighCapable/.github/blob/main/img-src/logo.jpg?raw=true" width = "30" height = "30" alt="LOGO"/> | [HighCapable](https://github.com/HighCapable) |
|-------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------|

This project belongs to the above-mentioned organization, **click the link above to follow this organization** and discover more good projects.

## What's this

This is a Gradle plugin to easily get the key-values in the Gradle project properties configuration file `gradle.properties`.

After using Kotlin DSL as a build script, it is not possible to directly use the Groovy weakly typed language to get the key-values functions
in `gradle.properties`.

At this time, we can only use a method like `properties["custom_key"]` to obtain it, which seems troublesome, and if the key name is negligent
and causes an error, it will cause problems.

This is the reason why this project was born.

Its function is to generate key-values entity class according to the specified properties configuration file, and unimpeded access to the properties
you set in the build script and project.

## Compatibility

The theory supports not very old Gradle, the recommended version is `7.x.x` and above.

Java projects and Android projects containing Kotlin plugins are supported, other types of projects are not supported yet.

> Build Script Language

- Kotlin DSL

It is recommended to use this language as the build script language first, which is also the language currently recommended by Gradle.

- Groovy DSL

Some functions may be incompatible, support will be gradually dropped in the future, and some functions may become unavailable.

## Get Started

- [Click here](docs/guide.md) to view the documentation

## Changelog

- [Click here](docs/changelog.md) to view the historical changelog

## Promotion

If you are looking for a Gradle plugin that can automatically manage Gradle project dependencies,
you can check out the [SweetDependency](https://github.com/HighCapable/SweetDependency) project.

This project also uses **SweetDependency**.

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
     <h2>Hey, please stay! 👋</h2>
     <h3>Here are related projects such as Android development tools, UI design, Gradle plugins, Xposed Modules and practical software. </h3>
     <h3>If the project below can help you, please give me a star! </h3>
     <h3>All projects are free, open source, and follow the corresponding open source license agreement. </h3>
     <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README.md">→ To see more about my projects, please click here ←</a></h1>
</div>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=HighCapable/SweetProperty&type=Date)

## License

- [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)

```
Apache License Version 2.0

Copyright (C) 2019-2024 HighCapable

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

Copyright © 2019-2024 HighCapable