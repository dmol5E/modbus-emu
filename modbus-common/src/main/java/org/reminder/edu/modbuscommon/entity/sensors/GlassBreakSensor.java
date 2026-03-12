package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.AbstractSensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class GlassBreakSensor extends AbstractSensor {

    private boolean broken;

    public GlassBreakSensor() {
        super(SensorType.GLASS_BREAK, "Датчик разбития стекла", "ДС");
        this.broken = false;
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "Целое";
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(SensorState.NORMAL);
        this.broken = false;
        updateValueDisplay();
    }

    @Override
    public Object getValue() {
        return broken;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.broken = (Boolean) value;
            updateValueDisplay();
            updateState();
        } else if (value instanceof Number) {
            this.broken = ((Number) value).intValue() != 0;
            updateValueDisplay();
            updateState();
        }
    }

    private void updateValueDisplay() {
        updateValueDisplay(broken ? "Разбито" : "Целое");
    }

    private void updateState() {
        if (!isEnabled()) {
            return;
        }

        if (broken) {
            setState(SensorState.ALARM);
        } else {
            setState(SensorState.NORMAL);
        }
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
        updateValueDisplay();
        updateState();
    }
}