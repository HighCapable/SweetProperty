# Changelog

## 1.0.0 | 2023.09.03

- The first version is submitted to Maven

## 1.0.1 | 2023.09.04

- After enabling `isEnableTypeAutoConversion`, you can use '' or "" to force the content of a value to be a string type
- Modify the content of the properties key-values in the generated code to use the optimized type to render

## 1.0.2 | 2023.09.07

- Use `net.lingala.zip4j` to replace JDK's default function of creating compressed files and fix the problem that the JAR created by Gradle 8.0.2+
  version on Windows platform is broken and the generated classes cannot be found
- Refactor the loading function of the automatically generated code part, and add an error message that classes may not be found
- Deprecated ~~`propertiesFileName`~~ method
- Added `propertiesFileNames` method, now you can set a group of properties file names at the same time
- Added `includeKeys` method, now you can set an array of properties key names to include only
- Added `keyValuesRules` method, now you can modify the actual parsing result of the value content during the properties key-values loading process