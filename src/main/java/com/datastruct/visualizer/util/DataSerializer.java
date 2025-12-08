package com.datastruct.visualizer.util;

import com.datastruct.visualizer.model.graph.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.BufferedWriter;

/**
 * 数据序列化工具类
 * Data serialization utility class
 */
public class DataSerializer {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 保存图数据到文件
     */
    public static void saveGraph(Graph graph, File file) throws IOException {
        ObjectNode rootNode = objectMapper.createObjectNode();
        
        // 基本信息
        rootNode.put("type", "graph");
        rootNode.put("implementation", graph instanceof AdjacencyMatrix ? "matrix" : "list");
        rootNode.put("numVertices", graph.getNumVertices());
        rootNode.put("isDirected", graph.isDirected());
        
        // 顶点标签
        ObjectNode labelsNode = objectMapper.createObjectNode();
        Map<Integer, String> labels = graph.getAllVertexLabels();
        for (Map.Entry<Integer, String> entry : labels.entrySet()) {
            labelsNode.put(entry.getKey().toString(), entry.getValue());
        }
        rootNode.set("vertexLabels", labelsNode);
        
        // 边信息
        ArrayNode edgesNode = objectMapper.createArrayNode();
        List<Edge> edges = graph.getAllEdges();
        for (Edge edge : edges) {
            ObjectNode edgeNode = objectMapper.createObjectNode();
            edgeNode.put("source", edge.getSource());
            edgeNode.put("destination", edge.getDestination());
            edgeNode.put("weight", edge.getWeight());
            edgesNode.add(edgeNode);
        }
        rootNode.set("edges", edgesNode);
        
        // 写入文件
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
    }
    
    /**
     * 从文件加载图数据
     */
    public static Graph loadGraph(File file) throws IOException {
        JsonNode rootNode = objectMapper.readTree(file);
        
        // 验证类型
        if (!"graph".equals(rootNode.get("type").asText())) {
            throw new IllegalArgumentException("文件格式不正确：不是图数据文件");
        }
        
        // 读取基本信息
        String implementation = rootNode.get("implementation").asText();
        int numVertices = rootNode.get("numVertices").asInt();
        boolean isDirected = rootNode.get("isDirected").asBoolean();
        
        // 创建图对象
        Graph graph;
        if ("matrix".equals(implementation)) {
            graph = new AdjacencyMatrix(numVertices, isDirected);
        } else {
            graph = new AdjacencyList(numVertices, isDirected);
        }
        
        // 设置顶点标签
        JsonNode labelsNode = rootNode.get("vertexLabels");
        if (labelsNode != null) {
            labelsNode.fields().forEachRemaining(entry -> {
                int vertex = Integer.parseInt(entry.getKey());
                String label = entry.getValue().asText();
                graph.setVertexLabel(vertex, label);
            });
        }
        
        // 添加边
        JsonNode edgesNode = rootNode.get("edges");
        if (edgesNode != null && edgesNode.isArray()) {
            for (JsonNode edgeNode : edgesNode) {
                int source = edgeNode.get("source").asInt();
                int destination = edgeNode.get("destination").asInt();
                double weight = edgeNode.get("weight").asDouble();
                graph.addEdge(source, destination, weight);
            }
        }

        // 保证加载后的标签与索引一致
        graph.resetVertexLabelsToIndex();
        
        return graph;
    }
    
    /**
     * 保存排序数据到文件
     */
    public static void saveSortingData(int[] array, String algorithmName, File file) throws IOException {
        ObjectNode rootNode = objectMapper.createObjectNode();
        
        rootNode.put("type", "sorting");
        rootNode.put("algorithm", algorithmName);
        
        // 数组数据
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (int value : array) {
            arrayNode.add(value);
        }
        rootNode.set("array", arrayNode);
        
        // 时间戳
        rootNode.put("timestamp", System.currentTimeMillis());
        
        // 写入文件
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
    }
    
    /**
     * 从文件加载排序数据
     */
    public static SortingData loadSortingData(File file) throws IOException {
        JsonNode rootNode = objectMapper.readTree(file);
        
        // 验证类型
        if (!"sorting".equals(rootNode.get("type").asText())) {
            throw new IllegalArgumentException("文件格式不正确：不是排序数据文件");
        }
        
        String algorithm = rootNode.get("algorithm").asText();
        
        // 读取数组
        JsonNode arrayNode = rootNode.get("array");
        int[] array = new int[arrayNode.size()];
        for (int i = 0; i < arrayNode.size(); i++) {
            array[i] = arrayNode.get(i).asInt();
        }
        
        return new SortingData(array, algorithm);
    }

    /**
     * 将图转换为 Mermaid 风格的描述字符串
     * @param graph 要转换的图
     * @param leftToRight true 则使用 LR（从左到右），否则使用 TD（从上到下）
     * @return Mermaid 文本
     */
    public static String graphToMermaid(Graph graph, boolean leftToRight) {
        if (graph == null) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("%% Generated by DataSerializer\n");
        sb.append("%% type: graph, implementation: ")
          .append(graph instanceof AdjacencyMatrix ? "matrix" : "list")
          .append("\n");

        sb.append(leftToRight ? "graph LR\n" : "graph TD\n");

        // 节点定义
        Map<Integer, String> labels = graph.getAllVertexLabels();
        for (int i = 0; i < graph.getNumVertices(); i++) {
            String lbl = labels.getOrDefault(i, String.valueOf(i));
            // 转义双引号和方括号
            lbl = lbl.replace("\"", "\\\"").replace("[", "(").replace("]", ")");
            sb.append("v").append(i).append("[")
              .append('\"').append(lbl).append('\"').append("]\n");
        }

        // 边
        for (Edge e : graph.getAllEdges()) {
            if (e == null) continue;
            int a = e.getSource();
            int b = e.getDestination();
            String arrow = graph.isDirected() ? " --> " : " -- ";
            String weightLabel = "";
            if (Math.abs(e.getWeight() - 1.0) > 1e-9) {
                weightLabel = "|" + String.format("%.2f", e.getWeight()) + "|";
            }
            sb.append("v").append(a).append(arrow).append(weightLabel).append("v").append(b).append("\n");
        }

        return sb.toString();
    }

    /**
     * 将数组（排序）转换为 Mermaid 风格的线性图表示
     * 例如: graph LR\n a0["5"] --> a1["2"] --> a2["9"]
     */
    public static String sortingArrayToMermaid(int[] array, String algorithmName) {
        if (array == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("%% Generated by DataSerializer\n");
        if (algorithmName != null && !algorithmName.isEmpty()) {
            sb.append("%% algorithm: ").append(algorithmName).append("\n");
        }
        sb.append("graph LR\n");

        for (int i = 0; i < array.length; i++) {
            String nodeId = "a" + i;
            sb.append(nodeId).append("[")
              .append('\"').append(array[i]).append('\"').append("]\n");
        }

        // 连线
        for (int i = 0; i < array.length - 1; i++) {
            sb.append("a").append(i).append(" --> a").append(i + 1).append("\n");
        }

        return sb.toString();
    }

    /**
     * 将 Mermaid 文本保存为文件（纯文本）
     */
    public static void saveMermaidToFile(String mermaidText, File file) throws IOException {
        if (mermaidText == null) mermaidText = "";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(mermaidText);
        }
    }
    
    /**
     * 排序数据容器类
     */
    public static class SortingData {
        private final int[] array;
        private final String algorithmName;
        
        public SortingData(int[] array, String algorithmName) {
            this.array = array.clone();
            this.algorithmName = algorithmName;
        }
        
        public int[] getArray() {
            return array.clone();
        }
        
        public String getAlgorithmName() {
            return algorithmName;
        }
    }
    
    /**
     * 验证文件格式
     */
    public static String getFileType(File file) throws IOException {
        try {
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode typeNode = rootNode.get("type");
            return typeNode != null ? typeNode.asText() : "unknown";
        } catch (Exception e) {
            throw new IOException("无法解析文件格式", e);
        }
    }
}
