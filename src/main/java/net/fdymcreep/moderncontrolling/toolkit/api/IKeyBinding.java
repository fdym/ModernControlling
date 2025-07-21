package net.fdymcreep.moderncontrolling.toolkit.api;

import net.minecraft.client.settings.KeyBinding;

import javax.annotation.Nullable;

public interface IKeyBinding {
    void press();

    @Nullable
    Integer getCloneID();

    void setCloneID(@Nullable Integer value);

    KeyBinding getOrigin();

    void setOrigin(KeyBinding origin);
}
