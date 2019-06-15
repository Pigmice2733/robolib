package com.pigmice.frc.lib.planning;

public interface ICSpace<T> {
    public boolean isFree(T configuration);
}
