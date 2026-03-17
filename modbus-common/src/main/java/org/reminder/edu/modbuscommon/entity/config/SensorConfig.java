package org.reminder.edu.modbuscommon.entity.config;

public final class SensorConfig {

    private SensorConfig() {
    }

    public static final class Thermal {
        private static final double DEFAULT_VALUE = 20.0;
        private static final double ALARM_THRESHOLD = 100.0;
        private static final double FAULT_THRESHOLD = 150.0;
        private static final double MIN_VALUE = -50.0;
        private static final double MAX_VALUE = 200.0;

        public static double getDefaultValue() { return DEFAULT_VALUE; }
        public static double getAlarmThreshold() { return ALARM_THRESHOLD; }
        public static double getFaultThreshold() { return FAULT_THRESHOLD; }
        public static double getMinValue() { return MIN_VALUE; }
        public static double getMaxValue() { return MAX_VALUE; }
    }

    public static final class Smoke {
        private static final int DEFAULT_VALUE = 0;
        private static final int ALARM_THRESHOLD = 30;
        private static final int FAULT_THRESHOLD = 90;
        private static final int MIN_VALUE = 0;
        private static final int MAX_VALUE = 100;

        public static int getDefaultValue() { return DEFAULT_VALUE; }
        public static int getAlarmThreshold() { return ALARM_THRESHOLD; }
        public static int getFaultThreshold() { return FAULT_THRESHOLD; }
        public static int getMinValue() { return MIN_VALUE; }
        public static int getMaxValue() { return MAX_VALUE; }
    }

    public static final class Pressure {
        private static final double DEFAULT_VALUE = 1013.25;
        private static final double ALARM_THRESHOLD_HIGH = 1050.0;
        private static final double ALARM_THRESHOLD_LOW = 900.0;
        private static final double FAULT_THRESHOLD_HIGH = 1100.0;
        private static final double FAULT_THRESHOLD_LOW = 800.0;
        private static final double MIN_VALUE = 0.0;
        private static final double MAX_VALUE = 1500.0;

        public static double getDefaultValue() { return DEFAULT_VALUE; }
        public static double getAlarmThresholdHigh() { return ALARM_THRESHOLD_HIGH; }
        public static double getAlarmThresholdLow() { return ALARM_THRESHOLD_LOW; }
        public static double getFaultThresholdHigh() { return FAULT_THRESHOLD_HIGH; }
        public static double getFaultThresholdLow() { return FAULT_THRESHOLD_LOW; }
        public static double getMinValue() { return MIN_VALUE; }
        public static double getMaxValue() { return MAX_VALUE; }
    }

    public static final class DoorCrack {
        private static final boolean DEFAULT_VALUE = false;

        public static boolean getDefaultValue() { return DEFAULT_VALUE; }
    }

    public static final class GlassBreak {
        private static final boolean DEFAULT_VALUE = false;

        public static boolean getDefaultValue() { return DEFAULT_VALUE; }
    }

    public static final class FireButton {
        private static final boolean DEFAULT_VALUE = false;

        public static boolean getDefaultValue() { return DEFAULT_VALUE; }
    }

    public static final class AlarmButton {
        private static final boolean DEFAULT_VALUE = false;

        public static boolean getDefaultValue() { return DEFAULT_VALUE; }
    }
}
