package net.fdymcreep.moderncontrolling.toolkit;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ControllingToolkit.MODID)
@Config.LangKey("config." + ControllingToolkit.MODID + ".general")
public class ControllingToolkitConfig {
    @Mod.EventBusSubscriber(modid = ControllingToolkit.MODID)
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ControllingToolkit.MODID)) {
                ConfigManager.sync(ControllingToolkit.MODID, Config.Type.INSTANCE);
            }
        }
    }

    @Config.LangKey("config." + ControllingToolkit.MODID + ".general.enableNcKeys")
    @Config.Comment("")
    public static boolean enableNcKeys = false;

    @Config.LangKey("config." + ControllingToolkit.MODID + ".general.ncKeyCodes")
    @Config.Comment("When a key in the list is pressed, all key bindings for this key can be activated.")
    public static int[] nonConflictKeyCodes = new int[100];

    @Config.LangKey("config." + ControllingToolkit.MODID + ".general.enableAKiNC")
    @Config.Comment("When ANY key is pressed, all key bindings for this key can be activated. Enabling this option will override the effect of \"Non conflict key codes\".")
    public static boolean enableAKiNC = false;
}
