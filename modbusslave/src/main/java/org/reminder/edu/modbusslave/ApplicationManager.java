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

import com.digitalpetri.modbus.server.ModbusRtuServer;
import com.digitalpetri.modbus.server.ProcessImage;
import com.digitalpetri.modbus.server.ReadWriteModbusServices;
import com.digitalpetri.modbus.server.SerialPortServerTransport;
import com.fazecast.jSerialComm.SerialPort;

import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.io.ModbusTransport;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.net.SerialConnection;
import net.wimpi.modbus.procimg.DigitalOut;
import net.wimpi.modbus.procimg.ObservableDigitalOut;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.util.SerialParameters;

public class ApplicationManager {

    private List<DataRegisterSensor> sensors;
    private List<DigitalOut> digOuts;
    private List<Register> registers;
    private List<CoilSensorMapper> mappers;

    private String portName;
    private int baudRate;
    private int dataBits;
    private int parity;
    private String stopBits;
    private String flowControl;
    private int slaveId;
    private Thread currentModBusListener;
    private MessageRenderer renderer;
    private Map<String, Integer> parityMapping;
    ModbusRtuServer server;

    public ApplicationManager() {
        sensors = Helper.createSensorsFromConfiguration();
        digOuts = new ArrayList<>();
        registers = new ArrayList<>();
        mappers = new ArrayList<>();

        for (DataRegisterSensor sensor : sensors) {
            registers.add(sensor.getRegister());

            List<ObservableDigitalOut> digs = Helper
                    .createFourObservableDigitalOut();
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
        this.renderer = new EmptyMessageRenderer();
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
            this.renderer.info("Listening " + this.portName);
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void stopModbusListener() {
        try {
            server.stop();
            this.renderer.info("Listening has been stopped.");
        } catch (ExecutionException | InterruptedException ex) {
            ex.printStackTrace();
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

    public void setRenderer(MessageRenderer renderer) {
        this.renderer = renderer;
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

    private static class ModBusListener extends Thread {

        private SerialConnection connection;
        private MessageRenderer log;

        public ModBusListener(SerialParameters params, MessageRenderer log) {
            this.connection = new SerialConnection(params);
            this.log = log;
            this.setDaemon(true);
        }

        @Override
        public void run() {

            try {
                connection.open();
                ModbusTransport transport = connection.getModbusTransport();

                while (!this.isInterrupted()) {
                    ModbusRequest request = transport.readRequest();
                    ModbusResponse response = null;

                    if (ModbusCoupler.getReference()
                            .getProcessImage() == null) {
                        response = request.createExceptionResponse(
                                Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                    } else {
                        response = request.createResponse();
                    }

                    log.info("Request: (" + request.getFunctionCode() + ") "
                            + request.getHexMessage().toUpperCase());
                    log.info("Response: (" + response.getFunctionCode() + ") "
                            + response.getHexMessage().toUpperCase());

                    transport.writeMessage(response);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                connection.close();
            }
        }
    }
}
