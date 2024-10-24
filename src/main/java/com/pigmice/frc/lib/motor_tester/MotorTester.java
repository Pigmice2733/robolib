package com.pigmice.frc.lib.motor_tester;

import java.util.ArrayList;
import java.util.Map;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class MotorTester {
    public static final ArrayList<TestMotor> motors = new ArrayList<TestMotor>();

    private static GenericEntry motorsEnabled;
    private static final ArrayList<GenericEntry> outputEntries = new ArrayList<GenericEntry>();

    public static void registerMotor(String name, MotorController motor) {
        motors.add(new TestMotor(name, motor));
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

    public static class TestMotor {
        public String name;
        public MotorController motor;

        public TestMotor(String name, MotorController motor) {
            this.name = name;
            this.motor = motor;
        }

        public void set(double percent) {
            motor.set(percent);
        };
    }
}