package org.reminder.edu.modbuscommon.entity.enums;

public enum SensorState {
    NORMAL("НОРМА"),
    ALARM("ТРЕВОГА"),
    FAULT("НЕИСПРАВНОСТЬ");

    private final String displayName;

    SensorState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
