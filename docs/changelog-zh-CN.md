# 更新日志

## 1.0.0 | 2023.09.03

- 首个版本提交至 Maven

## 1.0.1 | 2023.09.04

- 新增启用 `isEnableTypeAutoConversion` 后可以使用 '' 或 "" 强制设置一个键值内容为字符串类型
- 修改生成的代码中属性键值的内容使用优化后的类型呈现

## 1.0.2 | 2023.09.07

- 使用 `net.lingala.zip4j` 取代 JDK 默认创建压缩文档功能修复在 Windows 平台中 Gradle 8.0.2+ 版本创建的 JAR 损坏导致找不到生成的 Class 问题
- 重构自动生成代码部分的装载功能，增加可能找不到 Class 的错误提示
- 作废了 ~~`propertiesFileName`~~ 方法
- 新增 `propertiesFileNames` 方法，现在你可以同时设置一组属性配置文件名称了
- 新增 `includeKeys` 方法，现在你可以设置仅包含的属性键值名称数组了
- 新增 `keyValuesRules` 方法，现在你可以在属性键值装载过程中修改键值内容的实际解析结果

## 1.0.3 | 2023.09.26

- 自动生成代码功能将始终输出源码文件，以方便在生成失败的时候进行调试
- 修复 Gradle 生命周期问题
- 修复根项目大小写变化后识别为两个项目的问题
- 修复在全局配置中使用过其它配置方法后，子项目的 `all` 方法失效问题
- 改进并采用 Gradle 项目命名规范
- 新增插件自身检查更新功能
- 一些其它功能性的改进

## 1.0.4 | 2023.11.04

- 修复类似 `a=some` 和 `a_b=some` 的属性键值名称会造成重复方法名称的问题
- 修复使用 `${...}` 生成的插值内容依然会携带字符串类型引号问题
- 生成的代码使用 `@Nonnull` 标记以使其能够在 Kotlin DSL 脚本中识别为非空返回值类型
- 新增 `project(...)` 配置方法支持同时配置多个项目
- 一些其它功能性的改进

## 1.0.5 | 2023.11.08

- 修复遇到特殊字符和重复键值名称造成代码生成失败的严重问题

## 1.0.6 | 2025.08.19

- 修复在新版 Android Gradle Plugin 及 Android Studio/IDEA 中部署源码路径时的错误
  `removeContentEntry: removed content entry url 'build/generated/sweet-property' still exists after removing`
- 新增 `sourceSetName` 方法，允许自定义要部署的源集名称
- 修复在类型自动转换过程中某些 `commit id` 和 Hash 可能被识别为数值类型的问题