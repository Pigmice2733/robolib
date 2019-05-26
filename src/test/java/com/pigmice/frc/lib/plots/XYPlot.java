package com.pigmice.frc.lib.plots;

import java.awt.Color;
import java.util.ArrayList;

import com.pigmice.frc.lib.utils.Point;
import com.pigmice.frc.lib.utils.XY;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * XYPlot provides a simple interface to plot XY data and save it as charts.
 */
public class XYPlot extends Plot {
    /**
     * Data source for a series to plot.
     */
    public interface Data {
        XY get(double time);
    }

    /**
     * Constructs a new plot with one data series to start with.
     *
     * @param title      Title of the plot
     * @param seriesName Name of the first data series
     * @param data       Data source for the first data series
     * @param duration   Duration of the first data series (maximum value of time to
     *                   get data at)
     * @param step       Step value for time
     */
    public XYPlot(String title, String seriesName, Data data, double duration, double step) {
        this(title, seriesName, data, duration, step, getColor(0));
    }

    /**
     * Constructs a new plot with one data series of a custom color to start with.
     *
     * @param title      Title of the plot
     * @param seriesName Name of the first data series
     * @param data       Data source for the first data series
     * @param duration   Duration of the first data series (maximum value of time to
     *                   get data at)
     * @param step       Step value for time
     * @param color      The color to use for the first data series
     */
    public XYPlot(String title, String seriesName, Data data, double duration, double step, Color color) {
        this.title = title;

        XYSeries series = createSeries(seriesName, data, duration, step);
        dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        chart = ChartFactory.createXYLineChart(title, "X", "Y", dataset, PlotOrientation.VERTICAL, true, false, false);

        this.initialize(color);
    }

    /**
     * Adds a new point scatter plot.
     *
     * @param dataName Name of the point data series
     * @param points   The points to plot
     */
    public void addPoints(String dataName, ArrayList<Point> points) {
        XYSeries series = new XYSeries(dataName, false);
        for (XY p : points) {
            series.add(p.getX(), p.getY());
        }
        addPointSeries(series, Color.BLACK);
    }

    /**
     * Adds a new data series to the plot.
     *
     * @param seriesName Name of the data series
     * @param data       Data source for the series
     * @param duration   Duration of the first data series (maximum value of time to
     *                   get data at)
     * @param step       Step value for time
     */
    public void addSeries(String seriesName, Data data, double duration, double step) {
        int seriesIndex = dataset.getSeriesCount();
        addSeries(seriesName, data, duration, step, getColor(seriesIndex));
    }

    /**
     * Adds a new data series to the plot in a custom color.
     *
     * @param seriesName Name of the data series
     * @param data       Data source for the series
     * @param duration   Duration of the first data series (maximum value of time to
     *                   get data at)
     * @param step       Step value for time
     * @param color      The color to use for the data series
     */
    public void addSeries(String seriesName, Data data, double duration, double step, Color color) {
        XYSeries series = createSeries(seriesName, data, duration, step);
        addLineSeries(series, color);
    }

    /**
     * Creates a new XY data series
     *
     * @param seriesName Name of the series
     * @param data       Data source for the series
     * @param duration   Duration of the first data series (maximum value of time to
     *                   get data at)
     * @param step       Step value for time
     * @return An XY data series
     */
    private XYSeries createSeries(String seriesName, Data data, double duration, double step) {
        XYSeries series = new XYSeries(seriesName, false);
        for (double i = 0.0; i <= duration; i += step) {
            XY p = data.get(i);
            series.add(p.getX(), p.getY());
        }
        return series;
    }
}
