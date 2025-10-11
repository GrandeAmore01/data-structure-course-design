package com.datastruct.visualizer.model.sorting;

/**
 * 直接插入排序算法
 * Insertion Sort Algorithm
 */
public class InsertionSort extends SortingAlgorithm {
    
    @Override
    protected void performSort() {
        int n = workingArray.length;
        
        for (int i = 1; i < n; i++) {
            int key = workingArray[i];
            highlight(i, String.format("选择第 %d 个元素 %d 作为待插入元素", i, key));
            
            int j = i - 1;
            
            // 在已排序序列中找到插入位置
            while (j >= 0 && workingArray[j] > key) {
                compare(j, i, String.format("比较 %d 和 %d", workingArray[j], key));
                setValue(j + 1, workingArray[j], String.format("将 %d 向右移动", workingArray[j]));
                j--;
            }
            
            // 插入元素
            setValue(j + 1, key, String.format("将 %d 插入到位置 %d", key, j + 1));
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "直接插入排序";
    }
    
    @Override
    public String getDescription() {
        return "通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。";
    }
    
    @Override
    public String getTimeComplexity() {
        return "最好: O(n), 平均: O(n²), 最坏: O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}

