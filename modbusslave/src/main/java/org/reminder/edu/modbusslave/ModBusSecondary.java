package org.reminder.edu.modbusslave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.reminder.edu.configuration.ApplicationConfiguration;
import org.reminder.edu.modbusslave.comm.CoilSensorMapper;
import org.reminder.edu.modbusslave.comm.DataRegisterSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.serial.server.SerialPortServerTransport;
import com.digitalpetri.modbus.server.ModbusRtuServer;
import com.digitalpetri.modbus.server.ProcessImage;
import com.digitalpetri.modbus.server.ReadWriteModbusServices;
import com.fazecast.jSerialComm.SerialPort;
import com.google.inject.Inject;

import net.wimpi.modbus.procimg.DigitalOut;
import net.wimpi.modbus.procimg.ObservableDigitalOut;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleProcessImage;

public final class ModBusSecondary {
    
    private static final Logger logger = LoggerFactory.getLogger(ModBusSecondary.class);

    private List<DataRegisterSensor> sensors;
    private final List<DigitalOut> digOuts = new ArrayList<>();
    private final List<Register> registers = new ArrayList<>();
    private final List<CoilSensorMapper> mappers = new ArrayList<>();

    private String portName;
    private int baudRate;
    private int dataBits;
    private int parity;
    private String stopBits;
    private String flowControl;
    private int slaveId;
    private Thread currentModBusListener;
    private Map<String, Integer> parityMapping;
    ModbusRtuServer server;

    @Inject
    public ModBusSecondary() {
        sensors = Helper.createSensorsFromConfiguration();

        for (DataRegisterSensor sensor : sensors) {
            registers.add(sensor.getRegister());

            List<ObservableDigitalOut> digs = Helper.createFourObservableDigitalOut();
            mappers.add(new CoilSensorMapper(sensor, digs));
            digOuts.addAll(digs);
        }

        ApplicationConfiguration appConfig = ApplicationConfiguration
                .getInstance();

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

    protected SimpleProcessImage buildSimpleProcessImage() {
        SimpleProcessImage spi = new SimpleProcessImage();

        for (DigitalOut digIut : digOuts) {
            spi.addDigitalOut(digIut);
        }

        for (Register register : registers) {
            spi.addInputRegister(register);
        }

        return spi;
    }

    public void startModbusListener() {
        ProcessImage processImage = new ProcessImage();

        ReadWriteModbusServices modbusServices = new ReadWriteModbusServices() {

            @Override
            protected Optional<ProcessImage> getProcessImage(int unitId) {
                return Optional.of(processImage);
            }
            
        };

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

    public List<DataRegisterSensor> getSensors() {
        return sensors;
    }

    public List<CoilSensorMapper> getMappers() {
        return mappers;
    }

    public Set<String> getParityValues() {
        return this.parityMapping.keySet();
    }
}
