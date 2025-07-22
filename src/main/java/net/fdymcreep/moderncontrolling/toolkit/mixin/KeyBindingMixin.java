package net.fdymcreep.moderncontrolling.toolkit.mixin;

import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBinding;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBindingMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyBindingMap;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin implements IKeyBinding {
    @Shadow
    @Final
    private static KeyBindingMap HASH;

    @Shadow
    private int pressTime;

    @Override
    public void press() {this.pressTime++;}

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private static void inject$onTick(int keyCode, CallbackInfo ci) {
        if (keyCode != 0 && ControllingToolkitConfig.enableNcKeys && (ArrayUtils.contains(ControllingToolkitConfig.nonConflictKeyCodes, keyCode) || ControllingToolkitConfig.enableAKiNC)) {
            ci.cancel();
            ((IKeyBindingMap) HASH).lookupActives(keyCode).forEach(k -> ((IKeyBinding) k).press());
        }
    }
}
