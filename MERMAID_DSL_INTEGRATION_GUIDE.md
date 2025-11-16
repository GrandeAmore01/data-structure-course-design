# Mermaid DSL 集成实施指南

本指南提供了将 Mermaid DSL（领域特定语言）集成到数据结构可视化程序的完整步骤。

## 📋 项目改动概览

本集成包括三个主要文件的修改：

1. **DataSerializer.java** - 添加 Mermaid DSL 解析功能
2. **main.fxml** - 在 UI 中添加 Mermaid DSL 输入/输出面板
3. **MainController.java** - 添加 DSL 处理和转换逻辑

---

## 🔧 实施步骤

### 步骤 1: 更新 DataSerializer.java

**文件位置**: `src/main/java/com/datastruct/visualizer/util/DataSerializer.java`

**操作**:
将现有文件内容完全替换为改进版本。改进版包括：

- 新增 `parseMermaidToGraph()` 方法 - 从 Mermaid 文本解析图
- 新增 `parseMermaidToArray()` 方法 - 从 Mermaid 文本解析数组
- 新增 5 个私有辅助方法用于解析
- 新增 `EdgeDefinition` 内部类

**关键方法签名**:

```java
public static Graph parseMermaidToGraph(String mermaidText) throws IllegalArgumentException
public static int[] parseMermaidToArray(String mermaidText) throws IllegalArgumentException
```

**支持的 Mermaid 格式**:

图的格式:

```
graph LR
v0["顶点0"]
v1["顶点1"]
v0 --> |权重| v1
```

数组的格式:

```
graph LR
a0["5"]
a1["2"]
a2["9"]
a0 --> a1
a1 --> a2
```

---

### 步骤 2: 更新 main.fxml

**文件位置**: `src/main/resources/fxml/main.fxml`

**操作**:

1. 打开现有 `main.fxml`
2. 将每个 `<Tab>` 的内容结构从单纯 `<SplitPane>` 改为 `<VBox>`
3. 在 `<SplitPane>` 后添加可折叠的 `<TitledPane>`（用于 Mermaid DSL）

**对于图标签页 (graphTab)**:

- 在现有 SplitPane 下方添加一个 TitledPane
- TitledPane 中包含:
  - 标签: "输入 Mermaid DSL:"
  - TextArea: `graphMermaidInput` (高度 80px)
  - 按钮: `graphMermaidConvertButton` (文本 "从 DSL 加载图")
  - 标签: "转换输出:"
  - TextArea: `graphMermaidOutput` (高度 80px, 只读)
  - 按钮: 复制输出 (onAction="#copyGraphMermaidOutput")

**对于排序标签页 (sortingTab)**:

- 同样的结构，但使用 `sortingMermaidInput`, `sortingMermaidOutput`, `sortingMermaidConvertButton`
- 复制按钮: onAction="#copySortingMermaidOutput"

**关键 FXML 元素 ID**:

- 图相关: `graphMermaidInput`, `graphMermaidOutput`, `graphMermaidConvertButton`
- 排序相关: `sortingMermaidInput`, `sortingMermaidOutput`, `sortingMermaidConvertButton`

---

### 步骤 3: 更新 MainController.java

**文件位置**: `src/main/java/com/datastruct/visualizer/controller/MainController.java`

**操作**:

#### 3.1 添加 FXML 字段注入

在类的字段声明区添加（位置：在现有 `@FXML` 字段之后）:

```java
// Mermaid DSL 输入输出相关控件
@FXML private TextArea graphMermaidInput;
@FXML private TextArea graphMermaidOutput;
@FXML private Button graphMermaidConvertButton;
@FXML private TextArea sortingMermaidInput;
@FXML private TextArea sortingMermaidOutput;
@FXML private Button sortingMermaidConvertButton;
```

#### 3.2 在 setupEventHandlers() 中添加事件绑定

在 `setupEventHandlers()` 方法的末尾添加:

```java
// Mermaid DSL 处理事件绑定
if (graphMermaidConvertButton != null) {
    graphMermaidConvertButton.setOnAction(e -> handleGraphMermaidConvert());
}

if (sortingMermaidConvertButton != null) {
    sortingMermaidConvertButton.setOnAction(e -> handleSortingMermaidConvert());
}
```

#### 3.3 添加公开方法

在类中添加以下方法（位置：在现有公开方法之后）:

```java
/**
 * 处理图的 Mermaid DSL 输入
 */
public void handleMermaidGraphInput(String mermaidText) {
    // ... 方法体（见下文实现）
}

/**
 * 处理排序的 Mermaid DSL 输入
 */
public void handleMermaidSortingInput(String mermaidText) {
    // ... 方法体（见下文实现）
}

/**
 * 获取当前图的 Mermaid DSL 输出
 */
public String getMermaidGraphOutput() {
    // ... 方法体（见下文实现）
}

/**
 * 获取当前排序数组的 Mermaid DSL 输出
 */
public String getMermaidSortingOutput() {
    // ... 方法体（见下文实现）
}
```

#### 3.4 添加私有方法

在类中添加以下私有方法:

```java
@FXML
private void handleGraphMermaidConvert() {
    // ... 方法体（见下文实现）
}

@FXML
private void handleSortingMermaidConvert() {
    // ... 方法体（见下文实现）
}

@FXML
private void copyGraphMermaidOutput() {
    // ... 方法体（见下文实现）
}

@FXML
private void copySortingMermaidOutput() {
    // ... 方法体（见下文实现）
}

private String generateMermaidGraphDSL(Graph graph) {
    // ... 方法体（见下文实现）
}

private String generateMermaidSortingDataDSL(int[] array) {
    // ... 方法体（见下文实现）
}

private Graph parseMermaidGraph(String mermaidText) {
    // ... 方法体（见下文实现）
}

private int[] parseMermaidSortingData(String mermaidText) {
    // ... 方法体（见下文实现）
}

private EdgeDefinition parseEdgeLine(String line) {
    // ... 方法体（见下文实现）
}

private String arrayToString(int[] array) {
    // ... 方法体（见下文实现）
}

// 内部类
private static class EdgeDefinition {
    int source;
    int dest;
    double weight;
}
```

---

## 🎯 功能说明

### 图的 Mermaid DSL 转换

**输入示例**:

```
graph LR
v0["Node0"]
v1["Node1"]
v2["Node2"]
v0 --> |1.5| v1
v1 --> |2.0| v2
v0 -- v2
```

**生成的图**:

- 3 个顶点 (0, 1, 2)
- 3 条边: 0→1(权重 1.5), 1→2(权重 2.0), 0-2(权重 1.0，无向)

### 排序数组的 Mermaid DSL 转换

**输入示例**:

```
graph LR
a0["5"]
a1["2"]
a2["9"]
a3["1"]
a0 --> a1
a1 --> a2
a2 --> a3
```

**生成的数组**: `[5, 2, 9, 1]`

---

## ✅ 验证步骤

完成集成后，执行以下步骤验证功能:

1. **编译项目**:

   ```bash
   mvn clean compile
   ```

2. **运行程序**:

   ```bash
   mvn javafx:run
   ```

3. **测试图转换**:

   - 创建一个图（例如 4 个顶点）
   - 添加几条边
   - 点击"图数据结构"标签页下方的"Mermaid DSL 转换"
   - 点击"从 DSL 加载图"（虽然此时输入为空，应该显示错误）
   - 在输出区应该看到当前图的 Mermaid 表示

4. **测试排序转换**:
   - 设置一个数组（例如 "64, 34, 25, 12"）
   - 点击"排序算法"标签页下方的"Mermaid DSL 转换"
   - 点击"从 DSL 加载数组"
   - 在输出区应该看到数组的 Mermaid 表示

---

## 🐛 常见问题

### Q: 如何自定义 Mermaid DSL 输出格式？

A: 修改 `generateMermaidGraphDSL()` 和 `generateMermaidSortingDataDSL()` 方法中的字符串构建逻辑。

### Q: 支持哪些 Mermaid 图类型？

A: 当前支持 `graph LR`（左到右）和 `graph TD`（从上到下）有向图/无向图。

### Q: 如何添加新的 DSL 输入验证？

A: 在 `parseMermaidGraph()` 和 `parseMermaidSortingData()` 方法中添加额外的验证逻辑。

---

## 📦 文件清单

完成集成后，以下文件应被修改:

- `src/main/java/com/datastruct/visualizer/util/DataSerializer.java` ✓
- `src/main/resources/fxml/main.fxml` ✓
- `src/main/java/com/datastruct/visualizer/controller/MainController.java` ✓

不需要新建文件，所有改动都是在现有文件基础上的扩展。

---

## 🚀 后续优化建议

1. **支持更多 Mermaid 图类型**: 添加对其他图布局的支持（如 flowchart、state diagram 等）
2. **导出功能**: 添加将 Mermaid DSL 导出为文件的功能
3. **实时预览**: 集成 Mermaid.js 库进行实时渲染预览
4. **语法高亮**: 为 Mermaid 输入区添加代码着色
5. **错误恢复**: 完善错误处理和用户提示机制

---

## 📞 支持

如有问题或需要进一步的帮助，请参考：

- Mermaid 官方文档: https://mermaid.js.org/
- JavaFX 文档: https://openjfx.io/
- 项目的 README.md 和 USAGE.md
