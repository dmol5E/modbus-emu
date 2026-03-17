package org.reminder.edu.modbusslave.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.listener.SensorUpdateListener;
import org.reminder.edu.modbuscommon.entity.service.SensorBehaviorService;

public class SensorViewModel implements SensorUpdateListener {

    private final Sensor sensor;
    private final SensorBehaviorService sensorService;

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty shortName = new SimpleStringProperty();
    private final StringProperty valueDisplay = new SimpleStringProperty();
    private final ObjectProperty<SensorState> state = new SimpleObjectProperty<>();
    private final ObjectProperty<Boolean> enabled = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> id = new SimpleObjectProperty<>();
    private final ObjectProperty<SensorType> type = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> modbusAddress = new SimpleObjectProperty<>();

    public SensorViewModel(Sensor sensor, SensorBehaviorService sensorService) {
        this.sensor = sensor;
        this.sensorService = sensorService;
        sensorService.subscribe(sensor, this);
        syncFromSensor();
    }

    private void syncFromSensor() {
        name.set(sensor.getName());
        shortName.set(sensor.getShortName());
        valueDisplay.set(sensor.getValueDisplay());
        state.set(sensor.getState());
        enabled.set(sensor.isEnabled());
        id.set(sensor.getId());
        type.set(sensor.getType());
        modbusAddress.set(sensor.getModbusAddress());
    }

    public Sensor getSensor() {
        return sensor;
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

    public String getValueDisplay() {
        return valueDisplay.get();
    }

    public StringProperty valueDisplayProperty() {
        return valueDisplay;
    }

    public SensorState getState() {
        return state.get();
    }

    public ObjectProperty<SensorState> stateProperty() {
        return state;
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public ObjectProperty<Boolean> enabledProperty() {
        return enabled;
    }

    public Integer getId() {
        return id.get();
    }

    public ObjectProperty<Integer> idProperty() {
        return id;
    }

    public SensorType getType() {
        return type.get();
    }

    public ObjectProperty<SensorType> typeProperty() {
        return type;
    }

    public Integer getModbusAddress() {
        return modbusAddress.get();
    }

    public ObjectProperty<Integer> modbusAddressProperty() {
        return modbusAddress;
    }

    @Override
    public void onValueChanged(Sensor sensor, Object newValue) {
        Platform.runLater(() -> valueDisplay.set(sensor.getValueDisplay()));
    }

    @Override
    public void onStateChanged(Sensor sensor, SensorState newState) {
        Platform.runLater(() -> state.set(newState));
    }

    @Override
    public void onEnabledChanged(Sensor sensor, boolean enabled) {
        Platform.runLater(() -> this.enabled.set(enabled));
    }

    public void dispose() {
        sensorService.unsubscribe(sensor, this);
    }
}
