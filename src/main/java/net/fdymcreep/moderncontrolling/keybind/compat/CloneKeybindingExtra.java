package net.fdymcreep.moderncontrolling.keybind.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fdymcreep.moderncontrolling.keybind.util.KeyBindingsFileExtra;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyModifier;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.fdymcreep.moderncontrolling.toolkit.ControllingToolkit.LOGGER;

public class CloneKeybindingExtra extends KeyBindingsFileExtra {
    private final GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;

    @Nonnull
    public static String staticGetName() {
        return "CloneKeybinding";
    }

    @Nonnull
    @Override
    public String getName() {
        return "CloneKeybinding";
    }

    @Nonnull
    @Override
    public JsonObject sync(JsonObject origin) {
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : origin.entrySet()) {
            result.add(entry.getKey(), entry.getValue());
        }
        if (ToolkitCompat.enableCloneKeys()) {
            for (CloneKeybinding cloneKeybinding : CloneKeybinding.CLONE_KEYBINDINGS) {
                String v;
                if (cloneKeybinding.getKeyModifier() == KeyModifier.NONE) {
                    v = String.valueOf(cloneKeybinding.getKeyCode());
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append(cloneKeybinding.getKeyCode());
                    builder.append(":");
                    builder.append(cloneKeybinding.getKeyModifier().toString());
                    v = builder.toString();
                }
                result.add(
                        cloneKeybinding.origin.getKeyDescription() + "$" + cloneKeybinding.cloneID,
                        new JsonPrimitive(v)
                );
            }
        }
        return result;
    }

    @Override
    public void override(@Nonnull JsonObject jsonObject) {
        if (ToolkitCompat.enableCloneKeys()) {
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (entry.getValue() instanceof JsonPrimitive && ((JsonPrimitive) entry.getValue()).isString()) {
                    String k = entry.getKey();
                    String v = entry.getValue().getAsString();
                    String originDesc = k.split("\\$")[0];
                    int cloneID = Integer.parseInt(k.split("\\$")[1]);
                    int keyCode;
                    KeyModifier keyModifier = KeyModifier.NONE;
                    if (v.contains(":")) {
                        keyCode = Integer.parseInt(v.split(":")[0]);
                        keyModifier = KeyModifier.valueFromString(v.split(":")[1]);
                    } else keyCode = Integer.parseInt(v);
                    List<CloneKeybinding> matches = CloneKeybinding.CLONE_KEYBINDINGS
                            .stream()
                            .filter(c -> c.cloneID == cloneID)
                            .collect(Collectors.toList());
                    if (!matches.isEmpty()) {
                        matches.get(0).setKeyModifier(keyModifier).setKeyCode(keyCode);
                    } else {
                        List<KeyBinding> matches2 = Arrays.stream(this.gameSettings.keyBindings)
                                .filter(kb -> kb.getKeyDescription().equals(originDesc))
                                .collect(Collectors.toList());
                        if (!matches2.isEmpty()) {
                            CloneKeybinding cloneKeybinding = new CloneKeybinding(
                                    matches2.get(0), cloneID
                            );
                            cloneKeybinding.setKeyModifier(keyModifier).setKeyCode(keyCode);
                        } else {
                            LOGGER.warn("Could not find {}", originDesc);
                        }
                    }
                }
            }
        }
    }
}
