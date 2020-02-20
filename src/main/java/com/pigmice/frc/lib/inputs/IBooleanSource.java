package com.pigmice.frc.lib.inputs;

public interface IBooleanSource {
    boolean get();
    default void update() {}
}
