package org.reminder.edu.modbusslave;

import java.util.ArrayList;
import java.util.List;

import org.reminder.edu.configuration.ApplicationConfiguration;
import org.reminder.edu.modbusslave.comm.ModbusProcessImage;
import org.reminder.edu.modbusslave.entity.Sensor;
import org.reminder.edu.modbusslave.entity.sensors.*;

public class Helper {

    public static List<Sensor> createSensorsFromConfiguration(ModbusProcessImage modbusProcessImage) {
        List<Sensor> sensors = new ArrayList<>();

        ApplicationConfiguration config = ApplicationConfiguration.getInstance();

        for (int i = 0; i < config.getThermalSensorCount(); i++) {
            sensors.add(new ThermalSensor());
        }

        for (int i = 0; i < config.getSmokeSensorCount(); i++) {
            sensors.add(new SmokeSensor());
        }

        for (int i = 0; i < config.getPressureSensorCount(); i++) {
            sensors.add(new PressureSensor());
        }

        for (int i = 0; i < config.getFireButtonCount(); i++) {
            sensors.add(new FireButton());
        }

        for (int i = 0; i < config.getAlarmButtonCount(); i++) {
            sensors.add(new AlarmButton());
        }

        for (int i = 0; i < config.getCrackDoorSensorCount(); i++) {
            sensors.add(new DoorCrackSensor());
        }

        for (int i = 0; i < config.getGlassBreakSensorCount(); i++) {
            sensors.add(new GlassBreakSensor());
        }

        return sensors;
    }

    public static ThermalSensor createThermalSensor() {
        return new ThermalSensor();
    }

    public static SmokeSensor createSmokeSensor() {
        return new SmokeSensor();
    }

    public static PressureSensor createPressureSensor() {
        return new PressureSensor();
    }

    public static FireButton createFireButton() {
        return new FireButton();
    }

    public static AlarmButton createAlarmButton() {
        return new AlarmButton();
    }

    public static DoorCrackSensor createDoorCrackSensor() {
        return new DoorCrackSensor();
    }

    public static GlassBreakSensor createGlassBreakSensor() {
        return new GlassBreakSensor();
    }
}
