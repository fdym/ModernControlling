package net.fdymcreep.moderncontrolling.core.compat;

import net.minecraftforge.fml.common.Loader;

public class MoCoreCompatCheck {
    public static boolean moCKCanCompat() {
        return Loader.isModLoaded("moderncontrolling_keybind");
    }
}
