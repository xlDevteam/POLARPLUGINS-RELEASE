package com.polar.fighter.config.enums;

public enum RangedPotions {

    RANGING_POTION("Ranging potion("),
    DIVINE_RANGING("Divine ranging potion("),

    NONE(null);

    final String getItemName;
    RangedPotions(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}
