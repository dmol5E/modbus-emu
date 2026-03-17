package org.reminder.edu.modbuscommon.entity;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public interface Sensor {
    int getId();

    SensorType getType();

    String getName();

    String getShortName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    SensorState getState();

    void setState(SensorState state);

    void setState(int stateCode);

    int getStateCode();

    ReadOnlyObjectProperty<SensorState> stateProperty();

    StringProperty nameProperty();

    StringProperty shortNameProperty();

    void resetToDefault();

    void turnOn();

    void turnOff();

    boolean isOn();

    void onSensor();

    void offSensor();

    void setNormalStatus();

    void setAlarmStatus();

    void setDefectStatus();

    Object getValue();

    void setValue(Object value);

    ReadOnlyStringProperty valueDisplayProperty();

    int getModbusAddress();

    void setModbusAddress(int address);
}
