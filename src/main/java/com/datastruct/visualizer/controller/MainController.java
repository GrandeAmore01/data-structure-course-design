package com.datastruct.visualizer.controller;

import com.datastruct.visualizer.model.graph.*;
import com.datastruct.visualizer.model.sorting.*;
import com.datastruct.visualizer.view.GraphVisualizationPane;
import com.datastruct.visualizer.view.SortingVisualizationPane;
import com.datastruct.visualizer.util.DataSerializer;
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
    
    // 图相关控件
    @FXML private VBox graphContainer;
    @FXML private ComboBox<String> graphTypeCombo;
    @FXML private CheckBox directedCheckBox;
    @FXML private TextField numVerticesField;
    @FXML private Button createGraphButton;
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
    @FXML private TextArea algorithmInfoArea;
    
    // 文件操作控件
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem loadMenuItem;
    
    // 可视化面板
    private GraphVisualizationPane graphVisualizationPane;
    private SortingVisualizationPane sortingVisualizationPane;
    
    // 数据模型
    private Graph currentGraph;
    private SortingAlgorithm currentSortingAlgorithm;
    private List<SortingStep> currentSortingSteps;
    private int currentStepIndex;
    
    // 动画控制
    private Timeline sortingAnimation;
    private boolean isAnimationRunning = false;
    
    private Stage stage;

    // 点击交互：记录上一次被选中的顶点索引（用于点击两次建立/删除边）
    private int lastSelectedVertex = -1;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        setupEventHandlers();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void setupUI() {
        // 创建可视化面板
        graphVisualizationPane = new GraphVisualizationPane();
        sortingVisualizationPane = new SortingVisualizationPane();
        
        // 添加到容器
        graphContainer.getChildren().add(graphVisualizationPane);
        sortingContainer.getChildren().add(sortingVisualizationPane);

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
        
        // 初始化信息区域
        updateGraphInfo();
        updateAlgorithmInfo();
    }
    
    private void setupEventHandlers() {
        createGraphButton.setOnAction(e -> createGraph());
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
                            // Dijkstra
                            MST.ShortestPathResult res = MST.dijkstra(currentGraph, startVertex);
                            List<Integer> path = res.getPath(targetVertex);
                            if (path.isEmpty()) {
                                showAlert("信息", "从 " + startVertex + " 到 " + targetVertex + " 不可达");
                                return;
                            }

                            animatePath(path, "Dijkstra", res.getDistances()[targetVertex]);
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
        
        Timeline timeline = new Timeline();
        for (int i = 0; i < vertices.size(); i++) {
            final int vertex = vertices.get(i);
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(i * 1000),
                e -> {
                    graphVisualizationPane.highlightVertex(vertex);
                    updateGraphInfo(algorithmName + " 遍历: 访问顶点 " + vertex);
                }
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.play();
    }

    /**
     * 可视化显示一条最短路径：按顺序高亮顶点与对应的边，并在信息区显示算法名称和总距离
     */
    private void animatePath(List<Integer> vertices, String algorithmName, double totalDistance) {
        if (vertices == null || vertices.isEmpty()) return;

        // 清除已有高亮
        graphVisualizationPane.clearHighlights();

        Timeline timeline = new Timeline();
        double stepMillis = 1000; // 固定步长，若需要可改为 speedSlider.getValue()

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

        // 不做额外的 onFinished 转换，直接播放并保持橙色
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
        double stepMillis = 800;
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
        });

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
        
        double speed = speedSlider.getValue();
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
