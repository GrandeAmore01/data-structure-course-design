package com.datastruct.visualizer.model.graph;

import java.util.*;

/**
 * 图算法集合类
 * Collection of graph algorithms with step-by-step tracking
 */
public class GraphAlgorithms {
    
    /**
     * 带步骤记录的深度优先搜索
     */
    public static class DFSResult {
        private List<Integer> visitOrder;
        private List<DFSStep> steps;
        
        public DFSResult(List<Integer> visitOrder, List<DFSStep> steps) {
            this.visitOrder = visitOrder;
            this.steps = steps;
        }
        
        public List<Integer> getVisitOrder() { return visitOrder; }
        public List<DFSStep> getSteps() { return steps; }
    }
    
    /**
     * 带步骤记录的广度优先搜索
     */
    public static class BFSResult {
        private List<Integer> visitOrder;
        private List<BFSStep> steps;
        
        public BFSResult(List<Integer> visitOrder, List<BFSStep> steps) {
            this.visitOrder = visitOrder;
            this.steps = steps;
        }
        
        public List<Integer> getVisitOrder() { return visitOrder; }
        public List<BFSStep> getSteps() { return steps; }
    }
    
    /**
     * DFS步骤记录
     */
    public static class DFSStep {
        public enum StepType { START, VISIT, BACKTRACK, COMPLETE }
        
        private StepType type;
        private int currentVertex;
        private int parentVertex;
        private String description;
        
        public DFSStep(StepType type, int currentVertex, int parentVertex, String description) {
            this.type = type;
            this.currentVertex = currentVertex;
            this.parentVertex = parentVertex;
            this.description = description;
        }
        
        // Getters
        public StepType getType() { return type; }
        public int getCurrentVertex() { return currentVertex; }
        public int getParentVertex() { return parentVertex; }
        public String getDescription() { return description; }
    }
    
    /**
     * BFS步骤记录
     */
    public static class BFSStep {
        public enum StepType { START, ENQUEUE, DEQUEUE, VISIT, COMPLETE }
        
        private StepType type;
        private int currentVertex;
        private Queue<Integer> queueState;
        private String description;
        
        public BFSStep(StepType type, int currentVertex, Queue<Integer> queueState, String description) {
            this.type = type;
            this.currentVertex = currentVertex;
            this.queueState = new LinkedList<>(queueState);
            this.description = description;
        }
        
        // Getters
        public StepType getType() { return type; }
        public int getCurrentVertex() { return currentVertex; }
        public Queue<Integer> getQueueState() { return new LinkedList<>(queueState); }
        public String getDescription() { return description; }
    }
    
    /**
     * 执行带步骤记录的DFS
     */
    public static DFSResult performDFS(Graph graph, int startVertex) {
        List<Integer> visitOrder = new ArrayList<>();
        List<DFSStep> steps = new ArrayList<>();
        boolean[] visited = new boolean[graph.getNumVertices()];
        
        steps.add(new DFSStep(DFSStep.StepType.START, startVertex, -1, 
                  "开始深度优先搜索，起始顶点: " + startVertex));
        
        dfsHelper(graph, startVertex, visited, visitOrder, steps, -1);
        
        steps.add(new DFSStep(DFSStep.StepType.COMPLETE, -1, -1, "深度优先搜索完成"));
        
        return new DFSResult(visitOrder, steps);
    }
    
    private static void dfsHelper(Graph graph, int vertex, boolean[] visited, 
                                 List<Integer> visitOrder, List<DFSStep> steps, int parent) {
        visited[vertex] = true;
        visitOrder.add(vertex);
        
        steps.add(new DFSStep(DFSStep.StepType.VISIT, vertex, parent, 
                  "访问顶点 " + vertex));
        
        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                dfsHelper(graph, neighbor, visited, visitOrder, steps, vertex);
            }
        }
        
        if (parent != -1) {
            steps.add(new DFSStep(DFSStep.StepType.BACKTRACK, vertex, parent, 
                      "回溯到顶点 " + parent));
        }
    }
    
    /**
     * 执行带步骤记录的BFS
     */
    public static BFSResult performBFS(Graph graph, int startVertex) {
        List<Integer> visitOrder = new ArrayList<>();
        List<BFSStep> steps = new ArrayList<>();
        boolean[] visited = new boolean[graph.getNumVertices()];
        Queue<Integer> queue = new LinkedList<>();
        
        visited[startVertex] = true;
        queue.offer(startVertex);
        
        steps.add(new BFSStep(BFSStep.StepType.START, startVertex, queue,
                  "开始广度优先搜索，起始顶点: " + startVertex));
        
        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            visitOrder.add(vertex);
            
            steps.add(new BFSStep(BFSStep.StepType.DEQUEUE, vertex, queue,
                      "从队列中取出顶点 " + vertex));
            
            steps.add(new BFSStep(BFSStep.StepType.VISIT, vertex, queue,
                      "访问顶点 " + vertex));
            
            for (int neighbor : graph.getNeighbors(vertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                    
                    steps.add(new BFSStep(BFSStep.StepType.ENQUEUE, neighbor, queue,
                              "将邻接顶点 " + neighbor + " 加入队列"));
                }
            }
        }
        
        steps.add(new BFSStep(BFSStep.StepType.COMPLETE, -1, queue, "广度优先搜索完成"));
        
        return new BFSResult(visitOrder, steps);
    }
}

