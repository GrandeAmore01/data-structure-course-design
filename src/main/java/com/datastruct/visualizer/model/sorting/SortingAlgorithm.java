package com.datastruct.visualizer.model.sorting;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序算法抽象基类
 * Abstract base class for sorting algorithms
 */
public abstract class SortingAlgorithm {
    
    protected List<SortingStep> steps;
    protected int[] originalArray;
    protected int[] workingArray;
    
    public SortingAlgorithm() {
        this.steps = new ArrayList<>();
    }
    
    /**
     * 执行排序算法
     * @param array 待排序数组
     * @return 排序步骤列表
     */
    public List<SortingStep> sort(int[] array) {
        this.originalArray = array.clone();
        this.workingArray = array.clone();
        this.steps.clear();
        
        // 记录初始状态
        addStep(SortingStep.StepType.INITIAL, -1, -1, "初始数组状态");
        
        // 执行具体的排序算法
        performSort();
        
        // 记录完成状态
        addStep(SortingStep.StepType.COMPLETED, -1, -1, "排序完成");
        
        return new ArrayList<>(steps);
    }
    
    /**
     * 子类实现具体的排序算法
     */
    protected abstract void performSort();
    
    /**
     * 获取算法名称
     */
    public abstract String getAlgorithmName();
    
    /**
     * 获取算法描述
     */
    public abstract String getDescription();
    
    /**
     * 获取时间复杂度
     */
    public abstract String getTimeComplexity();
    
    /**
     * 获取空间复杂度
     */
    public abstract String getSpaceComplexity();
    
    /**
     * 添加排序步骤
     */
    protected void addStep(SortingStep.StepType type, int index1, int index2, String description) {
        SortingStep step = new SortingStep(
            workingArray.clone(),
            type,
            index1,
            index2,
            description
        );
        steps.add(step);
    }

    /**
     * 兼容：直接添加已有的 SortingStep（允许带 temp 信息的步骤对象）
     */
    protected void addStep(SortingStep step) {
        steps.add(step);
    }
    
    /**
     * 交换数组中两个元素
     */
    protected void swap(int i, int j) {
        if (i != j && i >= 0 && i < workingArray.length && j >= 0 && j < workingArray.length) {
            int temp = workingArray[i];
            workingArray[i] = workingArray[j];
            workingArray[j] = temp;
            
            addStep(SortingStep.StepType.SWAP, i, j, 
                String.format("交换位置 %d 和 %d 的元素: %d <-> %d", i, j, workingArray[j], workingArray[i]));
        }
    }
    
    /**
     * 比较两个元素
     */
    protected boolean compare(int i, int j, String description) {
        addStep(SortingStep.StepType.COMPARE, i, j, description);
        return workingArray[i] > workingArray[j];
    }
    
    /**
     * 标记当前操作的元素
     */
    protected void highlight(int index, String description) {
        addStep(SortingStep.StepType.HIGHLIGHT, index, -1, description);
    }
    
    /**
     * 设置元素值
     */
    protected void setValue(int index, int value, String description) {
        workingArray[index] = value;
        addStep(SortingStep.StepType.SET, index, -1, description);
    }
}

