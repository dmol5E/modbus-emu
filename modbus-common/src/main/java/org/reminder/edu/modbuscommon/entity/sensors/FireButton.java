package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class FireButton extends Sensor {

    private boolean pressed;

    public FireButton() {
        super(SensorType.FIRE_BUTTON, "Пожарная кнопка", "ПК");
        this.pressed = SensorConfig.FireButton.getDefaultValue();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "-";
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.NORMAL);
        this.pressed = SensorConfig.FireButton.getDefaultValue();
        updateValueDisplay(getDefaultValueDisplay());
    }

    @Override
    public Object getValue() {
        return pressed;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.pressed = (Boolean) value;
        } else if (value instanceof Number) {
            this.pressed = ((Number) value).intValue() != 0;
        }
    }

    public void press() {
        if (isEnabled()) {
            setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.ALARM);
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
