package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class SmokeSensor extends Sensor {

    private int smokeLevel;

    public SmokeSensor() {
        super(SensorType.SMOKE, "Дымовой датчик", "ДД");
        this.smokeLevel = SensorConfig.Smoke.getDefaultValue();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return String.format("%d%%", SensorConfig.Smoke.getDefaultValue());
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.NORMAL);
        this.smokeLevel = SensorConfig.Smoke.getDefaultValue();
        updateValueDisplay(getDefaultValueDisplay());
    }

    @Override
    public Object getValue() {
        return smokeLevel;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.smokeLevel = ((Number) value).intValue();
        }
    }

    public int getSmokeLevel() {
        return smokeLevel;
    }

    public void setSmokeLevel(int smokeLevel) {
        this.smokeLevel = smokeLevel;
    }
}
