package com.polar.fighter.config.enums;

public enum MagicBoost {

    MAGIC_POTION("Magic potion("),
    BATTLEMAGE("Battlemage potion("),
    DIVINE_BATTLEMAGE("Divine battlemage potion("),
    IMBUED_HEART("Imbued heart"),
    SATURATED_HEART("Saturated heart"),

    NONE(null);

    final String getItemName;
    MagicBoost(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}
