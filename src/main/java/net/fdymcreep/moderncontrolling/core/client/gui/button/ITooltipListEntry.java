package net.fdymcreep.moderncontrolling.core.client.gui.button;

import net.minecraft.client.gui.GuiListExtended;

public interface ITooltipListEntry extends GuiListExtended.IGuiListEntry {
    void drawEntryForeground(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks);
}
