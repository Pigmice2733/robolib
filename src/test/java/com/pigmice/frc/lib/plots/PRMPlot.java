package com.pigmice.frc.lib.plots;


import java.util.List;

import com.pigmice.frc.lib.planning.structures.Point;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * PRMPlot provides a simple interface to plot PRM roadmaps and save them as charts.
 */
public class PRMPlot extends Plot {
    /**
     * Constructs a new plot
     *
     * @param title    Title of the plot
     */
    public PRMPlot(String title) {
        this.title = title;

        dataset = new XYSeriesCollection();

        chart = ChartFactory.createXYLineChart(title, "Dim 1", "Dim 2", dataset, PlotOrientation.VERTICAL, true, false,
                false);
        chart.removeLegend();

        initialize(getColor(0));
    }

    public void addEdge(double x, double y, double a, double b) {
        XYSeries series = new XYSeries(String.valueOf(x) + String.valueOf(y) + String.valueOf(a) + String.valueOf(b), false);
        series.add(x, y);
        series.add(a, b);
        addLineSeries(series, getColor(1));
    }

    public void addSolution(List<Point> path) {
        XYSeries solution = new XYSeries("solution", false);
        for (Point p : path) {
            solution.add(p.get(0), p.get(1));
        }

        addLineSeries(solution, getColor(0));
    }
}
