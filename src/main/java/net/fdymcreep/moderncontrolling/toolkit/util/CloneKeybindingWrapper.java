package net.fdymcreep.moderncontrolling.toolkit.util;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;

public class CloneKeybindingWrapper extends KeyBinding {
    public final CloneKeybinding instance;

    public CloneKeybindingWrapper(CloneKeybinding instance) {
        super(
                "B6BAC432-C07A-3ADF-F644-8051B3797336",
                0,
                "CEFA4EDE-F5C1-F0E5-FD91-774C40F88DFF"
        ); // Processed in KeyBindingMixin and KeyBindingMapMixin.
        this.instance = instance;
    }

    @Override
    public boolean isKeyDown() {
        return instance.isKeyDown();
    }

    @Override
    public boolean isPressed() {
        return instance.isPressed();
    }

    @Override
    public int getKeyCode() {
        return instance.getKeyCode();
    }

    @Override
    public int getKeyCodeDefault() {
        return 0;
    }

    @Nonnull
    @Override
    public KeyModifier getKeyModifier() {
        return instance.getKeyModifier();
    }

    @Nonnull
    @Override
    public KeyModifier getKeyModifierDefault() {
        return KeyModifier.NONE;
    }

    @Override
    public void setKeyCode(int keyCode) {
        instance.setKeyCode(keyCode);
    }

    @Override
    public void setKeyModifierAndCode(@Nonnull KeyModifier keyModifier, int keyCode) {
        instance.setKeyModifier(keyModifier).setKeyCode(keyCode);
    }

    @Override
    public void setToDefault() {
        instance.setKeyModifier(KeyModifier.NONE).setKeyCode(0);
    }

    @Override
    public boolean isActiveAndMatches(int keyCode) {
        return instance.isActiveAndMatches(keyCode);
    }

    @Override
    public boolean isSetToDefaultValue() {
        return instance.getKeyCode() == 0 && instance.getKeyModifier().equals(KeyModifier.NONE);
    }

    @Nonnull
    @Override
    public IKeyConflictContext getKeyConflictContext() {
        return instance.origin.getKeyConflictContext();
    }

    @Override
    public void setKeyConflictContext(@Nonnull IKeyConflictContext keyConflictContext) {
        instance.origin.setKeyConflictContext(keyConflictContext);
    }

    @Nonnull
    @Override
    public String getKeyDescription() {
        return "->";
    }

    @Nonnull
    @Override
    public String getKeyCategory() {
        return instance.origin.getKeyCategory();
    }

    @Nonnull
    @Override
    public String getDisplayName() {
        return instance.getKeyModifier().getLocalizedComboName(instance.getKeyCode());
    }
}
