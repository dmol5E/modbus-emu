package org.reminder.edu.modbuscommon.entity.service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.listener.SensorUpdateListener;
import org.reminder.edu.modbuscommon.entity.repository.SensorRepository;
import org.reminder.edu.modbuscommon.entity.sensors.AlarmButton;
import org.reminder.edu.modbuscommon.entity.sensors.DoorCrackSensor;
import org.reminder.edu.modbuscommon.entity.sensors.FireButton;
import org.reminder.edu.modbuscommon.entity.sensors.GlassBreakSensor;
import org.reminder.edu.modbuscommon.entity.sensors.PressureSensor;
import org.reminder.edu.modbuscommon.entity.sensors.SmokeSensor;
import org.reminder.edu.modbuscommon.entity.sensors.ThermalSensor;

public class SensorBehaviorService {

    private final Map<SensorType, SensorBehaviorStrategy> strategies = new EnumMap<>(SensorType.class);
    private final Map<Sensor, List<SensorUpdateListener>> listeners = new java.util.HashMap<>();
    private final SensorRepository repository;

    public SensorBehaviorService(SensorRepository repository) {
        this.repository = repository;
        registerStrategies();
    }

    private void registerStrategies() {
        strategies.put(SensorType.THERMAL, NumericThresholdStrategy.builder()
            .type(SensorType.THERMAL)
            .alarmHigh(SensorConfig.Thermal::getAlarmThreshold)
            .faultHigh(SensorConfig.Thermal::getFaultThreshold)
            .valueGetter(s -> ((ThermalSensor) s).getTemperature())
            .valueSetter((s, v) -> ((ThermalSensor) s).setTemperature(v.doubleValue()))
            .defaultValue(SensorConfig.Thermal::getDefaultValue)
            .displayFormatter(v -> String.format("%.1f°C", v.doubleValue()))
            .build());

        strategies.put(SensorType.SMOKE, NumericThresholdStrategy.builder()
            .type(SensorType.SMOKE)
            .alarmHigh(() -> (double) SensorConfig.Smoke.getAlarmThreshold())
            .faultHigh(() -> (double) SensorConfig.Smoke.getFaultThreshold())
            .valueGetter(s -> ((SmokeSensor) s).getSmokeLevel())
            .valueSetter((s, v) -> ((SmokeSensor) s).setSmokeLevel(v.intValue()))
            .defaultValue(() -> (double) SensorConfig.Smoke.getDefaultValue())
            .displayFormatter(v -> String.format("%d%%", v.intValue()))
            .build());

        strategies.put(SensorType.PRESSURE, NumericThresholdStrategy.builder()
            .type(SensorType.PRESSURE)
            .alarmHigh(SensorConfig.Pressure::getAlarmThresholdHigh)
            .faultHigh(SensorConfig.Pressure::getFaultThresholdHigh)
            .alarmLow(SensorConfig.Pressure::getAlarmThresholdLow)
            .faultLow(SensorConfig.Pressure::getFaultThresholdLow)
            .valueGetter(s -> ((PressureSensor) s).getPressure())
            .valueSetter((s, v) -> ((PressureSensor) s).setPressure(v.doubleValue()))
            .defaultValue(SensorConfig.Pressure::getDefaultValue)
            .displayFormatter(v -> String.format("%.2f hPa", v.doubleValue()))
            .build());

        strategies.put(SensorType.DOOR_CRACK, BinaryStateStrategy.builder()
            .type(SensorType.DOOR_CRACK)
            .stateGetter(s -> ((DoorCrackSensor) s).isCracked())
            .stateSetter((s, v) -> ((DoorCrackSensor) s).setCracked(v))
            .defaultValue(() -> SensorConfig.DoorCrack.getDefaultValue())
            .normalDisplay("Закрыто")
            .alarmDisplay("Открыто")
            .build());

        strategies.put(SensorType.GLASS_BREAK, BinaryStateStrategy.builder()
            .type(SensorType.GLASS_BREAK)
            .stateGetter(s -> ((GlassBreakSensor) s).isBroken())
            .stateSetter((s, v) -> ((GlassBreakSensor) s).setBroken(v))
            .defaultValue(() -> SensorConfig.GlassBreak.getDefaultValue())
            .normalDisplay("Целое")
            .alarmDisplay("Разбито")
            .build());

        strategies.put(SensorType.FIRE_BUTTON, BinaryStateStrategy.builder()
            .type(SensorType.FIRE_BUTTON)
            .stateGetter(s -> ((FireButton) s).isPressed())
            .stateSetter((s, v) -> ((FireButton) s).setPressed(v))
            .defaultValue(() -> SensorConfig.FireButton.getDefaultValue())
            .build());

        strategies.put(SensorType.ALARM_BUTTON, BinaryStateStrategy.builder()
            .type(SensorType.ALARM_BUTTON)
            .stateGetter(s -> ((AlarmButton) s).isPressed())
            .stateSetter((s, v) -> ((AlarmButton) s).setPressed(v))
            .defaultValue(() -> SensorConfig.AlarmButton.getDefaultValue())
            .build());
    }

    public void setValue(Sensor sensor, Object value) {
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        strategy.validate(value);
        Object oldValue = sensor.getValue();
        sensor.setValue(value);
        strategy.updateState(sensor);
        notifyValueChanged(sensor, oldValue, value);
    }

    public void updateState(Sensor sensor) {
        if (!sensor.isEnabled()) {
            return;
        }
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        SensorState oldState = sensor.getState();
        strategy.updateState(sensor);
        if (oldState != sensor.getState()) {
            notifyStateChanged(sensor, oldState, sensor.getState());
        }
    }

    public void resetToDefault(Sensor sensor) {
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        SensorState oldState = sensor.getState();
        boolean oldEnabled = sensor.isEnabled();
        strategy.resetToDefault(sensor);
        if (oldState != sensor.getState()) {
            notifyStateChanged(sensor, oldState, sensor.getState());
        }
        if (oldEnabled != sensor.isEnabled()) {
            notifyEnabledChanged(sensor, oldEnabled, sensor.isEnabled());
        }
    }

    public void setEnabled(Sensor sensor, boolean enabled) {
        boolean oldEnabled = sensor.isEnabled();
        if (oldEnabled != enabled) {
            sensor.setEnabled(enabled);
            notifyEnabledChanged(sensor, oldEnabled, enabled);
        }
    }

    public String getValueDisplay(Sensor sensor) {
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        return strategy.getValueDisplay(sensor);
    }

    public List<Sensor> getAllSensors() {
        return repository.getAllSensors();
    }

    public Optional<Sensor> getSensorByAddress(int address) {
        return repository.getSensor(address);
    }

    public void subscribe(Sensor sensor, SensorUpdateListener listener) {
        if (!listeners.containsKey(sensor)) {
            listeners.put(sensor, new ArrayList<>());
        }
        List<SensorUpdateListener> sensorListeners = listeners.get(sensor);
        if (!sensorListeners.contains(listener)) {
            sensorListeners.add(listener);
        }
    }

    public void unsubscribe(Sensor sensor, SensorUpdateListener listener) {
        List<SensorUpdateListener> sensorListeners = listeners.get(sensor);
        if (sensorListeners != null) {
            sensorListeners.remove(listener);
            if (sensorListeners.isEmpty()) {
                listeners.remove(sensor);
            }
        }
    }

    private void notifyValueChanged(Sensor sensor, Object oldValue, Object newValue) {
        List<SensorUpdateListener> sensorListeners = listeners.get(sensor);
        if (sensorListeners != null) {
            for (SensorUpdateListener listener : sensorListeners) {
                listener.onValueChanged(sensor, newValue);
            }
        }
    }

    private void notifyStateChanged(Sensor sensor, SensorState oldState, SensorState newState) {
        List<SensorUpdateListener> sensorListeners = listeners.get(sensor);
        if (sensorListeners != null) {
            for (SensorUpdateListener listener : sensorListeners) {
                listener.onStateChanged(sensor, newState);
            }
        }
    }

    private void notifyEnabledChanged(Sensor sensor, boolean oldEnabled, boolean newEnabled) {
        List<SensorUpdateListener> sensorListeners = listeners.get(sensor);
        if (sensorListeners != null) {
            for (SensorUpdateListener listener : sensorListeners) {
                listener.onEnabledChanged(sensor, newEnabled);
            }
        }
    }

    private SensorBehaviorStrategy getStrategy(SensorType type) {
        return Optional.ofNullable(strategies.get(type))
            .orElseThrow(() -> new IllegalStateException(
                "No strategy registered for sensor type: " + type));
    }
}
