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
            int tempIndex = i; // temp initial position

            // 高亮并显示 temp（不从数组中删除 key，这里只是告诉可视化层绘制 temp）
            addStep(new SortingStep(workingArray.clone(), SortingStep.StepType.HIGHLIGHT, i, -1,
                    String.format("选择第 %d 个元素 %d 作为待插入元素", i, key), key, tempIndex));

            int j = i - 1;

            // 在已排序序列中找到插入位置
            while (j >= 0 && workingArray[j] > key) {
                // 比较步骤，携带 temp 信息以保证可视化层可以同时显示 temp
                addStep(new SortingStep(workingArray.clone(), SortingStep.StepType.COMPARE, j, i,
                        String.format("比较 %d 和 %d", workingArray[j], key), key, tempIndex));

                // 将较大的元素右移一位（算法实际更改数组），并在步骤中携带 temp 信息
                workingArray[j + 1] = workingArray[j];
                addStep(new SortingStep(workingArray.clone(), SortingStep.StepType.SET, j + 1, -1,
                        String.format("将 %d 向右移动", workingArray[j + 1]), key, tempIndex));

                j--;
            }

            // 插入 key 到目标位置，先在数组中设置，再记录步骤（插入完成，temp 不再需要显示）
            workingArray[j + 1] = key;
            addStep(new SortingStep(workingArray.clone(), SortingStep.StepType.SET, j + 1, -1,
                    String.format("将 %d 插入到位置 %d", key, j + 1), null, null));
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

