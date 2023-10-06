package com.polar.fighter.config.enums;

import lombok.Getter;
import net.runelite.api.Prayer;

public enum DefensivePrayer {

    PROTECT_MELEE(Prayer.PROTECT_FROM_MELEE),
    PROTECT_MISSILES(Prayer.PROTECT_FROM_MISSILES),
    PROTECT_MAGIC(Prayer.PROTECT_FROM_MAGIC),
    NONE(null);

    @Getter
    final Prayer prayer;

    DefensivePrayer(Prayer prayer) {
        this.prayer = prayer;
    }

}
