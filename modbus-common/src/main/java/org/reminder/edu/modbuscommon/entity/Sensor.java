package org.reminder.edu.modbuscommon.entity;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public abstract class Sensor {

    private static int idCounter = 0;

    private final int id;
    private final SensorType type;
    private final StringProperty name;
    private final StringProperty shortName;
    private final StringProperty valueDisplay;
    private final ReadOnlyObjectWrapper<SensorState> state;
    private boolean enabled;
    private int modbusAddress;

    protected Sensor(SensorType type, String baseName, String baseShortName) {
        this.id = ++idCounter;
        this.type = type;
        this.name = new SimpleStringProperty(baseName + " " + id);
        this.shortName = new SimpleStringProperty(baseShortName + id);
        this.state = new ReadOnlyObjectWrapper<>(SensorState.NORMAL);
        this.valueDisplay = new SimpleStringProperty(getDefaultValueDisplay());
        this.enabled = true;
        this.modbusAddress = id - 1;
    }

    public int getId() {
        return id;
    }

    public SensorType getType() {
        return type;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SensorState getState() {
        return state.get();
    }

    public void setState(SensorState state) {
        this.state.set(state);
    }

    public void setState(int stateCode) {
        switch (stateCode) {
            case 1:
                setState(SensorState.NORMAL);
                break;
            case 2:
                setState(SensorState.ALARM);
                break;
            case 3:
                setState(SensorState.FAULT);
                break;
            default:
                setState(SensorState.NORMAL);
        }
    }

    public int getStateCode() {
        SensorState currentState = getState();
        switch (currentState) {
            case NORMAL:
                return 1;
            case ALARM:
                return 2;
            case FAULT:
                return 3;
            default:
                return 0;
        }
    }

    public boolean isOn() {
        return isEnabled();
    }

    public void onSensor() {
        setEnabled(true);
    }

    public void offSensor() {
        setEnabled(false);
    }

    public void setNormalStatus() {
        setState(SensorState.NORMAL);
    }

    public void setAlarmStatus() {
        setState(SensorState.ALARM);
    }

    public void setDefectStatus() {
        setState(SensorState.FAULT);
    }

    public ReadOnlyObjectProperty<SensorState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    public int getModbusAddress() {
        return modbusAddress;
    }

    public void setModbusAddress(int address) {
        this.modbusAddress = address;
    }

    public ReadOnlyStringProperty valueDisplayProperty() {
        return valueDisplay;
    }

    protected void updateValueDisplay(String display) {
        this.valueDisplay.set(display);
    }

    protected abstract String getDefaultValueDisplay();

    public void turnOn() {
        setEnabled(true);
    }

    public void turnOff() {
        setEnabled(false);
    }

    public abstract void resetToDefault();

    public abstract Object getValue();

    public abstract void setValue(Object value);
}
