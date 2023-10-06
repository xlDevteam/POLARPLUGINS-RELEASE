package com.polar.fighter.overlay;

import com.polar.fighter.PolarFighter;
import com.polar.fighter.config.PolarFighterConfig;
import com.polar.util.PolarUtilsPlugin;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class PolarFighterMinimapOverlay extends Overlay
{
    private final PolarFighterConfig config;
    private final PolarFighter plugin;

    @Inject
    private PolarFighterMinimapOverlay(PolarFighterConfig config, PolarFighter polarFighter)
    {
        this.config = config;
        this.plugin = polarFighter;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (config.renderMinimapOverlay()) {

            if(plugin.getRadiusTile() != null) {
                renderFightArea(graphics);
            }

            if(plugin.getSafeSpotTile() != null) {
                renderSafespot(graphics);
            }

            for (NPC npc : PolarUtilsPlugin.getClient().getNpcs()) {
                if (npc != null && npc.getName() != null && npc.getName().equals(config.monsterName()) && npc.getLocalLocation().isInScene()) {
                    if(plugin.getRadiusTile() != null) {
                        if(npc.getWorldLocation().distanceTo(plugin.getRadiusTile()) <= config.searchRadius()) {
                            renderNPC(graphics, npc);
                        }
                    } else {
                        renderNPC(graphics, npc);
                    }
                }
            }
        }

        return null;
    }

    private void renderNPC(Graphics2D graphics, NPC actor)
    {
        final Point minimapLocation = actor.getMinimapLocation();

        if (minimapLocation != null)
        {
            // Define the color for the dot (e.g., Color.RED)
            Color dotColor = config.monsterColor();

            // Define the size of the dot (adjust as needed)
            int dotSize = 5;

            // Calculate the coordinates for the center of the dot
            int x = minimapLocation.getX() - dotSize / 2;
            int y = minimapLocation.getY() - dotSize / 2;

            // Set the dot color and draw it
            graphics.setColor(dotColor);
            graphics.fillOval(x, y, dotSize, dotSize);
        }
    }

    public void renderFightArea(Graphics2D graphics)
    {
        int radius = config.searchRadius()*4;
        LocalPoint lp = LocalPoint.fromWorld(PolarUtilsPlugin.getClient(), plugin.getRadiusTile());
        if (lp == null) return;
        Point mini = Perspective.localToMinimap(PolarUtilsPlugin.getClient(), lp);
        if (mini == null) return;
        graphics.setColor(config.atkRadiusColor());
        graphics.fillOval(mini.getX() - radius, mini.getY() - radius, radius*2, radius*2);
        graphics.setColor(new Color(config.atkRadiusColor().getRed(), config.atkRadiusColor().getGreen(), config.atkRadiusColor().getBlue()));
        graphics.drawOval(mini.getX() - radius, mini.getY() - radius, radius*2, radius*2);
    }

    public void renderSafespot(Graphics2D graphics)
    {
        int radius = 2;
        LocalPoint lp = LocalPoint.fromWorld(PolarUtilsPlugin.getClient(), plugin.getSafeSpotTile());
        if (lp == null) return;
        Point mini = Perspective.localToMinimap(PolarUtilsPlugin.getClient(), lp);
        if (mini == null) return;

        int x = mini.getX() - radius;
        int y = mini.getY() - radius;
        int width = radius * 2;
        int height = radius * 2;

        graphics.setColor(config.atkRadiusColor());
        graphics.fillRect(x, y, width, height); // Fill rectangle
        graphics.setColor(ColorScheme.GRAND_EXCHANGE_PRICE);
        graphics.drawRect(x, y, width, height); // Draw rectangle border
    }


}