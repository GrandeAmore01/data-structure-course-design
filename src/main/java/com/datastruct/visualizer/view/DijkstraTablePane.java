package com.datastruct.visualizer.view;

import com.datastruct.visualizer.model.graph.DijkstraSnapshot;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

/**
 * 一个简单的可滚动表格，用 GridPane 动态绘制 Dijkstra 快照数据。
 * 不依赖 FXML，可直接 new 后放入 ScrollPane。
 */
public class DijkstraTablePane extends VBox {

    private final GridPane grid = new GridPane();

    public DijkstraTablePane() {
        getChildren().add(grid);
        setPadding(new Insets(10));
    }

    /**
     * 将快照数据显示到表格中。
     * @param snapshots 迭代快照列表
     * @param vertexCount 顶点总数，用于生成列
     */
    public void setData(List<DijkstraSnapshot> snapshots, int vertexCount) {
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.setHgap(4);
        grid.setVgap(2);

        // 第一行：列标题（顶点）
        int col = 0;
        addHeader(col++, "步数/S");
        for (int v = 0; v < vertexCount; v++) {
            addHeader(col++, "v" + v + "\nlen.");
            addHeader(col++, "v" + v + "\npre.");
        }

        // 数据行
        int row = 1;
        for (DijkstraSnapshot snap : snapshots) {
            int c = 0;
            // 步数 + S
            String sStr = snap.getIteration() == 0 ? "初始" : String.valueOf(snap.getIteration());
            sStr += "\n{" + String.join(",", snap.getSSet().stream().map(i -> "v" + i).toList()) + "}";
            addCell(row, c++, sStr);

            double[] dist = snap.getDistances();
            int[] pre = snap.getPredecessors();
            for (int v = 0; v < vertexCount; v++) {
                // length
                String lenStr = Double.isInfinite(dist[v]) ? "∞" : String.valueOf((int) dist[v]);
                addCell(row, c++, lenStr);
                // pre
                String preStr = pre[v] == -1 ? "–" : "v" + pre[v];
                addCell(row, c++, preStr);
            }
            row++;
        }
    }

    private void addHeader(int col, String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("monospaced", 12));
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-font-weight: bold; -fx-border-color: black transparent transparent black; -fx-padding: 2 6 2 6;");
        GridPane.setHalignment(lbl, HPos.CENTER);
        grid.add(lbl, col, 0);
    }

    private void addCell(int row, int col, String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("monospaced", 12));
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-border-color: black transparent transparent black; -fx-padding: 2 6 2 6;");
        GridPane.setHalignment(lbl, HPos.CENTER);
        grid.add(lbl, col, row);
    }
}
