package com.pigmice.frc.lib.shuffleboard_helper;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile.Constraints;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardComponent;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class ShuffleboardHelper {
    public static ArrayList<ShuffleboardObject> shuffleboardObjects = new ArrayList<ShuffleboardObject>();

    public static abstract class ShuffleboardObject {
        public final ShuffleboardComponent<?> component;
        public boolean isDebug = true;

        /** Represents either an output or input */
        public ShuffleboardObject(ShuffleboardComponent<?> component) {
            this.component = component;
        }

        abstract public void update();

        public ShuffleboardObject asNotDebug() {
            isDebug = false;
            return this;
        }

        public ShuffleboardObject withPosition(int x, int y) {
            component.withPosition(x, y);
            return this;
        }

        public ShuffleboardObject withSize(int width, int height) {
            component.withSize(width, height);
            return this;
        }
    }

    public static class ShuffleboardOutput extends ShuffleboardObject {
        private final Supplier<Object> supplier;
        private final SimpleWidget widget;
        private final GenericEntry entry;

        /** Represents an output field */
        public ShuffleboardOutput(SimpleWidget widget, Supplier<Object> supplier) {
            super(widget);

            this.widget = widget;
            this.supplier = supplier;
            this.entry = widget.getEntry();
        }

        @Override
        public void update() {
            Object value = supplier.get();

            if (value != null)
                entry.setValue(value);
        }

        public ShuffleboardOutput asDial(double min, double max) {
            widget.withWidget(BuiltInWidgets.kDial).withProperties(Map.of("min", min, "max", max));
            return this;
        }

        public ShuffleboardOutput asNumberBar(double min, double max) {
            widget.withWidget(BuiltInWidgets.kNumberBar).withProperties(Map.of("min", min, "max", max));
            return this;
        }
    }

    public static class ShuffleboardInput extends ShuffleboardObject {
        private final Consumer<Object> consumer;
        private final SimpleWidget widget;
        private final GenericEntry entry;

        /** Represents an input field */
        public ShuffleboardInput(SimpleWidget widget, Consumer<Object> consumer) {
            super(widget);

            this.widget = widget;
            this.consumer = consumer;
            this.entry = widget.getEntry();
        }

        @Override
        public void update() {
            consumer.accept(entry.getDouble(0));
        }

        public ShuffleboardInput asDial(double min, double max) {
            widget.withWidget(BuiltInWidgets.kDial).withProperties(Map.of("min", min, "max", max));
            return this;
        }

        public ShuffleboardInput asNumberBar(double min, double max) {
            widget.withWidget(BuiltInWidgets.kNumberBar).withProperties(Map.of("min", min, "max", max));
            return this;
        }
    }

    public static class ProfiledControllerInput extends ShuffleboardObject {
        private final ProfiledPIDController controller;

        private final GenericEntry pIn;
        private final GenericEntry iIn;
        private final GenericEntry dIn;
        private final GenericEntry velIn;
        private final GenericEntry accIn;

        public ProfiledControllerInput(ShuffleboardLayout layout, ProfiledPIDController controller, double defaultVel,
                double defaultAccel) {
            super(layout);
            this.controller = controller;
            pIn = layout.add("P", controller.getP()).getEntry();
            iIn = layout.add("I", controller.getI()).getEntry();
            dIn = layout.add("D", controller.getD()).getEntry();
            velIn = layout.add("Vel", defaultVel).getEntry();
            accIn = layout.add("Acc", defaultAccel).getEntry();

        }

        @Override
        public void update() {
            controller.setP(pIn.getDouble(0));
            controller.setI(iIn.getDouble(0));
            controller.setD(dIn.getDouble(0));

            controller.setConstraints(new Constraints(velIn.getDouble(0), accIn.getDouble(0)));
        }
    }

    /**
     * Add a new output field to shuffleboard that will be automatically updated
     * 
     * @param supplier returns a value to output to shuffleboard
     */
    public static ShuffleboardOutput addOutput(String name, ShuffleboardContainer tab, Supplier<Object> supplier) {
        ShuffleboardOutput shuffleboardOutput = new ShuffleboardOutput(tab.add(name, supplier.get()), supplier);
        shuffleboardObjects.add(shuffleboardOutput);
        return shuffleboardOutput;
    }

    /**
     * Add a new input field to shuffleboard that will be automatically updated
     * 
     * @param consumer will be automatically called with the inputted value
     */
    public static ShuffleboardInput addInput(String name, ShuffleboardContainer container, Consumer<Object> consumer,
            Object defaultValue) {
        ShuffleboardInput shuffleboardInput = new ShuffleboardInput(container.add(name, defaultValue), consumer);
        shuffleboardObjects.add(shuffleboardInput);
        return shuffleboardInput;
    }

    /** Output the position, velocity, temperature, and output of a motor in amps */
    public static ShuffleboardLayout addMotorInfo(String name, ShuffleboardTab tab, CANSparkMax motor) {
        ShuffleboardLayout layout = tab.getLayout(name, BuiltInLayouts.kList)
                .withSize(2, 3);

        addOutput("Encoder Position", layout, () -> motor.getEncoder().getPosition()).withPosition(0, 1);
        addOutput("Encoder Velocity", layout, () -> motor.getEncoder().getVelocity()).withPosition(0, 2);
        addOutput("Temperature", layout, () -> motor.getMotorTemperature()).withPosition(0, 3);
        addOutput("Motor Output (amps)", layout, () -> motor.getOutputCurrent()).withPosition(0, 4);

        return layout;
    }

    /** Inputs for all the gains of a profiled PID controller */
    public static ShuffleboardLayout addProfiledController(String name, ShuffleboardTab tab,
            ProfiledPIDController controller, double defaultVel, double defaultAcc) {

        ShuffleboardLayout layout = tab.getLayout(name, BuiltInLayouts.kList)
                .withSize(2, 3);

        ProfiledControllerInput shuffleboardInput = new ProfiledControllerInput(layout, controller, defaultVel,
                defaultAcc);

        shuffleboardObjects.add(shuffleboardInput);

        return layout;
    }

    /**
     * <strong>Call in Robot.robotPeriodic</strong>, updates all shuffleboard inputs
     * and outputs.
     */
    public static void update(boolean debugEnabled) {
        for (ShuffleboardObject shuffleboardObject : shuffleboardObjects) {
            if (shuffleboardObject.isDebug && !debugEnabled)
                continue;
            shuffleboardObject.update();
        }
    }
}
