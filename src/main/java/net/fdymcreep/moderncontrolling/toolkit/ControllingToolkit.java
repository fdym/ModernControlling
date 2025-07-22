package net.fdymcreep.moderncontrolling.toolkit;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ControllingToolkit.MODID,
        name = ControllingToolkit.NAME,
        version = ControllingToolkit.VERSION,
        dependencies = "required-after-client:moderncontrolling_core",
        clientSideOnly = true,
        guiFactory = "net.fdymcreep.moderncontrolling.toolkit.MoCTGuiFactory"
)
public class ControllingToolkit {
    public static final String MODID = "moderncontrolling_toolkit";
    public static final String NAME = "Modern Controlling Toolkit";
    public static final String VERSION = "12.1.1.1";

    public static Logger logger;

    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }
}
