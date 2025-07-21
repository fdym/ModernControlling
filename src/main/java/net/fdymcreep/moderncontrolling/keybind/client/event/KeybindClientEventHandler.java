package net.fdymcreep.moderncontrolling.keybind.client.event;

import akka.japi.Pair;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.NewControlScreen;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.keybind.client.gui.screen.NewKeybindScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ControllingKeybind.MODID)
public class KeybindClientEventHandler {
    private final GuiButton KeybindScreenEntryButton;

    public KeybindClientEventHandler() {
        this.KeybindScreenEntryButton = new GuiButton(0, 0, 0, 150, 20, I18n.format("gui." + ControllingKeybind.MODID + ".newKeybindScreenEntryButton"));
    }

    @SubscribeEvent
    public void onGuiPreInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        if ((event.getGui() instanceof NewControlScreen) && (((NewControlScreen) event.getGui()).buttons.stream().noneMatch(pair -> pair.first().equals(this.KeybindScreenEntryButton)))) {
            ((NewControlScreen) event.getGui()).buttons.add(
                    new Pair<>(this.KeybindScreenEntryButton, 50)
            );
        }
    }

    @SubscribeEvent
    public void onActionPreformed(GuiScreenEvent.ActionPerformedEvent event) {
        if ((event.getButton().equals(this.KeybindScreenEntryButton)) && (event.getGui() instanceof NewControlScreen)) {
            ((NewControlScreen) event.getGui()).options.saveOptions();
            event.getGui().mc.displayGuiScreen(new NewKeybindScreen(event.getGui(), event.getGui().mc.gameSettings));
        }
    }
}
