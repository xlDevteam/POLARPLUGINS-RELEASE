package com.polar.fighter.config.enums;

public enum Food {

    ANCHOVY("Anchovy"),
    SHRIMPS("Shrimps"),
    SARDINE("Sardine"),
    COOKED_CHICKEN("Cooked Chicken"),
    HERRING("Herring"),
    MACKEREL("Mackerel"),
    TROUT("Trout"),
    COD("Cod"),
    PIKE("Pike"),
    SALMON("Salmon"),
    TUNA("Tuna"),
    LOBSTER("Lobster"),
    BASS("Bass"),
    SWORDFISH("Swordfish"),
    MONKFISH("Monkfish"),

    SHARK("Shark"),
    SEA_TURTLE("Sea Turtle"),
    MANTA_RAY("Manta Ray"),
    DARK_CRAB("Dark Crab"),
    KARAMBWAN("Karambwan"),
    ANGLERFISH("Anglerfish"),
    CHILLI_POTATO("Chilli Potato"),
    EGG_POTATO("Egg potato"),
    MUSHROOM_POTATO("Mushroom Potato"),
    CHEESE_POTATO("Potato with cheese"),
    TUNA_POTATO("Tuna Potato"),
    NONE(null);

    final String getItemName;

    Food(String getItemName) {
        this.getItemName = getItemName;
    }

    public String getItemName() {
        return this.getItemName;
    }
}
