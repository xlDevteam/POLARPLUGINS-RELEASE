package com.polar.fighter.config.enums;

import lombok.Getter;
import net.runelite.api.Prayer;

public enum OffensivePrayerTwo {

    BURST_OF_STRENGTH(Prayer.BURST_OF_STRENGTH),
    CLARITY_OF_THOUGHT(Prayer.CLARITY_OF_THOUGHT),
    SHARP_EYE(Prayer.SHARP_EYE),

    SUPERHUMAN_STRENGTH(Prayer.SUPERHUMAN_STRENGTH),
    IMPROVED_REFLEXES(Prayer.IMPROVED_REFLEXES),
    HAWK_EYE(Prayer.HAWK_EYE),


    ULTIMATE_STRENGTH(Prayer.ULTIMATE_STRENGTH),
    INCREDIBLE_REFLEXES(Prayer.INCREDIBLE_REFLEXES),
    EAGLE_EYE(Prayer.EAGLE_EYE),


    CHIVALRY(Prayer.CHIVALRY),
    PIETY(Prayer.PIETY),
    RIGOUR(Prayer.RIGOUR),

    NONE(null);

    @Getter
    final Prayer prayer;
    OffensivePrayerTwo(Prayer prayer) {
        this.prayer = prayer;
    }


}
