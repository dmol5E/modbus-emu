package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class PressureSensor extends Sensor {

    private double pressure;

    public PressureSensor() {
        super(SensorType.PRESSURE, "Датчик давления", "ДДав");
        this.pressure = SensorConfig.Pressure.getDefaultValue();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return String.format("%.1f гПа", SensorConfig.Pressure.getDefaultValue());
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.NORMAL);
        this.pressure = SensorConfig.Pressure.getDefaultValue();
        updateValueDisplay(getDefaultValueDisplay());
    }

    @Override
    public Object getValue() {
        return pressure;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.pressure = ((Number) value).doubleValue();
        }
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }
}
