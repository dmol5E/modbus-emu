package org.reminder.edu.modbuscommon.entity.service;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.exception.SensorValueException;

public class BinaryStateStrategy extends AbstractSensorBehaviorStrategy {

    private final Function<Sensor, Boolean> stateGetter;
    private final BiConsumer<Sensor, Boolean> stateSetter;
    private final Supplier<Boolean> defaultGetter;
    private final String normalDisplay;
    private final String alarmDisplay;

    private BinaryStateStrategy(Builder builder) {
        super(builder.type);
        this.stateGetter = builder.stateGetter;
        this.stateSetter = builder.stateSetter;
        this.defaultGetter = builder.defaultGetter;
        this.normalDisplay = builder.normalDisplay;
        this.alarmDisplay = builder.alarmDisplay;
    }

    @Override
    public void validate(Object value) {
        if (!(value instanceof Boolean) && !(value instanceof Number)) {
            throw SensorValueException.invalidValue(value, "Boolean/Number");
        }
    }

    @Override
    public void updateState(Sensor sensor) {
        if (!sensor.isEnabled()) {
            return;
        }

        boolean isTriggered = stateGetter.apply(sensor);
        sensor.setState(isTriggered ? SensorState.ALARM : SensorState.NORMAL);
    }

    @Override
    public void resetToDefault(Sensor sensor) {
        sensor.setEnabled(true);
        sensor.setState(SensorState.NORMAL);
        stateSetter.accept(sensor, defaultGetter.get());
    }

    @Override
    public String getValueDisplay(Sensor sensor) {
        boolean isTriggered = stateGetter.apply(sensor);
        return isTriggered ? alarmDisplay : normalDisplay;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SensorType type;
        private Function<Sensor, Boolean> stateGetter;
        private BiConsumer<Sensor, Boolean> stateSetter;
        private Supplier<Boolean> defaultGetter;
        private String normalDisplay = "Норма";
        private String alarmDisplay = "Тревога";

        public Builder type(SensorType type) {
            this.type = type;
            return this;
        }

        public Builder stateGetter(Function<Sensor, Boolean> stateGetter) {
            this.stateGetter = stateGetter;
            return this;
        }

        public Builder stateSetter(BiConsumer<Sensor, Boolean> stateSetter) {
            this.stateSetter = stateSetter;
            return this;
        }

        public Builder defaultValue(Supplier<Boolean> defaultGetter) {
            this.defaultGetter = defaultGetter;
            return this;
        }

        public Builder normalDisplay(String normalDisplay) {
            this.normalDisplay = normalDisplay;
            return this;
        }

        public Builder alarmDisplay(String alarmDisplay) {
            this.alarmDisplay = alarmDisplay;
            return this;
        }

        public BinaryStateStrategy build() {
            return new BinaryStateStrategy(this);
        }
    }
}
