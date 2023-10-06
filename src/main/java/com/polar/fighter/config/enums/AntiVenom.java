package com.polar.fighter.config.enums;

public enum AntiVenom {
    ANTIVENOM("Anti-venom"),
    ANTIVENOM_PLUS("Anti-venom+"),
    ANTIDOTE_PP("Antidote++"),
    ANTIPOISON("Antipoison"), NONE(null);


    private final String name;

    AntiVenom(String antivenomName) {
        this.name = antivenomName;
    }

    public String getName() {
        return name;
    }
}
