package org.reminder.edu.modbusmaster.entity;

import java.nio.ByteBuffer;

import org.reminder.edu.Updatable;
import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.listener.SensorUpdateListener;
import org.reminder.edu.modbuscommon.entity.service.SensorBehaviorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.client.ModbusRtuClient;
import com.digitalpetri.modbus.pdu.ReadInputRegistersRequest;
import com.digitalpetri.modbus.pdu.ReadInputRegistersResponse;
import com.digitalpetri.modbus.pdu.WriteSingleCoilRequest;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

public class SensorProxy implements Updatable, SensorUpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(SensorProxy.class);

    private final Sensor delegate;
    private final SensorBehaviorService sensorService;
    private Button button;
    private ModbusRtuClient client;
    private int slaveId;

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty shortName = new SimpleStringProperty();
    private final StringProperty valueDisplay = new SimpleStringProperty();
    private final ObjectProperty<SensorState> state = new SimpleObjectProperty<>();
    private final ObjectProperty<Boolean> enabled = new SimpleObjectProperty<>();

    public SensorProxy(Sensor sensor, SensorBehaviorService sensorService) {
        this.delegate = sensor;
        this.sensorService = sensorService;
        sensorService.subscribe(sensor, this);
        syncFromSensor();
    }

    private void syncFromSensor() {
        name.set(delegate.getName());
        shortName.set(delegate.getShortName());
        valueDisplay.set(delegate.getValueDisplay());
        state.set(delegate.getState());
        enabled.set(delegate.isEnabled());
    }

    public Sensor getDelegate() {
        return delegate;
    }

    public void setClient(ModbusRtuClient client) {
        this.client = client;
    }

    public void setButton(Button button) {
        this.button = button;
        state.addListener((obs, old, newState) -> {
            if (!enabled.get()) {
                javafx.application.Platform.runLater(() ->
                    button.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;"));
            } else {
                updateButtonStyle(newState);
            }
        });
        enabled.addListener((obs, old, isEnabled) -> {
            if (!isEnabled) {
                javafx.application.Platform.runLater(() ->
                    button.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;"));
            } else {
                updateButtonStyle(state.get());
            }
        });
        updateButtonStyle(state.get());
    }

    private void updateButtonStyle(SensorState sensorState) {
        if (button == null) return;
        String style = switch (sensorState) {
            case NORMAL -> "-fx-background-color: #4CAF50; -fx-text-fill: white;";
            case ALARM  -> "-fx-background-color: #F44336; -fx-text-fill: white;";
            case FAULT  -> "-fx-background-color: #FFC107; -fx-text-fill: black;";
        };
        javafx.application.Platform.runLater(() -> button.setStyle(style));
    }

    public Button getButton() {
        return button;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public int getModbusAddress() {
        return delegate.getModbusAddress();
    }

    public void setModbusAddress(int address) {
        delegate.setModbusAddress(address);
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

    public int getId() {
        return delegate.getId();
    }

    public SensorState getState() {
        return state.get();
    }

    public ObjectProperty<SensorState> stateProperty() {
        return state;
    }

    public int getStateCode() {
        return delegate.getStateCode();
    }

    public void setState(int codeState) {
        delegate.setState(codeState);
    }

    public boolean isOn() {
        return delegate.isOn();
    }

    public void onSensor() {
        delegate.onSensor();
    }

    public void offSensor() {
        delegate.offSensor();
    }

    public void setDefectStatus() {
        delegate.setDefectStatus();
    }

    public void setNormalStatus() {
        delegate.setNormalStatus();
    }

    public void setAlarmStatus() {
        delegate.setAlarmStatus();
    }

    @Override
    public void update() throws Exception {
        int statusAddress = delegate.getModbusAddress() * 2;
        int valueAddress  = delegate.getModbusAddress() * 2 + 1;
        SensorType type   = delegate.getType();

        if (type == SensorType.FIRE_BUTTON || type == SensorType.ALARM_BUTTON) {
            ReadInputRegistersRequest statusRequest = new ReadInputRegistersRequest(statusAddress, 1);
            ReadInputRegistersResponse statusResponse = client.readInputRegisters(slaveId, statusRequest);
            int statusRaw = ByteBuffer.wrap(statusResponse.registers()).getShort() & 0xFFFF;
            sensorService.setValue(delegate, (statusRaw & 0x04) != 0);
        } else {
            ReadInputRegistersRequest request = new ReadInputRegistersRequest(valueAddress, 1);
            ReadInputRegistersResponse response = client.readInputRegisters(slaveId, request);
            int rawValue = ByteBuffer.wrap(response.registers()).getShort() & 0xFFFF;
            sensorService.setValue(delegate, decodeValue(rawValue, type));
        }
    }

    private Object decodeValue(int rawValue, SensorType type) {
        switch (type) {
            case THERMAL:
                return rawValue / 10.0;
            case SMOKE:
                return rawValue;
            case PRESSURE:
                return (double) rawValue;
            case DOOR_CRACK:
            case GLASS_BREAK:
                return rawValue != 0;
            default:
                return rawValue;
        }
    }

    public void writeCoil(int address, boolean value) throws Exception {
        WriteSingleCoilRequest request = new WriteSingleCoilRequest(address, value);
        client.writeSingleCoil(slaveId, request);
    }

    @Override
    public void commit() {
    }

    public SensorType getType() {
        return delegate.getType();
    }

    public Object getValue() {
        return delegate.getValue();
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public ObjectProperty<Boolean> enabledProperty() {
        return enabled;
    }

    public void resetToDefault() {
        sensorService.resetToDefault(delegate);
    }

    public void setEnabled(boolean enabled) {
        sensorService.setEnabled(delegate, enabled);
    }

    public void setState(SensorState state) {
        delegate.setState(state);
    }

    public void setValue(Object value) {
        sensorService.setValue(delegate, value);
    }

    public void turnOff() {
        delegate.turnOff();
    }

    public void turnOn() {
        delegate.turnOn();
    }

    @Override
    public void onValueChanged(Sensor sensor, Object newValue) {
        valueDisplay.set(delegate.getValueDisplay());
    }

    @Override
    public void onStateChanged(Sensor sensor, SensorState newState) {
        state.set(newState);
    }

    @Override
    public void onEnabledChanged(Sensor sensor, boolean enabled) {
        this.enabled.set(enabled);
    }
}
