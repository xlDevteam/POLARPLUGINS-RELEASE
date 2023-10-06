package com.polar.fighter.state;


import com.ethan.EthanApiPlugin.Collections.Inventory;
import com.ethan.EthanApiPlugin.Collections.NPCs;
import com.ethan.EthanApiPlugin.Collections.TileItems;
import com.ethan.InteractionApi.InventoryInteraction;
import com.ethan.InteractionApi.NPCInteraction;
import com.ethan.InteractionApi.TileObjectInteraction;
import com.ethan.Packets.TileItemPackets;
import com.polar.fighter.config.enums.*;
import com.polar.fighter.PolarFighter;
import com.polar.fighter.config.PolarFighterConfig;
import com.polar.util.API.InventoryUtil;
import com.polar.util.API.ObjectUtil;
import com.polar.util.PolarUtilsPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class StateHandler {

    public boolean teleOut = false;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private PolarFighter plugin;
    @Inject
    private PolarFighterConfig config;
    private State previousState = null;
    private NPC npcToInteract;


    public State getNextState() {

        if (plugin.getCurrentHitpoints() > 0.15 * plugin.getMaxHitpoints()) {
            return State.ATTACK;
        }

        return State.IDLE;

    }

    public void handleState() {

        if (previousState != plugin.getState()) {
            log.info("Current State: {}", plugin.getState().name());
            previousState = plugin.getState();
        }

        if(plugin.wait > 0) {
            log.info(String.valueOf(plugin.wait));
            plugin.wait--;
            return;
        }

        switch (plugin.getState()) {
            case ATTACK:
                attackEnemys();
                break;
            case LOOT:
                //lootItems();
                break;
        }
    }

    private void attackEnemys() {

        eatFood();

        if (isCannonBroken()) {
            fixCannon();
        } else if (plugin.nextReload >= plugin.cballsLeft) {
            reloadCannon();
        }

        if (PolarUtilsPlugin.canInteract()) {
                    WorldPoint targetTile = plugin.getSafeSpotTile() != null ? plugin.getSafeSpotTile() : plugin.getRadiusTile();
                    if (targetTile != null) {
                        NPCs.search().withName(config.monsterName()).nearestToPoint(targetTile).ifPresent(npc -> npcToInteract = npc);
                    } else {
                        NPCs.search().withName(config.monsterName()).nearestToPlayer().ifPresent(npc -> npcToInteract = npc);
                    }
                    if(shouldInteractWithNPC()) {
                        NPCInteraction.interact(npcToInteract, "Attack");
                    }

            if (shouldMoveToSafeSpot()) {
                PolarUtilsPlugin.move(plugin.getSafeSpotTile());
                plugin.wait = 2;
            }

        }
    }

    private boolean shouldMoveToSafeSpot() {
        return plugin.getSafeSpotTile() != null && !PolarUtilsPlugin.playerPosition().equals(plugin.getSafeSpotTile()) && !PolarUtilsPlugin.isMoving();
    }

    private boolean shouldInteractWithNPC() {
        if (plugin.getRadiusTile() != null && npcToInteract.getWorldLocation().distanceTo(plugin.getRadiusTile()) <= config.searchRadius()
                && npcToInteract.getWorldArea().hasLineOfSightTo(client, PolarUtilsPlugin.playerPosition().toWorldArea())) {
            return true;
        } else {
            return npcToInteract != null && !npcToInteract.isInteracting()
                    && npcToInteract.getWorldArea().hasLineOfSightTo(client, PolarUtilsPlugin.playerPosition().toWorldArea());
        }
    }

    private void reloadCannon() {
        if (PolarUtilsPlugin.isMoving()) {
            return;
        }

        Optional<Widget> cannonball = InventoryUtil.nameContainsNoCase("cannonball").first();

        if (cannonball.isPresent()) {
            Optional<TileObject> to = ObjectUtil.nameContainsNoCase("dwarf multicannon").nearestToPlayer();
            if (to.isPresent()) {
                TileObjectInteraction.interact(to.get(), "Fire", "fire");
                plugin.nextReload = ThreadLocalRandom.current().nextInt(5, 15 + 1);
                plugin.wait = 2;
            }
        }
    }

    private boolean isCannonBroken() {
        Optional<TileObject> broken = ObjectUtil.nameContainsNoCase("broken multicannon").nearestToPlayer();

        if (broken.isPresent()) {
            return true;
        }

        return false;
    }
    private void fixCannon() {
        if (PolarUtilsPlugin.isMoving()) {
            return;
        }

        Optional<TileObject> broken = ObjectUtil.nameContainsNoCase("broken multicannon").nearestToPlayer();
        if (broken.isPresent()) {
            TileObjectInteraction.interact(broken.get(), "Repair", "repair");
            plugin.wait = 2;
        }
    }

    private void eatFood() {

        double healthPercentage = (double) plugin.getCurrentHitpoints() / plugin.getMaxHitpoints();
        double minHealthPercent = config.minHealthPercent() / 100.0;
        double prayerPercentage = (double) plugin.getCurrentPrayer() / plugin.getMaxPrayer();
        double minPrayerPercent = config.minPrayerPercent() / 100.0;

        if (client.getVarpValue(VarPlayer.POISON) > 0) {
            InventoryInteraction.useItem(config.antiVenom().getName(), "Drink");
        }

        if (client.getVarbitValue(Varbits.ANTIFIRE) == 0 && client.getVarbitValue(Varbits.SUPER_ANTIFIRE) == 0) {
            InventoryInteraction.useItem(config.antiFire().getName(), "Drink");
        }

        if (healthPercentage < minHealthPercent && config.foodType() != Food.NONE) {
            clientThread.invoke(() -> {
                Inventory.search().withAction("Eat").filter(item -> !item.getName().contains("Burnt") || !item.getName().contains("Raw")).first().ifPresent(item -> {
                    InventoryInteraction.useItem(item, "Eat");
                });
            });
        }

        if (prayerPercentage < minPrayerPercent && !config.oneTickFlick()) { // 55% threshold
            clientThread.invoke(() -> {
                Inventory.search().withAction("Drink").filter(item -> item.getName().contains("restore") || item.getName().contains("prayer")).first().ifPresent(item -> {
                    InventoryInteraction.useItem(item, "Drink");
                });
            });
        }

        if (config.useBoosts()) {

            int attackThreshold = plugin.getMaxAttack() + config.minBoostIncreaseATK(); // Adjust this threshold
            int strengthThreshold = plugin.getMaxStrength() + config.minBoostIncreaseStrength(); // Adjust this threshold
            int defenseThreshold = plugin.getMaxDefense() + config.minBoostIncreaseDEF(); // Adjust this threshold
            int rangedThreshold = plugin.getMaxRanged() + config.minBoostIncreaseRange(); // Adjust this threshold
            int magicThreshold = plugin.getMaxMagic() + config.minBoostIncreaseMagic(); // Adjust this threshold


            if (plugin.getCurrentAttack() < attackThreshold && config.attackBoost() != AttackBoost.NONE) {
                clientThread.invoke(() -> {
                    Inventory.search().withAction("Drink").filter(item -> item.getName().contains(config.attackBoost().getItemName())).first().ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                    });
                });
            }

            if (plugin.getCurrentStrength() < strengthThreshold && config.strengthBoost() != StrengthBoost.NONE) {
                clientThread.invoke(() -> {
                    Inventory.search().withAction("Drink").filter(item -> item.getName().contains(config.strengthBoost().getItemName())).first().ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                    });
                });
            }

            if (plugin.getCurrentDefense() < defenseThreshold && config.defenceBoost() != DefencePotions.NONE) {
                clientThread.invoke(() -> {
                    Inventory.search().withAction("Drink").filter(item -> item.getName().contains(config.defenceBoost().getItemName())).first().ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                    });
                });
            }

            if (plugin.getCurrentRanged() < rangedThreshold && config.rangedBoost() != RangedPotions.NONE) {
                clientThread.invoke(() -> {
                    Inventory.search().withAction("Drink").filter(item -> item.getName().contains(config.rangedBoost().getItemName())).first().ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                    });
                });
            }

            if (plugin.getCurrentMagic() < magicThreshold && config.magicBoost() != MagicBoost.NONE) {
                switch (config.magicBoost()) {
                    case IMBUED_HEART:
                    case SATURATED_HEART:
                        clientThread.invoke(() -> {
                            Inventory.search().withAction("Invigorate").filter(item -> item.getName().contains(config.rangedBoost().getItemName())).first().ifPresent(item -> {
                                InventoryInteraction.useItem(item, "Invigorate");
                            });
                        });
                        break;
                    default:
                        clientThread.invoke(() -> {
                            Inventory.search().withAction("Drink").filter(item -> item.getName().contains(config.rangedBoost().getItemName())).first().ifPresent(item -> {
                                InventoryInteraction.useItem(item, "Drink");
                            });
                        });
                        break;
                }
            }

        }

    }

}
