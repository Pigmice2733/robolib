package com.pigmice.frc.lib.plots;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * TimePlot provides a simple interface to plot values over time and save as
 * charts.
 */
public class TimePlot extends Plot {
    private double duration;

    /**
     * Data source for a series to plot.
     */
    public interface Data {
        double get(double time);
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
    public TimePlot(String title, String seriesName, Data data, double duration, double step) {
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
    public TimePlot(String title, String seriesName, Data data, double duration, double step, Color color) {
        this.title = title;
        this.duration = duration;

        XYSeries series = createSeries(seriesName, data, duration, step);
        dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        chart = ChartFactory.createXYLineChart(title, "Time", "Value", dataset, PlotOrientation.VERTICAL, true, false,
                false);

        initialize(color);
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
    public void addSeries(String seriesName, Data data, double step) {
        int seriesIndex = dataset.getSeriesCount();
        addSeries(seriesName, data, step, getColor(seriesIndex));
    }

    /**
     * Adds a new data series to the plot in a custom color.
     *
     * @param seriesName Name of the data series
     * @param data       Data source for the series
     * @param step       Step value for time
     * @param color      The color to use for the data series
     */
    public void addSeries(String seriesName, Data data, double step, Color color) {
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
        XYSeries series = new XYSeries(seriesName);
        for (double i = 0.0; i < duration; i += step) {
            series.add(i, data.get(i));
        }
        series.add(duration, data.get(duration));
        return series;
    }
}
