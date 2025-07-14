package net.fdymcreep.moderncontrolling.keybind.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class KeybindingsFile {
    public static class Bean {
        public Map<String, Integer> keybindingsMap;
        public Map<String, String> keyModifiersMap;
    }

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    protected Bean content;
    protected Minecraft mc;
    protected File file;
    public final String name;

    public KeybindingsFile(String name) throws JsonSyntaxException, IOException, JsonIOException {
        this.name = name;
        this.mc = Minecraft.getMinecraft();
        this.file = new File(
                new File(
                        this.mc.mcDataDir,
                        "keybindings"
                ),
                name
        );
        if (!this.file.exists()) {
            if (!this.file.getParentFile().exists()) {
                this.file.getParentFile().mkdirs();
            }
            this.file.createNewFile();
            try (FileWriter writer = new FileWriter(this.file)) {
                writer.write("{}");
            }
        }
        this.content = GSON.fromJson(new BufferedReader(new FileReader(this.file)), Bean.class);
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

    public Bean getContent() {
        return this.content;
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

    public void rereadContent() throws FileNotFoundException, JsonSyntaxException, JsonIOException {
        this.content = GSON.fromJson(new BufferedReader(new FileReader(this.file)), Bean.class);
    }

    public void saveContent() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file))) {
            String json = GSON.toJson(this.content);
            writer.write(json);
        }
    }
}
