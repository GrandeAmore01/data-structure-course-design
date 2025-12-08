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
    private static final Pattern ADD_EDGE_PATTERN = Pattern.compile("ADD_EDGE\\s+(\\d+)\\s+(\\d+)\\s+([0-9]*\\.?[0-9]+)", Pattern.CASE_INSENSITIVE);

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
            Matcher mAddE = ADD_EDGE_PATTERN.matcher(line);
            if (line.toUpperCase().startsWith("ADD_VERTEX")) {
                // ignore, vertex count handled later
                String[] tok = line.split("\\s+");
                if (tok.length >= 3) {
                    int idx = Integer.parseInt(tok[2]);
                    maxVertexIdx = Math.max(maxVertexIdx, idx);
                }
                continue;
            } else if (mAddE.matches()) {
                int src = Integer.parseInt(mAddE.group(1));
                int dst = Integer.parseInt(mAddE.group(2));
                double w = Double.parseDouble(mAddE.group(3));
                edges.add(new EdgeDef(src,dst,w));
                maxVertexIdx = Math.max(maxVertexIdx, Math.max(src,dst));
            } else if (mProp.matches()) {
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
            } else if (line.toUpperCase().startsWith("RUN_")) {
                // 算法执行指令由控制器处理，这里忽略
                continue;
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
        // labels（导入后统一以索引为准，避免显示与内部索引不一致）
        for (Map.Entry<Integer, String> e : vertexLabels.entrySet()) {
            g.setVertexLabel(e.getKey(), e.getValue());
        }
        g.resetVertexLabelsToIndex();
        // edges
        for (EdgeDef e : edges) {
            g.addEdge(e.src, e.dst, e.weight);
        }
        return g;
    }

    public static java.util.List<DslCommand> parseCommands(String text) throws IllegalArgumentException {
        java.util.List<DslCommand> list = new java.util.ArrayList<>();
        if (text == null) return list;
        String[] lines = text.split("\\r?\\n");
        int lineNo = 0;
        for (String raw: lines) {
            lineNo++;
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String lower = line.toLowerCase();
            try {
                if (lower.startsWith("add vertex")) {
                    String[] tok = line.split("\\s+",4);
                    int id = Integer.parseInt(tok[2]);
                    String label = tok.length>3? line.substring(line.indexOf("label")+6).replaceAll("\"","") : null;
                    if (label!=null) list.add(new DslCommand(DslCommand.Type.ADD_VERTEX,lineNo,String.valueOf(id),label));
                    else list.add(new DslCommand(DslCommand.Type.ADD_VERTEX,lineNo,String.valueOf(id)));
                } else if (lower.startsWith("remove vertex")) {
                    String[] tok = line.split("\\s+");
                    int id = Integer.parseInt(tok[2]);
                    list.add(new DslCommand(DslCommand.Type.REMOVE_VERTEX,lineNo,String.valueOf(id)));
                } else if (lower.startsWith("add edge")) {
                    // add edge u -> v weight w
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("add edge (\\d+) .*? (\\d+)(?: .*? (\\d+(?:\\.\\d+)?))?", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(line);
                    if(!m.find()) throw new IllegalArgumentException();
                    String u=m.group(1),v=m.group(2),w=m.group(3)==null?"1":m.group(3);
                    list.add(new DslCommand(DslCommand.Type.ADD_EDGE,lineNo,u,v,w));
                } else if (lower.startsWith("remove edge")) {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("remove edge (\\d+) .*? (\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(line);
                    if(!m.find()) throw new IllegalArgumentException();
                    list.add(new DslCommand(DslCommand.Type.REMOVE_EDGE,lineNo,m.group(1),m.group(2)));
                } else if (lower.startsWith("set label")) {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("set label (\\d+) \"([^\"]*)\"", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(line);
                    if(!m.find()) throw new IllegalArgumentException();
                    list.add(new DslCommand(DslCommand.Type.SET_LABEL,lineNo,m.group(1),m.group(2)));
                } else if (lower.startsWith("set directed")) {
                    String[] tok=line.split("\\s+");
                    list.add(new DslCommand(DslCommand.Type.SET_DIRECTED,lineNo,tok[2]));
                } else if (lower.startsWith("run dfs")) {
                    String[] tok=line.split("\\s+");
                    int idx=java.util.Arrays.asList(tok).indexOf("start");
                    list.add(new DslCommand(DslCommand.Type.RUN_DFS,lineNo,tok[idx+1]));
                } else if (lower.startsWith("run bfs")) {
                    String[] tok=line.split("\\s+");
                    int idx=java.util.Arrays.asList(tok).indexOf("start");
                    list.add(new DslCommand(DslCommand.Type.RUN_BFS,lineNo,tok[idx+1]));
                } else if (lower.startsWith("run dijkstra")) {
                    java.util.regex.Matcher m=java.util.regex.Pattern.compile("run dijkstra start (\\d+) target (\\d+)",java.util.regex.Pattern.CASE_INSENSITIVE).matcher(line);
                    if(!m.find()) throw new IllegalArgumentException();
                    list.add(new DslCommand(DslCommand.Type.RUN_DIJKSTRA,lineNo,m.group(1),m.group(2)));
                } else if (lower.startsWith("run mst")) {
                    list.add(new DslCommand(DslCommand.Type.RUN_MST,lineNo));
                } else {
                    throw new IllegalArgumentException("未知指令");
                }
            } catch(Exception ex){
                throw new IllegalArgumentException("解析 DSL 第 "+lineNo+" 行失败: "+line);
            }
        }
        return list;
    }

    private static class EdgeDef {
        int src, dst; double weight;
        EdgeDef(int s,int d,double w){src=s;dst=d;weight=w;}
    }
}
