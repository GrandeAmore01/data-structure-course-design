# IDE 运行指南

由于 Maven 命令行在某些环境下可能遇到问题，推荐使用 IDE 来运行程序。

## 推荐方法：使用 IDE 运行

### 1. IntelliJ IDEA

1. **打开项目**

   - 启动 IntelliJ IDEA
   - 选择 "Open" 或 "Open or Import"
   - 选择项目根目录：`d:\数据结构课设`
   - 选择 "Open as Maven Project"

2. **等待 Maven 导入**

   - IDEA 会自动识别 pom.xml
   - 等待依赖下载完成（右下角进度条）

3. **运行程序**
   - 找到文件：`src/main/java/com/datastruct/visualizer/DataStructureVisualizerAppSimple.java`
   - 右键点击文件
   - 选择 "Run 'DataStructureVisualizerAppSimple.main()'"

### 2. Eclipse

1. **导入项目**

   - 启动 Eclipse
   - File → Import → Maven → Existing Maven Projects
   - 选择项目根目录：`d:\数据结构课设`
   - 点击 Finish

2. **运行程序**
   - 找到文件：`src/main/java/com/datastruct/visualizer/DataStructureVisualizerAppSimple.java`
   - 右键点击文件
   - Run As → Java Application

### 3. VS Code

1. **安装扩展**

   - Java Extension Pack
   - Maven for Java

2. **打开项目**

   - File → Open Folder
   - 选择项目根目录：`d:\数据结构课设`

3. **运行程序**
   - 打开 `DataStructureVisualizerAppSimple.java`
   - 点击 main 方法上方的 "Run" 按钮

## 如果 IDE 运行失败

### 检查 JavaFX 配置

1. **确保 JavaFX 模块可用**

   ```bash
   java --list-modules | findstr javafx
   ```

2. **如果 JavaFX 不可用，添加 VM 参数**
   - 在 IDE 的运行配置中添加：
   ```
   --add-modules javafx.controls,javafx.fxml
   ```

### 手动添加 JavaFX 库

1. **下载 JavaFX SDK**

   - 访问：https://openjfx.io/
   - 下载 JavaFX 17 SDK for Windows

2. **在 IDE 中添加库**
   - IntelliJ IDEA: File → Project Structure → Libraries → + → Java
   - Eclipse: Project Properties → Java Build Path → Libraries → Add External JARs

## 成功运行的标志

当程序成功启动时，您应该看到：

- 一个标题为"数据结构与算法可视化模拟器"的窗口
- 两个标签页："图数据结构"和"排序算法"
- 左侧是可视化区域，右侧是控制面板
- 没有控制台错误信息

## 常见问题

### 1. "JavaFX runtime components are missing"

**解决方案**：添加 VM 参数 `--add-modules javafx.controls,javafx.fxml`

### 2. "Module not found"

**解决方案**：使用 `DataStructureVisualizerAppSimple.java` 而不是 `DataStructureVisualizerApp.java`

### 3. "ClassNotFoundException"

**解决方案**：确保 Maven 依赖已正确下载，重新导入项目

## 命令行备选方案

如果 IDE 也无法运行，可以尝试：

```bash
# 1. 编译项目
mvn clean compile

# 2. 生成classpath
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt

# 3. 手动运行（需要替换classpath.txt中的内容）
java -cp "target/classes;[classpath内容]" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
```

## 联系支持

如果所有方法都失败，请提供：

1. 使用的 IDE 和版本
2. Java 版本（`java -version`）
3. 具体的错误信息
4. 操作系统信息
