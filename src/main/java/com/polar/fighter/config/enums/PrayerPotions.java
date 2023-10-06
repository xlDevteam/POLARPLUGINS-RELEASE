package com.polar.fighter.config.enums;

public enum PrayerPotions {

    PRAYER_POTION("Prayer potion("),
    SUPER_RESTORE("Super restore("),

    NONE(null);

    final String getItemName;
    PrayerPotions(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}
