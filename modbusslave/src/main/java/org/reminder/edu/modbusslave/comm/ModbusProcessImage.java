package org.reminder.edu.modbusslave.comm;

import org.reminder.edu.modbusslave.entity.Sensor;
import org.reminder.edu.modbusslave.entity.enums.SensorState;
import org.reminder.edu.modbusslave.entity.enums.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpetri.modbus.server.ProcessImage;
import com.digitalpetri.modbus.server.ReadWriteModbusServices;

import java.nio.ByteBuffer;
import java.util.*;

public class ModbusProcessImage {

    private static final Logger logger = LoggerFactory.getLogger(ModbusProcessImage.class);

    private final List<Sensor> sensors = new ArrayList<>();
    private final ProcessImage processImage;
    private int coilBaseAddress = 0;
    private int registerBaseAddress = 0;

    public ModbusProcessImage() {
        this.processImage = new ProcessImage();
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);

        int sensorIndex = sensors.size() - 1;

        int enableCoilAddress = coilBaseAddress + sensorIndex * 4;
        int resetCoilAddress = coilBaseAddress + sensorIndex * 4 + 1;
        int alarmCoilAddress = coilBaseAddress + sensorIndex * 4 + 2;
        int faultCoilAddress = coilBaseAddress + sensorIndex * 4 + 3;

        processImage.get(tx -> {
            tx.writeCoils(map -> {
                map.put(enableCoilAddress, true);
                map.put(resetCoilAddress, false);
                map.put(alarmCoilAddress, false);
                map.put(faultCoilAddress, false);
            });
            return null;
        });

        int statusRegisterAddress = registerBaseAddress + sensorIndex * 2;
        int valueRegisterAddress = registerBaseAddress + sensorIndex * 2 + 1;

        updateSensorRegisters(sensor, statusRegisterAddress, valueRegisterAddress);

        logger.info("Added sensor {} at coils [{}-{}] and registers [{}-{}]",
                sensor.getName(),
                enableCoilAddress, faultCoilAddress,
                statusRegisterAddress, valueRegisterAddress);
    }

    public void updateSensorValue(Sensor sensor) {
        int sensorIndex = sensors.indexOf(sensor);
        int statusRegisterAddress = registerBaseAddress + sensorIndex * 2;
        int valueRegisterAddress = registerBaseAddress + sensorIndex * 2 + 1;
        updateSensorRegisters(sensor, statusRegisterAddress, valueRegisterAddress);
    }

    public void updateSensorState(Sensor sensor) {
        int sensorIndex = sensors.indexOf(sensor);
        int statusRegisterAddress = registerBaseAddress + sensorIndex * 2;
        int valueRegisterAddress = registerBaseAddress + sensorIndex * 2 + 1;
        updateSensorRegisters(sensor, statusRegisterAddress, valueRegisterAddress);
    }

    private void updateSensorRegisters(Sensor sensor, int statusAddress, int valueAddress) {
        int statusValue = encodeStatus(sensor);
        int valueValue = encodeValue(sensor);

        byte[] statusBytes = ByteBuffer.allocate(2).putShort((short) statusValue).array();
        byte[] valueBytes = ByteBuffer.allocate(2).putShort((short) valueValue).array();

        processImage.get(tx -> {
            tx.writeInputRegisters(map -> {
                map.put(statusAddress, statusBytes);
                map.put(valueAddress, valueBytes);
            });
            return null;
        });
    }

    public void setCoil(int address, boolean value) {
        processImage.get(tx -> {
            tx.writeCoils(map -> {
                map.put(address, value);
            });
            return null;
        });
    }

    public boolean getCoil(int address) {
        return processImage.get(tx -> {
            return tx.readCoils(map -> map.getOrDefault(address, false));
        });
    }

    public int getInputRegister(int address) {
        byte[] bytes = processImage.get(tx -> {
            return tx.readInputRegisters(map -> map.getOrDefault(address, new byte[2]));
        });
        return ByteBuffer.wrap(bytes).getShort() & 0xFFFF;
    }

    public void setInputRegister(int address, int value) {
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) value).array();
        processImage.get(tx -> {
            tx.writeInputRegisters(map -> {
                map.put(address, bytes);
            });
            return null;
        });
    }

    private int encodeStatus(Sensor sensor) {
        int status = 0;
        status |= sensor.isEnabled() ? 0x01 : 0x00;
        status |= sensor.getState() == SensorState.NORMAL ? 0x02 : 0x00;
        status |= sensor.getState() == SensorState.ALARM ? 0x04 : 0x00;
        status |= sensor.getState() == SensorState.FAULT ? 0x08 : 0x00;
        return status;
    }

    private int encodeValue(Sensor sensor) {
        SensorType type = sensor.getType();
        Object value = sensor.getValue();

        if (value == null) {
            return 0;
        }

        if (type == SensorType.THERMAL) {
            double temp = ((Number) value).doubleValue();
            return (int) (temp * 10);
        } else if (type == SensorType.SMOKE) {
            return ((Number) value).intValue();
        } else if (type == SensorType.PRESSURE) {
            double pressure = ((Number) value).doubleValue();
            return (int) pressure;
        } else if (type == SensorType.DOOR_CRACK || type == SensorType.GLASS_BREAK) {
            return ((Boolean) value) ? 1 : 0;
        }

        return 0;
    }

    public List<Sensor> getSensors() {
        return new ArrayList<>(sensors);
    }

    public ProcessImage getProcessImage() {
        return processImage;
    }

    public ReadWriteModbusServices createModbusServices() {
        return new ReadWriteModbusServices() {
            @Override
            protected Optional<ProcessImage> getProcessImage(int unitId) {
                return Optional.of(processImage);
            }
        };
    }
}
