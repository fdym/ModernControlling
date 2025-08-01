package net.fdymcreep.moderncontrolling.toolkit.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybinding;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static net.fdymcreep.moderncontrolling.toolkit.ControllingToolkit.LOGGER;
import static net.minecraft.client.settings.GameSettings.COLON_SPLITTER;

@Mixin(GameSettings.class)
public abstract class GameSettingsMixin {
    @Shadow
    private File optionsFile;

    @Shadow
    protected abstract NBTTagCompound dataFix(NBTTagCompound p_189988_1_);

    @Shadow
    public KeyBinding[] keyBindings;

    @WrapMethod(method = "loadOptions")
    private void wrap$loadOptions(Operation<Void> original) {
        original.call();
        if (!ControllingToolkitConfig.enableCloneKeys) return;
        FileInputStream fileInputStream = null;
        try {
            if (!this.optionsFile.exists()) return;
            List<String> lines = IOUtils.readLines(fileInputStream = new FileInputStream(this.optionsFile), StandardCharsets.UTF_8);
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            for (String s : lines) {
                try {
                    Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                    nbttagcompound.setString(iterator.next(), iterator.next());
                } catch (Exception var10) {
                    LOGGER.warn("Skipping bad option: {}", s);
                }
            }
            nbttagcompound = this.dataFix(nbttagcompound);
            for (String k : nbttagcompound.getKeySet()) {
                String v = nbttagcompound.getString(k);
                try {
                    if (k.startsWith("clonekey$")) {
                        String originDesc = k.split("\\$")[1];
                        int cloneID = Integer.parseInt(k.split("\\$")[2]);
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
                            List<KeyBinding> matches2 = Arrays.stream(this.keyBindings)
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
                } catch (Exception e) {
                    LOGGER.warn("Skipping bad option: {}:{}", k, v);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load options", e);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    @WrapMethod(method = "saveOptions")
    private void wrap$saveOptions(Operation<Void> original) {
        original.call();
        if (!ControllingToolkitConfig.enableCloneKeys) return;
        if (net.minecraftforge.fml.client.FMLClientHandler.instance().isLoading()) return;
        PrintWriter printwriter = null;
        try {
            printwriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(this.optionsFile.toPath(), StandardOpenOption.APPEND), StandardCharsets.UTF_8));
            for (CloneKeybinding cloneKeybinding : CloneKeybinding.CLONE_KEYBINDINGS) {
                StringBuilder builder = new StringBuilder("clonekey$");
                builder.append(cloneKeybinding.origin.getKeyDescription());
                builder.append("$");
                builder.append(cloneKeybinding.cloneID);
                builder.append(":");
                builder.append(cloneKeybinding.getKeyCode());
                if (cloneKeybinding.getKeyModifier() != KeyModifier.NONE) {
                    builder.append(":");
                    builder.append(cloneKeybinding.getKeyModifier());
                }
                printwriter.println(builder);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to save options", e);
        } finally {
            IOUtils.closeQuietly(printwriter);
        }
    }
}
