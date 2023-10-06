package com.polar.fighter.config.enums;

public enum StrengthBoost {

    STRENGTH_POTION("Strength potion("),
    SUPER_STRENGTH("Super strength("),
    DIVINE_STRENGTH("Divine super strength potion("),
    SUPER_COMBAT("Super combat potion("),
    DIVINE_COMBAT("Divine super combat potion("),


    NONE(null);

    final String getItemName;
    StrengthBoost(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}
