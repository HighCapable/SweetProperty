# Sweet Property 使用文档

在开始使用之前，建议你仔细阅读此文档，以便你能更好地了解它的作用方式与功能。

如果你的项目依然在使用 Groovy DSL 进行管理，推荐迁移到 Kotlin DSL。

在 Groovy DSL 中使用此插件发生的任何问题，我们都将不再负责排查和修复，并且在后期版本可能会完全不再支持 Groovy DSL。

注意：此文档中将不再详细介绍在 Groovy DSL 中的使用方法。

## 前提条件

请注意 `SweetProperty` 推荐使用 `pluginManagement` 新方式进行装载，它是自 Gradle `7.x.x` 版本开始添加的功能。

如果你的项目依然在使用 `buildscript` 的方式进行管理，推荐迁移到新方式，这里将不再提供旧版本的使用方式说明。

## 快速开始

首先，打开你根项目的 `settings.gradle.kts`。

在你根项目的 `settings.gradle.kts` 中加入如下代码。

如果已经存在 `pluginManagement` 则不需要重复添加。

> 示例如下

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.highcapable.sweetproperty") version "<version>"
}
```

请将上述代码中的 `<version>` 替换为
[Release](https://github.com/HighCapable/SweetProperty/releases) 中的最新版本， 请注意<u>**不要**</u>在后方加入 `apply false`。

上述配置完成后，运行一次 Gradle Sync。

此时 `SweetProperty` 将会自动搜索根项目和每个子项目中的 `gradle.properties` 文件，并读取其中的属性键值，为每个项目生成对应的代码。

## 功能配置

你可以对 `SweetProperty` 进行配置来实现自定义和个性化功能。

`SweetProperty` 为你提供了相对丰富的可自定义功能，下面是这些功能的说明与配置方法。

请在你的 `settings.gradle.kts` 中添加 `sweetProperty` 方法块以开始配置 `SweetProperty`。

> 示例如下

```kotlin
sweetProperty {
    // 启用 SweetProperty，设置为 false 将禁用所有功能
    isEnable = true
    // 全局配置
    // 你可以在全局配置中修改所有项目中的配置
    // 每个项目中未进行声明的配置将使用全局配置
    // 每个配置方法块中的功能完全一致
    // 你可以参考下方根项目、子项目的配置方法
    global {
        // 通用代码生成功能
        // 在这里你可以同时配置构建脚本和项目生成代码的相关功能
        all {
            // 启用功能
            // 你可以分别对 "sourcesCode"、"buildScript" 进行设置
            isEnable = true
            // 是否启用排除非字符串类型键值内容
            // 默认启用，启用后将从属性键值中排除不是字符串类型的键值及内容
            // 这可以排除例如一些系统环境变量的配置或内存中的数据
            isEnableExcludeNonStringValue = true
            // 是否启用类型自动转换功能
            // 默认启用，启用后将自动识别属性键值中的类型并转换为对应的类型
            // 在启用后如果你想要强制设置一个键值内容为字符串类型，你可以使用单引号或双引号包裹整个字符串
            // 注意：在关闭此功能后如上所述的功能也将同时失效
            // 例如 name=hello 和 number=1 它们将会被自动转换为 String 和 Int
            // 例如 stringNumber="1" 或 stringNumber='1' 它们将会被强制转换为 String
            isEnableTypeAutoConversion = true
            // 是否启用键值内容插值功能
            // 默认启用，启用后将自动识别属性键值内容中的 ${...} 内容并进行替换
            // 注意：插值的内容仅会从当前 (当前配置文件) 属性键值列表进行查找
            isEnableValueInterpolation = true
            // 设置属性配置文件名称数组
            // 属性配置文件将根据你设置的文件名称自动从当前根项目、子项目以及用户目录的根目录进行获取
            // 你可以添加多组属性配置文件名称，将按照顺序依次进行读取
            // 一般情况下不需要修改此设置，错误的文件名将导致获取到空键值内容
            // 你可以配置 "isAddDefault" 参数来决定是否添加默认的 "gradle.properties" 文件名称
            // 如果你有一个或多个自定义名称的属性键值文件，你可以修改这里的设置
            // 注意：建议为每个项目单独配置，而不是在全局中修改，以防发生问题
            propertiesFileNames(
                "some_other_1.properties",
                "some_other_2.properties",
                isAddDefault = true
            )
            // 设置固定存在的属性键值数组
            // 在这里可以设置一些一定存在的键值，这些键值无论能否从属性键值中得到都会进行生成
            // 这些键值在属性键值存在时使用属性键值的内容，不存在时使用这里设置的内容
            // 注意：属性键值名称不能存在特殊符号以及空格，否则可能会生成失败
            // 例如：你需要获取存于自己本机的密钥或证书，但是其他人的设备上没有这些键值
            // 此时这个功能就会变得非常有用，你可以设置在没有这些键值时的默认值
            // 当然，你也可以使用这个功能强行向生成的代码中添加额外的属性键值
            permanentKeyValues(
                "permanent.some.key1" to "some_value_1",
                "permanent.some.key2" to "some_value_2"
            )
            // 设置需要排除的属性键值名称数组
            // 在这里可以设置一些你希望从已知的属性键值中排除的键值名称
            // 这些键值在属性键值存在它们时被排除，不会出现在生成的代码中
            // 注意：如果你排除了 "permanentKeyValues" 中设置的键值，
            // 那么它们只会变为你设置的初始键值内容并继续保持存在
            // 你可以传入 Regex 或使用 String.toRegex 以使用正则功能
            excludeKeys(
                "exclude.some.key1",
                "exclude.some.key2"
            )
            // 设置需要包含的属性键值名称数组
            // 在这里可以设置一些你希望从已知的属性键值中包含的键值名称
            // 这些键值在属性键值存在它们时被包含，未被包含的键值不会出现在生成的代码中
            // 你可以传入 Regex 或使用 String.toRegex 以使用正则功能
            includeKeys(
                "include.some.key1",
                "include.some.key2"
            )
            // 设置属性键值规则数组
            // 你可以设置一组键值规则，使用 "createValueRule" 创建新的规则，用于解析得到的键值内容
            // 这些键值规则在属性键值存在它们时被应用
            keyValuesRules(
                "some.key1" to createValueRule { if (it.contains("_")) it.replace("_", "-") else it },
                "some.key2" to createValueRule { "$it-value" }
            )
            // 设置从何处生成属性键值
            // 默认为 "CURRENT_PROJECT" 和 "ROOT_PROJECT"
            // 你可以使用以下类型来进行设置
            // "CURRENT_PROJECT" ← 当前项目
            // "ROOT_PROJECT" ← 根项目
            // "GLOBAL" ← 全局 (用户目录)
            // "SYSTEM" ← 系统
            // "SYSTEM_ENV" ← 系统环境变量
            // SweetProperty 将从你设置的生成位置依次生成属性键值，生成位置的顺序跟随你设置的顺序决定
            // 风险提示："GLOBAL"、"SYSTEM"、"SYSTEM_ENV" 可能存在密钥和证书，请小心管理生成的代码
            generateFrom(CURRENT_PROJECT, ROOT_PROJECT)
        }
        // 项目生成代码功能
        // 此功能类似于 Android 项目自动生成的 BuildConfig
        // 在项目中生成的代码可直接被当前项目使用
        // 这里的配置包括 "all" 中的配置，你可以对其进行复写
        sourcesCode {
            // 自定义生成的目录路径
            // 你可以填写相对于当前项目的路径
            // 默认为 "build/generated/sweet-property"
            // 建议将生成的代码放置于 "build" 目录下，因为生成的代码不建议去修改它
            generateDirPath = "build/generated/sweet-property"
            // 自定义生成的包名
            // Android 项目默认使用 "android" 配置方法块中的 "namespace"
            // 普通的 Kotlin on Jvm 项目默认使用项目设置的 "project.group"
            // 你可以不进行设置，包名在一般情况下会自动进行匹配
            packageName = "com.example.mydemo"
            // 自定义生成的类名
            // 默认使用当前项目的名称 + "Properties"
            // 你可以不进行设置，类名在一般情况下会自动进行匹配
            className = "MyDemo"
            // 是否启用受限访问功能
            // 默认不启用，启用后将为生成的类和方法添加 "internal" 修饰符
            // 如果你的项目为工具库或依赖，通常情况下建议启用
            // 启用后可以防止其他开发者在引用你的库时调用到你的项目属性键值发生问题
            isEnableRestrictedAccess = false
        }
        // 构建脚本生成代码功能
        // 在构建脚本中生成的代码可直接被当前 "build.gradle.kts" 使用
        // 这里的配置包括 "all" 中的配置，你可以对其进行复写
        // 注意：Gradle 中也有一个 "buildscript" 方法块，只不过是小写的 "s"，请不要混淆
        buildScript {
            // 自定义构建脚本扩展方法名称
            // 默认为 "property"
            // 你将在 "build.gradle.kts" 中使用类似 "property.some.key" 的方式进行调用
            extensionName = "property"
        }
    }
    // 根项目 (Root Project) 配置
    // 这是一个特殊的配置方法块，只能用于根项目
    rootProject {
        all {
            // 配置 "all"
        }
        sourcesCode {
            // 配置 "sourcesCode"
        }
        buildScript {
            // 配置 "buildScript"
        }
    }
    // 其它项目与子项目配置
    // 在方法参数中填入需要配置的项目完整名称来配置对应的项目
    // 如果当前项目是子项目，你必须填写子项目前面的 ":"，例如 ":app"
    // 如果当前项目为嵌套型子项目，例如 app → sub
    // 此时你需要使用 ":" 来分隔多个子项目，例如 ":app:sub"
    // 注意：在 1.0.2 版本及以前是不需要添加 ":" 来标识子项目的，且添加后会报错
    //      这是一个错误做法，目前统一了 Gradle 的项目命名规范，请使用新的规范
    // 根项目的名称不能直接用来配置子项目，请使用 "rootProject"
    project(":app") {
        all {
            // 配置 "all"
        }
        sourcesCode {
            // 配置 "sourcesCode"
        }
        buildScript {
            // 配置 "buildScript"
        }
    }
}
```

如需在 Groovy DSL 中使用，请将所有变量的 `=` 改为空格，并删除 `Enable` 前方的 `is` 并将 `E` 小写即可。

如果你遇到了 `Gradle DSL method not found` 错误，解决方案为迁移到 Kotlin DSL。

如果你不想全部使用 Kotlin DSL，你也可以仅将 `settings.gradle` 迁移到 `settings.gradle.kts`。

## 使用示例

下面是一个项目的 `gradle.properties` 配置文件。

```properties
project.groupName=com.highcapable.sweetpropertydemo
project.description=Hello SweetProperty Demo!
project.version=1.0.0
```

在构建脚本 `build.gradle.kts` 中，我们就可以如下所示这样直接去使用这些键值。

这里以 Maven 发布的配置部分举例。

```kotlin
publications {
    create<MavenPublication>("maven") {
        groupId = property.project.groupName
        version = property.project.version
        pom.description.set(property.project.description)
        from(components["java"])
    }
}
```

同样地，你也可以在当前项目中调用生成的键值。

```kotlin
SweetPropertyDemoProperties.PROJECT_GROUP_NAME
SweetPropertyDemoProperties.PROJECT_DESCRIPTION
SweetPropertyDemoProperties.PROJECT_VERSION
```

下面再以 Android 项目举例。

在 Android 项目中通常需要配置很多重复、固定的属性，例如 `targetSdk`。

```properties
project.compileSdk=33
project.targetSdk=33
project.minSdk=22
```

当你设置了 `isEnableTypeAutoConversion = true` 时，`SweetProperty` 在生成实体类过程在默认配置下将尝试将其转换为对应的类型。

例如下方所使用的键值，其类型可被识别为整型，可被项目配置直接使用。

```kotlin
android {
    compileSdk = property.project.compileSdk
    defaultConfig {
        minSdk = property.project.minSdk
        targetSdk = property.project.targetSdk
    }
}
```

你可以无需再使用 `buildConfigField` 向 `BuildConfig` 添加代码，有了 `SweetProperty` 生成的属性键值代码，你可以更加灵活地管理你的项目。

你还可以在属性键值中使用 `${...}` 互相引用其中的内容，但不允许递归引用。

当你设置了 `isEnableValueInterpolation = true` 时，`SweetProperty` 将自动合并这些引用的内容到对应位置。

```properties
project.name=MyDemo
project.developer.name=myname
project.url=https://github.com/${project.developer.name}/${project.name}
```

注意：这个特性是 `SweetProperty` 提供的，原生的 `gradle.properties` 并不支持此功能。

**可能遇到的问题**

如果你的项目仅存在一个根项目，且没有导入任何子项目，此时如果扩展方法不能正常生成，
你可以将你的根项目迁移至子项目并在 `settings.gradle.kts` 中导入这个子项目，这样即可解决此问题。

我们一般推荐将项目的功能进行分类，根项目仅用来管理插件和一些配置。

**局限性说明**

`SweetProperty` 无法生成 `settings.gradle.kts` 中的扩展方法，因为这属于 `SweetProperty` 的上游。

## 问题反馈

如果你在使用 `SweetProperty` 的过程中遇到了任何问题，你都可以随时在 GitHub 开启一个 `issues` 向我们反馈。