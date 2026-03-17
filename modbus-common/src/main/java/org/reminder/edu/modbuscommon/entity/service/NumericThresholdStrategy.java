package org.reminder.edu.modbuscommon.entity.service;

import java.util.function.BiConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

import org.reminder.edu.modbuscommon.entity.Sensor;
import org.reminder.edu.modbuscommon.entity.enums.SensorState;
import org.reminder.edu.modbuscommon.entity.enums.SensorType;
import org.reminder.edu.modbuscommon.entity.exception.SensorValueException;

public class NumericThresholdStrategy extends AbstractSensorBehaviorStrategy {

    private final DoubleSupplier alarmHighGetter;
    private final DoubleSupplier faultHighGetter;
    private final DoubleSupplier alarmLowGetter;
    private final DoubleSupplier faultLowGetter;
    private final Function<Sensor, Number> valueGetter;
    private final BiConsumer<Sensor, Number> valueSetter;
    private final DoubleSupplier defaultGetter;
    private final Function<Number, String> displayFormatter;
    private final boolean hasLowThresholds;

    private NumericThresholdStrategy(Builder builder) {
        super(builder.type);
        this.alarmHighGetter = builder.alarmHighGetter;
        this.faultHighGetter = builder.faultHighGetter;
        this.alarmLowGetter = builder.alarmLowGetter;
        this.faultLowGetter = builder.faultLowGetter;
        this.valueGetter = builder.valueGetter;
        this.valueSetter = builder.valueSetter;
        this.defaultGetter = builder.defaultGetter;
        this.displayFormatter = builder.displayFormatter;
        this.hasLowThresholds = builder.alarmLowGetter != null && builder.faultLowGetter != null;
    }

    @Override
    public void validate(Object value) {
        if (!(value instanceof Number)) {
            throw SensorValueException.invalidValue(value, "Number");
        }
    }

    @Override
    public void updateState(Sensor sensor) {
        if (!sensor.isEnabled()) {
            return;
        }

        double value = valueGetter.apply(sensor).doubleValue();
        double faultHigh = faultHighGetter.getAsDouble();
        double alarmHigh = alarmHighGetter.getAsDouble();

        if (hasLowThresholds) {
            double faultLow = faultLowGetter.getAsDouble();
            double alarmLow = alarmLowGetter.getAsDouble();

            if (value >= faultHigh || value <= faultLow) {
                sensor.setState(SensorState.FAULT);
            } else if (value >= alarmHigh || value <= alarmLow) {
                sensor.setState(SensorState.ALARM);
            } else {
                sensor.setState(SensorState.NORMAL);
            }
        } else {
            if (value >= faultHigh) {
                sensor.setState(SensorState.FAULT);
            } else if (value >= alarmHigh) {
                sensor.setState(SensorState.ALARM);
            } else {
                sensor.setState(SensorState.NORMAL);
            }
        }
    }

    @Override
    public void resetToDefault(Sensor sensor) {
        sensor.setEnabled(true);
        sensor.setState(SensorState.NORMAL);
        valueSetter.accept(sensor, defaultGetter.getAsDouble());
    }

    @Override
    public String getValueDisplay(Sensor sensor) {
        Number value = valueGetter.apply(sensor);
        return displayFormatter.apply(value);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SensorType type;
        private DoubleSupplier alarmHighGetter;
        private DoubleSupplier faultHighGetter;
        private DoubleSupplier alarmLowGetter;
        private DoubleSupplier faultLowGetter;
        private Function<Sensor, Number> valueGetter;
        private BiConsumer<Sensor, Number> valueSetter;
        private DoubleSupplier defaultGetter;
        private Function<Number, String> displayFormatter;

        public Builder type(SensorType type) {
            this.type = type;
            return this;
        }

        public Builder alarmHigh(DoubleSupplier alarmHighGetter) {
            this.alarmHighGetter = alarmHighGetter;
            return this;
        }

        public Builder faultHigh(DoubleSupplier faultHighGetter) {
            this.faultHighGetter = faultHighGetter;
            return this;
        }

        public Builder alarmLow(DoubleSupplier alarmLowGetter) {
            this.alarmLowGetter = alarmLowGetter;
            return this;
        }

        public Builder faultLow(DoubleSupplier faultLowGetter) {
            this.faultLowGetter = faultLowGetter;
            return this;
        }

        public Builder valueGetter(Function<Sensor, Number> valueGetter) {
            this.valueGetter = valueGetter;
            return this;
        }

        public Builder valueSetter(BiConsumer<Sensor, Number> valueSetter) {
            this.valueSetter = valueSetter;
            return this;
        }

        public Builder defaultValue(DoubleSupplier defaultGetter) {
            this.defaultGetter = defaultGetter;
            return this;
        }

        public Builder displayFormatter(Function<Number, String> displayFormatter) {
            this.displayFormatter = displayFormatter;
            return this;
        }

        public NumericThresholdStrategy build() {
            return new NumericThresholdStrategy(this);
        }
    }
}
