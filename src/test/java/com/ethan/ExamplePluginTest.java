package com.ethan;

import com.ethan.EthanApiPlugin.EthanApiPlugin;
import com.ethan.PacketUtils.PacketUtilsPlugin;

import com.polar.fighter.PolarFighter;
import com.polar.util.PolarUtilsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class, PolarUtilsPlugin.class,
                PolarFighter.class);
        RuneLite.main(args);
    }
}