package com.datastruct.visualizer.model.graph;

import java.util.*;

/**
 * 最小生成树算法实现
 * Minimum Spanning Tree Algorithms
 */
public class MST {
    
    /**
     * Kruskal算法求最小生成树
     * @param graph 图
     * @return 最小生成树的边集合
     */
    public static List<Edge> kruskal(Graph graph) {
        List<Edge> mstEdges = new ArrayList<>();
        List<Edge> allEdges = graph.getAllEdges();
        
        // 如果是有向图，不能求最小生成树
        if (graph.isDirected()) {
            throw new IllegalArgumentException("有向图无法构建最小生成树");
        }
        
        // 按权重排序所有边
        allEdges.sort(Comparator.comparingDouble(Edge::getWeight));
        
        // 初始化并查集
        UnionFind uf = new UnionFind(graph.getNumVertices());
        
        // Kruskal算法主循环
        for (Edge edge : allEdges) {
            int source = edge.getSource();
            int dest = edge.getDestination();
            
            // 如果两个顶点不在同一连通分量中，添加这条边
            if (!uf.connected(source, dest)) {
                uf.union(source, dest);
                mstEdges.add(edge);
                
                // 如果已经有n-1条边，MST完成
                if (mstEdges.size() == graph.getNumVertices() - 1) {
                    break;
                }
            }
        }
        
        return mstEdges;
    }
    
    /**
     * Prim算法求最小生成树
     * @param graph 图
     * @param startVertex 起始顶点
     * @return 最小生成树的边集合
     */
    public static List<Edge> prim(Graph graph, int startVertex) {
        List<Edge> mstEdges = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        
        // 如果是有向图，不能求最小生成树
        if (graph.isDirected()) {
            throw new IllegalArgumentException("有向图无法构建最小生成树");
        }
        
        // 优先队列存储边，按权重排序
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(Edge::getWeight));
        
        // 从起始顶点开始
        visited.add(startVertex);
        
        // 添加从起始顶点出发的所有边
        addEdgesFromVertex(graph, startVertex, visited, pq);
        
        // Prim算法主循环
        while (!pq.isEmpty() && mstEdges.size() < graph.getNumVertices() - 1) {
            Edge edge = pq.poll();
            int dest = edge.getDestination();
            
            // 如果目标顶点已经访问过，跳过这条边
            if (visited.contains(dest)) {
                continue;
            }
            
            // 添加这条边到MST
            mstEdges.add(edge);
            visited.add(dest);
            
            // 添加从新顶点出发的所有边
            addEdgesFromVertex(graph, dest, visited, pq);
        }
        
        return mstEdges;
    }
    
    /**
     * 添加从指定顶点出发的所有边到优先队列
     */
    private static void addEdgesFromVertex(Graph graph, int vertex, Set<Integer> visited, PriorityQueue<Edge> pq) {
        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited.contains(neighbor)) {
                double weight = graph.getWeight(vertex, neighbor);
                pq.offer(new Edge(vertex, neighbor, weight));
            }
        }
    }
    
    /**
     * 计算最小生成树的总权重
     */
    public static double calculateMSTWeight(List<Edge> mstEdges) {
        return mstEdges.stream().mapToDouble(Edge::getWeight).sum();
    }
    
    /**
     * 并查集数据结构（用于Kruskal算法）
     */
    private static class UnionFind {
        private int[] parent;
        private int[] rank;
        
        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }
        
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // 路径压缩
            }
            return parent[x];
        }
        
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX != rootY) {
                // 按秩合并
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
        
        public boolean connected(int x, int y) {
            return find(x) == find(y);
        }
    }
}

