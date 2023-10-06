package com.polar.fighter.config.enums;

public enum DefencePotions {

    DEFENCE_POTION("Defence potion("),
    SUPER_DEFENCE("Super defence("),

    DIVINE_DEFENCE("Divine super defence potion("),
    NONE(null);

    final String getItemName;
    DefencePotions(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}
