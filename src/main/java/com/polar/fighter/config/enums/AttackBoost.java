package com.polar.fighter.config.enums;

public enum AttackBoost {

    ATTACK_POTION("Attack potion("),
    SUPER_ATTACK("Super attack("),
    DIVINE_ATTACK("Divine super attack potion("),


    NONE(null);

    final String getItemName;
    AttackBoost(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}
