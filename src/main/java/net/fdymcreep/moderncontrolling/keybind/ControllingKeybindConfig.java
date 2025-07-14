package net.fdymcreep.moderncontrolling.keybind;

import net.minecraftforge.common.config.Config;

@Config(modid = ControllingKeybind.MODID)
public class ControllingKeybindConfig {
    @Config.Comment("The currently used keybindings file name. The file is saved in .minecraft/keybindings . Do NOT modify this value when the game is open.")
    public static String keybindingsFilename = "default.json";

    @Config.Comment("Do NOT modify this value when the game is open.")
    public static boolean enableKeybindingsFile = true;
}
