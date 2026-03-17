package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class DoorCrackSensor extends Sensor {

    private boolean cracked;

    public DoorCrackSensor() {
        super(SensorType.DOOR_CRACK, "Датчик взлома двери", "ДВ");
        this.cracked = SensorConfig.DoorCrack.getDefaultValue();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "Закрыто";
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.NORMAL);
        this.cracked = SensorConfig.DoorCrack.getDefaultValue();
        updateValueDisplay(getDefaultValueDisplay());
    }

    @Override
    public Object getValue() {
        return cracked;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.cracked = (Boolean) value;
        } else if (value instanceof Number) {
            this.cracked = ((Number) value).intValue() != 0;
        }
    }

    public boolean isCracked() {
        return cracked;
    }

    public void setCracked(boolean cracked) {
        this.cracked = cracked;
    }
}
