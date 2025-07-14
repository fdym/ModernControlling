package net.fdymcreep.moderncontrolling.core.client.gui.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ImageButton extends GuiButton {
    public int u;
    public int v;
    public int tileWidth;
    public int tileHeigth;
    public final ResourceLocation resourceLocation;

    public ImageButton(
            int buttonId,
            int x, int y,
            int widthIn,
            int heightIn,
            int u, int v,
            int tileWidth,
            int tileHeigth,
            ResourceLocation resourceLocation
    ) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.u = u;
        this.v = v;
        this.tileWidth = tileWidth;
        this.tileHeigth = tileHeigth;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(this.resourceLocation);
//            GlStateManager.disableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (this.enabled) {
                if (this.hovered) {
                    this.drawTexturedModalRect(this.x, this.y, this.u, this.v + this.tileHeigth * 2, this.width, this.height);
                } else {
                    this.drawTexturedModalRect(this.x, this.y, this.u, this.v + this.tileHeigth, this.width, this.height);
                }
            } else {
                this.drawTexturedModalRect(this.x, this.y, this.u, this.v, this.width, this.height);
            }
//            GlStateManager.enableDepth();
        }
    }
}
