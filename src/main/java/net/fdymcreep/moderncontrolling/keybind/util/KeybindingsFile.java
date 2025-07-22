package net.fdymcreep.moderncontrolling.keybind.util;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fdymcreep.moderncontrolling.core.util.SettingsFile;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KeybindingsFile extends SettingsFile<KeybindingsFile.Bean> {
    public static class Bean {
        public Map<String, Integer> keybindingsMap;
        public Map<String, String> keyModifiersMap;
    }

    public KeybindingsFile(String name) throws JsonSyntaxException, IOException, JsonIOException {
        super(name, Bean.class);
        if (this.content.keybindingsMap == null) {
            this.content.keybindingsMap = new HashMap<>();
            for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
                this.content.keybindingsMap.put(keyBinding.getKeyDescription(), keyBinding.getKeyCode());
            }
        }
        if (this.content.keyModifiersMap == null) {
            this.content.keyModifiersMap = new HashMap<>();
            for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
                this.content.keyModifiersMap.put(keyBinding.getKeyDescription(), keyBinding.getKeyModifier().name());
            }
        }
    }

    public KeybindingsFile(String name, File file) throws JsonSyntaxException, IOException, JsonIOException {
        super(name, Bean.class, file);
        if (this.content.keybindingsMap == null) {
            this.content.keybindingsMap = new HashMap<>();
            for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
                this.content.keybindingsMap.put(keyBinding.getKeyDescription(), keyBinding.getKeyCode());
            }
        }
        if (this.content.keyModifiersMap == null) {
            this.content.keyModifiersMap = new HashMap<>();
            for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
                this.content.keyModifiersMap.put(keyBinding.getKeyDescription(), keyBinding.getKeyModifier().name());
            }
        }
    }

    public void syncFromKeybinding() {
        this.content.keybindingsMap.clear();
        this.content.keyModifiersMap.clear();
        for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
            this.content.keybindingsMap.put(keyBinding.getKeyDescription(), keyBinding.getKeyCode());
            this.content.keyModifiersMap.put(keyBinding.getKeyDescription(), keyBinding.getKeyModifier().name());
        }
    }

    public void overrideKeybindings() {
        for (String s : this.content.keybindingsMap.keySet()) {
            for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
                if (s.equals(keyBinding.getKeyDescription())) {
                    keyBinding.setKeyModifierAndCode(
                            KeyModifier.valueFromString(this.content.keyModifiersMap.get(s)),
                            this.content.keybindingsMap.get(s)
                    );
                    break;
                }
            }
        }
    }
}
