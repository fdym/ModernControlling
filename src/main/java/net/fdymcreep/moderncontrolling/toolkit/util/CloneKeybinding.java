package net.fdymcreep.moderncontrolling.toolkit.util;

import com.google.common.collect.Sets;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBindingMap;
import net.fdymcreep.moderncontrolling.toolkit.client.event.PostRemoveCloneKeybindingEvent;
import net.fdymcreep.moderncontrolling.toolkit.mixin.accessor.KeyBindingAccessor;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CloneKeybinding {
    public static final List<CloneKeybinding> CLONE_KEYBINDINGS = new ArrayList<>();
    public static final Map<CloneKeybinding, CloneKeybindingWrapper> CLONE_KEYBINDING_WRAPPERS = new HashMap<>();
    public static final Random RANDOM = new Random();
    protected int pressTime;
    protected boolean pressed;
    protected int keyCode = 0;
    protected KeyModifier keyModifier = KeyModifier.NONE;
    public final int cloneID;
    public final KeyBinding origin;
    public final CloneKeybindingWrapper wrapper;

    @Nullable
    public static CloneKeybinding lookupModifierAndKeyCode(int keyCode) {
        KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            CloneKeybinding binding = getBinding(keyCode, activeModifier);
            if (binding != null) {
                return binding;
            }
        }
        return getBinding(keyCode, KeyModifier.NONE);
    }

    public static Set<CloneKeybinding> lookupActives(int keyCode) {
        if (!ControllingToolkitConfig.enableNcKeys)
            return Sets.newHashSet(lookupModifierAndKeyCode(keyCode));
        final KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            Set<CloneKeybinding> bindings = getBindings(keyCode, activeModifier);
            if (!bindings.isEmpty()) return bindings;
        }
        return getBindings(keyCode, KeyModifier.NONE);
    }

    @Nullable
    private static CloneKeybinding getBinding(int keyCode, KeyModifier keyModifier) {
        Collection<CloneKeybinding> bindings = CLONE_KEYBINDINGS.stream()
                .filter(c -> c.getKeyModifier() == keyModifier && c.getKeyCode() == keyCode)
                .collect(Collectors.toList());
        for (CloneKeybinding cloneKeybinding : bindings)
            if (cloneKeybinding.isActiveAndMatches(keyCode)) return cloneKeybinding;
        return null;
    }

    public static Set<CloneKeybinding> getBindings(int keyCode, KeyModifier keyModifier) {
        if (!ControllingToolkitConfig.enableNcKeys)
            return Sets.newHashSet(getBinding(keyCode, keyModifier));
        Collection<CloneKeybinding> bindings = CLONE_KEYBINDINGS.stream()
                .filter(c -> c.getKeyModifier() == keyModifier && c.getKeyCode() == keyCode)
                .collect(Collectors.toList());
        Set<CloneKeybinding> result = Sets.newHashSet();
        for (CloneKeybinding binding : bindings)
            if (binding.isActiveAndMatches(keyCode))
                result.add(binding);
        return result;
    }

    public static void onTick(int keyCode) {
        if (keyCode != 0 && ControllingToolkitConfig.enableNcKeys && (
                ArrayUtils.contains(ControllingToolkitConfig.nonConflictKeyCodes, keyCode)
                || ControllingToolkitConfig.enableAKiNC
        )) {
            CloneKeybinding.lookupActives(keyCode).forEach(CloneKeybinding::press);
            ((IKeyBindingMap) KeyBindingAccessor.getHASH()).lookupActives(keyCode).forEach(k -> ((IKeyBinding) k).press());
        } else {
            if (keyCode != 0) {
                CloneKeybinding keybinding = lookupModifierAndKeyCode(keyCode);
                if (keybinding != null) keybinding.pressTime++;
            }
        }
    }

    public static void setKeyBindState(int keyCode, boolean pressed) {
        if (keyCode != 0)
            for (CloneKeybinding keybinding : CLONE_KEYBINDINGS.stream()
                    .filter(c -> c.getKeyCode() == keyCode)
                    .collect(Collectors.toList())
            ) keybinding.pressed = pressed;
    }

    public static void updateKeyBindState() {
        for (CloneKeybinding cloneKeybinding : CLONE_KEYBINDINGS) {
            try {
                setKeyBindState(cloneKeybinding.keyCode, cloneKeybinding.keyCode < 256 && Keyboard.isKeyDown(cloneKeybinding.keyCode));
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }

    public static void unpressAllKey() {
        for (CloneKeybinding cloneKeybinding : CLONE_KEYBINDINGS) {
            cloneKeybinding.unpressKey();
        }
    }

    public static void removeCloneKey(CloneKeybindingWrapper wrapper) {
        CloneKeybinding.CLONE_KEYBINDING_WRAPPERS.remove(wrapper.instance);
        CloneKeybinding.CLONE_KEYBINDINGS.remove(wrapper.instance);
        ((IKeyBinding) wrapper.instance.origin).getCloneWrappers().remove(wrapper);
        ((IKeyBinding) wrapper.instance.origin).getClones().remove(wrapper.instance);
        MinecraftForge.EVENT_BUS.post(new PostRemoveCloneKeybindingEvent(wrapper));
    }

    public CloneKeybinding(KeyBinding origin) {
        this(origin, RANDOM.nextInt(1000));
    }

    public CloneKeybinding(KeyBinding origin, int cloneID) {
        while (origin instanceof CloneKeybindingWrapper) {
            origin = ((CloneKeybindingWrapper) origin).instance.origin;
        }
        this.origin = origin;
        this.cloneID = cloneID;
        this.wrapper = new CloneKeybindingWrapper(this);
        CLONE_KEYBINDINGS.add(this);
        CLONE_KEYBINDING_WRAPPERS.put(this, this.wrapper);
        ((IKeyBinding) this.origin).getClones().add(this);
        ((IKeyBinding) this.origin).getCloneWrappers().add(this.wrapper);
    }

    public int getKeyCode() {
        return keyCode;
    }

    @SuppressWarnings("UnusedReturnValue")
    public CloneKeybinding setKeyCode(int keyCode) {
        this.keyCode = keyCode;
        return this;
    }

    public KeyModifier getKeyModifier() {
        return keyModifier;
    }

    @SuppressWarnings("UnusedReturnValue")
    public CloneKeybinding setKeyModifier(KeyModifier keyModifier) {
        if (keyModifier.matches(this.keyCode)) {
            keyModifier = KeyModifier.NONE;
        }
        this.keyModifier = keyModifier;
        return this;
    }

    public boolean isActiveAndMatches(int keyCode) {
        return keyCode != 0
                && keyCode == this.getKeyCode()
                && this.origin.getKeyConflictContext().isActive()
                && this.getKeyModifier().isActive(this.origin.getKeyConflictContext());
    }

    protected void unpressKey() {
        this.pressTime = 0;
        this.pressed = false;
    }

    public boolean isKeyDown() {
        return this.pressed
                && this.origin.getKeyConflictContext().isActive()
                && getKeyModifier().isActive(this.origin.getKeyConflictContext());
    }

    public boolean isPressed() {
        if (this.pressTime == 0) {
            return false;
        } else {
            --this.pressTime;
            return true;
        }
    }

    public void press() {this.pressTime++;}
}
