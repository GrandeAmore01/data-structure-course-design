# 数据结构与算法可视化模拟器

这是一个基于 JavaFX 开发的数据结构与算法可视化学习工具，旨在通过动态可视化展示帮助理解各种数据结构和算法的工作原理。

## 功能特性

### 🎯 基本功能

#### 图数据结构

- **邻接矩阵存储**：支持图的构建、顶点与边的插入、删除操作
- **邻接表存储**：支持图的构建以及各种图算法的可视化
- **图算法可视化**：
  - 深度优先搜索（DFS）
  - 广度优先搜索（BFS）
  - 最小生成树构建过程

#### 排序算法

- **直接插入排序**：动态展示插入过程
- **简单选择排序**：可视化选择和交换过程
- **快速排序**：展示分治思想和递归过程

#### 交互功能

- 手工绘制数据结构
- 动画播放控制（播放、暂停、重置）
- 速度调节
- 步骤详细说明

### 🚀 扩展功能

- **数据保存与加载**：支持将构建的数据结构保存为文件
- **最短路径算法**：Dijkstra 算法可视化
- **DSL 支持**：通过领域专用语言自动化绘制
- **LLM 集成**：自然语言交互（计划中）

## 技术架构

### 核心技术栈

- **Java 11+**：主要编程语言
- **JavaFX 17**：GUI 框架
- **Maven**：依赖管理和构建工具
- **Jackson**：JSON 序列化

### 架构设计

- **MVC 模式**：清晰的模型-视图-控制器分离
- **模块化设计**：便于扩展和维护
- **面向对象设计**：良好的封装和继承关系

## 项目结构

```
src/
├── main/
│   ├── java/com/datastruct/visualizer/
│   │   ├── DataStructureVisualizerApp.java    # 主应用程序
│   │   ├── controller/
│   │   │   └── MainController.java            # 主控制器
│   │   ├── model/
│   │   │   ├── graph/                         # 图数据结构模型
│   │   │   │   ├── Graph.java                 # 图抽象基类
│   │   │   │   ├── AdjacencyMatrix.java       # 邻接矩阵实现
│   │   │   │   ├── AdjacencyList.java         # 邻接表实现
│   │   │   │   └── Edge.java                  # 边类
│   │   │   └── sorting/                       # 排序算法模型
│   │   │       ├── SortingAlgorithm.java      # 排序算法基类
│   │   │       ├── InsertionSort.java         # 插入排序
│   │   │       ├── SelectionSort.java         # 选择排序
│   │   │       ├── QuickSort.java             # 快速排序
│   │   │       ├── SortingStep.java           # 排序步骤
│   │   └── view/
│   │       ├── GraphVisualizationPane.java    # 图可视化面板
│   │       └── SortingVisualizationPane.java  # 排序可视化面板
│   └── resources/
│       ├── fxml/
│       │   └── main.fxml                      # 主界面布局
│       └── css/
│           └── style.css                      # 样式表
├── module-info.java                           # 模块配置
└── pom.xml                                    # Maven配置
```

## 快速开始

### DSL 脚本用法

应用已内置“DSL 工作台”（Graph 标签页左侧）。

常用指令：

```
# 结构
add vertex 5 label "E"        # 添加顶点并设置标签
add edge 0 -> 5 weight 2       # 添加有向边
remove edge 1 -> 2             # 删除边
set label 3 "Goal"            # 修改顶点标签

# 算法
run dfs start 0                # 深度优先遍历
run bfs start 0                # 广度优先遍历
run dijkstra start 0 target 3  # 最短路径动画
run mst kruskal                # 最小生成树
```

使用步骤：

1. 在 DSL 工作台 TextArea 输入脚本（多行指令）。
2. 点击“执行脚本”按钮；结构指令即时更新图，算法指令按顺序播放动画。
3. 点击“清空”可快速清除脚本。

> 说明：当前版本暂不支持删除顶点、运行时切换有向/无向，批量语句将后续迭代。

### 环境要求

- Java 11 或更高版本
- Maven 3.6 或更高版本
- JavaFX 17（通过 Maven 自动管理）

### 编译和运行

1. **克隆项目**

```bash
git clone [项目地址]
cd 数据结构课设
```

2. **编译项目**

```bash
mvn clean compile
```

3. **运行应用程序**

```bash
mvn javafx:run
```

### 使用指南

#### 图数据结构操作

1. **创建图**

   - 选择存储类型（邻接矩阵/邻接表）
   - 输入顶点数量
   - 点击"创建"按钮

2. **添加边**

   - 输入起点和终点顶点索引
   - 设置边的权重
   - 点击"添加边"按钮

3. **运行算法**
   - 选择算法类型（DFS/BFS/最小生成树）
   - 设置起始顶点
   - 点击"运行算法"观看动画

#### 排序算法操作

1. **设置数组**

   - 在输入框中输入数组元素（用逗号分隔）
   - 点击"设置数组"按钮

2. **开始排序**

   - 选择排序算法
   - 调整动画速度
   - 点击"开始排序"观看动画

3. **控制播放**
   - 使用暂停/继续按钮控制动画
   - 使用重置按钮回到初始状态

## 设计亮点

### 1. 可扩展的算法框架

- 抽象基类设计，便于添加新算法
- 统一的步骤记录机制
- 灵活的可视化接口

### 2. 直观的用户界面

- 清晰的布局和导航
- 实时的状态反馈
- 丰富的交互操作

### 3. 教育友好的设计

- 详细的步骤说明
- 算法复杂度展示
- 可调节的动画速度

### 4. 模块化架构

- 清晰的职责分离
- 便于测试和维护
- 支持功能扩展

## 贡献指南

欢迎对项目进行改进！请遵循以下步骤：

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目 Issues
- 邮箱：[2139873372@qq.com]

## 运行说明

### 推荐运行方式

#### 方法 1：使用 IDE（最稳定）

1. 使用 IntelliJ IDEA、Eclipse 或 VS Code 打开项目
2. 等待 Maven 依赖下载完成
3. 运行 `DataStructureVisualizerAppSimple.java`
4. 详细说明请查看 [IDE 运行指南](IDE_RUN_GUIDE.md)

#### 方法 2：使用批处理脚本

```bash
# 双击运行以下任一脚本
run-maven.bat      # Maven方式
run-direct.bat     # 直接Java方式
start.bat          # 简化启动
```

#### 方法 3：手动编译运行

```bash
# 1. 编译项目
mvn clean compile

# 2. 生成classpath
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt

# 3. 运行程序（需要替换classpath.txt中的内容）
java -cp "target/classes;[classpath内容]" --add-modules javafx.controls,javafx.fxml com.datastruct.visualizer.DataStructureVisualizerAppSimple
```

### 故障排除

如果遇到问题，请查看：

- [故障排除指南](TROUBLESHOOTING.md)
- [IDE 运行指南](IDE_RUN_GUIDE.md)

---

**注意**：本项目主要用于教育和学习目的，旨在帮助理解数据结构和算法的基本概念。
