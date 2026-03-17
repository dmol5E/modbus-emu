package org.reminder.edu.modbuscommon.entity.sensors;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public class GlassBreakSensor extends Sensor {

    private boolean broken;

    public GlassBreakSensor() {
        super(SensorType.GLASS_BREAK, "Датчик разбития стекла", "ДС");
        this.broken = SensorConfig.GlassBreak.getDefaultValue();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "Целое";
    }

    @Override
    public void resetToDefault() {
        setEnabled(true);
        setState(org.reminder.edu.modbuscommon.entity.enums.SensorState.NORMAL);
        this.broken = SensorConfig.GlassBreak.getDefaultValue();
        updateValueDisplay(getDefaultValueDisplay());
    }

    @Override
    public Object getValue() {
        return broken;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            this.broken = (Boolean) value;
        } else if (value instanceof Number) {
            this.broken = ((Number) value).intValue() != 0;
        }
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }
}
