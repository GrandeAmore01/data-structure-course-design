package com.datastruct.visualizer.view;

import com.datastruct.visualizer.model.sorting.SortingStep;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * 排序算法可视化面板
 * Sorting Algorithm Visualization Pane
 */
public class SortingVisualizationPane extends Pane {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private int[] currentArray;
    private SortingStep currentStep;
    
    // 可视化配置
    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 400;
    private static final double MARGIN = 50;
    
    // 颜色配置
    private Color defaultColor = Color.LIGHTBLUE;
    private Color compareColor = Color.YELLOW;
    private Color swapColor = Color.RED;
    private Color highlightColor = Color.GREEN;
    private Color completedColor = Color.LIGHTGREEN;
    
    public SortingVisualizationPane() {
        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        
        setupCanvas();
        getChildren().add(canvas);
    }
    
    private void setupCanvas() {
        canvas.setStyle("-fx-border-color: gray; -fx-border-width: 1;");
        
        // 初始化画布
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    }
    
    /**
     * 显示排序步骤
     */
    public void showStep(SortingStep step) {
        this.currentStep = step;
        this.currentArray = step.getArrayState();
        redraw();
    }
    
    /**
     * 设置数组并绘制
     */
    public void setArray(int[] array) {
        this.currentArray = array.clone();
        this.currentStep = null;
        redraw();
    }
    
    /**
     * 重绘数组可视化
     */
    private void redraw() {
        if (currentArray == null || currentArray.length == 0) {
            return;
        }
        
        // 清空画布
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        
        // 计算绘制参数
        int arrayLength = currentArray.length;
        double availableWidth = CANVAS_WIDTH - 2 * MARGIN;
        double barWidth = availableWidth / arrayLength;
        
        // 找到数组中的最大值和最小值用于缩放
        int maxValue = findMax(currentArray);
        int minValue = findMin(currentArray);
        double valueRange = maxValue - minValue;
        if (valueRange == 0) valueRange = 1; // 避免除零
        
        double availableHeight = CANVAS_HEIGHT - 2 * MARGIN - 50; // 留空间给文字
        
        // 绘制数组元素
        for (int i = 0; i < arrayLength; i++) {
            double x = MARGIN + i * barWidth;
            double barHeight = (currentArray[i] - minValue) * availableHeight / valueRange;
            double y = CANVAS_HEIGHT - MARGIN - barHeight;
            
            // 根据当前步骤确定颜色
            Color barColor = getBarColor(i);
            gc.setFill(barColor);
            
            // 绘制柱状图
            gc.fillRect(x, y, barWidth - 2, barHeight);
            
            // 绘制边框
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeRect(x, y, barWidth - 2, barHeight);
            
            // 绘制数值标签
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font(12));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(currentArray[i]), 
                       x + (barWidth - 2) / 2, 
                       CANVAS_HEIGHT - MARGIN + 15);
            
            // 绘制索引标签
            gc.setFont(Font.font(10));
            gc.fillText(String.valueOf(i), 
                       x + (barWidth - 2) / 2, 
                       CANVAS_HEIGHT - MARGIN + 30);
        }
        
        // 绘制步骤描述
        if (currentStep != null) {
            drawStepDescription();
        }

        // 如果 currentStep 包含 temp 信息，绘制悬浮 temp（橙色）
        if (currentStep != null && currentStep.getTempValue() != null && currentStep.getTempIndex() != null) {
            int tempIdx = currentStep.getTempIndex();
            int[] arr = currentStep.getArrayState();
            if (tempIdx >= 0 && tempIdx < arr.length) {
                double x = MARGIN + tempIdx * ((CANVAS_WIDTH - 2 * MARGIN) / arr.length);
                double cx = x + (barWidth - 2) / 2;
                double cy = CANVAS_HEIGHT - MARGIN - (CANVAS_HEIGHT - 2 * MARGIN - 50) - 30; // 悬浮位置
                gc.setFill(Color.ORANGE);
                gc.fillOval(cx - 10, cy, 20, 20);
                gc.setStroke(Color.BLACK);
                gc.strokeOval(cx - 10, cy, 20, 20);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font(12));
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(String.valueOf(currentStep.getTempValue()), cx, cy + 14);
            }
        }
    }
    
    /**
     * 根据当前步骤确定柱状图颜色
     */
    private Color getBarColor(int index) {
        if (currentStep == null) {
            return defaultColor;
        }
        
        switch (currentStep.getType()) {
            case INITIAL:
                break;
            case COMPARE:
                if (index == currentStep.getIndex1() || index == currentStep.getIndex2()) {
                    return compareColor;
                }
                break;
            case SWAP:
                if (index == currentStep.getIndex1() || index == currentStep.getIndex2()) {
                    return swapColor;
                }
                break;
            case HIGHLIGHT:
            case SET:
                if (index == currentStep.getIndex1()) {
                    return highlightColor;
                }
                break;
            case COMPLETED:
                return completedColor;
        }
        
        return defaultColor;
    }
    
    /**
     * 绘制步骤描述
     */
    private void drawStepDescription() {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(14));
        gc.setTextAlign(TextAlignment.LEFT);
        
        String description = currentStep.getDescription();
        gc.fillText(description, MARGIN, 30);
        
        // 绘制步骤类型
        String stepType = getStepTypeDescription(currentStep.getType());
        gc.setFont(Font.font(12));
        gc.fillText("操作类型: " + stepType, MARGIN, 50);
    }
    
    /**
     * 获取步骤类型描述
     */
    private String getStepTypeDescription(SortingStep.StepType type) {
        switch (type) {
            case INITIAL:
                return "初始状态";
            case COMPARE:
                return "比较操作";
            case SWAP:
                return "交换操作";
            case HIGHLIGHT:
                return "高亮显示";
            case SET:
                return "设置值";
            case COMPLETED:
                return "排序完成";
            default:
                return "未知操作";
        }
    }
    
    /**
     * 找到数组中的最大值
     */
    private int findMax(int[] array) {
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    
    /**
     * 找到数组中的最小值
     */
    private int findMin(int[] array) {
        int min = array[0];
        for (int value : array) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
    
    /**
     * 设置颜色主题
     */
    public void setColorTheme(Color defaultColor, Color compareColor, Color swapColor, 
                             Color highlightColor, Color completedColor) {
        this.defaultColor = defaultColor;
        this.compareColor = compareColor;
        this.swapColor = swapColor;
        this.highlightColor = highlightColor;
        this.completedColor = completedColor;
        redraw();
    }
}

