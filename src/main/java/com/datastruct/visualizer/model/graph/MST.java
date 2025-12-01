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

    /* ------------------ 最短路径算法 ------------------ */

    /**
     * 最短路径结果封装
     */
    public static class ShortestPathResult {
        private final double[] distances;
        private final int[] predecessors;
        private final boolean hasNegativeCycle;

        public ShortestPathResult(double[] distances, int[] predecessors, boolean hasNegativeCycle) {
            this.distances = distances;
            this.predecessors = predecessors;
            this.hasNegativeCycle = hasNegativeCycle;
        }

        public double[] getDistances() {
            return distances.clone();
        }

        public int[] getPredecessors() {
            return predecessors.clone();
        }

        public boolean hasNegativeCycle() {
            return hasNegativeCycle;
        }

        /**
         * 重建从 source 到 target 的路径（若不可达返回空列表；若存在负权回路则抛出异常）
         */
        public List<Integer> getPath(int target) {
            if (hasNegativeCycle) {
                throw new IllegalStateException("图包含负权回路，路径不可确定");
            }
            if (target < 0 || target >= predecessors.length) return Collections.emptyList();
            if (Double.isInfinite(distances[target])) return Collections.emptyList();

            LinkedList<Integer> path = new LinkedList<>();
            int cur = target;
            while (cur != -1) {
                path.addFirst(cur);
                cur = predecessors[cur];
            }
            return path;
        }
    }

    /**
     * Dijkstra 算法（用于非负权图）
     * @param graph 图（支持有向图）
     * @param source 源点
    * @return ShortestPathResult，包含距离数组与前驱数组
     */
    public static ShortestPathResult dijkstra(Graph graph, int source) {
        int n = graph.getNumVertices();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("源点索引越界");
        }

        dist[source] = 0.0;

        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(v -> dist[v]));
        pq.offer(source);

        while (!pq.isEmpty()) {
            int u = pq.poll();
            // 如果弹出的节点的当前距离已经不是最新的，则跳过
            //（Comparator 会基于 dist 数组排序，这里用简单的跳过策略）
            for (int v : graph.getNeighbors(u)) {
                double weight = graph.getWeight(u, v);
                double alt = dist[u] + weight;
                if (alt < dist[v]) {
                    dist[v] = alt;
                    prev[v] = u;
                    pq.offer(v);
                }
            }
        }

        return new ShortestPathResult(dist, prev, false);
    }

    /**
     * Dijkstra 带步骤记录的实现，用于可视化算法执行过程。
     * 返回包含每一步（考虑边 / 松弛边 / 顶点确定 / 完成）的结果。
     */
    public static class DijkstraStep {
        public enum StepType { EXTRACT_MIN, CONSIDER_EDGE, RELAX_EDGE, FINALIZE_VERTEX, PATH_TO_TARGET_FOUND, COMPLETE }

        private StepType type;
        private Edge edge; // 用于 CONSIDER_EDGE 或 RELAX_EDGE
        private int vertex; // 用于 EXTRACT_MIN / FINALIZE_VERTEX
        private double newDistance; // 用于 RELAX_EDGE
        private List<Edge> pathEdges; // 用于 PATH_TO_TARGET_FOUND

        public DijkstraStep(StepType type) {
            this.type = type;
        }

        public DijkstraStep(StepType type, Edge edge) {
            this.type = type;
            this.edge = edge;
        }

        public DijkstraStep(StepType type, int vertex) {
            this.type = type;
            this.vertex = vertex;
        }

        public DijkstraStep(StepType type, Edge edge, double newDistance) {
            this.type = type;
            this.edge = edge;
            this.newDistance = newDistance;
        }

        public DijkstraStep(StepType type, List<Edge> pathEdges) {
            this.type = type;
            this.pathEdges = pathEdges == null ? null : new ArrayList<>(pathEdges);
        }

        public StepType getType() { return type; }
        public Edge getEdge() { return edge; }
        public int getVertex() { return vertex; }
        public double getNewDistance() { return newDistance; }
        public List<Edge> getPathEdges() { return pathEdges == null ? null : new ArrayList<>(pathEdges); }
    }

    public static class DijkstraResultWithSteps {
        private final double[] distances;
        private final int[] predecessors;
        private final List<DijkstraStep> steps;
        private final java.util.List<DijkstraSnapshot> snapshots; // NEW FIELD

        public DijkstraResultWithSteps(double[] distances, int[] predecessors, List<DijkstraStep> steps) {
            this.distances = distances.clone();
            this.predecessors = predecessors.clone();
            this.steps = new ArrayList<>(steps);
            this.snapshots = java.util.Collections.emptyList();
        }

        public DijkstraResultWithSteps(double[] distances, int[] predecessors, List<DijkstraStep> steps, java.util.List<DijkstraSnapshot> snapshots) {
            this.distances = distances.clone();
            this.predecessors = predecessors.clone();
            this.steps = new ArrayList<>(steps);
            this.snapshots = new java.util.ArrayList<>(snapshots);
        }

        public double[] getDistances() { return distances.clone(); }
        public int[] getPredecessors() { return predecessors.clone(); }
        public List<DijkstraStep> getSteps() { return new ArrayList<>(steps); }
        public java.util.List<DijkstraSnapshot> getSnapshots() { return new java.util.ArrayList<>(snapshots); }
    }

    /**
     * 带步骤的 Dijkstra 实现
     */
    public static DijkstraResultWithSteps dijkstraWithSteps(Graph graph, int source, int target) {
        int n = graph.getNumVertices();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        List<DijkstraStep> steps = new ArrayList<>();

        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("源点索引越界");
        }

        dist[source] = 0.0;

        java.util.List<DijkstraSnapshot> snapshots = new java.util.ArrayList<>();
        java.util.Set<Integer> settled = new java.util.HashSet<>();

        // 自定义优先队列：存顶点，Comparator 基于当前 dist
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(v -> dist[v]));
        pq.offer(source);

        double bestTargetDist = Double.POSITIVE_INFINITY; // 用于记录目前找到的到目标点的最好距离（若有）

        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (settled.contains(u)) continue; // 跳过过期条目

            // 记录弹出（确定最短距离）
            steps.add(new DijkstraStep(DijkstraStep.StepType.EXTRACT_MIN, u));
            steps.add(new DijkstraStep(DijkstraStep.StepType.FINALIZE_VERTEX, u));

            // 本轮所有松弛完成后记录快照（包括 u）
            settled.add(u);
            snapshots.add(new DijkstraSnapshot(snapshots.size(), java.util.Collections.unmodifiableSet(new java.util.HashSet<>(settled)), dist, prev));

            for (int v : graph.getNeighbors(u)) {
                double w = graph.getWeight(u, v);
                Edge e = new Edge(u, v, w);

                // 记录考虑这条边
                steps.add(new DijkstraStep(DijkstraStep.StepType.CONSIDER_EDGE, e));

                double alt = dist[u] + w;
                if (alt < dist[v]) {
                    dist[v] = alt;
                    prev[v] = u;
                    pq.offer(v);
                    // 记录松弛操作
                    steps.add(new DijkstraStep(DijkstraStep.StepType.RELAX_EDGE, e, alt));

                    // 如果这次松弛导致目标的距离被更新（或首次可达），则记录一条 PATH_TO_TARGET_FOUND 步骤
                    if (target >= 0 && target < n && !Double.isInfinite(dist[target]) && dist[target] < bestTargetDist) {
                        // 重建从 source 到 target 的路径（基于当前 prev）
                        LinkedList<Integer> path = new LinkedList<>();
                        int cur = target;
                        while (cur != -1) {
                            path.addFirst(cur);
                            cur = prev[cur];
                        }

                        List<Edge> pathEdges = new ArrayList<>();
                        if (path.size() >= 2) {
                            for (int i = 1; i < path.size(); i++) {
                                int a = path.get(i - 1);
                                int b = path.get(i);
                                double weight = graph.getWeight(a, b);
                                pathEdges.add(new Edge(a, b, weight));
                            }
                        }

                        steps.add(new DijkstraStep(DijkstraStep.StepType.PATH_TO_TARGET_FOUND, pathEdges));
                        bestTargetDist = dist[target];
                    }
                }
            }
        }

        steps.add(new DijkstraStep(DijkstraStep.StepType.COMPLETE));

        return new DijkstraResultWithSteps(dist, prev, steps, snapshots);
    }

    /**
     * Bellman-Ford 算法：支持负权边，并能检测负权回路
     * @param graph 图（支持有向图）
     * @param source 源点
     * @return ShortestPathResult，如果检测到负权回路则 hasNegativeCycle 为 true
     */
    public static ShortestPathResult bellmanFord(Graph graph, int source) {
        int n = graph.getNumVertices();
        double[] dist = new double[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);

        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("源点索引越界");
        }

        dist[source] = 0.0;
        List<Edge> edges = graph.getAllEdges();

        // 松弛 n-1 次
        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;
            for (Edge e : edges) {
                int u = e.getSource();
                int v = e.getDestination();
                double w = e.getWeight();
                if (!Double.isInfinite(dist[u]) && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    updated = true;
                }
            }
            if (!updated) break;
        }

        // 检测负权回路
        boolean hasNegCycle = false;
        for (Edge e : edges) {
            int u = e.getSource();
            int v = e.getDestination();
            double w = e.getWeight();
            if (!Double.isInfinite(dist[u]) && dist[u] + w < dist[v]) {
                hasNegCycle = true;
                break;
            }
        }

        return new ShortestPathResult(dist, prev, hasNegCycle);
    }

    /**
     * 便捷 API：使用 Dijkstra 得到 source -> target 的最短路径（若不可达返回空列表）
     */
    public static List<Integer> shortestPathDijkstra(Graph graph, int source, int target) {
        ShortestPathResult res = dijkstra(graph, source);
        return res.getPath(target);
    }

    /**
     * 便捷 API：使用 Dijkstra 得到 source -> target 的最短距离（不可达返回 Double.POSITIVE_INFINITY）
     */
    public static double shortestDistanceDijkstra(Graph graph, int source, int target) {
        ShortestPathResult res = dijkstra(graph, source);
        double[] d = res.getDistances();
        if (target < 0 || target >= d.length) throw new IllegalArgumentException("目标顶点索引越界");
        return d[target];
    }

    /**
     * 便捷 API：使用 Bellman-Ford 得到 source -> target 的最短路径（若存在负权回路则抛出异常）
     */
    public static List<Integer> shortestPathBellmanFord(Graph graph, int source, int target) {
        ShortestPathResult res = bellmanFord(graph, source);
        if (res.hasNegativeCycle()) throw new IllegalStateException("图包含负权回路，路径不可确定");
        return res.getPath(target);
    }

    /**
     * 便捷 API：使用 Bellman-Ford 得到 source -> target 的最短距离
     */
    public static double shortestDistanceBellmanFord(Graph graph, int source, int target) {
        ShortestPathResult res = bellmanFord(graph, source);
        if (res.hasNegativeCycle()) throw new IllegalStateException("图包含负权回路，距离不可确定");
        double[] d = res.getDistances();
        if (target < 0 || target >= d.length) throw new IllegalArgumentException("目标顶点索引越界");
        return d[target];
    }
    
    /**
     * 求最小生成树（基于优先队列的实现）
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
        
    // 主循环：从优先队列中选择最小边并扩展
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

