package org.reminder.edu.modbusslave.entity.enums;

public enum SensorType {
    THERMAL("Тепловой датчик", "ТД"),
    SMOKE("Дымовой датчик", "ДД"),
    PRESSURE("Датчик давления", "ДДав"),
    FIRE_BUTTON("Пожарная кнопка", "ПК"),
    ALARM_BUTTON("Тревожная кнопка", "ТК"),
    DOOR_CRACK("Датчик взлома двери", "ДВ"),
    GLASS_BREAK("Датчик разбития стекла", "ДС");

    private final String displayName;
    private final String shortName;

    SensorType(String displayName, String shortName) {
        this.displayName = displayName;
        this.shortName = shortName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean hasNumericValue() {
        return this == THERMAL || this == SMOKE || this == PRESSURE;
    }

    public boolean hasBooleanState() {
        return this == DOOR_CRACK || this == GLASS_BREAK || this == FIRE_BUTTON || this == ALARM_BUTTON;
    }
}
