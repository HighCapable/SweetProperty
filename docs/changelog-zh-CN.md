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