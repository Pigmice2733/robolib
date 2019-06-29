package com.pigmice.frc.lib.pidf;

import java.util.Arrays;
import java.util.List;

import com.pigmice.frc.lib.utils.Range;
import com.pigmice.frc.lib.utils.Utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PIDFTest {
    private static final double epsilon = 1e-6;

    private static class TestPoint {
        public double input;
        private double setpoint, velocity, acceleration;
        private double time;
        private double output;

        public TestPoint(double time, double output, double input, double setpoint, double velocity,
                double acceleration) {
            this.input = input;
            this.setpoint = setpoint;
            this.velocity = velocity;
            this.acceleration = acceleration;
            this.time = time;
            this.output = output;
        }
    }

    private static void checkData(PIDF controller, List<TestPoint> testData) {
        for (int i = 0; i < testData.size(); i++) {
            TestPoint datum = testData.get(i);
            double output = controller.calculateOutput(datum.input, datum.setpoint, datum.velocity, datum.acceleration,
                    datum.time);
            if (!Utils.almostEquals(datum.output, output, epsilon)) {
                System.out.format("PIDF error, datum #%d", i + 1);
            }
            Assertions.assertEquals(datum.output, output, epsilon);
        }
    }

    @Test
    public void positionPIDVATest() {
        Gains gains = new Gains(2.0, 4.0, 0.5, 0.0, 1.0, 0.25);
        Range outputBounds = new Range(-6.0, 6.0);
        PIDF pidva = new PIDF(gains, outputBounds);

        List<TestPoint> testData = Arrays.asList(
                // Fake profile: 6s, 18m - accel @ 3m/s for 2s, 6m/s for 1s, decel @ 3m/s for 2s
                new TestPoint(0.0, 0.75, 0.0, 0.0, 0.0, 3.0), new TestPoint(1.0, 6.0, 1.0, 1.5, 3.0, 3.0),
                new TestPoint(2.0, 6.0, 4.0, 6.0, 6.0, 0.0), new TestPoint(3.0, -6.0, 14.0, 12.0, 6.0, -3.0),
                new TestPoint(4.0, 4.5, 15.5, 16.5, 3.0, -3.0), new TestPoint(5.0, -2.5, 18.0, 18.0, 0.0, -3.0));

        pidva.initialize(0.0, 0.0, 0.0);
        checkData(pidva, testData);
    }

    @Test
    public void simplePIDTest() {
        Gains gains = new Gains(1.0, 0.0, 0.0);
        Range outputBounds = new Range(-1.0, 1.0);
        PIDF p = new PIDF(gains, outputBounds);

        p.initialize(0.0, 0.0, 0.0);
        double output = p.calculateOutput(-5.0, 0.0, 1.0);
        Assertions.assertEquals(1.0, output, epsilon);
    }

    @Test
    public void ratePIFTest() {
        Gains gains = new Gains(1.0, 0.25, 0.0, 1.0, 0.0, 0.0);
        Range outputBounds = new Range(-10.0, 10.0);
        PIDF pf = new PIDF(gains, outputBounds);

        List<TestPoint> testData = Arrays.asList(
                // Rate controller, shooter wheel etc. Feedforward drives output, PI adjusts it
                new TestPoint(2.0, 10.0, 0.0, 5.0, 0.0, 0.0), new TestPoint(3.0, 9.25, 3.0, 5.0, 0.0, 0.0),
                new TestPoint(4.0, 8.5, 4.0, 5.0, 0.0, 0.0), new TestPoint(5.0, 7.5, 5.0, 5.0, 0.0, 0.0),
                new TestPoint(6.0, -10.0, 5.0, -5.0, 0.0, 0.0), new TestPoint(7.0, -10.0, 2.0, -5.0, 0.0, 0.0),
                new TestPoint(8.0, -9.25, -3.0, -5.0, 0.0, 0.0), new TestPoint(9.0, -7.25, -5.0, -5.0, 0.0, 0.0));

        pf.initialize(0.0, 1.0, 0.5);
        checkData(pf, testData);
    }

    @Test
    public void continuousPDTest() {
        Gains gains = new Gains(0.02, 0.0, 0.01, 0.0, 0.0, 0.0);
        Range outputBounds = new Range(-2.0, 2.0);
        PIDF pd = new PIDF(gains, outputBounds);

        pd.setDerivativeOnInput(true);
        pd.setContinuous(new Range(10, 370), true);

        List<TestPoint> testData = Arrays.asList(
                // Continuous PD, like for swerve drive wheel angle control. Derivative on input
                // instead of error so that large setpoint changes don't cause derivative spike
                // Sensor input range is 10-370, and wraps around at 370 back to 10.
                new TestPoint(0.0, -2.0, 20.0, 280.0, 0.0, 0.0), new TestPoint(1.0, -0.5, 330.0, 280.0, 0.0, 0.0),
                new TestPoint(2.0, -0.1, 300.0, 280.0, 0.0, 0.0), new TestPoint(3.0, 0.2, 280.0, 280.0, 0.0, 0.0),
                new TestPoint(4.0, 2.0, 280.0, 25.0, 0.0, 0.0), new TestPoint(5.0, 0.6, 330.0, 25.0, 0.0, 0.0),
                new TestPoint(6.0, 0.05, 365.0, 25.0, 0.0, 0.0), new TestPoint(7.0, 0.25, 10.0, 25.0, 0.0, 0.0));

        pd.initialize(10.0, 0.0, 0.0);
        checkData(pd, testData);
    }
}
