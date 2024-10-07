package me.hashbeep.sedna.api.device.serial;

public interface SerialDevice {
    int read();

    boolean canPutByte();

    void putByte(byte value);

    default void flush() {
    }
}
