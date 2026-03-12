package org.reminder.edu.modbusmaster.entity;

import java.nio.ByteBuffer;

import org.reminder.edu.Updatable;
import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.client.ModbusRtuClient;
import com.digitalpetri.modbus.pdu.ReadInputRegistersRequest;
import com.digitalpetri.modbus.pdu.ReadInputRegistersResponse;
import com.digitalpetri.modbus.pdu.WriteSingleCoilRequest;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

public class SensorProxy implements Sensor, Updatable {

    private static final Logger logger = LoggerFactory.getLogger(SensorProxy.class);

    private final Sensor sensor;
    private Button button;
    private ModbusRtuClient client;
    private int slaveId;

    public SensorProxy(Sensor sensor) {
        this.sensor = sensor;
    }

    public void setClient(ModbusRtuClient client) {
        this.client = client;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public Button getButton() {
        return button;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    @Override
    public int getModbusAddress() {
        return sensor.getModbusAddress();
    }

    @Override
    public void setModbusAddress(int address) {
        sensor.setModbusAddress(address);
    }

    @Override
    public javafx.beans.property.ReadOnlyStringProperty valueDisplayProperty() {
        return sensor.valueDisplayProperty();
    }

    @Override
    public String getName() {
        return sensor.getName();
    }

    @Override
    public int getId() {
        return sensor.getId();
    }

    @Override
    public String getShortName() {
        return sensor.getShortName();
    }

    @Override
    public SensorState getState() {
        return sensor.getState();
    }

    @Override
    public int getStateCode() {
        return sensor.getStateCode();
    }

    @Override
    public void setState(int codeState) {
        sensor.setState(codeState);
    }

    @Override
    public boolean isOn() {
        return sensor.isOn();
    }

    @Override
    public void onSensor() {
        sensor.onSensor();
    }

    @Override
    public void offSensor() {
        sensor.offSensor();
    }

    @Override
    public void setDefectStatus() {
        sensor.setDefectStatus();
    }

    @Override
    public void setNormalStatus() {
        sensor.setNormalStatus();
    }

    @Override
    public void setAlarmStatus() {
        sensor.setAlarmStatus();
    }

    @Override
    public void update() throws Exception {
        int valueAddress = sensor.getModbusAddress() * 2 + 1;

        ReadInputRegistersRequest request = new ReadInputRegistersRequest(valueAddress, 1);
        ReadInputRegistersResponse response = client.readInputRegisters(slaveId, request);

        byte[] bytes = response.registers();
        int rawValue = ByteBuffer.wrap(bytes).getShort() & 0xFFFF;

        Object value = decodeValue(rawValue, sensor.getType());
        sensor.setValue(value);
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

    @Override
    public SensorType getType() {
        return sensor.getType();
    }

    @Override
    public Object getValue() {
        return sensor.getValue();
    }

    @Override
    public boolean isEnabled() {
        return sensor.isEnabled();
    }

    @Override
    public StringProperty nameProperty() {
        return sensor.nameProperty();
    }

    @Override
    public void resetToDefault() {
        sensor.resetToDefault();
    }

    @Override
    public void setEnabled(boolean enabled) {
        sensor.setEnabled(enabled);
    }

    @Override
    public void setState(SensorState state) {
        sensor.setState(state);
    }

    @Override
    public void setValue(Object value) {
        sensor.setValue(value);
    }

    @Override
    public StringProperty shortNameProperty() {
        return sensor.shortNameProperty();
    }

    @Override
    public ReadOnlyObjectProperty<SensorState> stateProperty() {
        return sensor.stateProperty();
    }

    @Override
    public void turnOff() {
        sensor.turnOff();
    }

    @Override
    public void turnOn() {
        sensor.turnOn();
    }
}