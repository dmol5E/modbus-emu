package org.reminder.edu.modbuscommon.entity.service;

import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public abstract class AbstractSensorBehaviorStrategy implements SensorBehaviorStrategy {
    
    protected final SensorType type;
    
    protected AbstractSensorBehaviorStrategy(SensorType type) {
        this.type = type;
    }
    
    @Override
    public SensorType getSupportedType() {
        return type;
    }
}
