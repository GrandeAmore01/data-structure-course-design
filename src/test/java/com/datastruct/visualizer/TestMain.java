// package com.datastruct.visualizer;

// import com.datastruct.visualizer.model.graph.*;
// import com.datastruct.visualizer.model.sorting.*;

// /**
//  * 简单测试类，验证核心功能
//  */
// public class TestMain {
    
//     public static void main(String[] args) {
//         System.out.println("数据结构可视化器测试开始...\n");
        
//         // 测试图数据结构
//         testGraphStructures();
        
//         // 测试排序算法
//         testSortingAlgorithms();
        
//         System.out.println("所有测试完成！");
//     }
    
//     private static void testGraphStructures() {
//         System.out.println("=== 测试图数据结构 ===");
        
//         // 测试邻接矩阵
//         System.out.println("测试邻接矩阵:");
//         Graph matrixGraph = new AdjacencyMatrix(4, false);
//         matrixGraph.addEdge(0, 1, 1.0);
//         matrixGraph.addEdge(1, 2, 2.0);
//         matrixGraph.addEdge(2, 3, 3.0);
//         matrixGraph.addEdge(3, 0, 4.0);
        
//         System.out.println("顶点数: " + matrixGraph.getNumVertices());
//         System.out.println("DFS遍历: " + matrixGraph.depthFirstSearch(0));
//         System.out.println("BFS遍历: " + matrixGraph.breadthFirstSearch(0));
        
//         // 测试邻接表
//         System.out.println("\n测试邻接表:");
//         Graph listGraph = new AdjacencyList(4, false);
//         listGraph.addEdge(0, 1, 1.0);
//         listGraph.addEdge(1, 2, 2.0);
//         listGraph.addEdge(2, 3, 3.0);
//         listGraph.addEdge(3, 0, 4.0);
        
//         System.out.println("顶点数: " + listGraph.getNumVertices());
//         System.out.println("DFS遍历: " + listGraph.depthFirstSearch(0));
//         System.out.println("BFS遍历: " + listGraph.breadthFirstSearch(0));
        
//         // 测试最小生成树
//         try {
//             System.out.println("\n测试最小生成树 (Kruskal):");
//             var mstEdges = MST.kruskal(listGraph);
//             System.out.println("MST边数: " + mstEdges.size());
//             System.out.println("MST总权重: " + MST.calculateMSTWeight(mstEdges));
//         } catch (Exception e) {
//             System.out.println("MST测试出错: " + e.getMessage());
//         }
        
//         System.out.println();
//     }
    
//     private static void testSortingAlgorithms() {
//         System.out.println("=== 测试排序算法 ===");
        
//         int[] testArray = {64, 34, 25, 12, 22, 11, 90};
//         System.out.println("原始数组: " + java.util.Arrays.toString(testArray));
        
//         // 测试插入排序
//         System.out.println("\n测试插入排序:");
//         InsertionSort insertionSort = new InsertionSort();
//         var insertionSteps = insertionSort.sort(testArray.clone());
//         System.out.println("步骤数: " + insertionSteps.size());
//         System.out.println("最终结果: " + java.util.Arrays.toString(
//             insertionSteps.get(insertionSteps.size() - 1).getArrayState()));
        
//         // 测试选择排序
//         System.out.println("\n测试选择排序:");
//         SelectionSort selectionSort = new SelectionSort();
//         var selectionSteps = selectionSort.sort(testArray.clone());
//         System.out.println("步骤数: " + selectionSteps.size());
//         System.out.println("最终结果: " + java.util.Arrays.toString(
//             selectionSteps.get(selectionSteps.size() - 1).getArrayState()));
        
//         // 测试快速排序
//         System.out.println("\n测试快速排序:");
//         QuickSort quickSort = new QuickSort();
//         var quickSteps = quickSort.sort(testArray.clone());
//         System.out.println("步骤数: " + quickSteps.size());
//         System.out.println("最终结果: " + java.util.Arrays.toString(
//             quickSteps.get(quickSteps.size() - 1).getArrayState()));
        
//         System.out.println();
//     }
// }

