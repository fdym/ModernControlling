package net.fdymcreep.moderncontrolling.toolkit.compat;

import net.minecraftforge.fml.common.Loader;

public class MoCTCompatCheck {
    public static boolean moCKCanCampat() {
        return Loader.isModLoaded("moderncontrolling_keybind");
    }
}
