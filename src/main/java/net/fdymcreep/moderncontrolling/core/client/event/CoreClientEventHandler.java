package net.fdymcreep.moderncontrolling.core.client.event;

import akka.japi.Pair;
import net.fdymcreep.moderncontrolling.core.ControllingCore;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.NewControlScreen;
import net.fdymcreep.moderncontrolling.core.compat.MoCoreCompatCheck;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ControllingCore.MODID)
public class CoreClientEventHandler {
    private GuiButton controllScreenEntryButton;
    private GuiButton keybindScreenEntryButton;

    @SubscribeEvent
    public void onGuiPreInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (event.getGui() instanceof NewControlScreen) {
            this.keybindScreenEntryButton = new GuiButton(0, 0, 0, 150, 20, I18n.format("gui.keyboard.dot"));
            if (!MoCoreCompatCheck.moCKCanCompat())
                ((NewControlScreen) event.getGui()).buttons.add(new Pair<>(this.keybindScreenEntryButton, 50));
        }
    }

    @SubscribeEvent
    public void onGuiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiOptions) {
            this.controllScreenEntryButton = new GuiButton(0, event.getGui().width / 2 + 5, event.getGui().height / 6 + 72 - 6, 150, 20, I18n.format("options.controls"));
            event.getButtonList().add(this.controllScreenEntryButton);
        }
    }

    @SubscribeEvent
    public void onActionPreformed(GuiScreenEvent.ActionPerformedEvent event) {
        if (event.getButton().equals(this.controllScreenEntryButton) && (event.getGui() instanceof GuiOptions)) {
            event.getGui().mc.displayGuiScreen(new NewControlScreen(event.getGui(), event.getGui().mc.gameSettings));
        } else if (!MoCoreCompatCheck.moCKCanCompat()
                && event.getGui() instanceof NewControlScreen
                && event.getButton().equals(this.keybindScreenEntryButton)
        ) {
            event.getGui().mc.displayGuiScreen(new GuiControls(event.getGui(), event.getGui().mc.gameSettings));
        }
    }
}
