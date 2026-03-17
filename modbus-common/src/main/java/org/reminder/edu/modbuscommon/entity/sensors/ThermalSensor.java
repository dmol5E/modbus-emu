package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class ThermalSensor extends Sensor {

    private double temperature;

    public ThermalSensor() {
        super(SensorType.THERMAL, "Тепловой датчик", "ТД");
        this.temperature = SensorConfig.Thermal.getDefaultValue();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return String.format("%.1f°C", SensorConfig.Thermal.getDefaultValue());
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.NORMAL);
        this.temperature = SensorConfig.Thermal.getDefaultValue();
        updateValueDisplay(getDefaultValueDisplay());
    }

    @Override
    public Object getValue() {
        return temperature;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.temperature = ((Number) value).doubleValue();
        }
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
