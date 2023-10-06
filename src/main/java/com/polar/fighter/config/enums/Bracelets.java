package com.polar.fighter.config.enums;

import net.runelite.api.ItemID;

public enum Bracelets {

    SLAUGHTER(ItemID.BRACELET_OF_SLAUGHTER),
    EXPEDITIOUS(ItemID.EXPEDITIOUS_BRACELET),
    NONE(-1);

    final int itemID;
    Bracelets(int itemID) {
        this.itemID = itemID;
    }
    public int getItemID() {
        return this.itemID;
    }

}