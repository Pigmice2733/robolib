package com.pigmice.frc.lib.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Plot provides a base class for plotting classes.
 */
class Plot {
    protected XYPlot plot;
    protected JFreeChart chart;
    protected String title;
    protected XYSeriesCollection dataset;
    protected XYLineAndShapeRenderer renderer;

    private final static List<Color> colors = Arrays.asList(Color.BLUE, Color.GREEN, Color.BLACK, Color.RED,
            Color.MAGENTA, Color.ORANGE);

    /**
     * Indicates whether a specific type/category of plot should be generated, based
     * on command line flags.
     *
     * @return Whether the specified plot type should be generated
     */
    public static Boolean shouldGraph(String type) {
        String graphProp = System.getProperty("graph").toLowerCase();
        return (graphProp.equals(type) || graphProp.equals("all") || graphProp.equals("a"));
    }

    /**
     * Initializes and sets up the renderer and plot. A JFreeChart must already have
     * been created.
     */
    protected void initialize(Color color) {
        plot = chart.getXYPlot();

        renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(false);
        renderer.setDefaultLinesVisible(true);
        renderer.setSeriesPaint(0, color);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        plot.setRangeZeroBaselineVisible(true);
        plot.setRangeZeroBaselineStroke(new BasicStroke(2.0f));
        plot.getRangeAxis().setAxisLineVisible(true);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
    }

    /**
     * Adds a new line data series to the plot in a custom color.
     *
     * @param series The XY data series to add to the plot as lines
     * @param color  The color to use for the data series
     */
    protected void addLineSeries(XYSeries series, Color color) {
        dataset.addSeries(series);

        int seriesIndex = dataset.getSeriesCount() - 1;
        renderer.setSeriesPaint(seriesIndex, color);
        renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
    }

    /**
     * Adds a new point data series to the plot in a custom color.
     *
     * @param series The XY data series to add to the plot
     * @param color  The color to use for the data series
     */
    protected void addPointSeries(XYSeries series, Color color) {
        dataset.addSeries(series);

        int seriesIndex = dataset.getSeriesCount() - 1;
        renderer.setSeriesPaint(seriesIndex, color);

        renderer.setSeriesLinesVisible(seriesIndex, false);
        renderer.setSeriesShapesVisible(seriesIndex, true);
    }

    /**
     * Gets a color based on the index of the series.
     *
     * @param seriesIndex The index of the series to choose a color for
     * @return The color for the series
     */
    protected static Color getColor(int seriesIndex) {
        int index = seriesIndex % colors.size();
        return colors.get(index);
    }

    /**
     * Saves the chart to the specified directory, within ./graphs. If the specified
     * directory doesn't exist, it will be created. Chart will be saved as a png
     * file with the same name as the title of the chart. If a file of the same name
     * already exists, it will be deleted.
     *
     * @param subDirectory The directory to save the chart in
     */
    public void save(String subDirectory) {
        String directory = "./graphs" + File.separator + subDirectory;
        File file = new File(directory + File.separator + title.toLowerCase() + ".png");

        File path = new File(directory);

        if (!path.exists()) {
            if (!file.getParentFile().mkdirs()) {
                System.err.println("Failed to create plot output directory " + file.getName());
                return;
            }
        }

        try {
            boolean success;
            if (!file.exists()) {
                success = file.createNewFile();
            } else {
                success = file.delete() && file.createNewFile();
            }
            if (!success) {
                System.err.println("Failed to create file " + file.getName());
                return;
            }
        } catch (IOException ex) {
            System.err.println(ex);
            return;
        }

        double aspectRatio = plot.getDomainAxis().getRange().getLength() / plot.getRangeAxis().getRange().getLength();

        int width = 1280;
        int height = (int) ((double) width / aspectRatio);

        try {
            ChartUtils.saveChartAsPNG(file, chart, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
