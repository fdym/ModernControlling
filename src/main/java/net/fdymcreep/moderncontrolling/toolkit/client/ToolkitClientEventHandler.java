package net.fdymcreep.moderncontrolling.toolkit.client;

import akka.japi.Pair;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.SettingsScreen;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkit;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IGameSettings;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.fdymcreep.moderncontrolling.toolkit.compat.MoCTCompatCheck;
import net.fdymcreep.moderncontrolling.toolkit.compat.KeybindCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = ControllingToolkit.MODID)
public class ToolkitClientEventHandler {
    public static final GuiButton createCloneKeybinding = new GuiButton(0, 0, 0, 150, 20, I18n.format("gui." + ControllingToolkit.MODID + ".settings.createACloneKeybinding"));
    public static final GuiButton removeCloneKeybinding = new GuiButton(0, 0, 0, 150, 20, I18n.format("gui." + ControllingToolkit.MODID + ".settings.removeACloneKeybinding"));
    
    
    @SubscribeEvent
    public static void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (!ControllingToolkitConfig.enableCloneKeybindings) return;
        GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
        for (KeyBinding keyBinding : ((IGameSettings) gameSettings).getCloneKeybindings()) {
            if (keyBinding.isPressed()
                    && ((IKeyBinding) keyBinding).getOrigin().getKeyCode() == keyBinding.getKeyCode()
            ) {
                ((IKeyBinding) ((IKeyBinding) keyBinding).getOrigin()).press();
            }
        }
    }

    @SubscribeEvent
    public static void onGuiPreInit(GuiScreenEvent.InitGuiEvent.Pre event) {
        if (!ControllingToolkitConfig.enableCloneKeybindings) return;
        if (event.getGui() instanceof SettingsScreen) {
            SettingsScreen gui = (SettingsScreen) event.getGui();
            if ((gui.domain.startsWith("keybindingSetting.general")) && (gui.options.stream().noneMatch(pair -> pair.first().equals(createCloneKeybinding)))) {
                gui.options.add(new Pair<>(createCloneKeybinding, 50));
            } else if (gui.domain.startsWith("keybindingSetting.clone")) {
                gui.options.add(new Pair<>(removeCloneKeybinding, 50));
            }
        }
    }

    @SubscribeEvent
    public static void onActionPreformed(GuiScreenEvent.ActionPerformedEvent event) {
        if (event.getButton().equals(createCloneKeybinding)) {
            if (event.getGui() instanceof SettingsScreen
                    && MoCTCompatCheck.moCKCanCampat()
                    && ((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting")
                    && ((SettingsScreen) event.getGui()).parentListEntry != null
            ) {
                KeyBinding origin = KeybindCompat.getKeyEntry(((SettingsScreen) event.getGui()).parentListEntry).keybinding;
                KeyBinding keyBinding = new KeyBinding(
                        origin.getKeyDescription(),
                        origin.getKeyConflictContext(),
                        KeyModifier.NONE,
                        0, origin.getKeyCategory()
                );
                ((IKeyBinding) keyBinding).setCloneID(ControllingToolkit.RANDOM.nextInt(1000));
                //noinspection DataFlowIssue
                ((IKeyBinding) keyBinding).setOrigin(origin);
                ((IGameSettings) event.getGui().mc.gameSettings).getCloneKeybindings().add(keyBinding);
                KeybindCompat.getKeyEntry(((SettingsScreen) event.getGui()).parentListEntry).parentList.controlsScreen.search();
            }
        } else if (event.getButton().equals(removeCloneKeybinding)) {
            if (event.getGui() instanceof SettingsScreen
                    && MoCTCompatCheck.moCKCanCampat()
                    && ((SettingsScreen) event.getGui()).domain.startsWith("keybindingSetting")
                    && ((SettingsScreen) event.getGui()).parentListEntry != null
            ) {
                KeyBinding keybinding =  KeybindCompat.getKeyEntry(((SettingsScreen) event.getGui()).parentListEntry).keybinding;
                ((IGameSettings) event.getGui().mc.gameSettings).getCloneKeybindings().remove(keybinding);
                event.getGui().mc.displayGuiScreen(((SettingsScreen) event.getGui()).parent);
            }
        }
    }
}
