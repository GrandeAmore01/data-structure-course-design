package com.datastruct.visualizer.model.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Dijkstra 迭代过程的快照，用于在 UI 中以表格呈现算法状态。
 */
public class DijkstraSnapshot {
    private final int iteration;            // 迭代计数（0 表示初始状态）
    private final Set<Integer> sSet;        // 已确定最短路径的顶点集合 S
    private final double[] distances;       // dist 数组快照
    private final int[] predecessors;       // prev 数组快照

    public DijkstraSnapshot(int iteration,
                             Set<Integer> sSet,
                             double[] distances,
                             int[] predecessors) {
        this.iteration = iteration;
        this.sSet = Collections.unmodifiableSet(new HashSet<>(sSet));
        this.distances = distances.clone();
        this.predecessors = predecessors.clone();
    }

    public int getIteration() {
        return iteration;
    }

    public Set<Integer> getSSet() {
        return sSet;
    }

    public double[] getDistances() {
        return distances.clone();
    }

    public int[] getPredecessors() {
        return predecessors.clone();
    }
}
