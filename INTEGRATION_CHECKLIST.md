# Mermaid DSL 集成 - 快速实施清单

## 📋 文件生成清单

已为你准备的完整文件（位于 `d:\数据结构课设\` 下）：

### 1. **参考文档**

- ✅ `MERMAID_DSL_INTEGRATION_GUIDE.md` - 完整的集成指南
- ✅ `MainController_MERMAID_METHODS.txt` - 所有需要添加的代码片段

### 2. **改进的源文件**

- ✅ `DataSerializer_UPDATED.java` - 完整的改进版 DataSerializer（可直接替换）
- ✅ `main_UPDATED.fxml` - 完整的改进版 FXML（可直接替换）

---

## 🚀 快速集成步骤（5 分钟）

### 步骤 1: 替换 DataSerializer.java

1. 打开 `src/main/java/com/datastruct/visualizer/util/DataSerializer.java`
2. 删除全部内容
3. 从 `DataSerializer_UPDATED.java` 复制全部内容粘贴进去
4. 保存文件

### 步骤 2: 替换 main.fxml

1. 打开 `src/main/resources/fxml/main.fxml`
2. 删除全部内容
3. 从 `main_UPDATED.fxml` 复制全部内容粘贴进去
4. 保存文件

### 步骤 3: 修改 MainController.java

1. 打开 `src/main/java/com/datastruct/visualizer/controller/MainController.java`
2. 在字段声明区（与其他 `@FXML` 字段一起），添加以下 6 个字段：

   ```java
   @FXML private TextArea graphMermaidInput;
   @FXML private TextArea graphMermaidOutput;
   @FXML private Button graphMermaidConvertButton;
   @FXML private TextArea sortingMermaidInput;
   @FXML private TextArea sortingMermaidOutput;
   @FXML private Button sortingMermaidConvertButton;
   ```

3. 在 `setupEventHandlers()` 方法的末尾，添加：

   ```java
   // Mermaid DSL 处理事件绑定
   if (graphMermaidConvertButton != null) {
       graphMermaidConvertButton.setOnAction(e -> handleGraphMermaidConvert());
   }
   if (sortingMermaidConvertButton != null) {
       sortingMermaidConvertButton.setOnAction(e -> handleSortingMermaidConvert());
   }
   ```

4. 在类的末尾，添加所有公开和私有方法（参考 `MainController_MERMAID_METHODS.txt`）

5. 保存文件

---

## ✅ 验证清单

集成完成后，按以下顺序验证：

### 1. 编译检查

```bash
cd d:\数据结构课设
mvn clean compile
```

期望：BUILD SUCCESS

### 2. 启动应用

```bash
mvn javafx:run
```

期望：应用正常启动，看到图和排序两个标签页

### 3. UI 验证

- [ ] 图标签页下方看到"Mermaid DSL 转换"可折叠面板
- [ ] 排序标签页下方看到"Mermaid DSL 转换"可折叠面板
- [ ] 每个面板都包含输入区、转换按钮、输出区、复制按钮

### 4. 功能验证 - 图转换

测试 1: 创建并导出图

```
1. 创建一个图（4 个顶点）
2. 添加几条边（例如 0->1 权重 1.5, 1->2 权重 2.0）
3. 点击"Mermaid DSL 转换"展开面板
4. 不输入任何东西，直接点击"从 DSL 加载图"
5. 应该显示错误："Mermaid 输入不能为空"
6. 【成功】✓
```

测试 2: 导出当前图为 Mermaid

```
1. 保持上面创建的图
2. 在 Mermaid DSL 转换面板中输入任意文本或留空
3. 点击"转换输出"（虽然在当前 UI 中可能没有这个按钮，检查是否需要添加）
【预期】输出区应该显示当前图的 Mermaid 表示
```

**手动测试导出**:
由于需要加入生成当前图的 Mermaid 的逻辑，你可能需要在控制器中添加一个按钮事件来调用 `getMermaidGraphOutput()` 并显示在输出区。

### 5. 功能验证 - 排序转换

测试 3: 导入 Mermaid 排序数据

```
1. 复制以下 Mermaid 文本到"排序"标签页的输入区：
   graph LR
   a0["5"]
   a1["2"]
   a2["9"]
   a0 --> a1
   a1 --> a2

2. 点击"从 DSL 加载数组"
3. 检查数组输入区是否显示 "5, 2, 9"
【成功】✓
```

### 6. 剪贴板功能验证

测试 4: 复制输出

```
1. 点击输出区下方的"复制输出"按钮
2. 应该看到"成功：已复制到剪贴板"的提示
3. 粘贴到记事本，验证内容正确
【成功】✓
```

---

## 🎯 关键功能说明

### Mermaid DSL 支持的格式

#### 图格式

```
graph LR
v0["顶点0"]
v1["顶点1"]
v0 --> |权重| v1
```

#### 数组格式

```
graph LR
a0["5"]
a1["2"]
a2["9"]
a0 --> a1
a1 --> a2
```

### 支持的转换操作

- ✅ 从 Mermaid DSL 解析图 (`parseMermaidToGraph`)
- ✅ 从 Mermaid DSL 解析数组 (`parseMermaidToArray`)
- ✅ 将图导出为 Mermaid DSL (`graphToMermaid`)
- ✅ 将数组导出为 Mermaid DSL (`sortingArrayToMermaid`)

---

## ❌ 常见问题排查

### 问题 1: 编译错误 - "无法找到符号 DataSerializer.parseMermaidToGraph"

**原因**: DataSerializer.java 没有正确替换
**解决**: 确保 `DataSerializer_UPDATED.java` 中的所有新方法都已添加到实际文件中

### 问题 2: FXML 加载错误 - "fx:id='graphMermaidInput' 无效"

**原因**: FXML 没有正确替换，或者 MainController 中缺少相应的 `@FXML` 字段
**解决**:

1. 检查 `main.fxml` 是否包含所有 `fx:id`
2. 检查 `MainController.java` 是否有相应的字段注入

### 问题 3: 运行时空指针异常

**原因**: FXML 中定义的组件未正确注入到控制器
**解决**:

1. 验证 FXML 文件中的 `fx:id` 与 Java 代码中的字段名完全一致
2. 确认 `@FXML` 注解正确使用

### 问题 4: 按钮无响应

**原因**: `setupEventHandlers()` 中未绑定事件或 `@FXML` 方法有问题
**解决**:

1. 检查 `setupEventHandlers()` 中的事件绑定代码
2. 确认 `handleGraphMermaidConvert()` 等方法使用 `@FXML` 注解

---

## 📊 集成前后对比

### 集成前

- 只能通过 UI 手动创建图和数组
- 无法使用 DSL 定义数据结构
- 无法快速导出为可用的文本格式

### 集成后

- ✅ 支持输入 Mermaid DSL 快速定义图和数组
- ✅ 支持将当前数据导出为 Mermaid DSL 文本
- ✅ 支持复制导出结果到剪贴板
- ✅ 提供可折叠的 UI 面板，不影响现有布局

---

## 📈 后续优化方向

1. **实时预览**: 集成 Mermaid.js 库，在 WebView 中实时渲染 Mermaid 输出
2. **语法检查**: 为 Mermaid 输入区添加 codemirror 库实现代码高亮和错误检查
3. **导入/导出**: 支持从文件导入 Mermaid DSL，并导出为文件
4. **更多图类型**: 扩展支持更多 Mermaid 图类型（state diagram, flowchart 等）
5. **撤销/重做**: 记录 DSL 操作历史，支持撤销和重做

---

## 📞 技术支持

- **Mermaid 语法**: https://mermaid.js.org/syntax/graph.html
- **JavaFX FXML**: https://openjfx.io/javadoc/11/
- **项目相关**: 查阅 `README.md` 或 `USAGE.md`
