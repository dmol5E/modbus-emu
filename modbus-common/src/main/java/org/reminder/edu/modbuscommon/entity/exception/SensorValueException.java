package org.reminder.edu.modbuscommon.entity.exception;

public class SensorValueException extends IllegalArgumentException {
    
    public SensorValueException(String message) {
        super(message);
    }
    
    public static SensorValueException invalidValue(Object value, String expectedType) {
        return new SensorValueException(
            String.format("Invalid sensor value: expected %s, got %s", expectedType, value)
        );
    }
    
    public static SensorValueException outOfRange(Object value, double min, double max) {
        return new SensorValueException(
            String.format("Value %s out of range [%.1f, %.1f]", value, min, max)
        );
    }
    
    public static SensorValueException outOfRangeInt(Object value, int min, int max) {
        return new SensorValueException(
            String.format("Value %s out of range [%d, %d]", value, min, max)
        );
    }
}
