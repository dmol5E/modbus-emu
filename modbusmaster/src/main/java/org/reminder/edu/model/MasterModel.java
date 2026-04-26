package org.reminder.edu.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.reminder.edu.modbuscommon.Helper;
import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.repository.SensorRepository;
import org.reminder.edu.modbuscommon.entity.service.SensorBehaviorService;
import org.reminder.edu.modbusmaster.entity.SensorProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.client.ModbusRtuClient;
import com.digitalpetri.modbus.serial.client.SerialPortClientTransport;
import com.fazecast.jSerialComm.SerialPort;

public class MasterModel implements SensorRepository {

    private static final Logger logger = LoggerFactory.getLogger(MasterModel.class);

    private ModbusRtuClient client;
    private int baudRate;
    private int dataBits;
    private String flowControl;
    private String parity;
    private String portName;
    private int slaveId;
    private String stopBits;

    private final Collection<SensorProxy> sensors;
    private final SensorBehaviorService sensorService;
    private final Map<Integer, Sensor> sensorByAddress = new HashMap<>();

    private static final Map<String, Integer> PARITY_MAPPING = new HashMap<>();

    static {
        PARITY_MAPPING.put("None", SerialPort.NO_PARITY);
        PARITY_MAPPING.put("Odd", SerialPort.ODD_PARITY);
        PARITY_MAPPING.put("Even", SerialPort.EVEN_PARITY);
        PARITY_MAPPING.put("Space", SerialPort.SPACE_PARITY);
    }

    public MasterModel() {
        List<Sensor> originSensors = Helper.createSensorsFromConfiguration();
        this.sensorService = new SensorBehaviorService(this);
        sensors = new LinkedList<>();
        for (Sensor sensor : originSensors) {
            sensorByAddress.put(sensor.getModbusAddress(), sensor);
            sensors.add(new SensorProxy(sensor, sensorService));
        }
    }

    public void openConnection(String portName, int baudRate, int dataBits,
                              String parity, String stopBits, int slaveId) throws Exception {
        if (isOpenConnection()) {
            return;
        }

        this.portName = portName;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.parity = parity;
        this.stopBits = stopBits;
        this.slaveId = slaveId;

        SerialPortClientTransport transport = SerialPortClientTransport.create(cfg -> {
            cfg.serialPort = portName;
            cfg.baudRate = baudRate;
            cfg.dataBits = dataBits;
            cfg.parity = PARITY_MAPPING.getOrDefault(parity, SerialPort.NO_PARITY);
            cfg.stopBits = Integer.parseInt(stopBits);
        });

        client = ModbusRtuClient.create(transport);
        client.connect();

        for (SensorProxy sensor : sensors) {
            sensor.setClient(client);
            sensor.setSlaveId(slaveId);
        }

        logger.info("Connection opened on port {} with baud rate {}", portName, baudRate);
    }

    public void closeConnection() {
        if (!isOpenConnection()) {
            return;
        }
        try {
            client.disconnect();
            client = null;
            logger.info("Connection closed");
        } catch (Exception e) {
            logger.error("Error closing connection", e);
        }
    }

    public boolean isOpenConnection() {
        return client != null && client.isConnected();
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

    @Override
    public List<Sensor> getAllSensors() {
        return new LinkedList<>(sensorByAddress.values());
    }

    @Override
    public java.util.Optional<Sensor> getSensor(int modbusAddress) {
        return java.util.Optional.ofNullable(sensorByAddress.get(modbusAddress));
    }

    @Override
    public void saveSensor(Sensor sensor) {
        sensorByAddress.put(sensor.getModbusAddress(), sensor);
    }

    @Override
    public void updateSensor(Sensor sensor) {
        // In-memory store: sensor object is already updated by reference
    }

    @Override
    public List<Sensor> getSensorsByType(SensorType type) {
        return sensorByAddress.values().stream()
            .filter(s -> s.getType() == type)
            .toList();
    }
}
