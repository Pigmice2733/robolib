package com.pigmice.frc.lib.controls;

import com.pigmice.frc.lib.utils.Utils;

public class Joysticks {
    public static double deadzone(double value, double deadzoneSize) {
        if (value <= deadzoneSize && value >= -deadzoneSize) {
            value = 0;
        } else {
            value = Utils.lerp(Math.abs(value), deadzoneSize, 1, 0, 1) * Math.signum(value);
        }

        return value;
    }
}
