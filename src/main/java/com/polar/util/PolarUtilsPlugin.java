package com.polar.util;

import com.ethan.EthanApiPlugin.Collections.*;
import com.ethan.EthanApiPlugin.Collections.query.QuickPrayer;
import com.ethan.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.ethan.EthanApiPlugin.EthanApiPlugin;
import com.ethan.InteractionApi.BankInteraction;
import com.ethan.InteractionApi.TileObjectInteraction;
import com.ethan.Packets.MousePackets;
import com.ethan.Packets.MovementPackets;
import com.ethan.Packets.TileItemPackets;
import com.ethan.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.polar.util.API.BankUtil;
import com.polar.util.API.PrayerUtil;
import com.polar.util.WalkerUtils.WalkerEngine;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientUI;

import java.util.Arrays;
import java.util.Optional;

import static net.runelite.api.Varbits.QUICK_PRAYER;

@Slf4j
@PluginDescriptor(name = "<html><font color=\"#0062ff\">[POL]</font> Utils</html>",
        description = "",
        tags = {"polar", ""})

public class PolarUtilsPlugin extends Plugin {

    @Inject
    private static WalkerEngine walkerEngine;

    static ClientUI clientUI = RuneLite.getInjector().getInstance(ClientUI.class);
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static ClientThread clientThread = RuneLite.getInjector().getInstance(ClientThread.class);
    static PluginManager pluginManager = RuneLite.getInjector().getInstance(PluginManager.class);
    static ItemManager itemManager = RuneLite.getInjector().getInstance(ItemManager.class);

    @Override
    protected void startUp() throws Exception {
        walkerEngine = new WalkerEngine();
        log.info("");
    }

    /* ********************************* BEGIN API METHODS ********************************** */

    /**
     * Checks if the player is logged in.
     *
     * @return true if the player is logged in, false otherwise.
     */
    public static boolean notLoggedIn() {
        return client.getGameState() != GameState.LOGGED_IN;
    }

    /**
     * Retrieves the current RuneLite client instance.
     *
     * @return The RuneLite client instance.
     */
    public static Client getClient() {
        return client;
    }

    /**
     * Retrieves the current RuneLite client user interface instance.
     *
     * @return The RuneLite client user interface instance.
     */
    public static ClientUI getClientUI() {
        return clientUI;
    }

    /**
     * Sends a message to the RuneScape game chatbox.
     *
     * @param message The message to send.
     */
    public static void sendClientMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }

    /**
     * Checks whether the player is currently able to interact with the game,
     * taking into account whether the player is moving or animating.
     *
     * @return true if the player can interact, false otherwise.
     */
    public static boolean canInteract() {
        return !isMoving() && !isAnimating();
    }

    /**
     * Checks if the player character is currently in motion (walking or running).
     *
     * @return true if the player is moving, false otherwise.
     */
    public static boolean isMoving() {
        return (client.getLocalPlayer().getPoseAnimation()
                == client.getLocalPlayer().getWalkAnimation()) || (client.getLocalPlayer().getPoseAnimation() == client.getLocalPlayer().getRunAnimation());
    }

    /**
     * Checks if the player character is currently performing an animation.
     *
     * @return true if the player is animating, false otherwise.
     */
    public static boolean isAnimating() {
        return client.getLocalPlayer().getAnimation() != -1;
    }

    /**
     * Retrieves the world coordinates (WorldPoint) of the player's current position.
     *
     * @return The WorldPoint representing the player's current position.
     */
    public static WorldPoint playerPosition(){
        return client.getLocalPlayer().getWorldLocation();
    }

    /**
     * Checks if the player character is currently on the ground plane (plane 0).
     *
     * @return true if the player is on the ground plane, false otherwise.
     */
    public static boolean onGroundPlane() {
        return client.getPlane() == 0;
    }

    /**
     * Checks if the player character is in a specific game region identified by its region ID.
     *
     * @param regionIDs The ID of the region(s) to check.
     * @return true if the player is in the specified region, false otherwise.
     */
    public static boolean playerInRegion(int... regionIDs) {
        int playerRegion = client.getLocalPlayer().getWorldLocation().getRegionID();

        for (int region : regionIDs) {
            if (playerRegion == region) {
                return true; // Player's region is within the specified regions
            }
        }

        return false; // Player's region is not within the specified regions
    }

    /**
     * Walks the player character to the nearest bank if possible.
     */
    public static void walkToBank() {
        walkerEngine.walkNearestBank();
    }

    /**
     * Initiates player character movement towards a specified WorldPoint destination using the Webwalker.
     * This method only needs to be called once to walk to the desired WorldPoint destination.
     *
     * @param point The WorldPoint destination to walk towards.
     */
    public static void walk(WorldPoint point) {
        walkerEngine.walk(point);
    }

    /**
     * Initiates player character movement towards a specified WorldPoint destination.
     *
     * @param point The WorldPoint destination to walk to.
     */
    public static void move(WorldPoint point) {
        MovementPackets.queueMovement(point);
    }

    /**
     * Checks if the quick prayer feature is currently enabled for the player.
     *
     * @return true if quick prayer is enabled, false otherwise.
     */
    public static boolean isQuickPrayerEnabled() {
        return client.getVarbitValue(QUICK_PRAYER) == 1;
    }

    /**
     * Checks if a specific quick prayer is currently active for the player.
     *
     * @param prayer The QuickPrayer to check.
     * @return true if the specified quick prayer is active, false otherwise.
     */
    public static boolean isQuickPrayerActive(QuickPrayer prayer) {
        return (client.getVarbitValue(4102) & (int) Math.pow(2, prayer.getIndex())) == Math.pow(2, prayer.getIndex());
    }

    /**
     * Shuts off all active prayers.
     */
    public static void shutOffPrayers() {
        for (Prayer prayer : Prayer.values()) {
            if(PrayerUtil.isPrayerActive(prayer)) {
                PrayerUtil.togglePrayer(prayer);
            }
        }
    }

    /**
     * Checks if the run energy is currently enabled for the player.
     *
     * @return true if run energy is enabled, false otherwise.
     */
    public static boolean isRunEnabled() {
        return client.getVarpValue(173) != 0;
    }

    /**
     * Enables the run energy for the player if it is currently disabled.
     */
    public static void enableRun() {
        if(!isRunEnabled()) {
            Widgets.search()
                    .withId(10485787)
                    .first()
                    .ifPresent(runWidget -> {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueWidgetActionPacket(1, runWidget.getId(), -1, -1);
                    });
            log.info("Enabled Run");
        } else {
            log.info("Run is already enabled!");
        }
    }

    /**
     * Checks if the bank is open
     * IF the bank is open, will return a true statement
     */
    public static boolean isBankOpen() {
        return client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER) != null;
    }

    /**
     * Attempts to find and interact with the nearest bank object.
     */
    public static void findBankOrWalk() {
        TileObjects.search()
                .filter(PolarUtilsPlugin::isBankObject)
                .nearestToPlayer()
                .ifPresent(PolarUtilsPlugin::openBank);
    }

    /**
     * Checks if a given tile object is a bank based on its name and actions.
     *
     * @param tileObject The tile object to check.
     * @return true if the tile object represents a bank, false otherwise.
     */
    public static boolean isBankObject(TileObject tileObject) {
        ObjectComposition objectComposition = TileObjectQuery.getObjectComposition(tileObject);
        String objectName = objectComposition.getName().toLowerCase();
        String[] objectActions = objectComposition.getActions();

        return objectName.contains("bank") || Arrays.stream(objectActions)
                .anyMatch(action -> action != null && action.toLowerCase().contains("bank") &&  tileObject.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()) <= 15);
    }

    /**
     * Opens the bank using a specified tile object.
     *
     * @param tileObject The tile object representing the bank.
     */
    public static void openBank(TileObject tileObject) {
        TileObjectInteraction.interact(tileObject, "Use", "Bank");
    }

    /**
     * Attempts to withdraw a specified quantity of an item from the bank if the bank is open.
     * If the bank is not open, this method returns without performing any action.
     *
     * @param itemName The name of the item to withdraw.
     * @param amount   The quantity of the item to withdraw.
     */
    public static void withdraw(String itemName, int amount) {
        if(!Bank.isOpen()) {
            return;
        }

        Optional<Widget> item = BankUtil.nameContainsNoCase(itemName).first();
        item.ifPresent(widget -> BankInteraction.withdrawX(widget, amount));

    }

    public static void withdraw(int id, int amount) {
        if(!Bank.isOpen()) {
            return;
        }

        Optional<Widget> item = Bank.search().withId(id).first();
        item.ifPresent(widget -> BankInteraction.withdrawX(widget, amount));

    }

    /**
     * Deposits the entire inventory into the bank.
     * If the bank is not open, this method returns without performing any action.
     */
    public static void depositAll() {
        if(!Bank.isOpen()) {
            return;
        }

        Optional<Widget> depositWidget = Widgets.search().withId(WidgetInfo.BANK_DEPOSIT_INVENTORY.getId()).first();
        depositWidget.ifPresent(widget -> WidgetPackets.queueWidgetAction(widget, "Deposit", "Deposit inventory"));
    }

    /**
     * Picks up the closest tile item above X value to the local player.
     *
     * @param value The minimum value to loot.
     * @return The closest tile item to the local player, or null if no tile items are found.
     */
    public static void pickupClosestTileItemAboveX(int value, int distanceThreshold) {
        Optional<ETileItem> groundObj = TileItems.search()
                .stackAboveXValue(value)
                .nearestToPlayer()
                .filter(tileItem -> {
                    boolean canReach = EthanApiPlugin.getTileInfo(tileItem.getLocation()).isReachable();
                    int itemDistance = tileItem.getLocation().distanceTo(playerPosition());
                    return itemDistance <= distanceThreshold && canReach;
                });

        groundObj.ifPresentOrElse(
                tileItem -> TileItemPackets.queueTileItemAction(tileItem, false),
                () -> log.info("No Tile Items Found!")
        );
    }

}
