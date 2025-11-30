package com.datastruct.visualizer.controller;

import com.datastruct.visualizer.model.graph.*;
import com.datastruct.visualizer.model.sorting.*;
import com.datastruct.visualizer.view.GraphVisualizationPane;
import com.datastruct.visualizer.view.SortingVisualizationPane;
import com.datastruct.visualizer.util.DataSerializer;
import com.datastruct.visualizer.util.DslCommand;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
// no extra imports

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * 主控制器类
 * Main Controller Class
 */
public class MainController implements Initializable {
    
    // FXML 注入的控件
    @FXML private TabPane mainTabPane;
    @FXML private Tab graphTab;
    @FXML private Tab sortingTab;
    @FXML private Tab dijkstraTableTab; // NEW
    
    // 图相关控件
    @FXML private VBox graphContainer;
    @FXML private ComboBox<String> graphTypeCombo;
    @FXML private CheckBox directedCheckBox;
    @FXML private TextField numVerticesField;
    @FXML private Button createGraphButton;
    @FXML private TextField removeVertexField;
    @FXML private Button addVertexButton;
    @FXML private Button removeVertexButton;
    @FXML private TextField sourceVertexField;
    @FXML private TextField destVertexField;
    @FXML private TextField edgeWeightField;
    @FXML private Button addEdgeButton;
    @FXML private Button removeEdgeButton;
    @FXML private ComboBox<String> graphAlgorithmCombo;
    @FXML private TextField startVertexField;
    @FXML private TextField targetVertexField;
    @FXML private Button runAlgorithmButton;
    @FXML private TextArea graphInfoArea;
    
    // 排序相关控件
    @FXML private VBox sortingContainer;
    @FXML private TextArea arrayInputField;//原来是TextField
    @FXML private Button setArrayButton;
    @FXML private ComboBox<String> sortingAlgorithmCombo;
    @FXML private Button startSortingButton;
    @FXML private Button pauseButton;
    @FXML private Button resetButton;
    @FXML private Slider speedSlider;
    @FXML private Label speedMillisLabel;
    @FXML private TextArea algorithmInfoArea;
    // 图面板的速度控件（与全局 speedSlider 绑定）
    @FXML private Slider graphSpeedSlider;
    @FXML private Label graphSpeedMillisLabel;
    
    // 文件操作控件
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem loadMenuItem;
    
    // DSL 处理: 从文本解析图并加载到可视化
    @FXML private TextArea dslInput; // DSL 输入区
    @FXML private Button loadDslButton;
    @FXML private Button clearDslButton;
    
    // 可视化面板
    private GraphVisualizationPane graphVisualizationPane;
    private SortingVisualizationPane sortingVisualizationPane;
    private com.datastruct.visualizer.view.DijkstraTablePane dijkstraTablePane; // NEW
    
    // 数据模型
    private Graph currentGraph;
    private SortingAlgorithm currentSortingAlgorithm;
    private List<SortingStep> currentSortingSteps;
    private int currentStepIndex;
    
    // 动画控制
    private Timeline sortingAnimation;
    private boolean isAnimationRunning = false;
    // 图算法动画控制
    private Timeline graphAnimation;
    private boolean isGraphAnimationRunning = false;
    
    private Stage stage;

    // 点击交互：记录上一次被选中的顶点索引（用于点击两次建立/删除边）
    private int lastSelectedVertex = -1;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        setupEventHandlers();
    }

    /**
     * 将 speedSlider 的值映射为动画步长（毫秒），使用“右快左慢”映射：
     * - slider 在最右端 -> 返回较小的毫秒（更快）
     * - slider 在最左端 -> 返回较大的毫秒（更慢）
     * 映射使用一个合理的区间（minMillis..maxMillis），并保证返回值为正。
     */
    private double sliderToMillis() {
        if (speedSlider == null) return 500.0;
        double sMin = speedSlider.getMin();
        double sMax = speedSlider.getMax();
        double val = speedSlider.getValue();

        // 定义目标毫秒区间（最小为最快，最大为最慢）
        double minMillis = 50.0;   // 最快（右端）
        double maxMillis = 2000.0; // 最慢（左端）

        if (sMax <= sMin) return 500.0;

        double norm = (val - sMin) / (sMax - sMin); // 0..1 (left..right)
        double inverted = 1.0 - norm; // 1..0 (left..right)

        double millis = minMillis + inverted * (maxMillis - minMillis);
        return Math.max(10.0, millis);
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void setupUI() {
        // 创建可视化面板
        graphVisualizationPane = new GraphVisualizationPane();
        sortingVisualizationPane = new SortingVisualizationPane();
        dijkstraTablePane = new com.datastruct.visualizer.view.DijkstraTablePane(); // NEW
        
        // 添加到容器
        graphContainer.getChildren().add(graphVisualizationPane);
        sortingContainer.getChildren().add(sortingVisualizationPane);
        // 将 Dijkstra 表格添加到主容器
        graphContainer.getChildren().add(dijkstraTablePane);

        // 注册图面板的顶点点击回调：用于点击交互添加/删除边
        graphVisualizationPane.setOnVertexClickedHandler(v -> {
            // 如果当前未创建图，提示并返回
            if (currentGraph == null) {
                showAlert("信息", "请先创建图（通过左侧创建面板）");
                return;
            }
            // 仅在图标签页启用交互
            if (mainTabPane.getSelectionModel().getSelectedItem() != graphTab) return;
            handleVertexClick(v);
        });
        
        // 初始化下拉框
        graphTypeCombo.getItems().addAll("邻接矩阵", "邻接表");
        graphTypeCombo.setValue("邻接矩阵");
        
    graphAlgorithmCombo.getItems().addAll("深度优先搜索", "广度优先搜索", "最短路径生成", "最小生成树(Kruskal)");
        graphAlgorithmCombo.setValue("深度优先搜索");
        
        sortingAlgorithmCombo.getItems().addAll("直接插入排序", "简单选择排序", "快速排序");
        sortingAlgorithmCombo.setValue("直接插入排序");
        
        // 设置默认值
        numVerticesField.setText("4");
        arrayInputField.setText("64, 34, 25, 12, 22, 11, 90");
        startVertexField.setText("0");
        speedSlider.setValue(500); // 默认动画速度
        // 初始化速度显示（毫秒）并监听滑块变化以更新显示
        if (speedMillisLabel != null) {
            speedMillisLabel.setText(String.format("%.0f ms", sliderToMillis()));
        }
        if (speedSlider != null) {
            speedSlider.valueProperty().addListener((obs, oldV, newV) -> {
                if (speedMillisLabel != null) {
                    speedMillisLabel.setText(String.format("%.0f ms", sliderToMillis()));
                }
            });
        }
        // 将图面板的滑块和标签与主滑块/标签绑定，使两处同步显示/控制
        if (graphSpeedSlider != null) {
            // 保证范围与初始值一致
            graphSpeedSlider.setMin(speedSlider.getMin());
            graphSpeedSlider.setMax(speedSlider.getMax());
            graphSpeedSlider.setValue(speedSlider.getValue());
            // 双向绑定数值
            graphSpeedSlider.valueProperty().bindBidirectional(speedSlider.valueProperty());
        }
        if (graphSpeedMillisLabel != null && speedMillisLabel != null) {
            // 将图面板的毫秒标签绑定到主标签文本
            graphSpeedMillisLabel.textProperty().bind(speedMillisLabel.textProperty());
        }
        
        // 初始化信息区域
        updateGraphInfo();
        updateAlgorithmInfo();
    }
    
    private void setupEventHandlers() {
        createGraphButton.setOnAction(e -> createGraph());
        addVertexButton.setOnAction(e -> addVertex());
        removeVertexButton.setOnAction(e -> removeVertex());
        addEdgeButton.setOnAction(e -> addEdge());
        removeEdgeButton.setOnAction(e -> removeEdge());
        runAlgorithmButton.setOnAction(e -> runGraphAlgorithm());
        
        setArrayButton.setOnAction(e -> setArray());
        startSortingButton.setOnAction(e -> startSorting());
        pauseButton.setOnAction(e -> pauseSorting());
        resetButton.setOnAction(e -> resetSorting());
        
        saveMenuItem.setOnAction(e -> saveData());
        loadMenuItem.setOnAction(e -> loadData());
        
        sortingAlgorithmCombo.setOnAction(e -> updateAlgorithmInfo());
    }
    
    // 图相关方法
    @FXML
    private void createGraph() {
        try {
            int numVertices = Integer.parseInt(numVerticesField.getText());
            if (numVertices <= 0 || numVertices > 20) {
                showAlert("错误", "顶点数量必须在1-20之间");
                return;
            }
            
            String graphType = graphTypeCombo.getValue();
            boolean isDirected = directedCheckBox == null ? true : directedCheckBox.isSelected();
            
            if ("邻接矩阵".equals(graphType)) {
                currentGraph = new AdjacencyMatrix(numVertices, isDirected);
            } else {
                currentGraph = new AdjacencyList(numVertices, isDirected);
            }
            
            graphVisualizationPane.setGraph(currentGraph);
            updateGraphInfo();
            // 重置点击交互状态
            lastSelectedVertex = -1;
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的顶点数量");
        }
    }
    
    @FXML
    private void addVertex() {
        if (currentGraph == null) {
            showAlert("错误", "请先创建图");
            return;
        }
        
        // 检查顶点数量限制
        if (currentGraph.getNumVertices() >= 20) {
            showAlert("错误", "顶点数量已达到上限（20个）");
            return;
        }
        
        // 停止正在运行的动画（如果有）
        if (graphAnimation != null && isGraphAnimationRunning) {
            graphAnimation.stop();
            isGraphAnimationRunning = false;
        }
        
        try {
            // 新顶点的索引
            int newVertexIndex = currentGraph.getNumVertices();
            
            // 使用索引作为标签
            currentGraph.addVertex(String.valueOf(newVertexIndex));
            graphVisualizationPane.setGraph(currentGraph);
            updateGraphInfo("已添加顶点 " + newVertexIndex);
            
            // 重置点击交互状态
            lastSelectedVertex = -1;
            
        } catch (Exception e) {
            showAlert("错误", "添加顶点失败: " + e.getMessage());
        }
    }
    
    @FXML
    private void removeVertex() {
        if (currentGraph == null) {
            showAlert("错误", "请先创建图");
            return;
        }
        
        // 停止正在运行的动画（如果有）
        if (graphAnimation != null && isGraphAnimationRunning) {
            graphAnimation.stop();
            isGraphAnimationRunning = false;
        }
        
        try {
            String indexText = removeVertexField.getText();
            if (indexText == null || indexText.trim().isEmpty()) {
                showAlert("错误", "请输入要删除的顶点索引");
                return;
            }
            
            int vertex = Integer.parseInt(indexText.trim());
            
            if (vertex < 0 || vertex >= currentGraph.getNumVertices()) {
                showAlert("错误", "顶点索引超出范围 (0-" + (currentGraph.getNumVertices() - 1) + ")");
                return;
            }
            
            if (currentGraph.getNumVertices() <= 1) {
                showAlert("错误", "无法删除最后一个顶点");
                return;
            }
            
            String removedLabel = currentGraph.getVertexLabel(vertex);
            currentGraph.removeVertex(vertex);
            graphVisualizationPane.setGraph(currentGraph);
            updateGraphInfo("已删除顶点: " + removedLabel + " (原索引: " + vertex + ")\\n注意: 索引大于 " + vertex + " 的顶点索引已减1");
            
            // 清空输入框
            removeVertexField.clear();
            // 重置点击交互状态
            lastSelectedVertex = -1;
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的顶点索引");
        } catch (Exception e) {
            showAlert("错误", "删除顶点失败: " + e.getMessage());
        }
    }
    
    @FXML
    private void addEdge() {
        if (currentGraph == null) {
            showAlert("错误", "请先创建图");
            return;
        }
        
        try {
            int source = Integer.parseInt(sourceVertexField.getText());
            int dest = Integer.parseInt(destVertexField.getText());
            double weight = Double.parseDouble(edgeWeightField.getText());
            
            if (source < 0 || source >= currentGraph.getNumVertices() ||
                dest < 0 || dest >= currentGraph.getNumVertices()) {
                showAlert("错误", "顶点索引超出范围");
                return;
            }
            
            currentGraph.addEdge(source, dest, weight);
            graphVisualizationPane.redraw();
            updateGraphInfo();
            
            // 清空输入框
            sourceVertexField.clear();
            destVertexField.clear();
            edgeWeightField.setText("1.0");
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的数值");
        }
    }
    
    @FXML
    private void removeEdge() {
        if (currentGraph == null) {
            showAlert("错误", "请先创建图");
            return;
        }
        
        try {
            int source = Integer.parseInt(sourceVertexField.getText());
            int dest = Integer.parseInt(destVertexField.getText());
            
            currentGraph.removeEdge(source, dest);
            graphVisualizationPane.redraw();
            updateGraphInfo();
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的顶点索引");
        }
    }
    
    @FXML
    private void runGraphAlgorithm() {
        if (currentGraph == null) {
            showAlert("错误", "请先创建图");
            return;
        }
        
        try {
            int startVertex = Integer.parseInt(startVertexField.getText());
            if (startVertex < 0 || startVertex >= currentGraph.getNumVertices()) {
                showAlert("错误", "起始顶点索引超出范围");
                return;
            }
            
            String algorithm = graphAlgorithmCombo.getValue();
            List<Integer> result;
            
            switch (algorithm) {
                case "深度优先搜索":
                    result = currentGraph.depthFirstSearch(startVertex);
                    animateTraversal(result, "DFS");
                    break;
                case "广度优先搜索":
                    result = currentGraph.breadthFirstSearch(startVertex);
                    animateTraversal(result, "BFS");
                    break;
                case "最短路径生成":
                    try {
                        // 读取目标顶点（新 UI 字段 targetVertexField）
                        String rawTarget = targetVertexField == null ? "" : targetVertexField.getText();
                        String targetText = rawTarget == null ? "" : rawTarget.trim();
                        if (targetText.isEmpty()) {
                            showAlert("错误", "请在目标顶点输入框中输入目标顶点索引（整数）");
                            return;
                        }

                        int targetVertex = Integer.parseInt(targetText);
                        if (targetVertex < 0 || targetVertex >= currentGraph.getNumVertices()) {
                            showAlert("错误", "目标顶点索引超出范围");
                            return;
                        }

                        // 检查是否存在负权边，若存在使用 Bellman-Ford，否则使用 Dijkstra
                        boolean hasNegative = currentGraph.getAllEdges().stream().anyMatch(e -> e.getWeight() < 0);

                        if (hasNegative) {
                            MST.ShortestPathResult res = MST.bellmanFord(currentGraph, startVertex);
                            if (res.hasNegativeCycle()) {
                                showAlert("错误", "检测到负权回路，最短路径不可确定");
                                return;
                            }

                            List<Integer> path = res.getPath(targetVertex);
                            if (path.isEmpty()) {
                                showAlert("信息", "从 " + startVertex + " 到 " + targetVertex + " 不可达");
                                return;
                            }

                            animatePath(path, "Bellman-Ford", res.getDistances()[targetVertex]);
                        } else {
                            // Dijkstra（使用带步骤的实现以便可视化生成过程）
                            MST.DijkstraResultWithSteps res = MST.dijkstraWithSteps(currentGraph, startVertex, targetVertex);
                            // 填充表格并切换至“迭代表”标签
                            if (dijkstraTablePane != null) {
                                dijkstraTablePane.setData(res.getSnapshots(), currentGraph.getNumVertices());
                            }
                            if (dijkstraTableTab != null) {
                                mainTabPane.getSelectionModel().select(dijkstraTableTab);
                            }
                            // 如果不可达，提示并返回
                            double distToTarget = res.getDistances()[targetVertex];
                            if (Double.isInfinite(distToTarget)) {
                                showAlert("信息", "从 " + startVertex + " 到 " + targetVertex + " 不可达");
                                return;
                            }

                            // 播放生成过程动画（不会在结束时把边变红，保持橙色）
                            animateShortestPathGeneration(res, startVertex, targetVertex, "Dijkstra");
                        }
                    } catch (NumberFormatException e) {
                        showAlert("错误", "请输入有效的目标顶点索引");
                    }
                    break;
                case "最小生成树(Kruskal)":
                    if (currentGraph.isDirected()) {
                        showAlert("错误", "最小生成树仅适用于无向图");
                        return;
                    }
                    animateKruskal();
                    break;
            }
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的起始顶点");
        }
    }
    
    private void animateTraversal(List<Integer> vertices, String algorithmName) {
        graphVisualizationPane.clearHighlights();

        // 停止之前的图动画（如果存在）
        if (graphAnimation != null) {
            graphAnimation.stop();
        }

    Timeline timeline = new Timeline();
    double stepMillis = sliderToMillis();
        for (int i = 0; i < vertices.size(); i++) {
            final int vertex = vertices.get(i);
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(i * stepMillis),
                e -> {
                    graphVisualizationPane.highlightVertex(vertex);
                    updateGraphInfo(algorithmName + " 遍历: 访问顶点 " + vertex);
                }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        // 绑定为当前图动画并启动
        graphAnimation = timeline;
        isGraphAnimationRunning = true;
        pauseButton.setText("暂停");
        timeline.play();
    }

    /**
     * 可视化显示一条最短路径：按顺序高亮顶点与对应的边，并在信息区显示算法名称和总距离
     */
    private void animatePath(List<Integer> vertices, String algorithmName, double totalDistance) {
        if (vertices == null || vertices.isEmpty()) return;

        // 清除已有高亮
        graphVisualizationPane.clearHighlights();

    // 停止任何正在运行的图动画
    if (graphAnimation != null) graphAnimation.stop();

    Timeline timeline = new Timeline();
    double stepMillis = sliderToMillis(); // 固定步长，受 speedSlider 控制

        // 记录路径上的边，动画期间将它们标记为"被考虑"(橘色)
        List<Edge> pathEdges = new ArrayList<>();

        for (int i = 0; i < vertices.size(); i++) {
            final int idx = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(idx * stepMillis),
                e -> {
                    int v = vertices.get(idx);
                    graphVisualizationPane.highlightVertex(v);

                    // 高亮前一条边（如果存在）：标记为被考虑（橙色），并保留直到算法结束
                    if (idx > 0) {
                        int u = vertices.get(idx - 1);
                        double w = currentGraph.getWeight(u, v);
                        Edge edge = new Edge(u, v, w);
                        if (!pathEdges.contains(edge)) pathEdges.add(edge);
                        graphVisualizationPane.highlightConsideredEdge(edge);
                    }

                    updateGraphInfo(algorithmName + " 最短路径: " + vertices.toString() + "，总距离=" + String.format("%.2f", totalDistance));
                }
            );
            timeline.getKeyFrames().add(keyFrame);
        }

        // 在最后一条边变为橙色后，停顿一段时间再把路径上的所有边统一变为红色
        double pauseMillis = 800; // 在最后一条边橙色显示后的停顿时长（毫秒）
        double convertTime = (vertices.size() - 1) * stepMillis + pauseMillis;

        // 在最后一条边变为橙色后，停顿一段时间并保持橙色（不转换为红色）
        KeyFrame pauseKF = new KeyFrame(Duration.millis(convertTime), ev -> {
            // 保持当前 considered (橙色) 状态，不做转换，供用户观察
            updateGraphInfo(algorithmName + " 最短路径（已完成，保持橙色显示）: " + vertices.toString() + "，总距离=" + String.format("%.2f", totalDistance));
        });

        timeline.getKeyFrames().add(pauseKF);

        // 绑定为当前图动画并启动（以便 pause/stop 控制）
        graphAnimation = timeline;
        isGraphAnimationRunning = true;
        pauseButton.setText("暂停");
        timeline.play();
    }

    /**
     * 可视化 Dijkstra 的生成过程（带步骤）。
     * 视觉规则：
     * - 每当算法考虑一条边时（CONSIDER_EDGE）短暂变为橙色；
     * - 当松弛（RELAX_EDGE）发生时，更新前驱数组并把从源到目标的当前最短路径（若存在）标记为橙色；
     * - 顶点最终确定时高亮顶点；
     * - 算法结束后，保留路径边的橙色（不变为红色）。
     */
    private void animateShortestPathGeneration(MST.DijkstraResultWithSteps res, int source, int target, String algorithmName) {
        if (res == null) return;

        List<MST.DijkstraStep> steps = res.getSteps();
        if (steps == null || steps.isEmpty()) return;

    graphVisualizationPane.clearHighlights();

    // 停止之前的图动画
    if (graphAnimation != null) graphAnimation.stop();

    double stepMillis = sliderToMillis();
    Timeline timeline = new Timeline();

        int n = currentGraph.getNumVertices();
        int[] prev = new int[n];
        Arrays.fill(prev, -1);

        List<Edge> currentPathEdges = new ArrayList<>();

        for (int i = 0; i < steps.size(); i++) {
            final int idx = i;
            MST.DijkstraStep step = steps.get(i);
            double t = idx * stepMillis;

            switch (step.getType()) {
                case EXTRACT_MIN:
                case FINALIZE_VERTEX: {
                    final int v = step.getVertex();
                    KeyFrame kf = new KeyFrame(Duration.millis(t), e -> {
                        graphVisualizationPane.highlightVertex(v);
                        updateGraphInfo(algorithmName + " 处理中: 定点 " + v + " 确定最短距离");
                    });
                    timeline.getKeyFrames().add(kf);
                    break;
                }
                case CONSIDER_EDGE: {
                    final Edge e = step.getEdge();
                    KeyFrame kf = new KeyFrame(Duration.millis(t), ev -> {
                        graphVisualizationPane.highlightConsideredEdge(e);
                        updateGraphInfo(algorithmName + " 处理中: 考虑边 " + e.getSource() + "->" + e.getDestination());
                    });
                    timeline.getKeyFrames().add(kf);

                    // 半步后取消临时考虑高亮（如果该边没有被 later relax 成为当前路径的一部分，会保持）
                    KeyFrame un = new KeyFrame(Duration.millis(t + stepMillis / 2), ev -> {
                        graphVisualizationPane.unhighlightConsideredEdge(e);
                    });
                    timeline.getKeyFrames().add(un);
                    break;
                }
                case RELAX_EDGE: {
                    final Edge e = step.getEdge();
                    final double newDist = step.getNewDistance();
                    KeyFrame kf = new KeyFrame(Duration.millis(t), ev -> {
                        // 更新前驱数组
                        int u = e.getSource();
                        int v = e.getDestination();
                        prev[v] = u;

                        // 计算从 source 到 target 的当前路径（若存在），并把该路径上的边标为橙色
                        List<Edge> newPathEdges = new ArrayList<>();
                        if (target >= 0 && target < prev.length) {
                            LinkedList<Integer> path = new LinkedList<>();
                            int cur = target;
                            while (cur != -1 && cur != source && cur < prev.length) {
                                path.addFirst(cur);
                                cur = prev[cur];
                                if (cur == -1) break;
                            }
                            if (cur == source) {
                                path.addFirst(source);
                                // build edges from path
                                for (int pi = 1; pi < path.size(); pi++) {
                                    int a = path.get(pi - 1);
                                    int b = path.get(pi);
                                    double w = currentGraph.getWeight(a, b);
                                    newPathEdges.add(new Edge(a, b, w));
                                }
                            }
                        }

                        // 更新高亮：移除旧的 path edges 中不在 newPath 的，添加 newPath 中新增的
                        for (Edge oldE : new ArrayList<>(currentPathEdges)) {
                            boolean found = false;
                            for (Edge ne : newPathEdges) {
                                if ((oldE.getSource() == ne.getSource() && oldE.getDestination() == ne.getDestination()) ||
                                    (!currentGraph.isDirected() && oldE.getSource() == ne.getDestination() && oldE.getDestination() == ne.getSource())) {
                                    found = true; break;
                                }
                            }
                            if (!found) {
                                graphVisualizationPane.unhighlightConsideredEdge(oldE);
                                currentPathEdges.remove(oldE);
                            }
                        }

                        for (Edge ne : newPathEdges) {
                            boolean already = false;
                            for (Edge ce : currentPathEdges) {
                                if ((ce.getSource() == ne.getSource() && ce.getDestination() == ne.getDestination()) ||
                                    (!currentGraph.isDirected() && ce.getSource() == ne.getDestination() && ce.getDestination() == ne.getSource())) {
                                    already = true; break;
                                }
                            }
                            if (!already) {
                                graphVisualizationPane.highlightConsideredEdge(ne);
                                currentPathEdges.add(ne);
                            }
                        }
                        // 把之前可能的候选红色路径保留不变，只有在 PATH_TO_TARGET_FOUND 步骤时更新

                        updateGraphInfo(algorithmName + " 处理中: 松弛边 " + e.getSource() + "->" + e.getDestination() + "，当前到目标距离=" + String.format("%.2f", newDist));
                    });
                    timeline.getKeyFrames().add(kf);
                    break;
                }
                    case PATH_TO_TARGET_FOUND: {
                        final List<Edge> pathEdges = step.getPathEdges();
                        KeyFrame kf = new KeyFrame(Duration.millis(t), ev -> {
                            // 清除上一次候选红色路径并标记新的候选路径为红色
                            graphVisualizationPane.markCandidatePathEdges(pathEdges);
                            updateGraphInfo(algorithmName + " 处理中: 发现可达目标的候选路径（红色）: " + (pathEdges == null ? "[]" : pathEdges.toString()));
                        });
                        timeline.getKeyFrames().add(kf);
                        break;
                    }
                case COMPLETE: {
                    KeyFrame kf = new KeyFrame(Duration.millis(t), ev -> {
                        updateGraphInfo(algorithmName + " 完成，稍后将最终路径标为绿色");
                    });
                    timeline.getKeyFrames().add(kf);

                    // 在完成后一小段时间，将当前路径边标记为 accepted (绿色)
                    KeyFrame finalizeKF = new KeyFrame(Duration.millis(t + stepMillis), ev -> {
                        // 使用 acceptEdge 将这些边变为绿色（并从 considered 中移除）
                        for (Edge finalE : new ArrayList<>(currentPathEdges)) {
                            graphVisualizationPane.acceptEdge(finalE);
                        }
                        updateGraphInfo(algorithmName + " 完成，最终最短路径已显示为绿色");
                        // 完成后，标记动画已停止
                        isGraphAnimationRunning = false;
                        pauseButton.setText("暂停");
                    });
                    timeline.getKeyFrames().add(finalizeKF);

                    break;
                }
            }
        }

        // 绑定为当前图动画并启动
        graphAnimation = timeline;
        isGraphAnimationRunning = true;
        pauseButton.setText("暂停");
        timeline.play();
    }

    // MST 可视化：项目保留 Kruskal 实现

    /**
     * Kruskal 可视化：按权重排序所有边，依次考虑每条边并展示是否被加入 MST
     */
    private void animateKruskal() {
        class EdgeAction {
            Edge edge;
            boolean accepted;
            EdgeAction(Edge edge, boolean accepted) { this.edge = edge; this.accepted = accepted; }
        }

        List<EdgeAction> actions = new ArrayList<>();

        // 取所有边并按权重排序
        List<Edge> allEdges = new ArrayList<>(currentGraph.getAllEdges());
        allEdges.sort(Comparator.comparingDouble(Edge::getWeight));

        // 本地并查集实现
        class UF {
            private int[] parent;
            public UF(int n) { parent = new int[n]; for (int i = 0; i < n; i++) parent[i] = i; }
            public int find(int x) { return parent[x]==x?x:(parent[x]=find(parent[x])); }
            public boolean union(int a, int b) {
                int ra = find(a), rb = find(b);
                if (ra == rb) return false;
                parent[ra] = rb; return true;
            }
        }

        UF uf = new UF(currentGraph.getNumVertices());
        List<Edge> mst = new ArrayList<>();

        for (int j = 0; j < allEdges.size(); j++) {
            Edge e = allEdges.get(j);
            int u = e.getSource();
            int v = e.getDestination();

            // 记录为被考虑
            actions.add(new EdgeAction(e, false));
            // 快照为剩余边（j+1 到末尾）

            // 若两端不连通，则接受
            if (uf.union(u, v)) {
                // union 返回 true 表示连接成功（即接受边）
                actions.add(new EdgeAction(e, true));
                mst.add(e);
                // 接受后快照（相比考虑后仍相同）
                if (mst.size() == currentGraph.getNumVertices() - 1) break;
            } else {
                // 被拒绝（已连通） -> 前面的 false step 已表示考虑
            }
        }

    // 动画播放
    graphVisualizationPane.clearHighlights();
    // 使用 speedSlider 控制图动画速度（右快左慢）
    double stepMillis = sliderToMillis();
    // 停止之前的图动画
    if (graphAnimation != null) graphAnimation.stop();
    Timeline timeline = new Timeline();

        // 记录最终被接受为 MST 的边（在算法构建阶段已经收集到 mst 列表）
        List<Edge> acceptedDuring = new ArrayList<>();

        for (int i = 0; i < actions.size(); i++) {
            EdgeAction act = actions.get(i);
            final int idx = i;
            KeyFrame kf = new KeyFrame(Duration.millis(idx * stepMillis), e -> {
                if (!act.accepted) {
                    // 被考虑的边：短暂高亮为橙色，半步后取消（仅用于可视化“被考虑”）
                    graphVisualizationPane.highlightConsideredEdge(act.edge);
                } else {
                    // 被接受的边（加入 MST）：标记为 pending accepted（保持橙色直到最终转换为绿色）
                    graphVisualizationPane.markPendingAcceptedEdge(act.edge);
                    if (!acceptedDuring.contains(act.edge)) acceptedDuring.add(act.edge);
                    updateGraphInfo("Kruskal: 添加边 " + act.edge.getSource() + " - " + act.edge.getDestination() + " (w=" + act.edge.getWeight() + ")");
                }
            });
            timeline.getKeyFrames().add(kf);

            // 如果是未被接受的考虑边，安排在半步时间后取消高亮
            if (!act.accepted) {
                KeyFrame un = new KeyFrame(Duration.millis(idx * stepMillis + stepMillis / 2), e -> {
                    graphVisualizationPane.unhighlightConsideredEdge(act.edge);
                });
                timeline.getKeyFrames().add(un);
            }
        }

        // 动画结束后：把所有被接受的边变为绿色（accepted），并显示最终 MST 权重
        timeline.setOnFinished(e -> {
            for (Edge ae : acceptedDuring) {
                graphVisualizationPane.acceptEdge(ae);
            }
            updateGraphInfo("Kruskal 完成，MST 权重=" + String.format("%.2f", MST.calculateMSTWeight(mst)));
            isGraphAnimationRunning = false;
            pauseButton.setText("暂停");
        });

        // 绑定为当前图动画并启动
        graphAnimation = timeline;
        isGraphAnimationRunning = true;
        pauseButton.setText("暂停");
        timeline.play();
    }
    
    // 排序相关方法
    @FXML
    private void setArray() {
        try {
            String input = arrayInputField.getText().trim();
            String[] parts = input.split("[,\\s]+");
            int[] array = new int[parts.length];
            
            for (int i = 0; i < parts.length; i++) {
                array[i] = Integer.parseInt(parts[i]);
            }
            
            sortingVisualizationPane.setArray(array);
            resetSorting();
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的数组，用逗号或空格分隔");
        }
    }
    
    @FXML
    private void startSorting() {
        String algorithmName = sortingAlgorithmCombo.getValue();
        
        try {
            String input = arrayInputField.getText().trim();
            String[] parts = input.split("[,\\s]+");
            int[] array = new int[parts.length];
            
            for (int i = 0; i < parts.length; i++) {
                array[i] = Integer.parseInt(parts[i]);
            }
            
            // 创建排序算法实例
            switch (algorithmName) {
                case "直接插入排序":
                    currentSortingAlgorithm = new InsertionSort();
                    break;
                case "简单选择排序":
                    currentSortingAlgorithm = new SelectionSort();
                    break;
                case "快速排序":
                    currentSortingAlgorithm = new QuickSort();
                    break;
                default:
                    showAlert("错误", "未知的排序算法");
                    return;
            }
            
            // 执行排序并获取步骤
            currentSortingSteps = currentSortingAlgorithm.sort(array);
            currentStepIndex = 0;
            
            // 开始动画
            startSortingAnimation();
            
        } catch (NumberFormatException e) {
            showAlert("错误", "请输入有效的数组");
        }
    }
    
    private void startSortingAnimation() {
        if (sortingAnimation != null) {
            sortingAnimation.stop();
        }
        
        double speed = sliderToMillis();
        sortingAnimation = new Timeline(new KeyFrame(
            Duration.millis(speed),
            e -> {
                if (currentStepIndex < currentSortingSteps.size()) {
                    SortingStep step = currentSortingSteps.get(currentStepIndex);
                    sortingVisualizationPane.showStep(step);
                    currentStepIndex++;
                } else {
                    sortingAnimation.stop();
                    isAnimationRunning = false;
                    startSortingButton.setText("开始排序");
                }
            }
        ));
        
        sortingAnimation.setCycleCount(Timeline.INDEFINITE);
        sortingAnimation.play();
        isAnimationRunning = true;
        startSortingButton.setText("重新开始");
    }
    
    @FXML
    private void pauseSorting() {
        // 如果当前在图标签页，则控制图动画的暂停/继续
        Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
        if (selected == graphTab) {
            if (graphAnimation != null) {
                if (isGraphAnimationRunning) {
                    graphAnimation.pause();
                    pauseButton.setText("继续");
                    isGraphAnimationRunning = false;
                } else {
                    graphAnimation.play();
                    pauseButton.setText("暂停");
                    isGraphAnimationRunning = true;
                }
            }
            return;
        }

        // 否则控制排序动画
        if (sortingAnimation != null) {
            if (isAnimationRunning) {
                sortingAnimation.pause();
                pauseButton.setText("继续");
                isAnimationRunning = false;
            } else {
                sortingAnimation.play();
                pauseButton.setText("暂停");
                isAnimationRunning = true;
            }
        }
    }
    
    @FXML
    private void resetSorting() {
        // 如果当前在图标签页，重置图动画与高亮
        Tab selected = mainTabPane.getSelectionModel().getSelectedItem();
        if (selected == graphTab) {
            if (graphAnimation != null) graphAnimation.stop();
            isGraphAnimationRunning = false;
            pauseButton.setText("暂停");
            // 清除图高亮
            if (graphVisualizationPane != null) graphVisualizationPane.clearHighlights();
            return;
        }

        // 否则重置排序动画
        if (sortingAnimation != null) {
            sortingAnimation.stop();
        }
        currentStepIndex = 0;
        isAnimationRunning = false;
        startSortingButton.setText("开始排序");
        pauseButton.setText("暂停");

        // 重置可视化
        String input = arrayInputField.getText().trim();
        if (!input.isEmpty()) {
            setArray();
        }
    }
    
    // 文件操作方法
    @FXML
    private void saveData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存数据");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();
                if (selectedTab == graphTab && currentGraph != null) {
                    // 保存图数据
                    DataSerializer.saveGraph(currentGraph, file);
                    showAlert("成功", "图数据已保存到: " + file.getName());
                } else if (selectedTab == sortingTab) {
                    // 保存排序数据
                    String input = arrayInputField.getText().trim();
                    if (!input.isEmpty()) {
                        String[] parts = input.split("[,\\s]+");
                        int[] array = new int[parts.length];
                        for (int i = 0; i < parts.length; i++) {
                            array[i] = Integer.parseInt(parts[i]);
                        }
                        String algorithmName = sortingAlgorithmCombo.getValue();
                        DataSerializer.saveSortingData(array, algorithmName, file);
                        showAlert("成功", "排序数据已保存到: " + file.getName());
                    } else {
                        showAlert("错误", "没有可保存的排序数据");
                    }
                } else {
                    showAlert("错误", "没有可保存的数据");
                }
            } catch (Exception e) {
                showAlert("错误", "保存失败: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void loadData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("加载数据");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JSON文件", "*.json"),
            new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String fileType = DataSerializer.getFileType(file);
                
                if ("graph".equals(fileType)) {
                    // 加载图数据
                    currentGraph = DataSerializer.loadGraph(file);
                    
                    // 更新UI
                    numVerticesField.setText(String.valueOf(currentGraph.getNumVertices()));
                    graphTypeCombo.setValue(currentGraph instanceof AdjacencyMatrix ? "邻接矩阵" : "邻接表");
                    if (directedCheckBox != null) {
                        directedCheckBox.setSelected(currentGraph.isDirected());
                    }
                    
                    // 切换到图标签页
                    mainTabPane.getSelectionModel().select(graphTab);
                    
                    // 更新可视化
                    graphVisualizationPane.setGraph(currentGraph);
                    updateGraphInfo();
                    lastSelectedVertex = -1;
                    
                    showAlert("成功", "图数据已加载: " + file.getName());
                    
                } else if ("sorting".equals(fileType)) {
                    // 加载排序数据
                    DataSerializer.SortingData sortingData = DataSerializer.loadSortingData(file);
                    
                    // 更新UI
                    int[] array = sortingData.getArray();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < array.length; i++) {
                        sb.append(array[i]);
                        if (i < array.length - 1) sb.append(", ");
                    }
                    arrayInputField.setText(sb.toString());
                    sortingAlgorithmCombo.setValue(sortingData.getAlgorithmName());
                    
                    // 切换到排序标签页
                    mainTabPane.getSelectionModel().select(sortingTab);
                    
                    // 更新可视化
                    sortingVisualizationPane.setArray(array);
                    updateAlgorithmInfo();
                    
                    showAlert("成功", "排序数据已加载: " + file.getName());
                    
                } else {
                    showAlert("错误", "不支持的文件类型: " + fileType);
                }
                
            } catch (Exception e) {
                showAlert("错误", "加载失败: " + e.getMessage());
            }
        }
    }
    
    // DSL 处理: 从文本解析图并加载到可视化
    @FXML
    private void handleLoadDsl() {
        if (dslInput == null) return;
        String script = dslInput.getText();
        if (script == null || script.trim().isEmpty()) {
            showAlert("错误", "DSL 输入不能为空");
            return;
        }
        String trimmed = script.stripLeading();
        if (trimmed.toLowerCase().startsWith("graph")) {
            // 旧版 graph { } 块语法
            try {
                Graph g = com.datastruct.visualizer.util.DslParser.parseGraph(trimmed);
                currentGraph = g;
                graphVisualizationPane.setGraph(g);
                updateGraphInfo();
                showAlert("成功", "已根据 DSL 创建/替换图");
            } catch (IllegalArgumentException ex) {
                showAlert("DSL 错误", ex.getMessage());
            }
            return;
        }
        // 行指令模式
        try {
            java.util.List<DslCommand> cmds = com.datastruct.visualizer.util.DslParser.parseCommands(script);
            executeCommands(cmds);
        } catch (IllegalArgumentException ex) {
            showAlert("DSL 错误", ex.getMessage());
        }
    }

    private void executeCommands(java.util.List<DslCommand> cmds) {
        for (DslCommand cmd : cmds) {
            try {
                switch (cmd.type) {
                    case ADD_VERTEX -> {
                        ensureGraph();
                        int id = Integer.parseInt(cmd.args[0]);
                        if (id < 0 || id >= currentGraph.getNumVertices()) {
                            showAlert("错误", "顶点索引超出范围: " + id);
                            return;
                        }
                        // 标签
                        if (cmd.args.length > 1) currentGraph.setVertexLabel(id, cmd.args[1]);
                        graphVisualizationPane.redraw();
                    }
                    case REMOVE_VERTEX -> {
                        showAlert("提示", "暂不支持删除顶点（图顶点数固定）");
                    }
                    case ADD_EDGE -> {
                        ensureGraph();
                        int u = Integer.parseInt(cmd.args[0]);
                        int v = Integer.parseInt(cmd.args[1]);
                        double w = Double.parseDouble(cmd.args[2]);
                        currentGraph.addEdge(u, v, w);
                        graphVisualizationPane.redraw();
                    }
                    case REMOVE_EDGE -> {
                        ensureGraph();
                        int u = Integer.parseInt(cmd.args[0]);
                        int v = Integer.parseInt(cmd.args[1]);
                        currentGraph.removeEdge(u, v);
                        graphVisualizationPane.redraw();
                    }
                    case SET_LABEL -> {
                        ensureGraph();
                        int id = Integer.parseInt(cmd.args[0]);
                        currentGraph.setVertexLabel(id, cmd.args[1]);
                        graphVisualizationPane.redraw();
                    }
                    case SET_DIRECTED -> {
                        showAlert("提示", "暂不支持运行时切换有向/无向，请创建新图");
                    }
                    case RUN_DFS -> {
                        graphAlgorithmCombo.setValue("深度优先搜索");
                        startVertexField.setText(cmd.args[0]);
                        runGraphAlgorithm();
                    }
                    case RUN_BFS -> {
                        graphAlgorithmCombo.setValue("广度优先搜索");
                        startVertexField.setText(cmd.args[0]);
                        runGraphAlgorithm();
                    }
                    case RUN_DIJKSTRA -> {
                        graphAlgorithmCombo.setValue("最短路径生成");
                        startVertexField.setText(cmd.args[0]);
                        targetVertexField.setText(cmd.args[1]);
                        runGraphAlgorithm();
                    }
                    case RUN_MST -> {
                        graphAlgorithmCombo.setValue("最小生成树(Kruskal)");
                        runGraphAlgorithm();
                    }
                }
            } catch (Exception e) {
                showAlert("执行错误", "第" + cmd.line + "行: " + e.getMessage());
                return;
            }
        }
    }

    private void ensureGraph() {
        if (currentGraph == null) {
            showAlert("错误", "请先创建图");
            throw new IllegalStateException();
        }
    }

    @FXML
    private void clearDsl() {
        if (dslInput != null) dslInput.clear();
    }
    
    // 辅助方法
    private void updateGraphInfo() {
        updateGraphInfo(null);
    }
    
    private void updateGraphInfo(String additionalInfo) {
        if (currentGraph == null) {
            graphInfoArea.setText("尚未创建图");
            return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("图信息:\\n");
        info.append("类型: ").append(currentGraph instanceof AdjacencyMatrix ? "邻接矩阵" : "邻接表").append("\\n");
        info.append("顶点数: ").append(currentGraph.getNumVertices()).append("\\n");
        info.append("是否有向: ").append(currentGraph.isDirected() ? "是" : "否").append("\\n");
        info.append("边数: ").append(currentGraph.getAllEdges().size()).append("\\n");
        
        if (additionalInfo != null) {
            info.append("\\n").append(additionalInfo);
        }
        
        graphInfoArea.setText(info.toString());
    }
    
    private void updateAlgorithmInfo() {
        String algorithmName = sortingAlgorithmCombo.getValue();
        if (algorithmName == null) return;
        
        SortingAlgorithm algorithm;
        switch (algorithmName) {
            case "直接插入排序":
                algorithm = new InsertionSort();
                break;
            case "简单选择排序":
                algorithm = new SelectionSort();
                break;
            case "快速排序":
                algorithm = new QuickSort();
                break;
            default:
                return;
        }
        
        StringBuilder info = new StringBuilder();
        info.append("算法: ").append(algorithm.getAlgorithmName()).append("\\n\\n");
        info.append("描述: ").append(algorithm.getDescription()).append("\\n\\n");
        info.append("时间复杂度: ").append(algorithm.getTimeComplexity()).append("\\n");
        info.append("空间复杂度: ").append(algorithm.getSpaceComplexity());
        
        algorithmInfoArea.setText(info.toString());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // (已移除) snapshotOfPQ 方法：侧边栏已被移除，不再需要该快照函数


    /**
     * 处理图面板上的顶点点击交互：
     * - 第一次点击选中顶点（高亮）
     * - 第二次点击与第一次不同顶点时，尝试在两顶点之间添加或删除一条边（根据当前是否存在边来切换）
     */
    private void handleVertexClick(Integer vertex) {
        if (currentGraph == null) return;

        // 如果尚未选中任何顶点，则选中当前顶点
        if (lastSelectedVertex == -1) {
            lastSelectedVertex = vertex;
            graphVisualizationPane.clearHighlights();
            graphVisualizationPane.highlightVertex(vertex);
            updateGraphInfo("已选中顶点: " + vertex + "，请点击另一个顶点以添加/删除边");
            return;
        }

        // 再次点击同一顶点 -> 取消选择
        if (lastSelectedVertex == vertex) {
            lastSelectedVertex = -1;
            graphVisualizationPane.clearHighlights();
            updateGraphInfo();
            return;
        }

        // 两次点击不同顶点：切换边（存在则删除，否则添加）
        int u = lastSelectedVertex;
        int v = vertex;

        try {
            double weight = 1.0;
            try {
                String wtxt = edgeWeightField.getText();
                if (wtxt != null && !wtxt.trim().isEmpty()) {
                    weight = Double.parseDouble(wtxt.trim());
                }
            } catch (NumberFormatException ignored) {
                // 使用默认权重 1.0
            }

            if (currentGraph.hasEdge(u, v)) {
                currentGraph.removeEdge(u, v);
                updateGraphInfo("已删除边: " + u + " -> " + v);
            } else {
                currentGraph.addEdge(u, v, weight);
                updateGraphInfo("已添加边: " + u + " -> " + v + " (权重=" + weight + ")");
            }

            graphVisualizationPane.clearHighlights();
            graphVisualizationPane.redraw();
        } catch (IllegalArgumentException ex) {
            showAlert("错误", "操作失败: " + ex.getMessage());
        } finally {
            lastSelectedVertex = -1;
        }
    }
}
