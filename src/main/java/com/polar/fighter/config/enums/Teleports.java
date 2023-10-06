package com.polar.fighter.config.enums;

public enum Teleports {

    TELETAB("teleport"),
    AMULET_OF_GLORY("Amulet of glory("),
    RING_OF_DUELING("Ring of dueling("),
    GAMES_NECKLACE("Games necklace("),
    COMBAT_BRACELET("Combat bracelet("),
    SKILLS_NECKLACE("Skills necklace("),
    RING_OF_WEALTH("Ring of wealth("),
    SLAYER_RING("Slayer ring("),
    ROYAL_SEED_POD("Royal seed pod"),
    NECKLACE_OF_PASSAGE("Necklace of passage("),
    XERICS_TALISMAN("Xeric's talisman"),
    ECTOPHIAL("Ectophial"),
    DIGSITE_PENDANT("Digsite pendant"),

    NONE(null);

    final String getItemName;
    Teleports(String getItemName) {
        this.getItemName = getItemName;
    }
    public String getItemName() {
        return this.getItemName;
    }

}