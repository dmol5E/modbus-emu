package org.reminder.edu.modbusslave;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.reminder.edu.configuration.ApplicationConfiguration;
import org.reminder.edu.modbusslave.comm.ModbusProcessImage;
import org.reminder.edu.modbusslave.entity.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.serial.server.SerialPortServerTransport;
import com.digitalpetri.modbus.server.ModbusRtuServer;
import com.digitalpetri.modbus.server.ReadWriteModbusServices;
import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;

public final class ModBusSecondary {

    private static final Logger logger = LoggerFactory.getLogger(ModBusSecondary.class);

    private final List<Sensor> sensors;
    private final ModbusProcessImage modbusProcessImage;

    private String portName;
    private int baudRate;
    private int dataBits;
    private int parity;
    private String stopBits;
    private String flowControl;
    private int slaveId;
    private Map<String, Integer> parityMapping;
    private ModbusRtuServer server;

    @Inject
    public ModBusSecondary() {
        this.modbusProcessImage = new ModbusProcessImage();
        this.sensors = Helper.createSensorsFromConfiguration(modbusProcessImage);

        for (Sensor sensor : sensors) {
            modbusProcessImage.addSensor(sensor);
        }

        ApplicationConfiguration appConfig = ApplicationConfiguration.getInstance();

        this.portName = "COM1";
        this.baudRate = appConfig.getBaudRate();
        this.parity = appConfig.getParity();
        Map<String, Integer> parityMapping = new HashMap<>();
        parityMapping.put("None", SerialPort.NO_PARITY);
        parityMapping.put("Odd", SerialPort.ODD_PARITY);
        parityMapping.put("Even", SerialPort.EVEN_PARITY);
        parityMapping.put("Mark", SerialPort.MARK_PARITY);
        parityMapping.put("Space", SerialPort.SPACE_PARITY);
        this.parityMapping = parityMapping;
        this.stopBits = appConfig.getStopBits();
        this.flowControl = "None";
        this.slaveId = appConfig.getSlaveUuid();
    }

    public void startModbusListener() {
        ReadWriteModbusServices modbusServices = modbusProcessImage.createModbusServices();

        server = ModbusRtuServer.create(
            SerialPortServerTransport.create(
                cfg -> {
                    cfg.serialPort = portName;
                    cfg.baudRate = baudRate;
                    cfg.parity = parity;
                    cfg.stopBits = Integer.parseInt(stopBits);
                }
            ),
            modbusServices);
        try {
            server.start();
            logger.info("Modbus server started on port: {}", this.portName);
        } catch (ExecutionException | InterruptedException ex) {
            logger.error("Error starting Modbus server on port: {}", this.portName, ex);
        }
    }

    public void stopModbusListener() {
        try {
            if (server != null) {
                server.stop();
            }
            logger.info("Modbus server stopped successfully");
        } catch (ExecutionException | InterruptedException ex) {
            logger.error("Error stopping Modbus server", ex);
        }
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public void setParity(String parity) {
        this.parity = this.parityMapping.get(parity);
    }

    public void setStopBits(String stopBits) {
        this.stopBits = stopBits;
    }

    public void setFlowControl(String flowControl) {
        this.flowControl = flowControl;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public ModbusProcessImage getModbusProcessImage() {
        return modbusProcessImage;
    }

    public Set<String> getParityValues() {
        return this.parityMapping.keySet();
    }
}
