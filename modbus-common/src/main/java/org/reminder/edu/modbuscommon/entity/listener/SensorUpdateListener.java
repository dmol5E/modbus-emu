package org.reminder.edu.modbuscommon.entity.listener;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;

public interface SensorUpdateListener {

    void onValueChanged(Sensor sensor, Object newValue);

    void onStateChanged(Sensor sensor, SensorState newState);

    void onEnabledChanged(Sensor sensor, boolean enabled);
}
