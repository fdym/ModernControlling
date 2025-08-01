package net.fdymcreep.moderncontrolling.toolkit.mixin;

import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBindingMap;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybinding;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybindingWrapper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyBindingMap;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements IKeyBinding {
    @Unique
    private final List<CloneKeybinding> clones = new ArrayList<>();

    @Unique
    private final List<CloneKeybindingWrapper> cloneWrappers = new ArrayList<>();

    @Shadow
    @Final
    private static KeyBindingMap HASH;

    @Shadow
    private int pressTime;

    @Shadow
    @Final
    private static Map<String, KeyBinding> KEYBIND_ARRAY;

    @Shadow
    @Final
    private static Set<String> KEYBIND_SET;

    @Override
    public void press() {this.pressTime++;}

    @Override
    public List<CloneKeybinding> getClones() {return this.clones;}

    @Override
    public List<CloneKeybindingWrapper> getCloneWrappers() {return this.cloneWrappers;}

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private static void inject$onTick(int keyCode, CallbackInfo ci) {
        if (keyCode != 0
                && ControllingToolkitConfig.enableNcKeys
                && (ArrayUtils.contains(ControllingToolkitConfig.nonConflictKeyCodes, keyCode) || ControllingToolkitConfig.enableAKiNC)
        ) {
            ci.cancel();
            ((IKeyBindingMap) HASH).lookupActives(keyCode).forEach(k -> ((IKeyBinding) k).press());
            if (ControllingToolkitConfig.enableCloneKeys) {
                CloneKeybinding.lookupActives(keyCode).forEach(CloneKeybinding::press);
            }
        }
    }

    @Inject(method = "isPressed", at = @At("HEAD"), cancellable = true)
    private void inject$isPressed(CallbackInfoReturnable<Boolean> cir) {
        if (clones.stream().anyMatch(CloneKeybinding::isPressed)) {
            this.pressTime = 0;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    private void inject$isKeyDown(CallbackInfoReturnable<Boolean> cir) {
        if (clones.stream().anyMatch(CloneKeybinding::isKeyDown)) cir.setReturnValue(true);
    }

    @Inject(
            method = "<init>(Ljava/lang/String;ILjava/lang/String;)V",
            at = @At("RETURN")
    )
    private void inject$init(String description, int keyCode, String category, CallbackInfo ci) {
        if (ControllingToolkitConfig.enableCloneKeys) {
            if (((KeyBinding) (Object) this) instanceof CloneKeybindingWrapper) {
                KEYBIND_ARRAY.remove(description, (KeyBinding) (Object) this);
                // see KeyBindingMapMixin.inject$addKey
                KEYBIND_SET.remove(category);
            }
        }
    }
}
