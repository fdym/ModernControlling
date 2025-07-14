package net.fdymcreep.moderncontrolling.core.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class TooltipButton extends GuiButton {
    public String tooltipText;
    public static final int TOOLTIP_MAX_WIDTH = 200;

    public TooltipButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public TooltipButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, null);
    }

    public TooltipButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, @Nullable String tooltipText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.tooltipText = tooltipText;
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        drawTooltip(mouseX, mouseY);
    }

    public void drawTooltip(int mouseX, int mouseY) {
        if (this.visible && this.hovered && (this.tooltipText != null) && (!this.tooltipText.isEmpty())) {
            GuiUtils.drawHoveringText(
                    Arrays.asList(this.tooltipText.split("\n")),
                    mouseX + 5, mouseY,
                    Minecraft.getMinecraft().currentScreen.width,
                    Minecraft.getMinecraft().currentScreen.height,
                    TOOLTIP_MAX_WIDTH,
                    Minecraft.getMinecraft().fontRenderer
            );
        }
    }
}

