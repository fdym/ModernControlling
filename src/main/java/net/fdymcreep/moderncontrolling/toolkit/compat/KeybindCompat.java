package net.fdymcreep.moderncontrolling.toolkit.compat;

import net.fdymcreep.moderncontrolling.keybind.client.gui.screen.NewKeybindList;
import net.minecraft.client.gui.GuiListExtended;

public class KeybindCompat {
    public static NewKeybindList.KeyEntry getKeyEntry(GuiListExtended.IGuiListEntry entry) {
        return (NewKeybindList.KeyEntry) entry;
    }
}
