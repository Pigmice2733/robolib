package com.pigmice.frc.lib.motor_tester;

import java.util.ArrayList;
import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class MotorTester {
    public static final ArrayList<TestMotor> motors = new ArrayList<TestMotor>();

    private static GenericEntry motorsEnabled;
    private static final ArrayList<GenericEntry> outputEntries = new ArrayList<GenericEntry>();

    public static void registerCANMotor(String name, MotorController motor) {
        motors.add(new TestCAN(name, motor));
    }

    public static void registerTalonMotor(String name, BaseMotorController motor) {
        motors.add(new TestTalon(name, motor));
    }

    private static boolean createdWidgets = false;

    /** <strong>Call in Robot.testInit</strong> **/
    public static void createWidgets() {
        if (createdWidgets)
            return;

        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Motor Tester");

        createdWidgets = true;

        for (var motor : motors) {
            outputEntries.add(shuffleboardTab.add(motor.name, 0)
                    .withWidget(BuiltInWidgets.kNumberSlider)
                    .withProperties(Map.of("min", -1, "max", 1)).getEntry());
        }

        motorsEnabled = shuffleboardTab.add("Enable Motors", false)
                .withWidget(BuiltInWidgets.kToggleSwitch).getEntry();
    }

    /** <strong>Call in Robot.testPeriodic</strong> **/
    public static void periodic() {
        if (!motorsEnabled.getBoolean(false)) {
            stopMotors();
            return;
        }

        stopMotors();

        int motorIndex = 0;
        for (var motor : motors) {
            motor.set(outputEntries.get(motorIndex).getDouble(0));
            motorIndex++;
        }
    }

    public static void stopMotors() {
        motors.forEach(m -> m.set(0));
    }

    public abstract static class TestMotor {
        public String name;

        public TestMotor(String name) {
            this.name = name;
        }

        public abstract void set(double percent);
    }

    public static class TestCAN extends TestMotor {
        private final MotorController motor;

        public TestCAN(String name, MotorController motor) {
            super(name);
            this.motor = motor;
        }

        @Override
        public void set(double percent) {
            motor.set(percent);
        }
    }

    public static class TestTalon extends TestMotor {
        private final BaseMotorController motor;

        public TestTalon(String name, BaseMotorController motor) {
            super(name);
            this.motor = motor;
        }

        @Override
        public void set(double percent) {
            motor.set(ControlMode.PercentOutput, percent);
        }
    }
}