package org.reminder.edu.modbuscommon;

import java.util.ArrayList;
import java.util.List;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.sensors.AlarmButton;
import org.reminder.edu.modbuscommon.entity.sensors.DoorCrackSensor;
import org.reminder.edu.modbuscommon.entity.sensors.FireButton;
import org.reminder.edu.modbuscommon.entity.sensors.GlassBreakSensor;
import org.reminder.edu.modbuscommon.entity.sensors.PressureSensor;
import org.reminder.edu.modbuscommon.entity.sensors.SmokeSensor;
import org.reminder.edu.modbuscommon.entity.sensors.ThermalSensor;

public class Helper {

    private static final int THERMAL_SENSOR_COUNT = 2;
    private static final int SMOKE_SENSOR_COUNT = 2;
    private static final int PRESSURE_SENSOR_COUNT = 2;
    private static final int FIRE_BUTTON_COUNT = 1;
    private static final int ALARM_BUTTON_COUNT = 2;
    private static final int CRACK_DOOR_SENSOR_COUNT = 2;
    private static final int GLASS_BREAK_SENSOR_COUNT = 3;

    public static List<Sensor> createSensorsFromConfiguration() {
        List<Sensor> sensors = new ArrayList<>();

        for (int i = 0; i < THERMAL_SENSOR_COUNT; i++) {
            sensors.add(new ThermalSensor());
        }

        for (int i = 0; i < SMOKE_SENSOR_COUNT; i++) {
            sensors.add(new SmokeSensor());
        }

        for (int i = 0; i < FIRE_BUTTON_COUNT; i++) {
            sensors.add(new FireButton());
        }

        for (int i = 0; i < ALARM_BUTTON_COUNT; i++) {
            sensors.add(new AlarmButton());
        }

        for (int i = 0; i < PRESSURE_SENSOR_COUNT; i++) {
            sensors.add(new PressureSensor());
        }

        for (int i = 0; i < GLASS_BREAK_SENSOR_COUNT; i++) {
            sensors.add(new GlassBreakSensor());
        }

        for (int i = 0; i < CRACK_DOOR_SENSOR_COUNT; i++) {
            sensors.add(new DoorCrackSensor());
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