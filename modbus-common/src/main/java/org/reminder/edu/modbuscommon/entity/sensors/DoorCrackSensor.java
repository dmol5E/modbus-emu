package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.AbstractSensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class DoorCrackSensor extends AbstractSensor {

    private boolean cracked;

    public DoorCrackSensor() {
        super(SensorType.DOOR_CRACK, "Датчик взлома двери", "ДВ");
        this.cracked = false;
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "Закрыто";
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(SensorState.NORMAL);
        this.cracked = false;
        updateValueDisplay();
    }

    @Override
    public Object getValue() {
        return cracked;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.cracked = (Boolean) value;
            updateValueDisplay();
            updateState();
        } else if (value instanceof Number) {
            this.cracked = ((Number) value).intValue() != 0;
            updateValueDisplay();
            updateState();
        }
    }

    private void updateValueDisplay() {
        updateValueDisplay(cracked ? "Открыто" : "Закрыто");
    }

    private void updateState() {
        if (!isEnabled()) {
            return;
        }

        if (cracked) {
            setState(SensorState.ALARM);
        } else {
            setState(SensorState.NORMAL);
        }
    }

    public boolean isCracked() {
        return cracked;
    }

    public void setCracked(boolean cracked) {
        this.cracked = cracked;
        updateValueDisplay();
        updateState();
    }
}