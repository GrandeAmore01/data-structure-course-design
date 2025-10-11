package com.datastruct.visualizer.model.sorting;

/**
 * 简单选择排序算法
 * Selection Sort Algorithm
 */
public class SelectionSort extends SortingAlgorithm {
    
    @Override
    protected void performSort() {
        int n = workingArray.length;
        
        for (int i = 0; i < n - 1; i++) {
            highlight(i, String.format("开始第 %d 轮选择，当前位置: %d", i + 1, i));
            
            int minIndex = i;
            int minValue = workingArray[i];
            
            // 在未排序部分找到最小元素
            for (int j = i + 1; j < n; j++) {
                compare(j, minIndex, String.format("比较 %d 和当前最小值 %d", workingArray[j], minValue));
                
                if (workingArray[j] < minValue) {
                    minIndex = j;
                    minValue = workingArray[j];
                    highlight(j, String.format("找到新的最小值 %d 在位置 %d", minValue, j));
                }
            }
            
            // 如果找到更小的元素，进行交换
            if (minIndex != i) {
                swap(i, minIndex);
            } else {
                highlight(i, String.format("位置 %d 的元素 %d 已经是最小值，无需交换", i, workingArray[i]));
            }
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "简单选择排序";
    }
    
    @Override
    public String getDescription() {
        return "在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，然后，再从剩余未排序元素中继续寻找最小（大）元素。";
    }
    
    @Override
    public String getTimeComplexity() {
        return "最好: O(n²), 平均: O(n²), 最坏: O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}

