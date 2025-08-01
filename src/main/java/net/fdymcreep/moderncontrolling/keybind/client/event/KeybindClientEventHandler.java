package net.fdymcreep.moderncontrolling.keybind.client.event;

import akka.japi.Pair;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.NewControlScreen;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.SettingsScreen;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.keybind.client.gui.screen.NewKeybindList;
import net.fdymcreep.moderncontrolling.keybind.client.gui.screen.NewKeybindScreen;
import net.fdymcreep.moderncontrolling.keybind.compat.MoCKCompatCheck;
import net.fdymcreep.moderncontrolling.keybind.compat.ToolkitCompat;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybindingWrapper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeybindClientEventHandler {
    private final GuiButton keybindScreenEntryButton;
    private final GuiButton cloneKeybindingButton;

    public KeybindClientEventHandler() {
        this.keybindScreenEntryButton = new GuiButton(0, 0, 0, 150, 20, I18n.format("gui.keyboard.dot"));
        this.cloneKeybindingButton = new GuiButton(0, 0, 0, 150, 20, "");
    }

    @SubscribeEvent
    public void onGuiPreInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        if ((event.getGui() instanceof NewControlScreen)
                && (((NewControlScreen) event.getGui()).buttons.stream().noneMatch(pair -> pair.first().equals(this.keybindScreenEntryButton)))
        ) {
            ((NewControlScreen) event.getGui()).buttons.add(
                    new Pair<>(this.keybindScreenEntryButton, 50)
            );
        } else if ((event.getGui() instanceof SettingsScreen)
                && MoCKCompatCheck.moCTCanCompat()
                && ToolkitCompat.enableCloneKeys()
                && (((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting"))
                && (((SettingsScreen) event.getGui()).options.stream().noneMatch(pair -> pair.first().equals(this.cloneKeybindingButton)))
        ) {
            if (((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting.normal")) {
                this.cloneKeybindingButton.displayString = I18n.format(
                        "gui." + ToolkitCompat.getMoCTModID() + ".settings.createACloneKeybinding"
                );
            } else if (((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting.clone")) {
                this.cloneKeybindingButton.displayString = I18n.format(
                        "gui." + ToolkitCompat.getMoCTModID() + ".settings.removeACloneKeybinding"
                );
            }
            ((SettingsScreen) event.getGui()).options.add(
                    new Pair<>(this.cloneKeybindingButton, 50)
            );
        }
    }

    @SubscribeEvent
    public void onActionPreformed(GuiScreenEvent.ActionPerformedEvent event) {
        if ((event.getButton().equals(this.keybindScreenEntryButton))
                && (event.getGui() instanceof NewControlScreen)
        ) {
            ((NewControlScreen) event.getGui()).options.saveOptions();
            event.getGui().mc.displayGuiScreen(new NewKeybindScreen(event.getGui(), event.getGui().mc.gameSettings));
        } else if ((event.getGui() instanceof SettingsScreen)
                && (((SettingsScreen) event.getGui()).parentListEntry instanceof NewKeybindList.KeybindingEntry)
                && MoCKCompatCheck.moCTCanCompat()
                && ToolkitCompat.enableCloneKeys()
                && (((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting"))
                && (event.getButton().equals(this.cloneKeybindingButton))
        ) {
            if (((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting.normal")) {
                ToolkitCompat.createCloneKey(
                        ((NewKeybindList.KeybindingEntry) (
                                (SettingsScreen) event.getGui()
                        ).parentListEntry).keybinding
                );
                event.getGui().mc.displayGuiScreen(((SettingsScreen) event.getGui()).parent);
            } else if (((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting.clone")) {
                ToolkitCompat.removeCloneKey((CloneKeybindingWrapper) (
                        (NewKeybindList.KeybindingEntry) (
                                (SettingsScreen) event.getGui()
                        ).parentListEntry).keybinding
                );
                event.getGui().mc.displayGuiScreen(((SettingsScreen) event.getGui()).parent);
            }
        }
    }
}
