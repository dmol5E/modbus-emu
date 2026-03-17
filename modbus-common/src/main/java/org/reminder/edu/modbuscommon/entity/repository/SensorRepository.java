package org.reminder.edu.modbuscommon.entity.repository;

import java.util.List;
import java.util.Optional;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;

public interface SensorRepository {

    List<Sensor> getAllSensors();

    Optional<Sensor> getSensor(int modbusAddress);

    void saveSensor(Sensor sensor);

    List<Sensor> getSensorsByType(SensorType type);
}
