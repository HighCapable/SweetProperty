# Sweet Property

![GitHub license](https://img.shields.io/github/license/HighCapable/SweetProperty?color=blue&cacheSeconds=https%3A%2F%2Fgithub.com%2FHighCapable%2FSweetProperty%2Fblob%2Fmaster%2FLICENSE)
![GitHub release](https://img.shields.io/github/v/release/HighCapable/SweetProperty?display_name=release&logo=github&color=green&link=https%3A%2F%2Fgithub.com%2FHighCapable%2FSweetProperty%2Freleases%2Flatest)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram)](https://t.me/HighCapable_Dev)

<img src="https://github.com/HighCapable/SweetProperty/blob/master/img-src/icon.png?raw=true" width = "100" height = "100" alt="LOGO"/>

An easy get project properties anywhere Gradle plugin.

English | [简体中文](https://github.com/HighCapable/SweetProperty/blob/master/README-zh-CN.md)

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

- [Click here](https://github.com/HighCapable/SweetProperty/blob/master/docs/guide.md) to view the documentation

## Changelog

- [Click here](https://github.com/HighCapable/SweetProperty/blob/master/docs/changelog.md) to view the historical changelog

## Promotion

If you are looking for a Gradle plugin that can automatically manage Gradle project dependencies,
you can check out the [SweetDependency](https://github.com/HighCapable/SweetDependency) project.

This project also uses **SweetDependency**.

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=HighCapable/SweetProperty&type=Date)

## License

- [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)

```
Apache License Version 2.0

Copyright (C) 2019-2023 HighCapable

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

Copyright © 2019-2023 HighCapable