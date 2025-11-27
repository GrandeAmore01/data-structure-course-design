// package com.datastruct.visualizer.util;

// import com.datastruct.visualizer.model.graph.*;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.node.ObjectNode;
// import com.fasterxml.jackson.databind.node.ArrayNode;

// import java.io.File;
// import java.io.IOException;
// import java.util.*;
// import java.io.FileWriter;
// import java.io.BufferedWriter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// /**
//  * 数据序列化工具类
//  * Data serialization utility class
//  */
// public class DataSerializer {
    
//     private static final ObjectMapper objectMapper = new ObjectMapper();
    
//     /**
//      * 保存图数据到文件
//      */
//     public static void saveGraph(Graph graph, File file) throws IOException {
//         ObjectNode rootNode = objectMapper.createObjectNode();
        
//         // 基本信息
//         rootNode.put("type", "graph");
//         rootNode.put("implementation", graph instanceof AdjacencyMatrix ? "matrix" : "list");
//         rootNode.put("numVertices", graph.getNumVertices());
//         rootNode.put("isDirected", graph.isDirected());
        
//         // 顶点标签
//         ObjectNode labelsNode = objectMapper.createObjectNode();
//         Map<Integer, String> labels = graph.getAllVertexLabels();
//         for (Map.Entry<Integer, String> entry : labels.entrySet()) {
//             labelsNode.put(entry.getKey().toString(), entry.getValue());
//         }
//         rootNode.set("vertexLabels", labelsNode);
        
//         // 边信息
//         ArrayNode edgesNode = objectMapper.createArrayNode();
//         List<Edge> edges = graph.getAllEdges();
//         for (Edge edge : edges) {
//             ObjectNode edgeNode = objectMapper.createObjectNode();
//             edgeNode.put("source", edge.getSource());
//             edgeNode.put("destination", edge.getDestination());
//             edgeNode.put("weight", edge.getWeight());
//             edgesNode.add(edgeNode);
//         }
//         rootNode.set("edges", edgesNode);
        
//         // 写入文件
//         objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
//     }
    
//     /**
//      * 从文件加载图数据
//      */
//     public static Graph loadGraph(File file) throws IOException {
//         JsonNode rootNode = objectMapper.readTree(file);
        
//         // 验证类型
//         if (!"graph".equals(rootNode.get("type").asText())) {
//             throw new IllegalArgumentException("文件格式不正确：不是图数据文件");
//         }
        
//         // 读取基本信息
//         String implementation = rootNode.get("implementation").asText();
//         int numVertices = rootNode.get("numVertices").asInt();
//         boolean isDirected = rootNode.get("isDirected").asBoolean();
        
//         // 创建图对象
//         Graph graph;
//         if ("matrix".equals(implementation)) {
//             graph = new AdjacencyMatrix(numVertices, isDirected);
//         } else {
//             graph = new AdjacencyList(numVertices, isDirected);
//         }
        
//         // 设置顶点标签
//         JsonNode labelsNode = rootNode.get("vertexLabels");
//         if (labelsNode != null) {
//             labelsNode.fields().forEachRemaining(entry -> {
//                 int vertex = Integer.parseInt(entry.getKey());
//                 String label = entry.getValue().asText();
//                 graph.setVertexLabel(vertex, label);
//             });
//         }
        
//         // 添加边
//         JsonNode edgesNode = rootNode.get("edges");
//         if (edgesNode != null && edgesNode.isArray()) {
//             for (JsonNode edgeNode : edgesNode) {
//                 int source = edgeNode.get("source").asInt();
//                 int destination = edgeNode.get("destination").asInt();
//                 double weight = edgeNode.get("weight").asDouble();
//                 graph.addEdge(source, destination, weight);
//             }
//         }
        
//         return graph;
//     }
    
//     /**
//      * 保存排序数据到文件
//      */
//     public static void saveSortingData(int[] array, String algorithmName, File file) throws IOException {
//         ObjectNode rootNode = objectMapper.createObjectNode();
        
//         rootNode.put("type", "sorting");
//         rootNode.put("algorithm", algorithmName);
        
//         // 数组数据
//         ArrayNode arrayNode = objectMapper.createArrayNode();
//         for (int value : array) {
//             arrayNode.add(value);
//         }
//         rootNode.set("array", arrayNode);
        
//         // 时间戳
//         rootNode.put("timestamp", System.currentTimeMillis());
        
//         // 写入文件
//         objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, rootNode);
//     }
    
//     /**
//      * 从文件加载排序数据
//      */
//     public static SortingData loadSortingData(File file) throws IOException {
//         JsonNode rootNode = objectMapper.readTree(file);
        
//         // 验证类型
//         if (!"sorting".equals(rootNode.get("type").asText())) {
//             throw new IllegalArgumentException("文件格式不正确：不是排序数据文件");
//         }
        
//         String algorithm = rootNode.get("algorithm").asText();
        
//         // 读取数组
//         JsonNode arrayNode = rootNode.get("array");
//         int[] array = new int[arrayNode.size()];
//         for (int i = 0; i < arrayNode.size(); i++) {
//             array[i] = arrayNode.get(i).asInt();
//         }
        
//         return new SortingData(array, algorithm);
//     }

//     /**
//      * 将图转换为 Mermaid 风格的描述字符串
//      * @param graph 要转换的图
//      * @param leftToRight true 则使用 LR（从左到右），否则使用 TD（从上到下）
//      * @return Mermaid 文本
//      */
//     public static String graphToMermaid(Graph graph, boolean leftToRight) {
//         if (graph == null) return "";

//         StringBuilder sb = new StringBuilder();
//         sb.append("%% Generated by DataSerializer\n");
//         sb.append("%% type: graph, implementation: ")
//           .append(graph instanceof AdjacencyMatrix ? "matrix" : "list")
//           .append("\n");

//         sb.append(leftToRight ? "graph LR\n" : "graph TD\n");

//         // 节点定义
//         Map<Integer, String> labels = graph.getAllVertexLabels();
//         for (int i = 0; i < graph.getNumVertices(); i++) {
//             String lbl = labels.getOrDefault(i, String.valueOf(i));
//             // 转义双引号和方括号
//             lbl = lbl.replace("\"", "\\\"").replace("[", "(").replace("]", ")");
//             sb.append("v").append(i).append("[")
//               .append('\"').append(lbl).append('\"').append("]\n");
//         }

//         // 边
//         for (Edge e : graph.getAllEdges()) {
//             if (e == null) continue;
//             int a = e.getSource();
//             int b = e.getDestination();
//             String arrow = graph.isDirected() ? " --> " : " -- ";
//             String weightLabel = "";
//             if (Math.abs(e.getWeight() - 1.0) > 1e-9) {
//                 weightLabel = "|" + String.format("%.2f", e.getWeight()) + "|";
//             }
//             sb.append("v").append(a).append(arrow).append(weightLabel).append("v").append(b).append("\n");
//         }

//         return sb.toString();
//     }

//     /**
//      * 将数组（排序）转换为 Mermaid 风格的线性图表示
//      * 例如: graph LR\n a0["5"] --> a1["2"] --> a2["9"]
//      */
//     public static String sortingArrayToMermaid(int[] array, String algorithmName) {
//         if (array == null) return "";
//         StringBuilder sb = new StringBuilder();
//         sb.append("%% Generated by DataSerializer\n");
//         if (algorithmName != null && !algorithmName.isEmpty()) {
//             sb.append("%% algorithm: ").append(algorithmName).append("\n");
//         }
//         sb.append("graph LR\n");

//         for (int i = 0; i < array.length; i++) {
//             String nodeId = "a" + i;
//             sb.append(nodeId).append("[")
//               .append('\"').append(array[i]).append('\"').append("]\n");
//         }

//         // 连线
//         for (int i = 0; i < array.length - 1; i++) {
//             sb.append("a").append(i).append(" --> a").append(i + 1).append("\n");
//         }

//         return sb.toString();
//     }

//     /**
//      * 将 Mermaid 文本保存为文件（纯文本）
//      */
//     public static void saveMermaidToFile(String mermaidText, File file) throws IOException {
//         if (mermaidText == null) mermaidText = "";
//         try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
//             bw.write(mermaidText);
//         }
//     }

//     /**
//      * 从 Mermaid 文本解析 Graph
//      * 支持格式：graph LR/TD, v0["label"], v0 --> v1, v0 -- v1 等
//      * @param mermaidText Mermaid 文本
//      * @return 解析得到的 Graph 对象
//      * @throws IllegalArgumentException 如果格式不正确
//      */
//     public static Graph parseMermaidToGraph(String mermaidText) throws IllegalArgumentException {
//         if (mermaidText == null || mermaidText.trim().isEmpty()) {
//             throw new IllegalArgumentException("Mermaid 文本不能为空");
//         }

//         String[] lines = mermaidText.split("\n");
//         boolean isDirected = true;
//         int maxVertexIndex = -1;
//         Map<Integer, String> labels = new HashMap<>();
//         List<EdgeDefinition> edges = new ArrayList<>();

//         for (String line : lines) {
//             line = line.trim();
//             if (line.isEmpty() || line.startsWith("%%")) {
//                 continue;
//             }

//             // 检查图的方向
//             if (line.startsWith("graph")) {
//                 isDirected = !line.contains("--") && line.contains("--");
//                 continue;
//             }

//             // 解析顶点定义
//             if (line.matches("v\\d+\\[.+\\]")) {
//                 String[] parts = line.split("\\[");
//                 if (parts.length >= 2) {
//                     int vertexIndex = extractVertexIndex(parts[0]);
//                     String label = extractNodeLabel(line);
//                     labels.put(vertexIndex, label);
//                     maxVertexIndex = Math.max(maxVertexIndex, vertexIndex);
//                 }
//                 continue;
//             }

//             // 解析边定义
//             EdgeDefinition edgeDef = parseEdgeLine(line, !isDirected);
//             if (edgeDef != null) {
//                 edges.add(edgeDef);
//                 maxVertexIndex = Math.max(maxVertexIndex, Math.max(edgeDef.source, edgeDef.destination));
//             }
//         }

//         if (maxVertexIndex < 0) {
//             throw new IllegalArgumentException("无法从 Mermaid 文本中解析出顶点");
//         }

//         // 创建图
//         Graph graph = new AdjacencyList(maxVertexIndex + 1, isDirected);

//         // 设置标签
//         for (Map.Entry<Integer, String> entry : labels.entrySet()) {
//             graph.setVertexLabel(entry.getKey(), entry.getValue());
//         }

//         // 添加边
//         for (EdgeDefinition edge : edges) {
//             double weight = edge.weight != null ? edge.weight : 1.0;
//             graph.addEdge(edge.source, edge.destination, weight);
//         }

//         return graph;
//     }

//     /**
//      * 从 Mermaid 文本解析数组
//      * 支持格式：graph LR, a0["value"], a0 --> a1 等
//      * @param mermaidText Mermaid 文本
//      * @return 解析得到的整数数组
//      * @throws IllegalArgumentException 如果格式不正确
//      */
//     public static int[] parseMermaidToArray(String mermaidText) throws IllegalArgumentException {
//         if (mermaidText == null || mermaidText.trim().isEmpty()) {
//             throw new IllegalArgumentException("Mermaid 文本不能为空");
//         }

//         String[] lines = mermaidText.split("\n");
//         Map<Integer, Integer> nodeValues = new TreeMap<>();
//         int maxIndex = -1;

//         for (String line : lines) {
//             line = line.trim();
//             if (line.isEmpty() || line.startsWith("%%") || line.startsWith("graph")) {
//                 continue;
//             }

//             // 解析节点定义
//             if (line.matches("a\\d+\\[.+\\]")) {
//                 int nodeIndex = extractNodeIndex(line);
//                 int value = extractNodeValue(line);
//                 nodeValues.put(nodeIndex, value);
//                 maxIndex = Math.max(maxIndex, nodeIndex);
//             }
//         }

//         if (maxIndex < 0) {
//             throw new IllegalArgumentException("无法从 Mermaid 文本中解析出数组");
//         }

//         // 构建数组
//         int[] result = new int[maxIndex + 1];
//         for (int i = 0; i <= maxIndex; i++) {
//             result[i] = nodeValues.getOrDefault(i, 0);
//         }

//         return result;
//     }

//     /**
//      * 提取顶点索引（图）
//      * 例如从 "v5" 中提取 5
//      */
//     private static int extractVertexIndex(String nodeDef) throws IllegalArgumentException {
//         Pattern pattern = Pattern.compile("v(\\d+)");
//         Matcher matcher = pattern.matcher(nodeDef);
//         if (matcher.find()) {
//             return Integer.parseInt(matcher.group(1));
//         }
//         throw new IllegalArgumentException("无法从 '" + nodeDef + "' 提取顶点索引");
//     }

//     /**
//      * 提取节点索引（排序）
//      * 例如从 "a3["value"]" 中提取 3
//      */
//     private static int extractNodeIndex(String nodeDef) throws IllegalArgumentException {
//         Pattern pattern = Pattern.compile("a(\\d+)");
//         Matcher matcher = pattern.matcher(nodeDef);
//         if (matcher.find()) {
//             return Integer.parseInt(matcher.group(1));
//         }
//         throw new IllegalArgumentException("无法从 '" + nodeDef + "' 提取节点索引");
//     }

//     /**
//      * 提取节点标签
//      * 例如从 "v1["label"]" 中提取 label
//      */
//     private static String extractNodeLabel(String nodeDef) throws IllegalArgumentException {
//         Pattern pattern = Pattern.compile("\\[\"(.*?)\"\\]");
//         Matcher matcher = pattern.matcher(nodeDef);
//         if (matcher.find()) {
//             String label = matcher.group(1);
//             // 反转义
//             label = label.replace("\\\"", "\"").replace("\\(", "[").replace("\\)", "]");
//             return label;
//         }
//         throw new IllegalArgumentException("无法从 '" + nodeDef + "' 提取标签");
//     }

//     /**
//      * 提取节点值（排序）
//      * 例如从 "a3["42"]" 中提取 42
//      */
//     private static int extractNodeValue(String nodeDef) throws IllegalArgumentException {
//         Pattern pattern = Pattern.compile("\\[\"(\\d+)\"\\]");
//         Matcher matcher = pattern.matcher(nodeDef);
//         if (matcher.find()) {
//             return Integer.parseInt(matcher.group(1));
//         }
//         throw new IllegalArgumentException("无法从 '" + nodeDef + "' 提取节点值");
//     }

//     /**
//      * 解析边定义
//      * 例如：v0 --> v1, v0 -- |2.5| v1 等
//      */
//     private static EdgeDefinition parseEdgeLine(String line, boolean isUndirected) {
//         // 匹配有向边 -->
//         Pattern directedPattern = Pattern.compile("(v\\d+)\\s*-->\\s*(?:\\|(.*?)\\|)?(v\\d+)");
//         Matcher directedMatcher = directedPattern.matcher(line);
//         if (directedMatcher.find()) {
//             int source = extractVertexIndex(directedMatcher.group(1));
//             int destination = extractVertexIndex(directedMatcher.group(3));
//             Double weight = null;
//             if (directedMatcher.group(2) != null && !directedMatcher.group(2).trim().isEmpty()) {
//                 try {
//                     weight = Double.parseDouble(directedMatcher.group(2).trim());
//                 } catch (NumberFormatException e) {
//                     weight = null;
//                 }
//             }
//             return new EdgeDefinition(source, destination, weight);
//         }

//         // 匹配无向边 --
//         Pattern undirectedPattern = Pattern.compile("(v\\d+)\\s*--\\s*(?:\\|(.*?)\\|)?(v\\d+)");
//         Matcher undirectedMatcher = undirectedPattern.matcher(line);
//         if (undirectedMatcher.find()) {
//             int source = extractVertexIndex(undirectedMatcher.group(1));
//             int destination = extractVertexIndex(undirectedMatcher.group(3));
//             Double weight = null;
//             if (undirectedMatcher.group(2) != null && !undirectedMatcher.group(2).trim().isEmpty()) {
//                 try {
//                     weight = Double.parseDouble(undirectedMatcher.group(2).trim());
//                 } catch (NumberFormatException e) {
//                     weight = null;
//                 }
//             }
//             return new EdgeDefinition(source, destination, weight);
//         }

//         // 匹配排序数组的箭头 -->（节点格式 a0, a1 等）
//         Pattern arrayPattern = Pattern.compile("(a\\d+)\\s*-->\\s*(a\\d+)");
//         Matcher arrayMatcher = arrayPattern.matcher(line);
//         if (arrayMatcher.find()) {
//             // 对于排序数组，我们也返回边定义但使用节点编号
//             int source = extractNodeIndex(arrayMatcher.group(1));
//             int destination = extractNodeIndex(arrayMatcher.group(2));
//             return new EdgeDefinition(source, destination, null);
//         }

//         return null;
//     }

//     /**
//      * 排序数据容器类
//      */
//     public static class SortingData {
//         private final int[] array;
//         private final String algorithmName;
        
//         public SortingData(int[] array, String algorithmName) {
//             this.array = array.clone();
//             this.algorithmName = algorithmName;
//         }
        
//         public int[] getArray() {
//             return array.clone();
//         }
        
//         public String getAlgorithmName() {
//             return algorithmName;
//         }
//     }

//     /**
//      * 边定义内部类
//      * 用于存储 Mermaid 解析中的边信息
//      */
//     public static class EdgeDefinition {
//         public final int source;
//         public final int destination;
//         public final Double weight;

//         public EdgeDefinition(int source, int destination, Double weight) {
//             this.source = source;
//             this.destination = destination;
//             this.weight = weight;
//         }

//         @Override
//         public String toString() {
//             return "EdgeDefinition{" +
//                     "source=" + source +
//                     ", destination=" + destination +
//                     ", weight=" + weight +
//                     '}';
//         }
//     }
    
//     /**
//      * 验证文件格式
//      */
//     public static String getFileType(File file) throws IOException {
//         try {
//             JsonNode rootNode = objectMapper.readTree(file);
//             JsonNode typeNode = rootNode.get("type");
//             return typeNode != null ? typeNode.asText() : "unknown";
//         } catch (Exception e) {
//             throw new IOException("无法解析文件格式", e);
//         }
//     }
// }
