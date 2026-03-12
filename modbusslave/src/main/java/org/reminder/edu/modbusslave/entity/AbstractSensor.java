package org.reminder.edu.modbusslave.entity;

import org.reminder.edu.modbusslave.entity.enums.SensorState;
import org.reminder.edu.modbusslave.entity.enums.SensorType;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractSensor implements Sensor {

    private static int idCounter = 0;

    private final int id;
    private final SensorType type;
    private final StringProperty name;
    private final StringProperty shortName;
    private final StringProperty valueDisplay;
    private final ReadOnlyObjectWrapper<SensorState> state;
    private boolean enabled;
    private int modbusAddress;

    protected AbstractSensor(SensorType type, String baseName, String baseShortName) {
        this.id = ++idCounter;
        this.type = type;
        this.name = new SimpleStringProperty(baseName + " " + id);
        this.shortName = new SimpleStringProperty(baseShortName + id);
        this.state = new ReadOnlyObjectWrapper<>(SensorState.NORMAL);
        this.valueDisplay = new SimpleStringProperty(getDefaultValueDisplay());
        this.enabled = true;
        this.modbusAddress = id - 1;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public SensorType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public SensorState getState() {
        return state.get();
    }

    @Override
    public void setState(SensorState state) {
        this.state.set(state);
    }

    @Override
    public ReadOnlyObjectProperty<SensorState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    @Override
    public int getModbusAddress() {
        return modbusAddress;
    }

    @Override
    public void setModbusAddress(int address) {
        this.modbusAddress = address;
    }

    @Override
    public ReadOnlyStringProperty valueDisplayProperty() {
        return valueDisplay;
    }

    protected void updateValueDisplay(String display) {
        this.valueDisplay.set(display);
    }

    protected abstract String getDefaultValueDisplay();

    @Override
    public void turnOn() {
        setEnabled(true);
    }

    @Override
    public void turnOff() {
        setEnabled(false);
    }

    @Override
    public abstract void resetToDefault();

    @Override
    public abstract Object getValue();

    @Override
    public abstract void setValue(Object value);
}
