package com.datastruct.visualizer.model.sorting;

/**
 * 排序算法的单个步骤
 * Single step in a sorting algorithm
 */
public class SortingStep {
    
    public enum StepType {
        INITIAL,     // 初始状态
        COMPARE,     // 比较操作
        SWAP,        // 交换操作
        HIGHLIGHT,   // 高亮显示
        SET,         // 设置值
        COMPLETED    // 完成状态
    }
    
    private final int[] arrayState;
    private final StepType type;
    private final int index1;
    private final int index2;
    private final String description;
    private final long timestamp;
    
    public SortingStep(int[] arrayState, StepType type, int index1, int index2, String description) {
        this.arrayState = arrayState.clone();
        this.type = type;
        this.index1 = index1;
        this.index2 = index2;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int[] getArrayState() {
        return arrayState.clone();
    }
    
    public StepType getType() {
        return type;
    }
    
    public int getIndex1() {
        return index1;
    }
    
    public int getIndex2() {
        return index2;
    }
    
    public String getDescription() {
        return description;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("SortingStep{type=%s, index1=%d, index2=%d, description='%s'}", 
                           type, index1, index2, description);
    }
}

