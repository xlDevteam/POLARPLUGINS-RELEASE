package com.polar.fighter.config.enums;

public enum SlayerMonster {

    LIZARDS("Ice cooler"),

    NONE(null);

    final String getItemName;
    SlayerMonster(String getItemName) {
        this.getItemName = getItemName;
    }

    public String getItemName() {
        return this.getItemName;
    }

}
