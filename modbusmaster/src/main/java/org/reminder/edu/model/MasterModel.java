package org.reminder.edu.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.reminder.edu.MessagePrinter;
import org.reminder.edu.modbusmaster.entity.SensorProxy;
import org.reminder.edu.modbusslave.Helper;
import org.reminder.edu.modbusslave.comm.DataRegisterSensor;
import org.reminder.edu.modbusslave.entity.Sensor;

import net.wimpi.modbus.net.SerialConnection;
import net.wimpi.modbus.util.SerialParameters;

public class MasterModel {

    private SerialConnection connection;

    private int baudRate;
    private int dataBits;
    private String flowControl;
    private int masterId;
    private String parity;
    private String portName;
    private int slaveId;
    private String stopBits;
    
    private Collection<SensorProxy> sensors;
    private MessagePrinter messageRenderer;
    
    public MasterModel() {
        List<DataRegisterSensor> originSensors = Helper.createSensorsFromConfiguration();
        sensors = new LinkedList<SensorProxy>();
        for (Sensor sensor: originSensors) {
            sensors.add(new SensorProxy(sensor));
        }
    }

    public void openConnection(SerialParameters params, int slaveId) throws Exception {
        if (isOpenConnection())
            return;
        connection = new SerialConnection(params);
        connection.open();
        for (SensorProxy sensor: sensors) {
            sensor.setConnection(connection);
            sensor.setSlaveId(slaveId);
        }
    }
    
    public void closeConnection() {
        if (!isOpenConnection())
            return;
        connection.close();
    }
    
    public SerialConnection getSerialConnection() {
        return this.connection;
    }
    
    public boolean isOpenConnection() {
        return connection == null ? false : connection.isOpen();
    }
    
    public Collection<SensorProxy> getSensors() {
        return sensors;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public String getFlowControl() {
        return flowControl;
    }

    public void setFlowControl(String flowControl) {
        this.flowControl = flowControl;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public String getStopBits() {
        return stopBits;
    }

    public void setStopBits(String stopBits) {
        this.stopBits = stopBits;
    }

    public MessagePrinter getMessageRenderer() {
        return messageRenderer;
    }

    public void setMessageRenderer(MessagePrinter messageRenderer) {
        this.messageRenderer = messageRenderer;
    }
}
