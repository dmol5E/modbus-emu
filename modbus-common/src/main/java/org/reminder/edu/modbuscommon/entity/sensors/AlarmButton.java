package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.AbstractSensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class AlarmButton extends AbstractSensor {

    public AlarmButton() {
        super(SensorType.ALARM_BUTTON, "Тревожная кнопка", "ТК");
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