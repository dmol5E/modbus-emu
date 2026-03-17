package org.reminder.edu.configuration;

import static org.reminder.edu.configuration.DefaultConfiguration.ALARM_BUTTON_COUNT;
import static org.reminder.edu.configuration.DefaultConfiguration.BAUD_RATE;
import static org.reminder.edu.configuration.DefaultConfiguration.CRACK_DOOR_SENSOR_COUNT;
import static org.reminder.edu.configuration.DefaultConfiguration.DATA_BITS;
import static org.reminder.edu.configuration.DefaultConfiguration.FIRE_BUTTON_COUNT;
import static org.reminder.edu.configuration.DefaultConfiguration.GLASS_BREAK_SENSOR_COUNT;
import static org.reminder.edu.configuration.DefaultConfiguration.PRESSURE_SENSOR_COUNT;
import static org.reminder.edu.configuration.DefaultConfiguration.SLAVE_ADRESS;
import static org.reminder.edu.configuration.DefaultConfiguration.SLAVE_UUID;
import static org.reminder.edu.configuration.DefaultConfiguration.SMOKE_SENSOR_COUNT;
import static org.reminder.edu.configuration.DefaultConfiguration.STOP_BITS;
import static org.reminder.edu.configuration.DefaultConfiguration.THERMAL_SENSOR_COUNT;

import com.fazecast.jSerialComm.SerialPort;

public class ApplicationConfiguration {

    private static final int MEASURING_DEVICE_COUNT = 15;
    private static ApplicationConfiguration instance;

    private int slaveAdress = SLAVE_ADRESS;
    private int parity = SerialPort.NO_PARITY;
    private int baudRate = BAUD_RATE;
    private int dataBits = DATA_BITS;
    private String stopBits = STOP_BITS;
    private int thermalSensorCount = THERMAL_SENSOR_COUNT;
    private int smokeSensorCount = SMOKE_SENSOR_COUNT;
    private int pressureSensorCount = PRESSURE_SENSOR_COUNT;
    private int fireButtonCount = FIRE_BUTTON_COUNT;
    private int alarmButtonCount = ALARM_BUTTON_COUNT;
    private int crackDoorSensorCount = CRACK_DOOR_SENSOR_COUNT;
    private int glassBreakSensorCount = GLASS_BREAK_SENSOR_COUNT;
    private int slaveUuid = SLAVE_UUID;

    private ApplicationConfiguration() {}

    public int getMeasuringDeviceCount() {
        return MEASURING_DEVICE_COUNT;
    }

    public int getThermalSensorCount() {
        return thermalSensorCount;
    }

    public void setThermalSensorCount(int thermalSensorCount) {
        this.thermalSensorCount = thermalSensorCount;
    }

    public int getSmokeSensorCount() {
        return smokeSensorCount;
    }

    public void setSmokeSensorCount(int smokeSensorCount) {
        this.smokeSensorCount = smokeSensorCount;
    }

    public int getPressureSensorCount() {
        return pressureSensorCount;
    }

    public void setPressureSensorCount(int pressureSensorCount) {
        this.pressureSensorCount = pressureSensorCount;
    }

    public int getFireButtonCount() {
        return fireButtonCount;
    }

    public void setFireButtonCount(int fireButtonCount) {
        this.fireButtonCount = fireButtonCount;
    }

    public int getAlarmButtonCount() {
        return alarmButtonCount;
    }

    public void setAlarmButtonCount(int alarmButtonCount) {
        this.alarmButtonCount = alarmButtonCount;
    }

    public int getCrackDoorSensorCount() {
        return crackDoorSensorCount;
    }

    public void setCrackDoorSensorCount(int crackDoorSensorCount) {
        this.crackDoorSensorCount = crackDoorSensorCount;
    }

    public int getGlassBreakSensorCount() {
        return glassBreakSensorCount;
    }

    public void setGlassBreakSensorCount(int glassBreakSensorCount) {
        this.glassBreakSensorCount = glassBreakSensorCount;
    }

    public int getSlaveAdress() {
        return slaveAdress;
    }

    public void setSlaveAdress(int slaveAdress) {
        this.slaveAdress = slaveAdress;
    }

    public int getParity() {
        return parity;
    }

    public void setParity(int parity) {
        this.parity = parity;
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

    public String getStopBits() {
        return stopBits;
    }

    public void setStopBits(String stopBits) {
        this.stopBits = stopBits;
    }

    public int getSlaveUuid() {
        return slaveUuid;
    }

    public void setSlaveUuid(int slaveUuid) {
        this.slaveUuid = slaveUuid;
    }

    public static ApplicationConfiguration getInstance() {
        if (instance == null) {
            instance = new ApplicationConfiguration();
        }
        return instance;
    }
}
