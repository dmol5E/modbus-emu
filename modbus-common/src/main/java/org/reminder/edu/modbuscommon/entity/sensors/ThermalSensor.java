package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.AbstractSensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class ThermalSensor extends AbstractSensor {

    private static final double DEFAULT_TEMPERATURE = 20.0;
    private static final double ALARM_THRESHOLD = 100.0;
    private static final double FAULT_THRESHOLD = 150.0;

    private double temperature;

    public ThermalSensor() {
        super(SensorType.THERMAL, "Тепловой датчик", "ТД");
        this.temperature = DEFAULT_TEMPERATURE;
    }

    @Override
    protected String getDefaultValueDisplay() {
        return String.format("%.1f°C", DEFAULT_TEMPERATURE);
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(SensorState.NORMAL);
        this.temperature = DEFAULT_TEMPERATURE;
        updateValueDisplay();
    }

    @Override
    public Object getValue() {
        return temperature;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.temperature = ((Number) value).doubleValue();
            updateValueDisplay();
            updateState();
        }
    }

    private void updateValueDisplay() {
        updateValueDisplay(String.format("%.1f°C", temperature));
    }

    private void updateState() {
        if (!isEnabled()) {
            return;
        }

        if (temperature >= FAULT_THRESHOLD) {
            setState(SensorState.FAULT);
        } else if (temperature >= ALARM_THRESHOLD) {
            setState(SensorState.ALARM);
        } else {
            setState(SensorState.NORMAL);
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        updateValueDisplay();
        updateState();
    }

    public double getAlarmThreshold() {
        return ALARM_THRESHOLD;
    }

    public double getFaultThreshold() {
        return FAULT_THRESHOLD;
    }
}
