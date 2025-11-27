package com.datastruct.visualizer.model.graph;

import java.util.*;

/**
 * 图的抽象基类
 * Abstract base class for graph data structures
 */
public abstract class Graph {
    
    protected int numVertices;
    protected boolean isDirected;
    protected Map<Integer, String> vertexLabels;
    
    public Graph(int numVertices, boolean isDirected) {
        this.numVertices = numVertices;
        this.isDirected = isDirected;
        this.vertexLabels = new HashMap<>();
        
        // 默认标签为顶点索引
        for (int i = 0; i < numVertices; i++) {
            vertexLabels.put(i, String.valueOf(i));
        }
    }
    
    // 抽象方法 - 子类必须实现
    public abstract void addEdge(int source, int destination, double weight);
    public abstract void removeEdge(int source, int destination);
    public abstract boolean hasEdge(int source, int destination);
    public abstract double getWeight(int source, int destination);
    public abstract List<Integer> getNeighbors(int vertex);
    public abstract List<Edge> getAllEdges();
    
    // 动态顶点操作方法
    public abstract void addVertex(String label);
    public abstract void removeVertex(int vertex);
    
    // 通用方法
    public int getNumVertices() {
        return numVertices;
    }
    
    public boolean isDirected() {
        return isDirected;
    }
    
    public void setVertexLabel(int vertex, String label) {
        if (vertex >= 0 && vertex < numVertices) {
            vertexLabels.put(vertex, label);
        }
    }
    
    public String getVertexLabel(int vertex) {
        return vertexLabels.getOrDefault(vertex, String.valueOf(vertex));
    }
    
    public Map<Integer, String> getAllVertexLabels() {
        return new HashMap<>(vertexLabels);
    }
    
    // 深度优先搜索
    public List<Integer> depthFirstSearch(int startVertex) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[numVertices];
        dfsHelper(startVertex, visited, result);
        return result;
    }
    
    private void dfsHelper(int vertex, boolean[] visited, List<Integer> result) {
        visited[vertex] = true;
        result.add(vertex);
        
        for (int neighbor : getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                dfsHelper(neighbor, visited, result);
            }
        }
    }
    
    // 广度优先搜索
    public List<Integer> breadthFirstSearch(int startVertex) {
        List<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[numVertices];
        Queue<Integer> queue = new LinkedList<>();
        
        visited[startVertex] = true;
        queue.offer(startVertex);
        
        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            result.add(vertex);
            
            for (int neighbor : getNeighbors(vertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
        
        return result;
    }
    
    // 验证顶点索引是否有效
    protected boolean isValidVertex(int vertex) {
        return vertex >= 0 && vertex < numVertices;
    }
}

