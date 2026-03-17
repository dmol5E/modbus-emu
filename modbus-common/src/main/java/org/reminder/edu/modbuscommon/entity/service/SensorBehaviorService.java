package org.reminder.edu.modbuscommon.entity.service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.config.SensorConfig;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.sensors.AlarmButton;
import org.reminder.edu.modbuscommon.entity.sensors.DoorCrackSensor;
import org.reminder.edu.modbuscommon.entity.sensors.FireButton;
import org.reminder.edu.modbuscommon.entity.sensors.GlassBreakSensor;
import org.reminder.edu.modbuscommon.entity.sensors.PressureSensor;
import org.reminder.edu.modbuscommon.entity.sensors.SmokeSensor;
import org.reminder.edu.modbuscommon.entity.sensors.ThermalSensor;

public class SensorBehaviorService {

    private final Map<SensorType, SensorBehaviorStrategy> strategies = new EnumMap<>(SensorType.class);

    public SensorBehaviorService() {
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
        sensor.setValue(value);
        strategy.updateState(sensor);
    }

    public void updateState(Sensor sensor) {
        if (!sensor.isEnabled()) {
            return;
        }
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        strategy.updateState(sensor);
    }

    public void resetToDefault(Sensor sensor) {
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        strategy.resetToDefault(sensor);
    }

    public String getValueDisplay(Sensor sensor) {
        SensorBehaviorStrategy strategy = getStrategy(sensor.getType());
        return strategy.getValueDisplay(sensor);
    }

    private SensorBehaviorStrategy getStrategy(SensorType type) {
        return Optional.ofNullable(strategies.get(type))
            .orElseThrow(() -> new IllegalStateException(
                "No strategy registered for sensor type: " + type));
    }
}
