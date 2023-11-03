# Sweet Property Documentation

Before you start using it, it is recommended that you read this document carefully so that you can better understand how it works and its functions.

If your project is still managed using Groovy DSL, it is recommended to migrate to Kotlin DSL.

We will no longer be responsible for troubleshooting and fixing any issues that occur with this plugin in Groovy DSL,
and Groovy DSL support may be dropped entirely in later releases.

Note: Usage in the Groovy DSL will not be detailed in this document.

## Prerequisites

Please note that `SweetProperty` is recommended to be loaded using the new `pluginManagement` method, which is a feature added since Gradle `7.x.x`
version.

If your project is still managed using the `buildscript` method, it is recommended to migrate to the new method, and the instructions for using the
old version will no longer be provided here.

## Quick Start

First, open `settings.gradle.kts` in your root project.

Add the following code to `settings.gradle.kts` in your root project.

If `pluginManagement` already exists, there is no need to add it again.

> The following example

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

Please replace `<version>` in the above code with the latest version in
[Release](https://github.com/HighCapable/SweetProperty/releases), please note <u>**DO NOT**</u> add `apply false` after.

After the above configuration is completed, run Gradle Sync once.

## Function Configuration

You can configure `SweetProperty` to achieve custom and personalized functions.

`SweetProperty` provides you with a relatively rich set of customizable functions.

The following are the descriptions and configuration methods of these functions.

Please add `sweetProperty` method block to your `settings.gradle.kts` to start configuring `SweetProperty`.

> The following example

```kotlin
sweetProperty {
    // Enable SweetProperty, set to false will disable all functions
    isEnable = true
    // Global configuration
    // You can modify the configuration in all projects in the global configuration
    // Configurations that are not declared in each project will use the global configuration
    // The functions in each configuration method block are exactly the same
    // You can refer to the configuration method of the root project and sub-projects below
    global {
        // General code generation function
        // Here you can configure the related functions of the build script and the project generated code at the same time
        all {
            // Enable functionality
            // You can set "sourcesCode" and "buildScript" respectively
            isEnable = true
            // Whether to enable the exclusion of non-string type key-values content
            // Enabled by default, when enabled, key-values and content that are not string types will be excluded from the properties key-values
            // This can exclude e.g. configuration of some system environment variables or data in memory
            isEnableExcludeNonStringValue = true
            // Whether to enable the type automatic conversion function
            // Enabled by default, when enabled,
            // the type in the properties key-values will be automatically recognized and converted to the corresponding type
            // After enabling, if you want to force the content of a value to be a string type,
            // you can use single quotes or double quotes to wrap the entire string
            // Note: After turning off this function, the functions mentioned above will also become invalid at the same time
            // For example name=hello and number=1 they will be automatically converted to String and Int
            // For example stringNumber="1" or stringNumber='1' they will be coerced to String
            isEnableTypeAutoConversion = true
            // Whether to enable key-values content interpolation
            // Enabled by default, after enabling, the ${...} content in the properties key-values content
            // will be automatically recognized and replaced
            // Note: The interpolated content will only be searching from the current (current configuration file) properties key-values list
            isEnableValueInterpolation = true
            // Set properties names array
            // The properties file will be automatically obtained from the root directory of
            // the current root project, subproject and user directory according to the file name you set
            // You can add multiple sets of properties file names, which will be read in order
            // In general, you don't need to modify this setting, the wrong file name will lead to getting empty key-values content
            // You can configure the "isAddDefault" parameter to decide whether to add the default "gradle.properties" file name
            // If you have one or more properties files with custom names, you can modify the settings here
            // Note: It is recommended to configure each project individually, rather than modifying globally, in case of problems
            propertiesFileNames(
                "some_other_1.properties",
                "some_other_2.properties",
                isAddDefault = true
            )
            // Set fixed properties key-values array
            // Here you can set some key values that must exist,
            // and these key values will be generated regardless of whether they can be obtained from the properties key-values
            // These key-values use the content of the properties key-values when the properties key-values exists,
            // and use the content set here when it does not exist
            // Note: There cannot be special symbols and spaces in the attribute key value name, otherwise the generation may fail
            // For example: you need to obtain the key or certificate stored on your own devices,
            // but these keys are not available on other people's devices
            // At this point this function will become very useful, you can set the default value when there are no these key-values
            // Of course, you can also use this function to forcefully add additional properties key-values to the generated code
            permanentKeyValues(
                "permanent.some.key1" to "some_value_1",
                "permanent.some.key2" to "some_value_2"
            )
            // Set properties key-values array names that need to be excluded
            // Here you can set some key names that you want to exclude from the known properties keys
            // These keys are excluded when they exist in the properties keys and will not appear in the generated code
            // Note: If you exclude the key-values set in "permanentKeyValues",
            // then they will only become the initial key-values content you set and continue to exist
            // You can pass in a Regex or use String.toRegex to use the regex function
            excludeKeys(
                "exclude.some.key1",
                "exclude.some.key2"
            )
            // Set properties key-values array names that need to be included
            // Here you can set some key-values names that you want to include from known properties keys
            // These keys are included when the properties key-values exists
            // Key-values that are not included will not appear in the generated code
            // You can pass in a Regex or use String.toRegex to use the regex function
            includeKeys(
                "include.some.key1",
                "include.some.key2"
            )
            // Set properties key-values rules array
            // You can set a set of key-values rules and use "createValueRule" to create new rules for parsing the obtained value content
            // These key-values rules are applied when the properties key-values exists
            keyValuesRules(
                "some.key1" to createValueRule { if (it.contains("_")) it.replace("_", "-") else it },
                "some.key2" to createValueRule { "$it-value" }
            )
            // Set where to generate properties key-values
            // Defaults to "CURRENT_PROJECT" and "ROOT_PROJECT"
            // You can use the following types to set
            // "CURRENT_PROJECT" ← Current project
            // "ROOT_PROJECT" ← Root project
            // "GLOBAL" ← Global (user directory)
            // "SYSTEM" ← System
            // "SYSTEM_ENV" ← System environment variable
            // SweetProperty will generate properties key-values sequentially from the generation positions you set,
            // and the order of generation positions is determined by the order you set
            // Pay Attention: "GLOBAL", "SYSTEM", "SYSTEM_ENV" may have keys and certificates, please manage the generated code carefully
            generateFrom(CURRENT_PROJECT, ROOT_PROJECT)
        }
        // Project generation code function
        // This function is similar to the BuildConfig automatically generated by Android projects
        // The code generated in the project can be directly used by the current project
        // The configuration here includes the configuration in "all", you can override it
        sourcesCode {
            // Custom generated directory path
            // You can fill in the path relative to the current project
            // Defaults to "build/generated/sweet-property"
            // It is recommended to place the generated code in the "build" directory,
            // because the generated code is not recommended to be modified
            generateDirPath = "build/generated/sweet-property"
            // Custom generated package name
            // Android projects use the "namespace" in the "android" configuration method block by default
            // Ordinary Kotlin on Jvm projects use the "project.group" of the project settings by default
            // You don’t need to set it, the package name will automatically match under normal circumstances
            packageName = "com.example.mydemo"
            // Custom generated class name
            // By default, the name of the current project + "Properties" is used
            // You don't need to set it, the class name will be automatically matched under normal circumstances
            className = "MyDemo"
            // Whether to enable restricted access
            // Not enabled by default
            // When enabled, the "internal" modifier will be added to the generated classes and methods
            // If your project is a tool library or dependency, it is usually recommended to enable it
            // Once enabled, it can prevent other developers from calling your project properties key-values when referencing your library
            isEnableRestrictedAccess = false
        }
        // Build script generate code function
        // The code generated in the build script can be directly used by the current "build.gradle.kts"
        // The configuration here includes the configuration in "all", you can override it
        // Note: There is also a "buildscript" method block in Gradle, but it is just a lowercase "s", please don't confuse it
        buildScript {
            // Custom build script extension method name
            // Defaults to "property"
            // You will call it in "build.gradle.kts" using something like "property.some.key"
            extensionName = "property"
        }
    }
    // Root project configuration
    // This is a special configuration method block that can only be used in root projects
    rootProject {
        all {
            // Configure "all"
        }
        sourcesCode {
            // Configure "sourcesCode"
        }
        buildScript {
            // Configure "buildScript"
        }
    }
    // Other projects and sub-projects configurations
    // Fill in the full name of the project that needs to be configured in the method parameters to configure the corresponding project
    // If the current project is a sub-project, you must fill in the ":" in front of the sub-project, such as ":app"
    // If the current project is a nested sub-project, such as app → sub
    // At this time you need to use ":" to separate multiple sub-projects, such as ":app:sub"
    // Note: In version 1.0.2 and before, there is no need to add ":" to identify sub-projects, and an error will be thrown after adding it
    //       This is a wrong approach, Gradle's project naming convention is currently unified, please use the new convention
    // The name of the root project cannot be used directly to configure sub-projects, please use "rootProject"
    project(":app") {
        all {
            // Configure "all"
        }
        sourcesCode {
            // Configure "sourcesCode"
        }
        buildScript {
            // Configure "buildScript"
        }
    }
    // Configure multiple projects and sub-projects at the same time
    // Fill in the method parameters with the array of complete names of the projects that need to be configured
    // to configure each corresponding project
    project(":modules:library1", ":modules:library2") {
        all {
            // Configure "all"
        }
        sourcesCode {
            // Configure "sourcesCode"
        }
        buildScript {
            // Configure "buildScript"
        }
    }
```

If you want to use it in Groovy DSL, please change the `=` of all variables to spaces, delete the `is` in front of `Enable` and lowercase `E`.

If you encounter `Gradle DSL method not found` error, the solution is to migrate to Kotlin DSL.

If you don't want to use the Kotlin DSL entirely, you can also migrate just `settings.gradle` to `settings.gradle.kts`.

## Usage Example

Below is a project's `gradle.properties` configuration file.

```properties
project.groupName=com.highcapable.sweetpropertydemo
project.description=Hello SweetProperty Demo!
project.version=1.0.0
```

In the build script `build.gradle.kts`, we can use these keys directly as shown below.

Here is an example of the configuration part published by Maven.

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

Similarly, you can also call the generated key-value in the current project.

```kotlin
SweetPropertyDemoProperties.PROJECT_GROUP_NAME
SweetPropertyDemoProperties.PROJECT_DESCRIPTION
SweetPropertyDemoProperties.PROJECT_VERSION
```

Let's take the Android project as an example.

In Android projects, it is usually necessary to configure many repeated and fixed properties, such as `targetSdk`.

```properties
project.compileSdk=33
project.targetSdk=33
project.minSdk=22
```

When you set the `isEnableTypeAutoConversion = true`, `SweetProperty` will try to convert it to the corresponding type under the
default configuration during the process of generating the entity class.

For example, the type of the key-value used below can be recognized as an integer and can be directly used by the project configuration.

```kotlin
android {
    compileSdk = property.project.compileSdk
    defaultConfig {
        minSdk = property.project.minSdk
        targetSdk = property.project.targetSdk
    }
}
```

You no longer need to use `buildConfigField` to add code to `BuildConfig`,
with the properties key-values code generated by `SweetProperty`, you can manage your project more flexibly.

You can also use `${...}` in properties values to refer to each other, but recursive references are not allowed.

When you set the `isEnableValueInterpolation = true`,
`SweetProperty` will automatically merge the contents of these references into the corresponding locations.

```properties
project.name=MyDemo
project.developer.name=myname
project.url=https://github.com/${project.developer.name}/${project.name}
```

Note: This feature is provided by `SweetProperty`, and the native `gradle.properties` does not support this feature.

**Possible Problems**

If your project only has one root project and does not import any sub-projects,
if extension methods are not generated properly,
you can solve this problem by migrating your root project to a sub-projects and importing this sub-projects in `settings.gradle.kts`.

We generally recommend classifying the functions of the project, and the root project is only used to manage plugins and some configurations.

**Limitations Note**

`SweetProperty` cannot generated extension methods in `settings.gradle.kts`, because this belongs to the upstream of `SweetProperty`.

## Feedback

If you encounter any problems while using `SweetProperty`, you can always open an `issues` on GitHub to give us feedback.