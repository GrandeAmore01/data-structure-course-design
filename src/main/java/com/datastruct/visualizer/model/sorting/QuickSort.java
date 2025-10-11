package com.datastruct.visualizer.model.sorting;

/**
 * 快速排序算法
 * Quick Sort Algorithm
 */
public class QuickSort extends SortingAlgorithm {
    
    @Override
    protected void performSort() {
        quickSort(0, workingArray.length - 1);
    }
    
    private void quickSort(int low, int high) {
        if (low < high) {
            highlight(low, String.format("开始快速排序区间 [%d, %d]", low, high));
            
            // 分割数组并获取分割点
            int pivotIndex = partition(low, high);
            
            highlight(pivotIndex, String.format("分割完成，基准元素 %d 在位置 %d", workingArray[pivotIndex], pivotIndex));
            
            // 递归排序左右子数组
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        }
    }
    
    private int partition(int low, int high) {
        // 选择最后一个元素作为基准
        int pivot = workingArray[high];
        highlight(high, String.format("选择 %d 作为基准元素", pivot));
        
        int i = low - 1; // 小于基准的元素的最后位置
        
        for (int j = low; j < high; j++) {
            compare(j, high, String.format("比较 %d 和基准 %d", workingArray[j], pivot));
            
            if (workingArray[j] <= pivot) {
                i++;
                if (i != j) {
                    swap(i, j);
                } else {
                    highlight(j, String.format("元素 %d 已经在正确位置", workingArray[j]));
                }
            }
        }
        
        // 将基准元素放到正确位置
        swap(i + 1, high);
        
        return i + 1;
    }
    
    @Override
    public String getAlgorithmName() {
        return "快速排序";
    }
    
    @Override
    public String getDescription() {
        return "通过一趟排序将要排序的数据分割成独立的两部分，其中一部分的所有数据都比另外一部分的所有数据都要小，然后再按此方法对这两部分数据分别进行快速排序。";
    }
    
    @Override
    public String getTimeComplexity() {
        return "最好: O(n log n), 平均: O(n log n), 最坏: O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(log n)";
    }
}

