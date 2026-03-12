package org.reminder.edu.modbusslave.entity.sensors;

import org.reminder.edu.modbusslave.entity.AbstractSensor;
import org.reminder.edu.modbusslave.entity.enums.SensorState;
import org.reminder.edu.modbusslave.entity.enums.SensorType;

public class FireButton extends AbstractSensor {

    public FireButton() {
        super(SensorType.FIRE_BUTTON, "Пожарная кнопка", "ПК");
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "-";
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(SensorState.NORMAL);
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {
    }

    public void press() {
        if (isEnabled()) {
            setState(SensorState.ALARM);
        }
    }
}
