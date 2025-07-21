package net.fdymcreep.moderncontrolling.toolkit.mixin;

import com.google.common.collect.Sets;
import net.fdymcreep.moderncontrolling.toolkit.ControllingToolkitConfig;
import net.fdymcreep.moderncontrolling.toolkit.api.IKeyBindingMap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Set;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(KeyBindingMap.class)
public abstract class KeyBindingMapMixin implements IKeyBindingMap {
    @Shadow(remap = false)
    @Final
    private static EnumMap<KeyModifier, IntHashMap<Collection<KeyBinding>>> map;

    @Shadow(remap = false)
    @Nullable
    public abstract KeyBinding lookupActive(int keyCode);

    @Shadow(remap = false)
    @Nullable
    protected abstract KeyBinding getBinding(int keyCode, KeyModifier keyModifier);

    @Override
    public Set<KeyBinding> lookupActives(int keyCode) {
        if (!ControllingToolkitConfig.enableNcKeys)
            return Sets.newHashSet(this.lookupActive(keyCode));
        final KeyModifier activeModifier = KeyModifier.getActiveModifier();
        if (!activeModifier.matches(keyCode)) {
            final Set<KeyBinding> bindings = getBindings(keyCode, activeModifier);
            if (!bindings.isEmpty()) return bindings;
        }
        return getBindings(keyCode, KeyModifier.NONE);
    }

    @Override
    public Set<KeyBinding> getBindings(int keyCode, KeyModifier keyModifier) {
        if (!ControllingToolkitConfig.enableNcKeys)
            return Sets.newHashSet(this.getBinding(keyCode, keyModifier));
        final Collection<KeyBinding> bindings = map.get(keyModifier).lookup(keyCode);
        final Set<KeyBinding> result = Sets.newHashSet();
        if (bindings == null) return result;
        for (KeyBinding binding : bindings)
            if (binding.isActiveAndMatches(keyCode))
                result.add(binding);
        return result;
    }
}
