package com.polar.fighter;


import com.google.inject.Inject;
import com.google.inject.Provides;
import com.polar.fighter.overlay.PolarFighterMinimapOverlay;
import com.polar.fighter.state.State;
import com.polar.fighter.state.StateHandler;
import com.polar.fighter.config.PolarFighterConfig;
import com.polar.fighter.overlay.PolarFighterInfoOverlay;
import com.polar.fighter.overlay.PolarFighterTileOverlay;
import com.polar.util.API.PrayerUtil;
import com.polar.util.PolarUtilsPlugin;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@PluginDescriptor(
        name = "<html><font color=\"#0062ff\">[POL]</font> Fighter</html>",
        enabledByDefault = false,
        tags = {"Dino"}
)
@Slf4j
public class PolarFighter extends Plugin {

    public int nextReload;
    public int cballsLeft;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Getter
    @Inject
    private PolarFighterConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PolarFighterInfoOverlay infoOverlay;
    @Inject
    private PolarFighterTileOverlay polarFighterTileOverlay;
    @Inject
    private PolarFighterMinimapOverlay polarFighterMinimapOverlay;

    @Inject
    private StateHandler stateHandler;
    @Inject
    private KeyManager keyManager;

    private int[] prevLevels;

    private final String TARGET = ColorUtil.wrapWithColorTag("Safespot Tile", ColorScheme.GRAND_EXCHANGE_LIMIT);
    private final String TARGET2 = ColorUtil.wrapWithColorTag("Attack Area Center", ColorScheme.GRAND_EXCHANGE_LIMIT);

    @Setter
    public int wait;

    @Getter
    public State state;

    @Getter
    private boolean pluginRunning = false;

    @Getter
    int currentHitpoints, currentPrayer, currentStrength, currentAttack, currentDefense, currentRanged, currentMagic,
            maxHitpoints, maxPrayer, maxStrength, maxAttack, maxDefense, maxRanged, maxMagic;

    @Getter
    public Prayer[] prayersToFlick;

    @Getter
    private WorldPoint safeSpotTile;

    @Getter
    private WorldPoint radiusTile;

    @Inject
    private ItemManager itemManager;

    private Point lastMenuOpenedPoint;

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    public void togglePlugin() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        pluginRunning = !pluginRunning;
        if (!pluginRunning) {
            clientThread.invoke(PolarUtilsPlugin::shutOffPrayers);
            clientThread.invoke((this::reset));
            radiusTile = null;
            safeSpotTile = null;
            //stateHandler.getBreakHandler().stopPlugin(this);
        } else {
           // stateHandler.getBreakHandler().startPlugin(this);
        }
    }

    @Provides
    public PolarFighterConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PolarFighterConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        prevLevels = new int[Skill.values().length];
        prayersToFlick = new Prayer[4];
        nextReload = ThreadLocalRandom.current().nextInt(5, 15 + 1);
        keyManager.registerKeyListener(pluginToggle);
        overlayManager.add(infoOverlay);
        overlayManager.add(polarFighterTileOverlay);
        overlayManager.add(polarFighterMinimapOverlay);
        clientThread.invoke(() -> cballsLeft = client.getVarpValue(VarPlayer.CANNON_AMMO));
        //WebhookMessages.sendPluginTurnedOnMessage(config.webhookUrl());
    }

    @Override
    protected void shutDown() throws Exception {
        prayersToFlick = null;
        pluginRunning = false;
        radiusTile = null;
        safeSpotTile = null;
        keyManager.unregisterKeyListener(pluginToggle);
        overlayManager.remove(infoOverlay);
        overlayManager.remove(polarFighterTileOverlay);
        overlayManager.remove(polarFighterMinimapOverlay);
        //WebhookMessages.sendPluginTurnedOffMessage(config.webhookUrl());
    }

    public void reset() {
        prayersToFlick = new Prayer[4];
        pluginRunning = false;
        radiusTile = null;
        safeSpotTile = null;
        stateHandler.teleOut = false;
        state = null;
    }

    @Subscribe
    public void onGameTick(GameTick e) {

        if (PolarUtilsPlugin.notLoggedIn() || !pluginRunning) return;

        updateVals();

        handlePrayers(config.usePrayers());

        state = stateHandler.getNextState();
        stateHandler.handleState();

    }

    private final int[] prevExperience = new int[Skill.values().length];

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Skill skill = statChanged.getSkill();
        int newExperience = statChanged.getXp();

        int skillIdx = skill.ordinal();
        int prevExp = prevExperience[skillIdx];

        if (newExperience != prevExp) {
            if (prevExp != 0) {
                int newLevel = getLevelForExperience(newExperience);
                int prevLevel = getLevelForExperience(prevExp);

                if (newLevel > prevLevel) {
                    log.info(skill.getName(), "{} level increased to {}", newLevel);
                }
            }
        }

        prevExperience[skillIdx] = newExperience;
    }

    private int getLevelForExperience(int experience) {
        int points = 0;
        int output = 0;

        for (int level = 1; level <= 99; level++) {
            points += Math.floor(level + 300.0 * Math.pow(2.0, level / 7.0));
            output = (int) Math.floor((double) points / 4);
            if ((output - 1) >= experience) {
                return level;
            }
        }

        return 99; // Maximum level
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged varbitChanged) {
        if (varbitChanged.getVarpId() == VarPlayer.CANNON_AMMO) {
            cballsLeft = varbitChanged.getValue();
        }
    }

    private void updateVals() {

        maxHitpoints = client.getRealSkillLevel(Skill.HITPOINTS);
        maxPrayer = client.getRealSkillLevel(Skill.PRAYER);
        maxAttack = client.getRealSkillLevel(Skill.ATTACK);
        maxStrength = client.getRealSkillLevel(Skill.STRENGTH);
        maxDefense = client.getRealSkillLevel(Skill.DEFENCE);
        maxRanged = client.getRealSkillLevel(Skill.RANGED);
        maxMagic = client.getRealSkillLevel(Skill.MAGIC);

        currentHitpoints = client.getBoostedSkillLevel(Skill.HITPOINTS);
        currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
        currentAttack = client.getBoostedSkillLevel(Skill.ATTACK);
        currentStrength = client.getBoostedSkillLevel(Skill.STRENGTH);
        currentDefense = client.getBoostedSkillLevel(Skill.DEFENCE);
        currentRanged = client.getBoostedSkillLevel(Skill.RANGED);
        currentMagic = client.getBoostedSkillLevel(Skill.MAGIC);

    }

    private void handlePrayers(Boolean pluginRunning) {
        if(prayersToFlick == null) {
            return;
        }

        if (pluginRunning) {
            for (Prayer prayer : prayersToFlick) {
                if (prayer != null && !PrayerUtil.isPrayerActive(prayer)) {
                    PrayerUtil.togglePrayer(prayer);
                }
            }

            if (config.oneTickFlick()) {
                PrayerUtil.toggleMultiplePrayers(prayersToFlick);
                PrayerUtil.toggleMultiplePrayers(prayersToFlick);
            }
        }
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!"fighter".equals(event.getGroup())) {
            return;
        }

        if(event.getKey().equals("defensivePrayer") || event.getKey().equals("offensivePrayerOne") || event.getKey().equals("offensivePrayerTwo")
                || event.getKey().equals("usePrayers")) {
            clientThread.invoke(PolarUtilsPlugin::shutOffPrayers);
            prayersToFlick = new Prayer[4];
        }

    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {

        if (event.getOption().equals("Walk here") && event.getTarget().isEmpty()) {

            if(safeSpotTile != null) {
                addMenuEntry(event, "Remove", TARGET);
            } else {
                addMenuEntry(event, "Set", TARGET);
            }

            if(radiusTile != null) {
                addMenuEntry(event, "Remove", TARGET2);
            } else {
                addMenuEntry(event, "Set", TARGET2);
            }

        }

    }

    private void onMenuOptionClicked(MenuEntry entry) {

        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }

        if (entry.getOption().equals("Set") && entry.getTarget().equals(TARGET)) {
            safeSpotTile = getSelectedWorldPoint();
        }

        if (entry.getOption().equals("Remove") && entry.getTarget().equals(TARGET)) {
            safeSpotTile = null;
        }

        if (entry.getOption().equals("Set") && entry.getTarget().equals(TARGET2)) {
            radiusTile = getSelectedWorldPoint();
        }

        if (entry.getOption().equals("Remove") && entry.getTarget().equals(TARGET2)) {
            radiusTile = null;
        }

    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        lastMenuOpenedPoint = client.getMouseCanvasPosition();
    }

    private void addMenuEntry(MenuEntryAdded event, String option, String target) {
        List<MenuEntry> entries = new LinkedList<>(Arrays.asList(client.getMenuEntries()));

        if (entries.stream().anyMatch(e -> e.getOption().equals(option) && e.getTarget().equals(target))) {
            return;
        }

        client.createMenuEntry(1)
                .setOption(option)
                .setTarget(target)
                .setParam0(event.getActionParam0())
                .setParam1(event.getActionParam1())
                .setIdentifier(event.getIdentifier())
                .setType(MenuAction.RUNELITE)
                .onClick(this::onMenuOptionClicked);
    }

    private WorldPoint getSelectedWorldPoint() {
        if (client.getWidget(WidgetInfo.WORLD_MAP_VIEW) == null) {
            if (client.getSelectedSceneTile() != null) {
                return client.isInInstancedRegion() ?
                        WorldPoint.fromLocalInstance(client, client.getSelectedSceneTile().getLocalLocation()) :
                        client.getSelectedSceneTile().getWorldLocation();
            }
        } else {
            return calculateMapPoint(client.isMenuOpen() ? lastMenuOpenedPoint : client.getMouseCanvasPosition());
        }
        return null;
    }

    public Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint) {
        WorldMap worldMap = client.getWorldMap();

        float pixelsPerTile = worldMap.getWorldMapZoom();

        Widget map = client.getWidget(WidgetInfo.WORLD_MAP_VIEW);
        if (map != null) {
            Rectangle worldMapRect = map.getBounds();

            int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
            int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

            Point worldMapPosition = worldMap.getWorldMapPosition();

            int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
            int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
            int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

            int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
            int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

            yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
            xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

            yGraphDiff = worldMapRect.height - yGraphDiff;
            yGraphDiff += (int) worldMapRect.getY();
            xGraphDiff += (int) worldMapRect.getX();

            return new Point(xGraphDiff, yGraphDiff);
        }
        return null;
    }

    public WorldPoint calculateMapPoint(Point point) {
        WorldMap worldMap = client.getWorldMap();
        float zoom = worldMap.getWorldMapZoom();
        final WorldPoint mapPoint = new WorldPoint(worldMap.getWorldMapPosition().getX(), worldMap.getWorldMapPosition().getY(), 0);
        final Point middle = mapWorldPointToGraphicsPoint(mapPoint);

        if (point == null || middle == null) {
            return null;
        }

        final int dx = (int) ((point.getX() - middle.getX()) / zoom);
        final int dy = (int) ((-(point.getY() - middle.getY())) / zoom);

        return mapPoint.dx(dx).dy(dy);
    }

}
