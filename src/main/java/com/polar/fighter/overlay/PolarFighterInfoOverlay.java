package com.polar.fighter.overlay;


import com.ethan.EthanApiPlugin.Collections.Inventory;
import com.polar.fighter.PolarFighter;
import com.polar.fighter.config.PolarFighterConfig;
import com.polar.fighter.config.enums.*;
import com.polar.fighter.config.enums.OffensivePrayerOne;
import net.runelite.api.Client;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PolarFighterInfoOverlay extends Overlay {
    private final PolarFighter plugin;
    private final PolarFighterConfig config;
    private final PanelComponent panelComponent = new PanelComponent();
    private Client client;

    @Inject
    public PolarFighterInfoOverlay(PolarFighter plugin, PolarFighterConfig config, Client client) {
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }


    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().clear(); // Clear existing components

        String overlayTitle = "Polar Fighter";

            panelComponent.getChildren().clear(); // Clear existing components

            if(plugin.isPluginRunning()) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(overlayTitle)
                        .color(ColorScheme.GRAND_EXCHANGE_LIMIT)
                        .build());
            } else {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(overlayTitle)
                        .color(ColorScheme.PROGRESS_ERROR_COLOR)
                        .build());
            }

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("State: ")
                    .right(String.valueOf(plugin.getState()))
                    .build());

            if(config.usePrayers()) {

                if(config.defensivePrayer() != DefensivePrayer.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Defensive Prayer: ")
                            .right(config.defensivePrayer().name())
                            .build());
                }

                if(config.offensivePrayerOne() != OffensivePrayerOne.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Offensive Prayer: ")
                            .right(config.offensivePrayerOne().name())
                            .build());
                }

                /*if(config.offensivePrayerTwo() != OffensivePrayerTwo.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Offensive Prayer: ")
                            .right(config.offensivePrayerTwo().name())
                            .build());
                } */

            }

            if(!config.oneTickFlick() && config.usePrayers()) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Prayer Boost: ")
                        .right(config.prayerPotion().name())
                        .build());
            }

            if(config.useBoosts()) {

                if(config.attackBoost() != AttackBoost.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Attack Boost: ")
                            .right(config.attackBoost().name())
                            .build());
                }


                if(config.strengthBoost() != StrengthBoost.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Strength Boost: ")
                            .right(config.strengthBoost().name())
                            .build());
                }

                if(config.rangedBoost() != RangedPotions.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Ranged Boost: ")
                            .right(config.rangedBoost().name())
                            .build());
                }

                if(config.defenceBoost() != DefencePotions.NONE) {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Defence Boost: ")
                            .right(config.defenceBoost().name())
                            .build());
                }

            }

            if(config.foodType() != Food.NONE) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Food: " + config.foodType().name() )
                        .right("In Inv: " + Inventory.search().withAction("Eat").result().size())
                        .build());
            }

        panelComponent.setPreferredSize(new Dimension(
                    graphics.getFontMetrics().stringWidth(overlayTitle) + 170,
                    0));

        panelComponent.setBackgroundColor(new Color(30, 30, 30, 175)); //money

        if(config.renderInfoOverlay()) {
            return this.panelComponent.render(graphics);
        } else {
            return null;
        }


    }
}