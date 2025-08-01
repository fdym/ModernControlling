package net.fdymcreep.moderncontrolling.keybind.compat;

import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkit;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybinding;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybindingWrapper;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class ToolkitCompat {
    private ToolkitCompat() {}

    public static boolean enableCloneKeys() {
        return ControllingToolkitConfig.enableCloneKeys;
    }

    public static List<KeyBinding> insertCloneKeys(List<KeyBinding> origin) {
        List<KeyBinding> result = new ArrayList<>();
        for (KeyBinding keyBinding : origin) {
            result.add(keyBinding);
            if (!(keyBinding instanceof CloneKeybindingWrapper)) {
                result.addAll(((IKeyBinding) keyBinding).getCloneWrappers());
            }
        }
        return result;
    }

    public static boolean isClone(KeyBinding keyBinding) {
        return keyBinding instanceof CloneKeybindingWrapper;
    }

    public static String getMoCTModID() {
        return ControllingToolkit.MODID;
    }

    public static void createCloneKey(KeyBinding origin) {
        CloneKeybinding cloneKeybinding = new CloneKeybinding(origin);
    }

    public static void removeCloneKey(CloneKeybindingWrapper wrapper) {
        CloneKeybinding.removeCloneKey(wrapper);
    }

    public static KeyBinding getOrigin(KeyBinding keyBinding) {
        if (isClone(keyBinding)) {
            return ((CloneKeybindingWrapper) keyBinding).instance.origin;
        } else return keyBinding;
    }

    public static CloneKeybindingWrapper toWrapper(KeyBinding keyBinding) throws ClassCastException {
        return (CloneKeybindingWrapper) keyBinding;
    }
}
