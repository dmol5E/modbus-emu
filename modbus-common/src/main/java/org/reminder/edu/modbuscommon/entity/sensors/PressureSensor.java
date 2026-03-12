package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.AbstractSensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class PressureSensor extends AbstractSensor {

    private static final double DEFAULT_PRESSURE = 1013.25;
    private static final double ALARM_THRESHOLD_HIGH = 1050.0;
    private static final double ALARM_THRESHOLD_LOW = 900.0;
    private static final double FAULT_THRESHOLD_HIGH = 1100.0;
    private static final double FAULT_THRESHOLD_LOW = 800.0;

    private double pressure;

    public PressureSensor() {
        super(SensorType.PRESSURE, "Датчик давления", "ДДав");
        this.pressure = DEFAULT_PRESSURE;
    }

    @Override
    protected String getDefaultValueDisplay() {
        return String.format("%.1f гПа", DEFAULT_PRESSURE);
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(SensorState.NORMAL);
        this.pressure = DEFAULT_PRESSURE;
        updateValueDisplay();
    }

    @Override
    public Object getValue() {
        return pressure;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.pressure = ((Number) value).doubleValue();
            updateValueDisplay();
            updateState();
        }
    }

    private void updateValueDisplay() {
        updateValueDisplay(String.format("%.1f гПа", pressure));
    }

    private void updateState() {
        if (!isEnabled()) {
            return;
        }

        if (pressure >= FAULT_THRESHOLD_HIGH || pressure <= FAULT_THRESHOLD_LOW) {
            setState(SensorState.FAULT);
        } else if (pressure >= ALARM_THRESHOLD_HIGH || pressure <= ALARM_THRESHOLD_LOW) {
            setState(SensorState.ALARM);
        } else {
            setState(SensorState.NORMAL);
        }
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
        updateValueDisplay();
        updateState();
    }

    public double getAlarmThresholdHigh() {
        return ALARM_THRESHOLD_HIGH;
    }

    public double getAlarmThresholdLow() {
        return ALARM_THRESHOLD_LOW;
    }

    public double getFaultThresholdHigh() {
        return FAULT_THRESHOLD_HIGH;
    }

    public double getFaultThresholdLow() {
        return FAULT_THRESHOLD_LOW;
    }
}
