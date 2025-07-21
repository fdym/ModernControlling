package net.fdymcreep.moderncontrolling.keybind.compat;

import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkit;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IGameSettings;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class ToolkitCompat {
    @Nonnull
    public static String getKeyDesc(KeyBinding keyBinding) {
        return ToolkitCompat.isClone(keyBinding) ? "->" : I18n.format(keyBinding.getKeyDescription());
    }

    public static void insertCloneKeybindings(List<KeyBinding> originList) {
        if (!ControllingToolkitConfig.enableCloneKeybindings) return;
        IGameSettings gameSettings = (IGameSettings) Minecraft.getMinecraft().gameSettings;
        for (KeyBinding keyBinding : gameSettings.getCloneKeybindings()) {
            KeyBinding originKeybinding = ((IKeyBinding) keyBinding).getOrigin();
            if (originList.contains(originKeybinding)) {
                int originIndex = originList.indexOf(originKeybinding);
                List<KeyBinding> targets = gameSettings.getCloneKeybindings()
                        .stream()
                        .filter(kb -> originKeybinding.equals(((IKeyBinding) kb).getOrigin()))
                        .collect(Collectors.toList());
                try {
                    originList.addAll(originIndex + 1, targets);
                } catch (IndexOutOfBoundsException e) {
                    if (originIndex != -1) originList.addAll(targets);
                }
            }
        }
    }

    public static boolean isClone(KeyBinding keyBinding) {
        return ((IKeyBinding) keyBinding).getCloneID() != null;
    }

    public static KeyBinding getOrigin(KeyBinding keyBinding) {
        return ((IKeyBinding) keyBinding).getOrigin();
    }

    public static KeyBinding createNew(KeyBinding origin) {
        KeyBinding keyBinding = new KeyBinding(
                origin.getKeyDescription(),
                origin.getKeyConflictContext(),
                KeyModifier.NONE, 0,
                origin.getKeyCategory()
        );
        ((IKeyBinding) keyBinding).setCloneID(ControllingToolkit.RANDOM.nextInt(1000));
        //noinspection DataFlowIssue
        ((IKeyBinding) keyBinding).setOrigin(origin);
        return keyBinding;
    }

    public static void addCloneKeybinding(GameSettings gameSettings, KeyBinding keyBinding) {
        if (!ControllingToolkitConfig.enableCloneKeybindings) return;
        ((IGameSettings) gameSettings).getCloneKeybindings().add(keyBinding);
    }

    public static void removeCloneKeybinding(GameSettings gameSettings, KeyBinding keyBinding) {
        if (!ControllingToolkitConfig.enableCloneKeybindings) return;
        ((IGameSettings) gameSettings).getCloneKeybindings().remove(keyBinding);
    }
}
