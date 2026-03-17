package org.reminder.edu.modbuscommon.entity.service;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public interface SensorBehaviorStrategy {
    
    void validate(Object value);
    
    void updateState(Sensor sensor);
    
    void resetToDefault(Sensor sensor);
    
    String getValueDisplay(Sensor sensor);
    
    SensorType getSupportedType();
}
