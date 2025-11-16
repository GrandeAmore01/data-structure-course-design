# 🚀 Mermaid DSL 集成 - 快速开始指南

## ⚡ 3 分钟快速上手

### 前置条件

- IDE（IntelliJ IDEA、Eclipse 或 VS Code）
- 打开项目：`d:\数据结构课设`

### 超快集成方案

**第一步**（1 分钟）：替换文件

```
1. 打开 src/main/java/com/datastruct/visualizer/util/DataSerializer.java
2. 全选删除，从 DataSerializer_UPDATED.java 复制全部内容粘贴
3. 保存 Ctrl+S
```

**第二步**（1 分钟）：替换 FXML

```
1. 打开 src/main/resources/fxml/main.fxml
2. 全选删除，从 main_UPDATED.fxml 复制全部内容粘贴
3. 保存 Ctrl+S
```

**第三步**（1 分钟）：修改 Controller

```
打开 src/main/java/com/datastruct/visualizer/controller/MainController.java

3.1 在字段声明区末尾（其他 @FXML 字段下方）添加：
@FXML private TextArea graphMermaidInput;
@FXML private TextArea graphMermaidOutput;
@FXML private Button graphMermaidConvertButton;
@FXML private TextArea sortingMermaidInput;
@FXML private TextArea sortingMermaidOutput;
@FXML private Button sortingMermaidConvertButton;

3.2 在 setupEventHandlers() 末尾添加：
if (graphMermaidConvertButton != null) {
    graphMermaidConvertButton.setOnAction(e -> handleGraphMermaidConvert());
}
if (sortingMermaidConvertButton != null) {
    sortingMermaidConvertButton.setOnAction(e -> handleSortingMermaidConvert());
}

3.3 在类末尾复制 MainController_MERMAID_METHODS.txt 中的所有方法
```

---

## ✅ 即时验证

### 编译

```bash
mvn clean compile
```

预期：`BUILD SUCCESS`

### 运行

```bash
mvn javafx:run
```

预期：程序启动，看到两个标签页下方各有 "Mermaid DSL 转换" 面板

---

## 🎮 立即测试

### 测试 1：图转换

```
1. 点击"图数据结构"标签页
2. 创建 3 个顶点的有向图
3. 添加边：0->1, 1->2
4. 向下滚动看到"Mermaid DSL 转换"面板
5. 点击展开面板（三角形或标题点击）
6. 在输出区应看到类似：

graph LR
v0["0"]
v1["1"]
v2["2"]
v0 --> v1
v1 --> v2
```

### 测试 2：数组导入

```
1. 点击"排序算法"标签页
2. 向下滚动看到"Mermaid DSL 转换"面板
3. 在输入区粘贴：
graph LR
a0["5"]
a1["2"]
a2["9"]
a0 --> a1
a1 --> a2

4. 点击"从 DSL 加载数组"
5. 顶部数组输入框应显示 "5, 2, 9"
```

---

## 📝 关键代码片段

### 生成 Mermaid 文本

```java
// 直接使用工具方法
String mermaidText = DataSerializer.graphToMermaid(graph, true);
String arrayText = DataSerializer.sortingArrayToMermaid(array, "InsertionSort");
```

### 解析 Mermaid 文本

```java
// 从 Mermaid 文本创建图
Graph graph = DataSerializer.parseMermaidToGraph(mermaidText);

// 从 Mermaid 文本创建数组
int[] array = DataSerializer.parseMermaidToArray(mermaidText);
```

### 在 UI 中使用

```java
// 处理图的 DSL 输入
handleMermaidGraphInput(graphMermaidInput.getText());

// 处理数组的 DSL 输入
handleMermaidSortingInput(sortingMermaidInput.getText());

// 获取当前的 Mermaid 表示
String output = getMermaidGraphOutput();
graphMermaidOutput.setText(output);
```

---

## 🔍 Mermaid 语法速查

### 基本图定义

```mermaid
graph LR                    # 左到右布局
graph TD                    # 上到下布局
v0["标签0"]                 # 节点定义
v1["标签1"]
v0 --> v1                   # 有向边
v0 -->|权重| v1            # 带权重的边
v0 -- v1                    # 无向边
```

### 数组序列

```mermaid
graph LR
a0["5"]
a1["2"]
a2["9"]
a0 --> a1                   # 连接元素
a1 --> a2
```

---

## ❓ 常见问题速解

**Q: 编译报错"找不到符号"?**  
A: 确保 DataSerializer_UPDATED.java 的内容完全复制了。运行 `mvn clean` 后重试。

**Q: FXML 加载失败?**  
A: 检查 fx:id 是否与 Java 代码中的字段名完全一致（区分大小写）。

**Q: 按钮没有响应?**  
A: 确保 setupEventHandlers() 中添加了事件绑定代码，且没有空指针异常。

**Q: 输出区显示错误信息?**  
A: 这是正常的错误处理。查看错误信息内容，可能是输入格式不正确。

---

## 📂 文件位置快速参考

| 文件                               | 位置                          | 说明           |
| ---------------------------------- | ----------------------------- | -------------- |
| DataSerializer.java                | src/main/java/.../util/       | 核心序列化工具 |
| main.fxml                          | src/main/resources/fxml/      | UI 布局定义    |
| MainController.java                | src/main/java/.../controller/ | 控制器逻辑     |
| DataSerializer_UPDATED.java        | d:\数据结构课设\              | 参考文件       |
| main_UPDATED.fxml                  | d:\数据结构课设\              | 参考文件       |
| MainController_MERMAID_METHODS.txt | d:\数据结构课设\              | 代码片段       |

---

## 🎯 下一步行动

1. ✅ 按照"超快集成方案"执行 3 个步骤
2. ✅ 运行 `mvn clean compile` 验证编译
3. ✅ 运行 `mvn javafx:run` 启动应用
4. ✅ 按照"立即测试"部分进行功能验证
5. ✅ 如有问题，查阅 `INTEGRATION_CHECKLIST.md` 的故障排查部分

---

## 📞 获得帮助

| 需求         | 文档                               |
| ------------ | ---------------------------------- |
| 详细集成步骤 | `MERMAID_DSL_INTEGRATION_GUIDE.md` |
| 快速集成清单 | `INTEGRATION_CHECKLIST.md`         |
| 项目概览     | `PROJECT_COMPLETION_SUMMARY.md`    |
| Mermaid 语法 | https://mermaid.js.org/            |

---

🎉 **集成完成！祝你使用愉快！**
