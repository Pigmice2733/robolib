package com.pigmice.frc.lib.controller_rumbler;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj2.command.Commands;

public class ControllerRumbler {
    private static XboxController driver;
    private static XboxController operator;

    public static void setControllers(XboxController driver, XboxController operator) {
        ControllerRumbler.driver = driver;
        ControllerRumbler.operator = operator;
    }

    public static void rumbleBoth(RumbleType rumbleType, double seconds, double strength) {
        rumblerDriver(rumbleType, seconds, strength);
        rumblerOperator(rumbleType, seconds, strength);
    }

    public static void rumblerDriver(RumbleType rumbleType, double seconds, double strength) {
        rumblerController(driver, rumbleType, seconds, strength);
    }

    public static void rumblerOperator(RumbleType rumbleType, double seconds, double strength) {
        rumblerController(operator, rumbleType, seconds, strength);
    }

    public static void rumblerController(XboxController controller, RumbleType rumbleType, double seconds,
            double strength) {
        if (controller == null)
            return;

        Commands.sequence(Commands.runOnce(() -> controller.setRumble(rumbleType, strength)),
                Commands.waitSeconds(seconds),
                Commands.runOnce(() -> controller.setRumble(rumbleType, 0))).schedule();
    }

    public static void stopBothControllers() {
        stopDriver();
        stopOperator();
    }

    public static void stopDriver() {
        stopController(driver);
    }

    public static void stopOperator() {
        stopController(operator);
    }

    public static void stopController(XboxController controller) {
        if (controller == null)
            return;

        controller.setRumble(RumbleType.kBothRumble, 0);
    }
}