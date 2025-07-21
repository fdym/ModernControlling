package net.fdymcreep.moderncontrolling.core;

import net.fdymcreep.moderncontrolling.core.client.event.CoreClientEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

@Mod(
        modid = ControllingCore.MODID,
        name = ControllingCore.NAME,
        version = ControllingCore.VERSION,
        clientSideOnly = true
)
public class ControllingCore {
    public static final String MODID = "moderncontrolling_core";
    public static final String NAME = "Modern Controlling Core";
    public static final String VERSION = "12.1.1";

    @Nullable
    public static Class<?> getClassSafe(String s) {
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CoreClientEventHandler());
    }
}
