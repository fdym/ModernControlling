package net.fdymcreep.moderncontrolling.toolkit.mixin.accessor;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyBindingMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("HASH")
    static KeyBindingMap getHASH() {
        throw new AssertionError();
    }
}
