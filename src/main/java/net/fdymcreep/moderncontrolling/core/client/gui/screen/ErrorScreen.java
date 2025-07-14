package net.fdymcreep.moderncontrolling.core.client.gui.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class ErrorScreen extends GuiScreen {
    protected GuiScreen parent;
    protected GuiButton errorReturnButton;
    public static final int MESSAGE_WIDTH = 200;

    public ErrorScreen(GuiScreen parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        errorReturnButton = new GuiButton(
                0, width / 2 - 50, height / 2 + 40, 100, 20,
                I18n.format("gui.back")
        );
        this.buttonList.add(errorReturnButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        List<String> lines = fontRenderer.listFormattedStringToWidth(
                getErrorMessage(), MESSAGE_WIDTH
        );
        int textY = (height / 2) - 40;
        for (String line : lines) {
            fontRenderer.drawString(line, width / 2 - MESSAGE_WIDTH / 2, textY, 0xFFFFFF);
            textY += 10;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.equals(this.errorReturnButton)) {
            onClickButton();
        }
    }

    public void onClickButton() {
        mc.displayGuiScreen(parent);
    }

    public abstract String getErrorMessage();
}

