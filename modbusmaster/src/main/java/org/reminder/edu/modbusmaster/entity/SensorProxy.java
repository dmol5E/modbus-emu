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

public class SensorProxy extends Sensor implements Updatable {

    private static final Logger logger = LoggerFactory.getLogger(SensorProxy.class);

    private final Sensor delegate;
    private Button button;
    private ModbusRtuClient client;
    private int slaveId;

    public SensorProxy(Sensor sensor) {
        super(sensor.getType(), sensor.getName(), sensor.getShortName());
        this.delegate = sensor;
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
        return delegate.getModbusAddress();
    }

    @Override
    public void setModbusAddress(int address) {
        delegate.setModbusAddress(address);
    }

    @Override
    public javafx.beans.property.ReadOnlyStringProperty valueDisplayProperty() {
        return delegate.valueDisplayProperty();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public String getShortName() {
        return delegate.getShortName();
    }

    @Override
    public SensorState getState() {
        return delegate.getState();
    }

    @Override
    public int getStateCode() {
        return delegate.getStateCode();
    }

    @Override
    public void setState(int codeState) {
        delegate.setState(codeState);
    }

    @Override
    public boolean isOn() {
        return delegate.isOn();
    }

    @Override
    public void onSensor() {
        delegate.onSensor();
    }

    @Override
    public void offSensor() {
        delegate.offSensor();
    }

    @Override
    public void setDefectStatus() {
        delegate.setDefectStatus();
    }

    @Override
    public void setNormalStatus() {
        delegate.setNormalStatus();
    }

    @Override
    public void setAlarmStatus() {
        delegate.setAlarmStatus();
    }

    @Override
    public void update() throws Exception {
        int valueAddress = delegate.getModbusAddress() * 2 + 1;

        ReadInputRegistersRequest request = new ReadInputRegistersRequest(valueAddress, 1);
        ReadInputRegistersResponse response = client.readInputRegisters(slaveId, request);

        byte[] bytes = response.registers();
        int rawValue = ByteBuffer.wrap(bytes).getShort() & 0xFFFF;

        Object value = decodeValue(rawValue, delegate.getType());
        delegate.setValue(value);
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
        return delegate.getType();
    }

    @Override
    public Object getValue() {
        return delegate.getValue();
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public StringProperty nameProperty() {
        return delegate.nameProperty();
    }

    @Override
    public void resetToDefault() {
        delegate.resetToDefault();
    }

    @Override
    public void setEnabled(boolean enabled) {
        delegate.setEnabled(enabled);
    }

    @Override
    public void setState(SensorState state) {
        delegate.setState(state);
    }

    @Override
    public void setValue(Object value) {
        delegate.setValue(value);
    }

    @Override
    public StringProperty shortNameProperty() {
        return delegate.shortNameProperty();
    }

    @Override
    public ReadOnlyObjectProperty<SensorState> stateProperty() {
        return delegate.stateProperty();
    }

    @Override
    public void turnOff() {
        delegate.turnOff();
    }

    @Override
    public void turnOn() {
        delegate.turnOn();
    }

    @Override
    protected String getDefaultValueDisplay() {
        return "-";
    }
}
