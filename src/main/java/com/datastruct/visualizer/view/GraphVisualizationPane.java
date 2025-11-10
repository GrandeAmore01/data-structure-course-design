package com.datastruct.visualizer.view;

import com.datastruct.visualizer.model.graph.Edge;
import com.datastruct.visualizer.model.graph.Graph;
import java.util.function.Consumer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;

/**
 * 图可视化面板
 * Graph Visualization Pane
 */
public class GraphVisualizationPane extends Pane {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private Graph graph;
    
    // 顶点位置信息
    private Map<Integer, VertexPosition> vertexPositions;
    
    // 可视化配置
    private static final double VERTEX_RADIUS = 25;
    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;
    
    // 颜色配置
    private Color vertexColor = Color.LIGHTBLUE;
    private Color edgeColor = Color.BLACK;
    private Color highlightColor = Color.RED;
    // 最小生成树（MST）可视化使用的颜色
    private Color consideredEdgeColor = Color.ORANGE; // 被考虑的边
    private Color acceptedEdgeColor = Color.LIMEGREEN; // 被接受的边
    private Color textColor = Color.BLACK;
    
    // 高亮状态
    private Set<Integer> highlightedVertices;
    private Set<Edge> highlightedEdges;
    // 专门用于表示算法过程中的边状态（使用端点键避免对象实例/浮点比较问题）
    private Set<String> consideredEdges;
    private Set<String> acceptedEdges;
    // 候选路径（在算法执行过程中发现的可达目标的当前最短候选路径）——这些边将以 highlightColor (红色) 显示
    private Set<Edge> candidateHighlightedEdges;
    // 候选路径的键集合（用于绘制时优先级判断）
    private Set<String> candidateEdgeKeys;
    // 在 Kruskal 动画中：已被接受但尚未在结尾转换为绿色的边（应保持橙色）
    private Set<String> pendingAcceptedEdges;
    // 顶点点击回调（如果设置，点击顶点时会调用）
    private Consumer<Integer> vertexClickHandler;
    
    public GraphVisualizationPane() {
        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.vertexPositions = new HashMap<>();
        this.highlightedVertices = new HashSet<>();
        this.highlightedEdges = new HashSet<>();
    this.consideredEdges = new HashSet<>();
    this.acceptedEdges = new HashSet<>();
    this.pendingAcceptedEdges = new HashSet<>();
    this.candidateHighlightedEdges = new HashSet<>();
    this.candidateEdgeKeys = new HashSet<>();
        
        setupCanvas();
        getChildren().add(canvas);
    }
    
    private void setupCanvas() {
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
    }
    
    /**
     * 设置要显示的图
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
        generateVertexPositions();
        redraw();
    }
    
    /**
     * 生成顶点位置（圆形布局）
     */
    private void generateVertexPositions() {
        if (graph == null) return;
        
        vertexPositions.clear();
        int numVertices = graph.getNumVertices();
        double centerX = CANVAS_WIDTH / 2;
        double centerY = CANVAS_HEIGHT / 2;
        double radius = Math.min(centerX, centerY) - VERTEX_RADIUS - 50;
        
        if (numVertices == 1) {
            vertexPositions.put(0, new VertexPosition(centerX, centerY));
        } else {
            for (int i = 0; i < numVertices; i++) {
                double angle = 2 * Math.PI * i / numVertices - Math.PI / 2; // 从顶部开始
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);
                vertexPositions.put(i, new VertexPosition(x, y));
            }
        }
    }
    
    /**
     * 重绘图形
     */
    public void redraw() {
        if (graph == null) return;
        
        // 清空画布
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // 绘制边
        drawEdges();
        
        // 绘制顶点
        drawVertices();
    }
    
    /**
     * 绘制边
     */
    private void drawEdges() {
        gc.setLineWidth(2);
        gc.setFont(Font.font(12));
        
        for (Edge edge : graph.getAllEdges()) {
            VertexPosition sourcePos = vertexPositions.get(edge.getSource());
            VertexPosition destPos = vertexPositions.get(edge.getDestination());
            
            if (sourcePos == null || destPos == null) continue;
            
            // 设置边的颜色
            String key = edgeKey(edge);
            if (acceptedEdges.contains(key)) {
                gc.setStroke(acceptedEdgeColor);
            } else if (candidateEdgeKeys.contains(key)) {
                // 候选路径优先于被考虑的橙色显示，显示为红色
                gc.setStroke(highlightColor);
            } else if (consideredEdges.contains(key)) {
                gc.setStroke(consideredEdgeColor);
            } else if (highlightedEdges.contains(edge)) {
                gc.setStroke(highlightColor);
            } else {
                gc.setStroke(edgeColor);
            }
            
            // 计算边的起点和终点（避免与顶点重叠）
            double[] startPoint = calculateEdgePoint(sourcePos, destPos, VERTEX_RADIUS);
            double[] endPoint = calculateEdgePoint(destPos, sourcePos, VERTEX_RADIUS);
            
            // 绘制边
            gc.strokeLine(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
            
            // 如果是有向图，绘制箭头
            if (graph.isDirected()) {
                drawArrow(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
            }
            
            // 绘制权重
            if (edge.getWeight() != 1.0) {
                double midX = (startPoint[0] + endPoint[0]) / 2;
                double midY = (startPoint[1] + endPoint[1]) / 2;
                
                gc.setFill(Color.WHITE);
                gc.fillOval(midX - 15, midY - 8, 30, 16);
                gc.setFill(textColor);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.format("%.1f", edge.getWeight()), midX, midY + 4);
            }
        }
    }
    
    /**
     * 绘制顶点
     */
    private void drawVertices() {
        gc.setFont(Font.font(14));
        gc.setTextAlign(TextAlignment.CENTER);
        
        for (int i = 0; i < graph.getNumVertices(); i++) {
            VertexPosition pos = vertexPositions.get(i);
            if (pos == null) continue;
            
            // 设置顶点颜色
            if (highlightedVertices.contains(i)) {
                gc.setFill(highlightColor);
            } else {
                gc.setFill(vertexColor);
            }
            
            // 绘制顶点圆圈
            gc.fillOval(pos.x - VERTEX_RADIUS, pos.y - VERTEX_RADIUS, 
                       VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);
            
            // 绘制顶点边框
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(pos.x - VERTEX_RADIUS, pos.y - VERTEX_RADIUS, 
                         VERTEX_RADIUS * 2, VERTEX_RADIUS * 2);
            
            // 绘制顶点标签
            gc.setFill(textColor);
            gc.fillText(graph.getVertexLabel(i), pos.x, pos.y + 5);
        }
    }
    
    /**
     * 计算边与顶点圆圈的交点
     */
    private double[] calculateEdgePoint(VertexPosition from, VertexPosition to, double radius) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return new double[]{from.x, from.y};
        
        double ratio = radius / distance;
        return new double[]{
            from.x + dx * ratio,
            from.y + dy * ratio
        };
    }
    
    /**
     * 绘制箭头
     */
    private void drawArrow(double x1, double y1, double x2, double y2) {
        double arrowLength = 15;
        double arrowAngle = Math.PI / 6;
        
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
        double x3 = x2 - arrowLength * Math.cos(angle - arrowAngle);
        double y3 = y2 - arrowLength * Math.sin(angle - arrowAngle);
        
        double x4 = x2 - arrowLength * Math.cos(angle + arrowAngle);
        double y4 = y2 - arrowLength * Math.sin(angle + arrowAngle);
        
        gc.strokeLine(x2, y2, x3, y3);
        gc.strokeLine(x2, y2, x4, y4);
    }
    
    /**
     * 处理鼠标点击事件
     */
    private void handleMouseClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        // 检查是否点击了某个顶点
        for (Map.Entry<Integer, VertexPosition> entry : vertexPositions.entrySet()) {
            VertexPosition pos = entry.getValue();
            double distance = Math.sqrt(Math.pow(x - pos.x, 2) + Math.pow(y - pos.y, 2));
            
            if (distance <= VERTEX_RADIUS) {
                // 优先调用注册的处理器
                if (vertexClickHandler != null) {
                    vertexClickHandler.accept(entry.getKey());
                }

                // 仍然保留对默认行为的回调钩子
                onVertexClicked(entry.getKey());
                break;
            }
        }
    }

    /**
     * 注册顶点点击处理回调（可为 null 表示移除）
     */
    public void setOnVertexClickedHandler(Consumer<Integer> handler) {
        this.vertexClickHandler = handler;
    }
    
    /**
     * 顶点点击事件处理
     */
    protected void onVertexClicked(int vertex) {
        // 子类可以重写此方法
        System.out.println("顶点 " + vertex + " 被点击");
    }
    
    /**
     * 高亮顶点
     */
    public void highlightVertex(int vertex) {
        highlightedVertices.add(vertex);
        redraw();
    }
    
    /**
     * 取消高亮顶点
     */
    public void unhighlightVertex(int vertex) {
        highlightedVertices.remove(vertex);
        redraw();
    }
    
    /**
     * 高亮边
     */
    public void highlightEdge(Edge edge) {
        highlightedEdges.add(edge);
        redraw();
    }
    
    /**
     * 取消高亮边
     */
    public void unhighlightEdge(Edge edge) {
        highlightedEdges.remove(edge);
        redraw();
    }

    /**
     * 将一组边标记为“候选到目标路径”（红色），并清除之前的候选路径高亮。
     */
    public void markCandidatePathEdges(List<Edge> edges) {
        if (graph == null) return;

        // 清除之前的候选高亮
        clearCandidatePathEdges();

        if (edges == null || edges.isEmpty()) return;

        for (Edge e : edges) {
            // 在当前图中寻找对应的 Edge 实例以保证绘制一致
            Edge matched = null;
            for (Edge ge : graph.getAllEdges()) {
                int a = ge.getSource();
                int b = ge.getDestination();
                int ea = e.getSource();
                int eb = e.getDestination();

                if (graph.isDirected()) {
                    if (a == ea && b == eb) { matched = ge; break; }
                } else {
                    if ((a == ea && b == eb) || (a == eb && b == ea)) { matched = ge; break; }
                }
            }

            if (matched != null) {
                String k = edgeKey(matched);
                // 从 considered 中移除，保证候选路径显示为红色（优先于橙色）
                consideredEdges.remove(k);
                highlightedEdges.add(matched);
                candidateHighlightedEdges.add(matched);
                candidateEdgeKeys.add(k);
            } else {
                String k = edgeKey(e);
                consideredEdges.remove(k);
                highlightedEdges.add(e);
                candidateHighlightedEdges.add(e);
                candidateEdgeKeys.add(k);
            }
        }

        redraw();
    }

    /**
     * 清除候选路径（红色）高亮，仅清除由 markCandidatePathEdges 添加的那部分。
     */
    public void clearCandidatePathEdges() {
        if (candidateHighlightedEdges == null || candidateHighlightedEdges.isEmpty()) return;
        for (Edge e : new ArrayList<>(candidateHighlightedEdges)) {
            highlightedEdges.remove(e);
        }
        candidateHighlightedEdges.clear();
        candidateEdgeKeys.clear();
        redraw();
    }

    /**
     * 标记为正在被考虑（短暂高亮）
     */
    public void highlightConsideredEdge(Edge edge) {
        if (edge == null) return;
        consideredEdges.add(edgeKey(edge));
        redraw();
    }

    public void unhighlightConsideredEdge(Edge edge) {
        if (edge == null) return;
        // 如果此边已被标记为 pendingAccepted，则不应被取消橙色显示
        String key = edgeKey(edge);
        if (pendingAcceptedEdges.contains(key)) return;
        // 如果此边属于候选路径，则不要从 considered 中移除（候选路径以红色优先显示，并由候选集合管理）
        if (candidateEdgeKeys.contains(key)) return;

        consideredEdges.remove(key);
        redraw();
    }

    /**
     * 将边标记为已接受（MST的一部分），会持续高亮
     */
    public void acceptEdge(Edge edge) {
        if (edge == null) return;
        String key = edgeKey(edge);
        // 移除任何 pending 标记（如果存在），并将边标记为最终 accepted
        pendingAcceptedEdges.remove(key);
        acceptedEdges.add(key);
        // 也从 considered 中移除（如果存在）
        consideredEdges.remove(key);
        // 如果之前是候选路径（红色），把候选记录移除
        candidateEdgeKeys.remove(key);
        // 同时从 candidateHighlightedEdges 中移除对应实例
        if (candidateHighlightedEdges != null && !candidateHighlightedEdges.isEmpty()) {
            candidateHighlightedEdges.removeIf(e -> edgeKey(e).equals(key));
        }
        redraw();
    }

    public void unacceptEdge(Edge edge) {
        if (edge == null) return;
        String key = edgeKey(edge);
        acceptedEdges.remove(key);
        // 同时移除可能的 pending 标记
        pendingAcceptedEdges.remove(key);
        redraw();
    }
    
    /**
     * 清除所有高亮
     */
    public void clearHighlights() {
        highlightedVertices.clear();
        highlightedEdges.clear();
        consideredEdges.clear();
        acceptedEdges.clear();
        pendingAcceptedEdges.clear();
        if (candidateHighlightedEdges != null) candidateHighlightedEdges.clear();
        if (candidateEdgeKeys != null) candidateEdgeKeys.clear();
        redraw();
    }

    /**
     * 在 Kruskal 动画中标记某条边为已被接受但尚未转换为绿色（应保持橙色显示）
     */
    public void markPendingAcceptedEdge(Edge edge) {
        if (edge == null) return;
        String key = edgeKey(edge);
        // 将其放入 considered（橙色）集合，同时标记为 pending，这样后续的 unhighlight 操作不会移除它
        consideredEdges.add(key);
        pendingAcceptedEdges.add(key);
        redraw();
    }

    /**
     * 将路径上的临时被考虑的边（橙色）转换为最终高亮（使用内部 graph 中的 Edge 实例以保证 equals/hash 一致）
     * 这样可以避免因为不同 Edge 实例导致的不匹配问题。
     */
    public void finalizePathEdges(List<Edge> edges) {
        if (edges == null || graph == null) return;

        for (Edge e : edges) {
            // 移除临时 considered 标记
            consideredEdges.remove(edgeKey(e));

            // 在当前图中寻找对应的 Edge 实例（优先使用图内的实例以保证绘制匹配）
            Edge matched = null;
            for (Edge ge : graph.getAllEdges()) {
                int a = ge.getSource();
                int b = ge.getDestination();
                int ea = e.getSource();
                int eb = e.getDestination();

                if (graph.isDirected()) {
                    if (a == ea && b == eb) { matched = ge; break; }
                } else {
                    // 无向图，允许端点顺序互换匹配
                    if ((a == ea && b == eb) || (a == eb && b == ea)) { matched = ge; break; }
                }
            }

            if (matched != null) {
                highlightedEdges.add(matched);
            } else {
                // 回退：使用传入的实例（尽管可能导致 equals/hash 不同）
                highlightedEdges.add(e);
            }
        }

        redraw();
    }

    // 辅助：生成边的唯一键（无向图保持顺序一致）
    private String edgeKey(Edge e) {
        if (e == null) return "";
        int a = e.getSource();
        int b = e.getDestination();
        // 对于无向图，保证较小索引在前以便统一键名
        if (!graph.isDirected() && a > b) {
            int tmp = a; a = b; b = tmp;
        }
        return a + "#" + b;
    }

    /**
     * 获取当前高亮颜色（用于在控制器中临时替换并恢复）
     */
    public Color getHighlightColor() {
        return highlightColor;
    }

    /**
     * 设置高亮颜色并重绘。
     */
    public void setHighlightColor(Color color) {
        if (color == null) return;
        this.highlightColor = color;
        redraw();
    }
    
    /**
     * 顶点位置内部类
     */
    private static class VertexPosition {
        double x, y;
        
        VertexPosition(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}

