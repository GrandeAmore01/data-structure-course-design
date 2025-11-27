package com.datastruct.visualizer.util;

import com.datastruct.visualizer.model.graph.Graph;
import com.datastruct.visualizer.model.graph.AdjacencyMatrix;
import com.datastruct.visualizer.model.graph.AdjacencyList;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据结构类，用于存储 DSL 解析后的结果。
 */
public class VisualizationData {

    private String graphType;
    private boolean directed;
    private int numVertices;
    private List<Edge> edges;

    private String sortingAlgorithm;
    private int[] array;

    public VisualizationData() {
        this.edges = new ArrayList<>();
    }

    // 图相关方法
    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
    }

    public void addEdge(int from, int to, double weight) {
        this.edges.add(new Edge(from, to, weight));
    }

    public Graph getGraph() {
        Graph graph;
        if ("adjacency_matrix".equals(graphType)) {
            graph = new AdjacencyMatrix(numVertices, directed);
        } else {
            graph = new AdjacencyList(numVertices, directed);
        }

        for (Edge edge : edges) {
            graph.addEdge(edge.from, edge.to, edge.weight);
        }

        return graph;
    }

    public boolean isGraph() {
        return graphType != null;
    }

    // 排序相关方法
    public void setSortingAlgorithm(String sortingAlgorithm) {
        this.sortingAlgorithm = sortingAlgorithm;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public int[] getArray() {
        return array;
    }

    public boolean isSorting() {
        return sortingAlgorithm != null;
    }

    // 内部类表示边
    private static class Edge {
        int from;
        int to;
        double weight;

        Edge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}