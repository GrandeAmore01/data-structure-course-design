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
