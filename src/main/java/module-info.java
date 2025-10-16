module datastructurevisualizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires org.slf4j;
    
    exports com.datastruct.visualizer;
    exports com.datastruct.visualizer.controller;
    exports com.datastruct.visualizer.model.graph;
    exports com.datastruct.visualizer.model.sorting;
    exports com.datastruct.visualizer.view;
    exports com.datastruct.visualizer.util;
    
     // 关键修复：向 javafx.fxml 开放控制器包（允许反射访问私有成员）
    opens com.datastruct.visualizer.controller to javafx.fxml;


}
