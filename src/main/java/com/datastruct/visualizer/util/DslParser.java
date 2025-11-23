package com.datastruct.visualizer.util;

import com.datastruct.visualizer.model.graph.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析自定义 DSL 文本并构造 Graph
 */
public class DslParser {

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("(type|directed|vertices)\\s+(\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern VERTEX_LABEL_PATTERN = Pattern.compile("vertex\\s+(\\d+)\\s+label\\s+\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern EDGE_PATTERN = Pattern.compile("edge\\s+(\\d+)\\s*(->|--)\\s*(\\d+)(?:\\s+weight\\s+([0-9]*\\.?[0-9]+))?", Pattern.CASE_INSENSITIVE);

    public static Graph parseGraph(String dslText) throws IllegalArgumentException {
        if (dslText == null) throw new IllegalArgumentException("DSL 为空");
        List<String> lines = Arrays.asList(dslText.split("\\r?\\n"));
        boolean inBlock = false;

        boolean directed = false; // 默认无向
        String type = "adjacency_list"; // 默认
        int vertexCountExplicit = -1;

        Map<Integer, String> vertexLabels = new HashMap<>();
        List<EdgeDef> edges = new ArrayList<>();

        int maxVertexIdx = -1;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (!inBlock) {
                if (line.equalsIgnoreCase("graph{" ) || line.equalsIgnoreCase("graph {")) {
                    inBlock = true; continue;
                } else {
                    throw new IllegalArgumentException("DSL 必须以 'graph {' 开始");
                }
            }
            if (line.equals("}")) { inBlock = false; break; }

            Matcher mProp = PROPERTY_PATTERN.matcher(line);
            Matcher mV = VERTEX_LABEL_PATTERN.matcher(line);
            Matcher mE = EDGE_PATTERN.matcher(line);
            if (mProp.matches()) {
                String key = mProp.group(1).toLowerCase();
                String val = mProp.group(2);
                switch (key) {
                    case "type": type = val.toLowerCase(); break;
                    case "directed": directed = Boolean.parseBoolean(val); break;
                    case "vertices": vertexCountExplicit = Integer.parseInt(val); break;
                }
            } else if (mV.matches()) {
                int idx = Integer.parseInt(mV.group(1));
                String label = mV.group(2);
                vertexLabels.put(idx, label);
                maxVertexIdx = Math.max(maxVertexIdx, idx);
            } else if (mE.matches()) {
                int src = Integer.parseInt(mE.group(1));
                int dst = Integer.parseInt(mE.group(3));
                String wStr = mE.group(4);
                double w = wStr == null ? 1.0 : Double.parseDouble(wStr);
                edges.add(new EdgeDef(src, dst, w));
                maxVertexIdx = Math.max(maxVertexIdx, Math.max(src, dst));
            } else {
                throw new IllegalArgumentException("无法解析行: " + line);
            }
        }
        if (inBlock) throw new IllegalArgumentException("DSL 缺少结束 '}'");

        int n = vertexCountExplicit > 0 ? vertexCountExplicit : maxVertexIdx + 1;
        if (n <= 0) throw new IllegalArgumentException("未检测到顶点");
        Graph g;
        if ("adjacency_matrix".equals(type)) {
            g = new AdjacencyMatrix(n, directed);
        } else {
            g = new AdjacencyList(n, directed);
        }
        // labels
        for (Map.Entry<Integer, String> e : vertexLabels.entrySet()) {
            g.setVertexLabel(e.getKey(), e.getValue());
        }
        // edges
        for (EdgeDef e : edges) {
            g.addEdge(e.src, e.dst, e.weight);
        }
        return g;
    }

    private static class EdgeDef {
        int src, dst; double weight;
        EdgeDef(int s,int d,double w){src=s;dst=d;weight=w;}
    }
}
