package com.pigmice.frc.lib.plots;

import com.pigmice.frc.lib.motion.setpoint.ISetpoint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * ProfilePlot provides a simple interface to plot motion profiles and save as
 * charts.
 */
public class ProfilePlot extends Plot {
    /**
     * Data source for a series to plot.
     */
    public interface Data {
        ISetpoint get(double time);
    }

    /**
     * Constructs a new plot with the acceleration, velocity, and position of the
     * profile over time.
     *
     * @param title    Title of the plot
     * @param data     Data source for the first data series
     * @param duration Duration of the first data series (maximum value of time to
     *                 get data at)
     * @param step     Step value for time
     */
    public ProfilePlot(String title, Data data, double duration, double step) {
        this.title = title;

        XYSeries acceleration = new XYSeries("Acceleration", false);
        XYSeries velocity = new XYSeries("Velocity", false);
        XYSeries position = new XYSeries("Position", false);

        for (double i = 0.0; i < duration; i += step) {
            ISetpoint sp = data.get(i);
            acceleration.add(i, sp.getAcceleration());
            velocity.add(i, sp.getVelocity());
            position.add(i, sp.getPosition());
        }

        dataset = new XYSeriesCollection();
        dataset.addSeries(acceleration);
        dataset.addSeries(velocity);
        dataset.addSeries(position);

        chart = ChartFactory.createXYLineChart(title, "Time", "Value", dataset, PlotOrientation.VERTICAL, true, false,
                false);

        initialize(getColor(0));
    }
}
