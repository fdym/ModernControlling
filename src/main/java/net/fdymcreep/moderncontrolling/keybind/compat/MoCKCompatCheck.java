package net.fdymcreep.moderncontrolling.keybind.compat;

import net.minecraftforge.fml.common.Loader;

public class MoCKCompatCheck {
    private MoCKCompatCheck() {}

    public static boolean moCTCanCompat() {
        return Loader.isModLoaded("moderncontrolling_toolkit");
    }
}
