package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.AbstractSensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class SmokeSensor extends AbstractSensor {

    private static final int DEFAULT_SMOKE_LEVEL = 0;
    private static final int ALARM_THRESHOLD = 30;
    private static final int FAULT_THRESHOLD = 90;

    private int smokeLevel;

    public SmokeSensor() {
        super(SensorType.SMOKE, "Дымовой датчик", "ДД");
        this.smokeLevel = DEFAULT_SMOKE_LEVEL;
    }

    @Override
    protected String getDefaultValueDisplay() {
        return String.format("%d%%", DEFAULT_SMOKE_LEVEL);
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(SensorState.NORMAL);
        this.smokeLevel = DEFAULT_SMOKE_LEVEL;
        updateValueDisplay();
    }

    @Override
    public Object getValue() {
        return smokeLevel;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.smokeLevel = Math.min(100, Math.max(0, ((Number) value).intValue()));
            updateValueDisplay();
            updateState();
        }
    }

    private void updateValueDisplay() {
        updateValueDisplay(String.format("%d%%", smokeLevel));
    }

    private void updateState() {
        if (!isEnabled()) {
            return;
        }

        if (smokeLevel >= FAULT_THRESHOLD) {
            setState(SensorState.FAULT);
        } else if (smokeLevel >= ALARM_THRESHOLD) {
            setState(SensorState.ALARM);
        } else {
            setState(SensorState.NORMAL);
        }
    }

    public int getSmokeLevel() {
        return smokeLevel;
    }

    public void setSmokeLevel(int smokeLevel) {
        this.smokeLevel = Math.min(100, Math.max(0, smokeLevel));
        updateValueDisplay();
        updateState();
    }

    public int getAlarmThreshold() {
        return ALARM_THRESHOLD;
    }

    public int getFaultThreshold() {
        return FAULT_THRESHOLD;
    }
}
