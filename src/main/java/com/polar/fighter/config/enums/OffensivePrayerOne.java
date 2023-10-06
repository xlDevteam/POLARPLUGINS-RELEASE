package com.polar.fighter.config.enums;

import lombok.Getter;
import net.runelite.api.Prayer;

public enum OffensivePrayerOne {

    BURST_OF_STRENGTH(Prayer.BURST_OF_STRENGTH),
    CLARITY_OF_THOUGHT(Prayer.CLARITY_OF_THOUGHT),
    SHARP_EYE(Prayer.SHARP_EYE),
    MYSTIC_WILL(Prayer.MYSTIC_WILL),

    SUPERHUMAN_STRENGTH(Prayer.SUPERHUMAN_STRENGTH),
    IMPROVED_REFLEXES(Prayer.IMPROVED_REFLEXES),
    HAWK_EYE(Prayer.HAWK_EYE),
    MYSTIC_LORE(Prayer.MYSTIC_LORE),


    ULTIMATE_STRENGTH(Prayer.ULTIMATE_STRENGTH),
    INCREDIBLE_REFLEXES(Prayer.INCREDIBLE_REFLEXES),
    EAGLE_EYE(Prayer.EAGLE_EYE),
    MYSTIC_MIGHT(Prayer.MYSTIC_MIGHT),


    CHIVALRY(Prayer.CHIVALRY),
    PIETY(Prayer.PIETY),
    RIGOUR(Prayer.RIGOUR),
    AUGURY(Prayer.AUGURY),

    NONE(null);

    @Getter
    final Prayer prayer;
    OffensivePrayerOne(Prayer prayer) {
        this.prayer = prayer;
    }


}
