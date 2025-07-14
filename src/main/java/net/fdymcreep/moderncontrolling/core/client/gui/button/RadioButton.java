package net.fdymcreep.moderncontrolling.core.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;

public class RadioButton extends GuiButton {
    private String originButtonText;
    protected RadioButton.Group group;
    protected boolean pressedFlag = false;
    public final int index;

    public RadioButton(int buttonId, int x, int y, String buttonText, int index, RadioButton.Group group) {
        super(buttonId, x, y, (group.getIndex() == index ? "[X] " : "[ ] ") + buttonText);
        this.originButtonText = buttonText;
        this.index = index;
        this.group = group;
    }

    public RadioButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int index, RadioButton.Group group) {
        super(buttonId, x, y, widthIn, heightIn, (group.getIndex() == index ? "[X] " : "[ ] ") + buttonText);
        this.originButtonText = buttonText;
        this.index = index;
        this.group = group;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int color = this.enabled ? (this.hovered ? 0xFF00FFFF : 0xFF0000FF) : 0xFF777777;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        drawRect(
                this.x,
                this.y,
                this.x + this.width,
                this.y + 1,
                color
        );
        drawRect(
                this.x,
                this.y + this.height - 1,
                this.x + this.width,
                this.y + this.height,
                color
        );
        drawRect(
                this.x,
                this.y,
                this.x + 1,
                this.y + this.height,
                color
        );
        drawRect(
                this.x + this.width - 1,
                this.y,
                this.x + this.width,
                this.y + this.height,
                color
        );
        this.mouseDragged(mc, mouseX, mouseY);

        this.drawString(mc.fontRenderer, this.displayString, this.x + 5, this.y + (this.height - 8) / 2, color);
    }

    @Override
    public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY) && !this.pressedFlag) {
            this.group.setIndex(index);
            this.displayString = "[X] " + this.originButtonText;
            this.pressedFlag = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.pressedFlag = false;
    }

    protected void onIndexUpdate() {
        this.displayString = "[ ] " + this.originButtonText;
    }

    public static class Group extends ArrayList<RadioButton> {
        private int index;

        public Group(int defaultIndex) {
            super();
            this.index = defaultIndex;
        }

        public Group(Collection<? extends RadioButton> collection, int defaultIndex) {
            super(collection);
            this.index = defaultIndex;
        }

        public Group(int initialCapacity, int defaultIndex) {
            super(initialCapacity);
            this.index = defaultIndex;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            for (RadioButton button : this) {
                if (button.index != index) {
                    button.onIndexUpdate();
                }
            }
            this.index = index;
        }
    }
}
