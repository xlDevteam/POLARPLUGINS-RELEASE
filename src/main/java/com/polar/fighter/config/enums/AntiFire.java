package com.polar.fighter.config.enums;

public enum AntiFire {
    EXTENDED_SUPER_ANTIFIRE("Extended super antifire"),
    SUPER_ANTIFIRE("Super antifire potion"),
    EXTENDED_ANTIFIRE("Extended antifire"),
    ANTIFIRE("Antifire potion"), NONE(null);

    private final String name;

    AntiFire(String antifireName) {
        this.name = antifireName;
    }

    public String getName() {
        return name;
    }
}

