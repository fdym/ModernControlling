package net.fdymcreep.moderncontrolling.toolkit.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybinding;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @WrapOperation(
            method = {"runTickKeyboard", "runTickMouse"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;onTick(I)V"
            )
    )
    private void wrap$runTick$onTick(int keyCode, Operation<Void> original) {
        original.call(keyCode);
        if (ControllingToolkitConfig.enableCloneKeys) {
            CloneKeybinding.onTick(keyCode);
        }
    }

    @WrapOperation(
            method = {"runTickKeyboard", "runTickMouse"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;setKeyBindState(IZ)V"
            )
    )
    private void wrap$runTick$setKeyBindState(int keyCode, boolean pressed, Operation<Void> original) {
        original.call(keyCode, pressed);
        if (ControllingToolkitConfig.enableCloneKeys) {
            CloneKeybinding.setKeyBindState(keyCode, pressed);
        }
    }

    @Inject(
            method = "setIngameFocus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;updateKeyBindState()V",
                    shift = At.Shift.AFTER
            )
    )
    private void inject$setIngameFocus$updateKeyBindState(CallbackInfo ci) {
        if (ControllingToolkitConfig.enableCloneKeys) {
            CloneKeybinding.updateKeyBindState();
        }
    }

    @Inject(
            method = "displayGuiScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/KeyBinding;unPressAllKeys()V",
                    shift = At.Shift.AFTER
            )
    )
    private void inject$displayGuiScreen$unPressAllKeys(CallbackInfo ci) {
        if (ControllingToolkitConfig.enableCloneKeys) {
            CloneKeybinding.unpressAllKey();
        }
    }
}
