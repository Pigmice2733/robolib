package com.pigmice.frc.lib.shuffleboard_helper;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class ShuffleboardHelper {
    private static boolean debugEnabled = true;

    public static ArrayList<ShuffleboardObject> shuffleboardObjects = new ArrayList<ShuffleboardObject>();

    public static abstract class ShuffleboardObject {
        public final SimpleWidget widget;
        public final GenericEntry entry;
        public boolean isDebug = true;

        /** Represents either an output or input */
        public ShuffleboardObject(SimpleWidget widget) {
            this.widget = widget;
            this.entry = widget.getEntry();
        }

        abstract public void update();
    }

    public static class ShuffleboardOutput extends ShuffleboardObject {
        Supplier<Object> supplier;

        /** Represents an output field */
        public ShuffleboardOutput(SimpleWidget widget, Supplier<Object> supplier) {
            super(widget);

            this.supplier = supplier;
        }

        @Override
        public void update() {
            Object value = supplier.get();

            if (value != null)
                entry.setValue(value);
        }

        public ShuffleboardOutput asNotDebug() {
            isDebug = false;
            return this;
        }

        public ShuffleboardOutput withPosition(int x, int y) {
            widget.withPosition(x, y);
            return this;
        }

        public ShuffleboardObject withSize(int width, int height) {
            widget.withSize(width, height);
            return this;
        }

        public ShuffleboardOutput asDial(float min, float max) {
            widget.withWidget(BuiltInWidgets.kDial).withProperties(Map.of("min", min, "max", max));
            return this;
        }

        public ShuffleboardOutput asNumberBar(float min, float max) {
            widget.withWidget(BuiltInWidgets.kNumberBar).withProperties(Map.of("min", min, "max", max));
            return this;
        }
    }

    public static class ShuffleboardInput extends ShuffleboardObject {
        Consumer<Double> consumer;

        /** Represents an input field */
        public ShuffleboardInput(SimpleWidget widget, Consumer<Double> consumer) {
            super(widget);
            this.consumer = consumer;
        }

        @Override
        public void update() {
            consumer.accept(entry.getDouble(0));
        }

        public ShuffleboardInput asNotDebug() {
            isDebug = false;
            return this;
        }

        public ShuffleboardInput withPos(int x, int y) {
            widget.withPosition(x, y);
            return this;
        }

        public ShuffleboardInput withSize(int width, int height) {
            widget.withSize(width, height);
            return this;
        }

        public ShuffleboardInput asNumberSlider(float min, float max) {
            widget.withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", min, "max", max));
            return this;
        }
    }

    /**
     * Add a new output field to shuffleboard that will be automatically updated
     * 
     * @param supplier returns a value to output to shuffleboard
     */
    public static ShuffleboardOutput addOutput(String name, ShuffleboardContainer tab, Supplier<Object> supplier) {
        ShuffleboardOutput shuffleboardOutput = new ShuffleboardOutput(tab.add(name, 0), supplier);
        shuffleboardObjects.add(shuffleboardOutput);
        return shuffleboardOutput;
    }

    /**
     * Add a new input field to shuffleboard that will be automatically updated
     * 
     * @param consumer will be automatically called with the inputted value
     */
    public static ShuffleboardInput addInput(String name, ShuffleboardContainer container, Consumer<Double> consumer) {
        ShuffleboardInput shuffleboardInput = new ShuffleboardInput(container.add(name, 0), consumer);
        shuffleboardObjects.add(shuffleboardInput);
        return shuffleboardInput;
    }

    /** Output the position, velocity, temperature, and output of a motor in amps */
    public static void addMotorInfo(String name, ShuffleboardTab tab, CANSparkMax motor, int xPos, int yPos) {
        ShuffleboardLayout layout = tab.getLayout(name, BuiltInLayouts.kList)
                .withSize(2, 3)
                .withPosition(xPos, yPos);

        addOutput("Encoder Position", layout, () -> motor.getEncoder().getPosition()).withPosition(0, 1);
        addOutput("Encoder Velocity", layout, () -> motor.getEncoder().getVelocity()).withPosition(0, 2);
        addOutput("Temperature", layout, () -> motor.getMotorTemperature()).withPosition(0, 3);
        addOutput("Motor Output (amps)", layout, () -> motor.getOutputCurrent()).withPosition(0, 4);
    }

    /**
     * <strong>Call in Robot.robotPeriodic</strong>, updates all shuffleboard inputs
     * and outputs.
     */
    public static void update() {
        for (ShuffleboardObject shuffleboardObject : shuffleboardObjects) {
            if (shuffleboardObject.isDebug && !debugEnabled)
                continue;
            shuffleboardObject.update();
        }
    }
}
