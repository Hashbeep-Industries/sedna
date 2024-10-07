package me.hashbeep.sedna.api.device.rtc;

import me.hashbeep.sedna.api.device.Device;

public interface RealTimeCounter extends Device {
    long getTime();

    int getFrequency();
}
