package com.datastruct.visualizer;

import java.util.*;

import com.datastruct.visualizer.model.graph.AdjacencyList;
import com.datastruct.visualizer.model.graph.Graph;

/**
 * Stores parsed data from the DSL, including graph structures and sorting steps.
 */
public class VisualizationData {

    private final Map<String, List<String>> adjacencyList;
    private List<Integer> sortingData;

    public VisualizationData() {
        this.adjacencyList = new HashMap<>();
        this.sortingData = new ArrayList<>();
    }

    /**
     * Adds an edge to the graph.
     *
     * @param from The starting node.
     * @param to   The ending node.
     */
    public void addEdge(String from, String to) {
        adjacencyList.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    /**
     * Gets the adjacency list representing the graph.
     *
     * @return The adjacency list.
     */
    public Map<String, List<String>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Sets the sorting data.
     *
     * @param sortingData A list of integers to be sorted.
     */
    public void setSortingData(List<Integer> sortingData) {
        this.sortingData = sortingData;
    }

    /**
     * Gets the sorting data.
     *
     * @return A list of integers to be sorted.
     */
    public List<Integer> getSortingData() {
        return sortingData;
    }

    public boolean isGraph() {
        return !adjacencyList.isEmpty();
    }

    public Graph getGraph() {
        Graph graph = new AdjacencyList(adjacencyList.size(), true);
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            String from = entry.getKey();
            for (String to : entry.getValue()) {
                graph.addEdge(Integer.parseInt(from), Integer.parseInt(to), 1.0);
            }
        }
        return graph;
    }

    public boolean isSorting() {
        return !sortingData.isEmpty();
    }

    public int[] getArray() {
        return sortingData.stream().mapToInt(Integer::intValue).toArray();
    }
}