package org.reminder.edu.modbusslave.repository;

import java.util.List;
import java.util.Optional;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.repository.SensorRepository;
import org.reminder.edu.modbusslave.comm.ModbusProcessImage;

import com.google.inject.Inject;

public class ProcessImageRepository implements SensorRepository {

    private final ModbusProcessImage processImage;

    @Inject
    public ProcessImageRepository(ModbusProcessImage processImage) {
        this.processImage = processImage;
    }

    @Override
    public List<Sensor> getAllSensors() {
        return processImage.getSensors();
    }

    @Override
    public Optional<Sensor> getSensor(int modbusAddress) {
        return processImage.getSensors().stream()
            .filter(sensor -> sensor.getModbusAddress() == modbusAddress)
            .findFirst();
    }

    @Override
    public void saveSensor(Sensor sensor) {
        processImage.addSensor(sensor);
    }

    @Override
    public List<Sensor> getSensorsByType(SensorType type) {
        return processImage.getSensors().stream()
            .filter(sensor -> sensor.getType() == type)
            .toList();
    }
}
