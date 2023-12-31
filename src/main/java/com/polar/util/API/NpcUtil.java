package com.polar.util.API;

import com.ethan.EthanApiPlugin.Collections.NPCs;
import com.ethan.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.api.NPC;

import java.util.Optional;

public class NpcUtil {

    public static Optional<NPC> getNpc(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return NPCs.search().withName(name).nearestToPlayer();
        } else {
            return nameContainsNoCase(name).nearestToPlayer();
        }
    }

    public static NPCQuery nameContainsNoCase(String name) {
        return NPCs.search().filter(npcs -> npcs.getName() != null && npcs.getName().toLowerCase().contains(name.toLowerCase()));
    }

}
