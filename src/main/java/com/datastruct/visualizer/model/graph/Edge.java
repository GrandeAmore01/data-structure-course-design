package com.datastruct.visualizer.model.graph;

/**
 * 图的边类
 * Edge class for graph data structure
 */
public class Edge {
    private final int source;
    private final int destination;
    private final double weight;
    
    public Edge(int source, int destination) {
        this(source, destination, 1.0);
    }
    
    public Edge(int source, int destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
    
    public int getSource() {
        return source;
    }
    
    public int getDestination() {
        return destination;
    }
    
    public double getWeight() {
        return weight;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Edge edge = (Edge) obj;
        return source == edge.source && 
               destination == edge.destination && 
               Double.compare(edge.weight, weight) == 0;
    }
    
    @Override
    public int hashCode() {
        return source * 31 + destination;
    }
    
    @Override
    public String toString() {
        return String.format("Edge{%d -> %d, weight=%.2f}", source, destination, weight);
    }
}

