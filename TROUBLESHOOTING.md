# 故障排除指南

## 常见问题及解决方案

### 1. JavaFX 运行时错误

**错误信息**：

```
[ERROR] Failed to execute goal org.openjfx:javafx-maven-plugin:0.0.8:run
```

**可能原因**：

- JavaFX 模块路径配置问题
- Java 版本不兼容
- 模块化配置错误

**解决方案**：

#### 方案 1：使用简化版本

```bash
mvn exec:java@run-simple
```

#### 方案 2：手动运行（推荐）

```bash
# 编译项目
mvn clean compile

# 运行简化版本
java -cp "target/classes;%USERPROFILE%\.m2\repository\org\openjfx\javafx-controls\17.0.2\javafx-controls-17.0.2.jar;%USERPROFILE%\.m2\repository\org\openjfx\javafx-fxml\17.0.2\javafx-fxml-17.0.2.jar" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
```

#### 方案 3：使用 IDE 运行

1. 在 IDE 中打开项目
2. 找到 `DataStructureVisualizerAppSimple.java`
3. 右键选择"Run"

### 2. 模块路径错误

**错误信息**：

```
Error occurred during initialization of boot layer
java.lang.module.FindException: Module not found
```

**解决方案**：

- 使用非模块化版本：`DataStructureVisualizerAppSimple.java`
- 或者确保所有依赖都正确配置

### 3. 编译错误

**错误信息**：

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**解决方案**：

1. 检查 Java 版本：`java -version`（需要 11+）
2. 检查 Maven 版本：`mvn -version`
3. 清理并重新编译：`mvn clean compile`

### 4. 资源文件找不到

**错误信息**：

```
java.lang.IllegalArgumentException: Invalid location: /fxml/main.fxml
```

**解决方案**：

1. 确保资源文件在正确位置：`src/main/resources/fxml/main.fxml`
2. 检查文件编码（应为 UTF-8）
3. 重新编译项目

### 5. 依赖问题

**错误信息**：

```
ClassNotFoundException: com.fasterxml.jackson.databind.ObjectMapper
```

**解决方案**：

1. 重新下载依赖：`mvn clean install`
2. 检查网络连接
3. 清理本地仓库：`mvn dependency:purge-local-repository`

## 运行方式总结

### 推荐运行方式（按优先级）

1. **IDE 运行**（最稳定）

   - 导入 Maven 项目
   - 运行 `DataStructureVisualizerAppSimple.java`

2. **简化脚本运行**

   ```bash
   run-simple.bat
   ```

3. **Maven exec 插件**

   ```bash
   mvn exec:java@run-simple
   ```

4. **手动命令行**
   ```bash
   mvn clean compile
   java -cp "target/classes;[所有依赖jar路径]" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
   ```

### 环境要求

- **Java**: 11 或更高版本
- **Maven**: 3.6 或更高版本
- **JavaFX**: 17.0.2（通过 Maven 自动管理）
- **操作系统**: Windows/Linux/macOS

### 检查环境

```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version

# 检查JavaFX是否可用
java --list-modules | findstr javafx
```

## 如果所有方法都失败

1. **重新安装 JavaFX**

   - 下载 JavaFX 17 SDK
   - 设置 JAVA_HOME 和 PATH

2. **使用 IDE**

   - IntelliJ IDEA 或 Eclipse
   - 导入 Maven 项目
   - 配置 JavaFX 库

3. **降级 JavaFX 版本**

   - 修改 pom.xml 中的 javafx.version
   - 使用更稳定的版本如 11.0.2

4. **联系支持**
   - 提供完整的错误日志
   - 说明操作系统和 Java 版本
   - 描述具体的错误步骤

## 成功运行的标志

当程序成功启动时，您应该看到：

1. 一个标题为"数据结构与算法可视化模拟器"的窗口
2. 两个标签页："图数据结构"和"排序算法"
3. 左侧是可视化区域，右侧是控制面板
4. 没有控制台错误信息

如果看到这些，说明程序运行正常！
