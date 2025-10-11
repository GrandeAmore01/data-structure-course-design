package com.datastruct.visualizer;

import com.datastruct.visualizer.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 数据结构可视化模拟器主应用程序 - 简化版本（非模块化）
 * Data Structure Visualizer Main Application - Simple Version (Non-modular)
 */
public class DataStructureVisualizerAppSimple extends Application {
    
    private static final String TITLE = "数据结构与算法可视化模拟器";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // 加载FXML布局
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            BorderPane root = loader.load();
            
            // 获取控制器并设置舞台引用
            MainController controller = loader.getController();
            controller.setStage(primaryStage);
            
            // 创建场景
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            // 设置舞台属性
            primaryStage.setTitle(TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();
            
            System.out.println("应用程序启动成功");
            
        } catch (Exception e) {
            System.err.println("应用程序启动失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args) {
        // 设置系统属性以确保JavaFX正常工作
        System.setProperty("javafx.preloader", "");
        
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("启动失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
