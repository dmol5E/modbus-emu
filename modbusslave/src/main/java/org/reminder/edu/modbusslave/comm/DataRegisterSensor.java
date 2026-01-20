package org.reminder.edu.modbusslave.comm;

import org.reminder.edu.modbusslave.entity.Sensor;
import org.reminder.edu.modbusslave.entity.SensorImpl;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.wimpi.modbus.procimg.Register;

public class DataRegisterSensor extends SensorImpl implements Sensor {

    private Register register;
    private IntegerProperty registerNumber;
    private IntegerProperty registerValue;
    private StringProperty comment;

    public DataRegisterSensor(String name, String alarmMessage,String shortName, Register register) {
        super(name, alarmMessage, shortName);
        this.register = register;
        onSensor();
        setNormalStatus();
        getRegisterNumber().setValue(this.getId() - 1);
        getRegisterValue().setValue(register.getValue());
        getComment().set(this.getName());
    }

    @Override
    public void onSensor() {
        super.onSensor();
        byte[] bytes = register.toBytes();
        bytes[1] = (byte) (bytes[1] | 1);
        register.setValue(bytes);
        // System.out.println("Sensor: " + this.getName() + " on!");
    }

    @Override
    public void offSensor() {
        super.offSensor();
        byte[] bytes = register.toBytes();
        bytes[1] = (byte) (bytes[1] & ~1);
        // System.out.println("Sensor: " + this.getName() + " off!");
    }

    @Override
    public void setNormalStatus() {
        super.setNormalStatus();
        byte[] bytes = register.toBytes();
        bytes[1] = (byte) ((bytes[1] | 0b10) & ~0b1100);
    }

    @Override
    public void setAlarmStatus() {
        super.setAlarmStatus();
        byte[] bytes = register.toBytes();
        bytes[1] = (byte) ((bytes[1] | 0b100) & ~0b1010);
    }

    @Override
    public void setDefectStatus() {
        super.setDefectStatus();
        byte[] bytes = register.toBytes();
        bytes[1] = (byte) ((bytes[1] | 0b1000) & ~0b0110);
    }

    public Register getRegister() {
        return this.register;
    }

    public IntegerProperty getRegisterNumber() {
        if (registerNumber == null) {
            registerNumber = new SimpleIntegerProperty(this, "registerNumber");
        }
        return registerNumber;
    }

    public IntegerProperty getRegisterValue() {
        if (registerValue == null) {
            registerValue = new SimpleIntegerProperty(this, "registerValue");
        }
        return registerValue;
    }

    public StringProperty getComment() {
        if (comment == null) {
            comment = new SimpleStringProperty(this, "comment");
        }
        return comment;
    }
}
