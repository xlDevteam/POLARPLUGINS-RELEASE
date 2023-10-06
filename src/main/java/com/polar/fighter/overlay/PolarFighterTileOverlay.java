package com.polar.fighter.overlay;

import com.google.inject.Inject;
import com.polar.fighter.PolarFighter;
import com.polar.fighter.config.PolarFighterConfig;
import com.polar.util.PolarUtilsPlugin;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PolarFighterTileOverlay extends Overlay {
    PolarFighter plugin;
    PolarFighterConfig config;
    Client client;

    ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    PolarFighterTileOverlay(Client client, PolarFighter plugin, PolarFighterConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (config.renderMonsterOverlay()) {
            renderNPCTile(config.monsterName(), graphics);
        }

        if(config.renderMonsterOutlineOverlay()) {
            renderNPC(config.monsterName());
        }

        if (plugin.getSafeSpotTile() != null && config.renderSafespotTile()) {
            LocalPoint localPoint = LocalPoint.fromWorld(client, plugin.getSafeSpotTile());
            if (localPoint != null) {
                Polygon polygon = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                if (polygon != null) {
                    // Render the polygon
                    OverlayUtil.renderPolygon(graphics, polygon, ColorScheme.PROGRESS_COMPLETE_COLOR);

                    LocalPoint lp = LocalPoint.fromWorld(PolarUtilsPlugin.getClient(), plugin.getSafeSpotTile());
                    if(lp != null) {
                        Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, "Safespot", 0);
                        if (canvasTextLocation != null)
                        {
                            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, "Safespot", ColorScheme.PROGRESS_COMPLETE_COLOR);
                        }
                    }
                }
            }
        }

        if(plugin.getRadiusTile() != null && config.renderRadiusTiles()) {
            renderRadius(graphics);
        }

        if (plugin.getRadiusTile() != null && config.renderRadiusCenter()) {
            LocalPoint localPoint = LocalPoint.fromWorld(client, plugin.getRadiusTile());
            if (localPoint != null) {
                Polygon polygon = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
                if (polygon != null) {
                    // Render the polygon
                    OverlayUtil.renderPolygon(graphics, polygon, config.atkRadiusColor());
                    // Render text centered on the tile
                    LocalPoint lp = LocalPoint.fromWorld(PolarUtilsPlugin.getClient(), plugin.getRadiusTile());
                    if(lp != null) {
                        Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, "Attack Radius Center", 0);
                        if (canvasTextLocation != null)
                        {
                            OverlayUtil.renderTextLocation(graphics, canvasTextLocation, "Attack Radius Center", config.atkRadiusColor());
                        }
                    }

                }
            }
        }

        return null;
    }

    private void renderNPCTile(String npcName, Graphics2D graphics2D) {
        for (NPC npc : client.getNpcs()) {
            if (npc != null && npc.getName() != null && npc.getName().equals(npcName) && npc.getLocalLocation().isInScene()) {
                if(plugin.getRadiusTile() != null) {
                    if(npc.getWorldLocation().distanceTo(plugin.getRadiusTile()) <= config.searchRadius()) {
                        renderTile(graphics2D, npc.getWorldLocation());
                    }
                } else {
                    renderTile(graphics2D, npc.getWorldLocation());
                }
            }
        }
    }

    private void renderNPC(String npcName) {
        for (NPC npc : client.getNpcs()) {
            if (npc != null && npc.getName() != null && npc.getName().equals(npcName) && npc.getLocalLocation().isInScene()) {
                if(plugin.getRadiusTile() != null) {
                    if(npc.getWorldLocation().distanceTo(plugin.getRadiusTile()) <= config.searchRadius()) {
                        renderOutline(npc);
                    }
                } else {
                    renderOutline(npc);
                }
            }
        }
    }

    private void renderRadius(Graphics2D graphics) {
        int radiusInTiles = config.searchRadius(); // Get the search radius from your config
        WorldPoint radiusLoc = plugin.getRadiusTile();

        List<WorldPoint> circlePoints = generateCirclePoints(radiusLoc, radiusInTiles);

        for (WorldPoint worldPoint : circlePoints) {

            Polygon poly = Perspective.getCanvasTilePoly(client, Objects.requireNonNull(LocalPoint.fromWorld(client, worldPoint)));

            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, config.atkRadiusColor());
            }
        }

    }

    private List<WorldPoint> generateCirclePoints(WorldPoint center, int radiusInTiles) {
        List<WorldPoint> circlePoints = new ArrayList<>();
        int x0 = center.getX();
        int y0 = center.getY();

        for (int x = x0 - radiusInTiles; x <= x0 + radiusInTiles; x++) {
            for (int y = y0 - radiusInTiles; y <= y0 + radiusInTiles; y++) {
                if (isWithinCircle(x0, y0, x, y, radiusInTiles)) {
                    circlePoints.add(new WorldPoint(x, y, center.getPlane()));
                }
            }
        }

        return circlePoints;
    }

    private boolean isWithinCircle(int centerX, int centerY, int x, int y, int radius) {
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }

    private void renderTile(final Graphics2D graphics2D, WorldPoint renderPoint) {
        if (renderPoint == null) {
            return;
        }

        LocalPoint localPoint = LocalPoint.fromWorld(client, renderPoint);

        if (localPoint != null) {
            Polygon polygon = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
            if (polygon != null) { // Check if the polygon is not null before rendering
                OverlayUtil.renderPolygon(graphics2D, polygon, config.monsterColor());
            }
        }
    }

    private void renderOutline(NPC npc) {
        if (npc == null) {
            return;
        }

        Shape p = npc.getConvexHull();

        if (p != null) {
            modelOutlineRenderer.drawOutline(npc, config.monsterOutline(), config.monsterColor(), config.monsterOutline() + 4);
        }
    }


}
