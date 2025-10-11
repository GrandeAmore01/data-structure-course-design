package com.datastruct.visualizer.model.graph;

import java.util.*;

/**
 * 邻接表存储的图
 * Graph implemented with adjacency list
 */
public class AdjacencyList extends Graph {
    
    private List<List<EdgeNode>> adjacencyList;
    
    public AdjacencyList(int numVertices, boolean isDirected) {
        super(numVertices, isDirected);
        this.adjacencyList = new ArrayList<>(numVertices);
        
        // 初始化邻接表
        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }
    
    @Override
    public void addEdge(int source, int destination, double weight) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            throw new IllegalArgumentException("顶点索引无效");
        }
        
        // 检查边是否已存在
        List<EdgeNode> sourceList = adjacencyList.get(source);
        for (EdgeNode node : sourceList) {
            if (node.vertex == destination) {
                node.weight = weight; // 更新权重
                return;
            }
        }
        
        // 添加新边
        sourceList.add(new EdgeNode(destination, weight));
        
        // 如果是无向图，添加反向边
        if (!isDirected) {
            List<EdgeNode> destList = adjacencyList.get(destination);
            boolean found = false;
            for (EdgeNode node : destList) {
                if (node.vertex == source) {
                    node.weight = weight;
                    found = true;
                    break;
                }
            }
            if (!found) {
                destList.add(new EdgeNode(source, weight));
            }
        }
    }
    
    @Override
    public void removeEdge(int source, int destination) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            throw new IllegalArgumentException("顶点索引无效");
        }
        
        // 移除正向边
        List<EdgeNode> sourceList = adjacencyList.get(source);
        sourceList.removeIf(node -> node.vertex == destination);
        
        // 如果是无向图，移除反向边
        if (!isDirected) {
            List<EdgeNode> destList = adjacencyList.get(destination);
            destList.removeIf(node -> node.vertex == source);
        }
    }
    
    @Override
    public boolean hasEdge(int source, int destination) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            return false;
        }
        
        List<EdgeNode> sourceList = adjacencyList.get(source);
        return sourceList.stream().anyMatch(node -> node.vertex == destination);
    }
    
    @Override
    public double getWeight(int source, int destination) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            return Double.MAX_VALUE;
        }
        
        List<EdgeNode> sourceList = adjacencyList.get(source);
        for (EdgeNode node : sourceList) {
            if (node.vertex == destination) {
                return node.weight;
            }
        }
        return Double.MAX_VALUE;
    }
    
    @Override
    public List<Integer> getNeighbors(int vertex) {
        if (!isValidVertex(vertex)) {
            return new ArrayList<>();
        }
        
        List<Integer> neighbors = new ArrayList<>();
        for (EdgeNode node : adjacencyList.get(vertex)) {
            neighbors.add(node.vertex);
        }
        return neighbors;
    }
    
    @Override
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        
        for (int i = 0; i < numVertices; i++) {
            for (EdgeNode node : adjacencyList.get(i)) {
                edges.add(new Edge(i, node.vertex, node.weight));
            }
        }
        
        return edges;
    }
    
    // 获取邻接表的副本
    public List<List<EdgeNode>> getAdjacencyList() {
        List<List<EdgeNode>> copy = new ArrayList<>();
        for (List<EdgeNode> list : adjacencyList) {
            List<EdgeNode> listCopy = new ArrayList<>();
            for (EdgeNode node : list) {
                listCopy.add(new EdgeNode(node.vertex, node.weight));
            }
            copy.add(listCopy);
        }
        return copy;
    }
    
    // 打印邻接表
    public void printAdjacencyList() {
        System.out.println("邻接表:");
        for (int i = 0; i < numVertices; i++) {
            System.out.print("顶点 " + getVertexLabel(i) + ": ");
            List<EdgeNode> neighbors = adjacencyList.get(i);
            if (neighbors.isEmpty()) {
                System.out.print("无邻接顶点");
            } else {
                for (int j = 0; j < neighbors.size(); j++) {
                    EdgeNode node = neighbors.get(j);
                    System.out.printf("%s(%.1f)", getVertexLabel(node.vertex), node.weight);
                    if (j < neighbors.size() - 1) {
                        System.out.print(" -> ");
                    }
                }
            }
            System.out.println();
        }
    }
    
    /**
     * 边节点内部类
     */
    public static class EdgeNode {
        public int vertex;
        public double weight;
        
        public EdgeNode(int vertex, double weight) {
            this.vertex = vertex;
            this.weight = weight;
        }
        
        @Override
        public String toString() {
            return String.format("EdgeNode{vertex=%d, weight=%.2f}", vertex, weight);
        }
    }
}
