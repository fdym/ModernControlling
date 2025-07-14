package net.fdymcreep.moderncontrolling.core.client.gui.button;

public interface ITooltipList {
    void drawForeground(int mouseXIn, int mouseYIn, float partialTicks);

    void drawSlotForeground(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks);
}
