package net.fdymcreep.moderncontrolling.toolkit.mixin;

import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IGameSettings;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.settings.KeyModifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(GameSettings.class)
public abstract class GameSettingsMixin implements IGameSettings {
    @Unique
    private final List<KeyBinding> cloneKeybindings = new ArrayList<>();

    @Final
    @Shadow
    private static Logger LOGGER;

    @Shadow
    private File optionsFile;

    @Shadow
    public KeyBinding[] keyBindings;

    @Shadow
    protected abstract NBTTagCompound dataFix(NBTTagCompound nbtTagCompound);

    @Override
    public List<KeyBinding> getCloneKeybindings() {
        return this.cloneKeybindings;
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(
            method = "loadOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/io/IOUtils;closeQuietly(Ljava/io/InputStream;)V",
                    shift = At.Shift.AFTER,
                    remap = false
            ),
            cancellable = true
    )
    private void inject$loadOptions(CallbackInfo ci) {
        BufferedInputStream inputStream = null;
        if (ControllingToolkitConfig.enableCloneKeybindings) {
            try {
                if (!this.optionsFile.exists()) ci.cancel();
                List<String> list = IOUtils.readLines(inputStream = new BufferedInputStream(Files.newInputStream(this.optionsFile.toPath())), StandardCharsets.UTF_8); // Forge: fix MC-117449, MC-151173

                NBTTagCompound nbttagcompound = new NBTTagCompound();
                for (String s : list) {
                    try {
                        Iterator<String> iterator = GameSettings.COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
                        nbttagcompound.setString(iterator.next(), iterator.next());
                    } catch (Exception var10) {
                        LOGGER.warn("Skipping bad option: {}", s);
                    }
                }
                nbttagcompound = this.dataFix(nbttagcompound);

                for (String k : nbttagcompound.getKeySet()) {
                    String v = nbttagcompound.getString(k);

                    try {
//                        for (KeyBinding keyBinding : this.cloneKeybindings) {
//                            String s = new StringBuilder()
//                                    .append("clonekey_")
//                                    .append(keyBinding.getKeyDescription())
//                                    .append("_")
//                                    .toString();
//                            if (k.startsWith(s) && ((Integer) Integer.parseInt(k.split("_")[2])).equals(((IKeyBinding) keyBinding).getCloneID())) {
//                                if (v.contains(":")) {
//                                    keyBinding.setKeyModifierAndCode(
//                                            KeyModifier.valueFromString(v.split(":")[1]),
//                                            Integer.parseInt(v.split(":")[0])
//                                    );
//                                } else {
//                                    keyBinding.setKeyModifierAndCode(KeyModifier.NONE, Integer.parseInt(v));
//                                }
//                            }
//                        }

                        if (k.startsWith("clonekey$")) {
                            KeyBinding origin = this.getCloneKeybindings()
                                    .stream()
                                    .filter(kb -> {
                                        boolean b = ((Integer) Integer.parseInt(k.split("\\$")[2])).equals(((IKeyBinding) kb).getCloneID());
                                        return b && kb.getKeyDescription().equals(k.split("\\$")[1]);
                                    })
                                    .findFirst()
                                    .orElse(null);
                            if (origin != null) {
                                if (v.contains(":")) {
                                    origin.setKeyModifierAndCode(
                                            KeyModifier.valueFromString(v.split(":")[1]),
                                            Integer.parseInt(v.split(":")[0])
                                    );
                                } else {
                                    origin.setKeyModifierAndCode(KeyModifier.NONE, Integer.parseInt(v));
                                }
                            } else {
                                origin = Arrays.stream(this.keyBindings)
                                        .filter(
                                                kb -> kb.getKeyDescription().equals(k.split("\\$")[1])
                                        )
                                        .findFirst()
                                        .orElse(null);
                                if (origin != null) {
                                    KeyBinding keyBinding;
                                    keyBinding = new KeyBinding(
                                            origin.getKeyDescription(),
                                            origin.getKeyConflictContext(),
                                            KeyModifier.NONE, 0,
                                            origin.getKeyCategory()
                                    );
                                    if (v.contains(":")) {
                                        keyBinding.setKeyModifierAndCode(
                                                KeyModifier.valueFromString(v.split(":")[1]),
                                                Integer.parseInt(v.split(":")[0])
                                        );
                                    } else keyBinding.setKeyCode(Integer.parseInt(v));
                                    ((IKeyBinding) keyBinding).setCloneID(Integer.parseInt(k.split("\\$")[2]));
                                    ((IKeyBinding) keyBinding).setOrigin(origin);
                                    this.cloneKeybindings.add(keyBinding);
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Skipping bad option: {}:{}", k, v);
                    }
                }
                KeyBinding.resetKeyBindingArrayAndHash();
            } catch (Exception e) {
                LOGGER.error("Failed to load options", e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    @Inject(
            method = "saveOptions",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/GameSettings;sendSettingsToServer()V",
                    shift = At.Shift.AFTER
            )
    )
    private void inject$saveOptions(CallbackInfo ci) {
        if (ControllingToolkitConfig.enableCloneKeybindings) {
            PrintWriter printwriter = null;
            try {
                printwriter = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(this.optionsFile.toPath(), StandardOpenOption.APPEND), StandardCharsets.UTF_8));
                for (KeyBinding keybinding : this.cloneKeybindings) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("clonekey$");
                    builder.append(keybinding.getKeyDescription());
                    builder.append("$");
                    builder.append(((IKeyBinding) keybinding).getCloneID());
                    builder.append(":");
                    builder.append(keybinding.getKeyCode());
                    if (keybinding.getKeyModifier() != KeyModifier.NONE) {
                        builder.append(":");
                        builder.append(keybinding.getKeyModifier());
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
}
