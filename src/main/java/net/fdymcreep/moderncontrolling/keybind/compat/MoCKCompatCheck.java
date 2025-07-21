package net.fdymcreep.moderncontrolling.keybind.compat;

import net.minecraftforge.fml.common.Loader;

public class MoCKCompatCheck {
    public static boolean moCTCanCampat() {
        return Loader.isModLoaded("moderncontrolling_toolkit");
    }
}
