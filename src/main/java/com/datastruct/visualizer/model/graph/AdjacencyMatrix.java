package com.datastruct.visualizer.model.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * 邻接矩阵存储的图
 * Graph implemented with adjacency matrix
 */
public class AdjacencyMatrix extends Graph {
    
    private double[][] matrix;
    private static final double INFINITY = Double.MAX_VALUE;
    
    public AdjacencyMatrix(int numVertices, boolean isDirected) {
        super(numVertices, isDirected);
        this.matrix = new double[numVertices][numVertices];
        
        // 初始化矩阵
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                matrix[i][j] = (i == j) ? 0 : INFINITY;
            }
        }
    }
    
    @Override
    public void addEdge(int source, int destination, double weight) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            throw new IllegalArgumentException("顶点索引无效");
        }
        
        matrix[source][destination] = weight;
        
        // 如果是无向图，添加反向边
        if (!isDirected) {
            matrix[destination][source] = weight;
        }
    }
    
    @Override
    public void removeEdge(int source, int destination) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            throw new IllegalArgumentException("顶点索引无效");
        }
        
        matrix[source][destination] = INFINITY;
        
        // 如果是无向图，移除反向边
        if (!isDirected) {
            matrix[destination][source] = INFINITY;
        }
    }
    
    @Override
    public boolean hasEdge(int source, int destination) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            return false;
        }
        return matrix[source][destination] != INFINITY;
    }
    
    @Override
    public double getWeight(int source, int destination) {
        if (!isValidVertex(source) || !isValidVertex(destination)) {
            return INFINITY;
        }
        return matrix[source][destination];
    }
    
    @Override
    public List<Integer> getNeighbors(int vertex) {
        if (!isValidVertex(vertex)) {
            return new ArrayList<>();
        }
        
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            if (matrix[vertex][i] != INFINITY) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }
    
    @Override
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (matrix[i][j] != INFINITY && i != j) {
                    edges.add(new Edge(i, j, matrix[i][j]));
                }
            }
        }
        
        return edges;
    }
    
    // 获取邻接矩阵的副本
    public double[][] getMatrix() {
        double[][] copy = new double[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, numVertices);
        }
        return copy;
    }
    
    // 打印邻接矩阵
    public void printMatrix() {
        System.out.println("邻接矩阵:");
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (matrix[i][j] == INFINITY) {
                    System.out.print("∞\t");
                } else {
                    System.out.printf("%.1f\t", matrix[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    @Override
    public void addVertex(String label) {
        // 创建新的更大的矩阵
        int newSize = numVertices + 1;
        double[][] newMatrix = new double[newSize][newSize];
        
        // 复制原有数据
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }
        
        // 初始化新行和新列
        for (int i = 0; i < newSize; i++) {
            newMatrix[numVertices][i] = (i == numVertices) ? 0 : INFINITY;
            newMatrix[i][numVertices] = (i == numVertices) ? 0 : INFINITY;
        }
        
        // 更新矩阵和顶点数
        matrix = newMatrix;
        numVertices = newSize;
        
        // 强制标签与索引一致
        resetVertexLabelsToIndex();
    }
    
    @Override
    public void removeVertex(int vertex) {
        if (!isValidVertex(vertex)) {
            throw new IllegalArgumentException("顶点索引无效: " + vertex);
        }
        
        // 创建新的更小的矩阵
        int newSize = numVertices - 1;
        if (newSize <= 0) {
            throw new IllegalArgumentException("无法删除最后一个顶点");
        }
        
        double[][] newMatrix = new double[newSize][newSize];
        
        // 复制数据，跳过被删除的行和列
        int newRow = 0;
        for (int i = 0; i < numVertices; i++) {
            if (i == vertex) continue;
            
            int newCol = 0;
            for (int j = 0; j < numVertices; j++) {
                if (j == vertex) continue;
                newMatrix[newRow][newCol] = matrix[i][j];
                newCol++;
            }
            newRow++;
        }
        
        // 更新矩阵和顶点数
        matrix = newMatrix;
        numVertices = newSize;
        
        // 强制标签与索引一致
        resetVertexLabelsToIndex();
    }
}

