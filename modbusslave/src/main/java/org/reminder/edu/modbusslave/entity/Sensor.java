package org.reminder.edu.modbusslave.entity;

import org.reminder.edu.modbusslave.entity.enums.SensorState;
import org.reminder.edu.modbusslave.entity.enums.SensorType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;

public interface Sensor {

    int getId();

    SensorType getType();

    String getName();

    String getShortName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    SensorState getState();

    void setState(SensorState state);

    ReadOnlyObjectProperty<SensorState> stateProperty();

    StringProperty nameProperty();

    StringProperty shortNameProperty();

    void resetToDefault();

    void turnOn();

    void turnOff();

    Object getValue();

    void setValue(Object value);

    ReadOnlyStringProperty valueDisplayProperty();

    int getModbusAddress();

    void setModbusAddress(int address);
}
