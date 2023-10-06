package com.polar.fighter.config;

import com.polar.fighter.config.enums.*;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("fighter")
public interface PolarFighterConfig extends Config {

    @ConfigSection(
            name = "<html><font color=#0062ff>Polar Fighter</font></html>",
            description = "",
            position = -2
    )
    String Title = "titleSection";

    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = -1,
            section = Title
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            name = "Monster Name",
            description = "",
            position = 0,
            keyName = "monsterName",
            section = Title
    )
    default String monsterName() {
        return "";
    }

    @Range(
            min = 0,
            max = 18
    )
    @ConfigItem(
            keyName = "searchRadius",
            name = "Attack Radius",
            description = "",
            position = 1,
            section = Title
    )
    default int searchRadius() {
        return 1;
    }

    @ConfigSection(
            name = "<html><font color=#0062ff>Settings (Combat)</font></html>",
            description = "",
            position = 1,
            closedByDefault = true
    )
    String combat = "combatSection";

    @ConfigItem(keyName = "useBoosts", name = "Use Boost Potions?", description = "", position = 1, section = combat)
    default boolean useBoosts() { return true; }

    /* @ConfigItem(keyName = "useFood", name = "Use Food?", description = "", position = 2, section = combat)
    default boolean useFood() { return true; } */

    @ConfigItem(keyName = "usePrayers", name = "Use Prayers?", description = "", position = 3, section = combat)
    default boolean usePrayers() { return true; }

    @ConfigItem(keyName = "oneTick", name = "One Tick Prayers?", description = "", position = 4, section = combat)
    default boolean oneTickFlick() { return false; }

    @ConfigItem(
            name = "Defence Prayer",
            description = "",
            position = 5,
            keyName = "defensivePrayer",
            section = combat
    )
    default DefensivePrayer defensivePrayer() {
        return DefensivePrayer.NONE;
    }

    @ConfigItem(
            name = "Offense Prayer",
            description = "",
            position = 6,
            keyName = "offensivePrayerOne",
            section = combat
    )
    default OffensivePrayerOne offensivePrayerOne() {
        return OffensivePrayerOne.NONE;
    }

    /*@ConfigItem(
            name = "Offense Prayer",
            description = "",
            position = 7,
            keyName = "offensivePrayerTwo",
            section = combat
    )
    default OffensivePrayerTwo offensivePrayerTwo() {
        return OffensivePrayerTwo.NONE;
    } */

    @ConfigItem(
            name = "Food",
            description = "",
            position = 8,
            keyName = "foodType",
            section = combat
    )
    default Food foodType() {
        return Food.SHARK;
    }

    @Range(
            min = 0,
            max = 18
    )
    @ConfigItem(
            keyName = "foodAmount",
            name = "Food Amount",
            description = "",
            position = 9,
            section = combat
    )
    default int foodAmount() {
        return 4;
    }

    @Range(
            min = 10,
            max = 100
    )
    @ConfigItem(
            keyName = "minHealthPercent",
            name = "Min Health %",
            description = "",
            position = 10,
            section = combat
    )
    default int minHealthPercent() {
        return 65;
    }

    @ConfigItem(
            name = "Prayer Potion",
            description = "",
            position = 11,
            keyName = "prayerPotion",
            section = combat
    )
    default PrayerPotions prayerPotion() {
        return PrayerPotions.NONE;
    }

    @Range(
            min = 10,
            max = 100
    )
    @ConfigItem(
            keyName = "minPrayerPercent",
            name = "Min Prayer %",
            description = "",
            position = 12,
            section = combat
    )
    default int minPrayerPercent() {
        return 45;
    }

    @ConfigItem(
            name = "Attack Boost",
            description = "",
            position = 13,
            keyName = "combatBoostsatk",
            section = combat
    )
    default AttackBoost attackBoost() {
        return AttackBoost.NONE;
    }

    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "minBoostIncreaseatk",
            name = "Boost Lvls ATK",
            description = "",
            position = 14,
            section = combat
    )
    default int minBoostIncreaseATK() {
        return 5;
    }

    @ConfigItem(
            name = "Strength Boost",
            description = "",
            position = 15,
            keyName = "combatBoostsSTr",
            section = combat
    )
    default StrengthBoost strengthBoost() {
        return StrengthBoost.NONE;
    }
    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "minBoostIncreasestr",
            name = "Boost Lvls STR",
            description = "",
            position = 16,
            section = combat
    )
    default int minBoostIncreaseStrength() {
        return 3;
    }

    @ConfigItem(
            name = "Defence Boost",
            description = "",
            position = 17,
            keyName = "combatBoostsDEF",
            section = combat
    )
    default DefencePotions defenceBoost() {
        return DefencePotions.NONE;
    }

    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "minBoostIncreaseDEF",
            name = "Boost Lvls DEF",
            description = "",
            position = 18,
            section = combat
    )
    default int minBoostIncreaseDEF() {
        return 5;
    }

    @ConfigItem(
            name = "Ranging Boost",
            description = "",
            position = 19,
            keyName = "combatBoostsRANGE",
            section = combat
    )
    default RangedPotions rangedBoost() {
        return RangedPotions.NONE;
    }

    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "minBoostIncreaseRANGE",
            name = "Boost Lvls RANGE",
            description = "",
            position = 20,
            section = combat
    )
    default int minBoostIncreaseRange() {
        return 5;
    }

    @ConfigItem(
            name = "Magic Boost",
            description = "",
            position = 21,
            keyName = "magicBoost",
            section = combat
    )
    default MagicBoost magicBoost() {
        return MagicBoost.NONE;
    }

    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "minBoostIncreaseMagic",
            name = "Boost Lvls MAGIC",
            description = "",
            position = 22,
            section = combat
    )
    default int minBoostIncreaseMagic() {
        return 5;
    }

    @ConfigItem(
            name = "Anti Fire",
            description = "",
            position = 23,
            keyName = "antiFire",
            section = combat
    )
    default AntiFire antiFire() {
        return AntiFire.NONE;
    }

    @ConfigItem(
            name = "Anti Venom",
            description = "",
            position = 23,
            keyName = "venoma",
            section = combat
    )
    default AntiVenom antiVenom() {
        return AntiVenom.NONE;
    }

    /*@ConfigSection(
            name = "<html><font color=#0062ff>Settings (Loot)</font></html>",
            description = "",
            position = 1,
            closedByDefault = true
    )
    String loot = "loot";

    @ConfigItem(keyName = "loot", name = "Loot items?", description = "", position = 0, section = loot)
    default boolean loot() { return false; }

    @Range(
            min = 1
    )
    @ConfigItem(
            keyName = "minLootValue",
            name = "Min Loot Value",
            description = "",
            position = 1,
            section = loot
    )
    default int minLootValue() {
        return 5000;
    }

    @ConfigItem(keyName = "alchLoot", name = "Alch loot?", description = "", position = 2, section = loot)
    default boolean alchLoot() { return false; }

    @Range(
            min = 1
    )
    @ConfigItem(
            keyName = "alchLootValue",
            name = "Min Alch Value",
            description = "",
            position = 3,
            section = loot
    )
    default int alchLootValue() {
        return 20000;
    }*/


    @ConfigSection(
            name = "<html><font color=#0062ff>Settings (Overlay)</font></html>",
            description = "",
            position = 2,
            closedByDefault = true
    )
    String overlay = "overlay";

    @ConfigItem(keyName = "renderInfoOverlay", name = "Render Info Overlay?", description = "", position = 0, section = overlay)
    default boolean renderInfoOverlay() { return false; }

    @ConfigItem(keyName = "renderMonsterOverlay", name = "Render Monster Tile?", description = "", position = 1, section = overlay)
    default boolean renderMonsterOverlay() { return false; }

    @ConfigItem(keyName = "renderMonsterOverlay2", name = "Render Monster Outline?", description = "", position = 2, section = overlay)
    default boolean renderMonsterOutlineOverlay() { return false; }

    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "monsterOutlineSize",
            name = "Outline Thickness",
            description = "",
            position = 3,
            section = overlay
    )
    default int monsterOutline() {
        return 7;
    }

    @ConfigItem(keyName = "renderSafespotTile", name = "Render Safespot Tile?", description = "", position = 4, section = overlay)
    default boolean renderSafespotTile() { return false; }

    @ConfigItem(keyName = "renderRadiusCenter", name = "Render Attack Radius Center?", description = "", position = 5, section = overlay)
    default boolean renderRadiusCenter() { return false; }

    @ConfigItem(keyName = "renderRadiusTiles", name = "Render Attack Radius Tiles?", description = "", position = 6, section = overlay)
    default boolean renderRadiusTiles() { return false; }

    @ConfigItem(keyName = "renderMinimapOverlay", name = "Render Minimap Overlay?", description = "", position = 7, section = overlay)
    default boolean renderMinimapOverlay() { return false; }

    @Alpha
    @ConfigItem(
            keyName = "monsterColor",
            name = "Monster Highlight Color",
            description = "",
            position = 8,
            section = overlay
    )
    default Color monsterColor() {
        return new Color(0, 140, 255, 150);
    }

    @Alpha
    @ConfigItem(
            keyName = "radiusColor",
            name = "Attack Radius Color",
            description = "",
            position = 9,
            section = overlay
    )
    default Color atkRadiusColor() {
        return new Color(47, 0, 255, 150);
    }

}
