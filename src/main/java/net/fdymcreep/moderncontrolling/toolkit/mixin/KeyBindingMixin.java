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
    @Nullable
    @Unique
    private Integer cloneID = null;

    @Unique
    private KeyBinding origin = null;

    @Shadow
    @Final
    private static KeyBindingMap HASH;

    @Shadow
    private int pressTime;

    @Override
    public void press() {this.pressTime++;}

    @Nullable
    @Override
    public Integer getCloneID() {
        return this.cloneID;
    }

    @Override
    public void setCloneID(@Nullable Integer cloneID) {
        this.cloneID = cloneID;
    }

    @Override
    public KeyBinding getOrigin() {
        return this.origin;
    }

    @Override
    public void setOrigin(KeyBinding origin) {
        this.origin = origin;
    }

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private static void inject$onTick(int keyCode, CallbackInfo ci) {
        if (keyCode != 0 && (ArrayUtils.contains(ControllingToolkitConfig.nonConflictKeyCodes, keyCode) || ControllingToolkitConfig.enableAKiNC)) {
            ci.cancel();
//            KeyBindingMapExtend.lookupActives(HASH, keyCode).forEach(KeyBindingExtend::press);
//            ((IKeyBindingMap) HASH).lookupActives(keyCode).forEach(KeyBindingExtend::press);
            ((IKeyBindingMap) HASH).lookupActives(keyCode).forEach(k -> ((IKeyBinding) k).press());
        }
    }
}
