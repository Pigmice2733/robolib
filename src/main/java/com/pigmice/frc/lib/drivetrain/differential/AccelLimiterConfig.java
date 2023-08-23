package com.pigmice.frc.lib.drivetrain.differential;

public class AccelLimiterConfig {
    public final double MAX_ACCELERATION; // m/s/s
    public final double MAX_ANGULAR_ACCELERATION; // rad/s/s

    // All feed forward values in volts
    public final double LINEAR_V;
    public final double LINEAR_A;
    public final double ANGULAR_S;
    public final double ANGULAR_V;
    public final double ANGULAR_A;

    /**
     * 
     * @param maxAcceleration        max allowed linear accel (meters/sec/sec)
     * @param maxAngularAcceleration max allowed angular accel (rad/sec/sec)
     * @param linearV                linear V feedforward constant from SysID
     * @param linearA                linear A feedforward constant from SysID
     * @param angularS               angular S feedforward constant from SysID
     * @param angularV               angular V feedforward constant from SysID
     * @param angularA               angular A feedforward constant from SysID
     */
    public AccelLimiterConfig(double maxAcceleration, double maxAngularAcceleration,
            double linearV, double linearA, double angularS, double angularV,
            double angularA) {
        this.MAX_ACCELERATION = maxAcceleration;
        this.MAX_ANGULAR_ACCELERATION = maxAngularAcceleration;

        this.LINEAR_V = linearV;
        this.LINEAR_A = linearA;

        this.ANGULAR_S = angularS;
        this.ANGULAR_V = angularV;
        this.ANGULAR_A = angularA;
    }
}
