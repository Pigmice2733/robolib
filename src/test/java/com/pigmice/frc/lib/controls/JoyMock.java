package com.pigmice.frc.lib.controls;

import edu.wpi.first.wpilibj.GenericHID;

class JoyMock extends GenericHID {
    private final int button;

    public JoyMock(int button) {
        super(0);
        this.button = button;
    }

    private int count = 0;

    @Override
    public double getY(GenericHID.Hand h) {
        return 0.0;
    }

    @Override
    public double getX(GenericHID.Hand h) {
        return 0.0;
    }

    public boolean getRawButton(int button) {
        if (button == this.button) {
            switch (count++) {
            case 0:
                return false;
            case 1:
                return true;
            case 2:
                return true;
            case 3:
                return false;
            case 4:
                return false;
            case 5:
                return true;
            default:
                break;
            }
        }
        return false;
    }
}
