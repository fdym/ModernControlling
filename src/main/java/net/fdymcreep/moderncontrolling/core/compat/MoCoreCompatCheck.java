package net.fdymcreep.moderncontrolling.core.compat;

import net.minecraftforge.fml.common.Loader;

public class MoCoreCompatCheck {
    private MoCoreCompatCheck() {}

    public static boolean moCKCanCompat() {
        return Loader.isModLoaded("moderncontrolling_keybind");
    }
}
