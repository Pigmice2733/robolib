package com.pigmice.frc.lib.controls;

public class InputMock implements IBooleanSource {
    private boolean[] outputs;
    private int current;

    public InputMock(boolean[] outputs) {
       this.outputs = outputs;
       current = 0;
    }

    public void update() {
        current = (current + 1) % outputs.length;
    }

    public boolean get() {
        return outputs[current];
    }
}
