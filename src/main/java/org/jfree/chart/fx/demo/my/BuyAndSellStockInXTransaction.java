/* ----------------------------
 * CrosshairOverlayFXDemo1.java
 * ----------------------------
 * Copyright 2014-2022 by David Gilbert. All rights reserved.
 *
 * https://github.com/jfree/jfree-fxdemos
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   - Neither the name of the Object Refinery Limited nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL OBJECT REFINERY LIMITED BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.jfree.chart.fx.demo.my;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.fx.overlay.CrosshairOverlayFX;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A demo showing crosshairs that follow the data points on an XYPlot.
 */
public class BuyAndSellStockInXTransaction extends Application {

    static class MyDemoPane extends StackPane implements ChartMouseListenerFX {

        private final ChartViewer chartViewer;

        private final Crosshair xCrosshair;

        private final Crosshair yCrosshair;

        public MyDemoPane() {
            XYDataset dataset = createDataset();
            JFreeChart chart = createChart(dataset);
            XYPlot plot = (XYPlot) chart.getPlot();
            plot.getDomainAxis().setAutoTickUnitSelection(false);

            this.chartViewer = new ChartViewer(chart);
            this.chartViewer.addChartMouseListener(this);
            getChildren().add(this.chartViewer);

            CrosshairOverlayFX crosshairOverlay = new CrosshairOverlayFX();
            this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY,
                    new BasicStroke(0f));
            this.xCrosshair.setStroke(new BasicStroke(1.5f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1,
                    new float[]{2.0f, 2.0f}, 0));
            this.xCrosshair.setLabelVisible(true);
            this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY,
                    new BasicStroke(0f));
            this.yCrosshair.setStroke(new BasicStroke(1.5f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1,
                    new float[]{2.0f, 2.0f}, 0));
            this.yCrosshair.setLabelVisible(true);
            crosshairOverlay.addDomainCrosshair(xCrosshair);
            crosshairOverlay.addRangeCrosshair(yCrosshair);

            Platform.runLater(() -> this.chartViewer.getCanvas().addOverlay(crosshairOverlay));
        }

        @Override
        public void chartMouseClicked(ChartMouseEventFX event) {
            // ignore
        }

        @Override
        public void chartMouseMoved(ChartMouseEventFX event) {
            Rectangle2D dataArea = this.chartViewer.getCanvas().getRenderingInfo().getPlotInfo().getDataArea();
            JFreeChart chart = event.getChart();
            XYPlot plot = (XYPlot) chart.getPlot();
            ValueAxis xAxis = plot.getDomainAxis();
            double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea,
                    RectangleEdge.BOTTOM);
            // make the crosshairs disappear if the mouse is out of range
            if (!xAxis.getRange().contains(x)) {
                x = Double.NaN;
            }

            ValueAxis yAxis = plot.getRangeAxis();
            double y = yAxis.java2DToValue(event.getTrigger().getY(), dataArea,
                    RectangleEdge.LEFT);
            if (!yAxis.getRange().contains(y)) {
                y = Double.NaN;
            }
            this.xCrosshair.setValue(x);
            this.yCrosshair.setValue(y);
        }

    }

    private static XYDataset createDataset() {
        Solution solution = new Solution();
        solution.maxProfit(3, solution.createPrices());
        List<List<Integer>> track = solution.track;

        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(createXYSeries(track.get(0), "price"));
        int count = (track.size() - 1) / 2;
        for (int i = 0; i < count; i++) {
            xySeriesCollection.addSeries(createXYSeries(track.get(2 * i + 1), "buy" + i));
            xySeriesCollection.addSeries(createXYSeries(track.get(2 * i + 2), "sell" + i));
        }
        return xySeriesCollection;
    }

    private static XYSeries createXYSeries(List<Integer> list, String name) {
        XYSeries xySeries = new XYSeries(name);
        for (int i = 0; i < list.size(); i++) {
            xySeries.add(i, list.get(i));
        }
        return xySeries;
    }

    private static JFreeChart createChart(XYDataset dataset) {
        return ChartFactory.createXYLineChart(
                "BuyAndSellStockInXTransaction", "X", "Y", dataset);
    }

    /**
     * Adds a chart viewer to the stage and displays it.
     *
     * @param stage the stage.
     */
    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new MyDemoPane()));
        stage.setTitle("BuyAndSellStockInXTransaction");
        stage.setWidth(1600);
        stage.setHeight(800);
        stage.show();
    }

    /**
     * Entry point.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    private static class Solution {

        private List<List<Integer>> track = new ArrayList<>();

        private int[] createPrices() {
            return new int[]{13, 13, 15, 11, 12, 13, 12, 14, 7, 13, 0, 9, 3, 2, 5, 4};
        }

        public int maxProfit(int k, int[] prices) {
            int[] dp = new int[k * 2];
            for (int i = 0; i < dp.length; i++) {
                dp[i] = Integer.MAX_VALUE;
                i++;
                dp[i] = 0;
            }

            track.add(new ArrayList<>());
            for (int price : prices) {
                track.get(0).add(price);
            }
            for (int i = 0; i < k * 2; i++) {
                track.add(new ArrayList<>());
            }

            for (int i = 0; i < prices.length; i++) {
                dp[0] = Math.min(dp[0], prices[i]);
                dp[1] = Math.max(dp[1], prices[i] - dp[0]);
                track.get(1).add(dp[0]);
                track.get(2).add(dp[1]);
                for (int j = 2; j < dp.length; j++) {
                    dp[j] = Math.min(dp[j], prices[i] - dp[j - 1]);
                    track.get(j + 1).add(dp[j]);
                    j++;
                    dp[j] = Math.max(dp[j], prices[i] - dp[j - 1]);
                    track.get(j + 1).add(dp[j]);

                }
            }

            return dp[dp.length - 1];
        }
    }
}

