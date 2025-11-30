package com.datastruct.visualizer.util;

public class DslCommand {
    public enum Type {
        ADD_VERTEX, REMOVE_VERTEX,
        ADD_EDGE, REMOVE_EDGE,
        SET_LABEL, SET_DIRECTED,
        RUN_DFS, RUN_BFS, RUN_DIJKSTRA, RUN_MST
    }

    public final Type type;
    public final String[] args;
    public final int line;

    public DslCommand(Type type, int line, String... args) {
        this.type = type;
        this.args = args;
        this.line = line;
    }
}
