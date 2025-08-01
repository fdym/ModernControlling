package net.fdymcreep.moderncontrolling.keybind.util;

import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

public abstract class KeyBindingsFileExtra {
    @Override
    public String toString() {
        return this.getName();
    }

    @Nonnull
    public abstract String getName();

    @Nonnull
    public abstract JsonObject sync(JsonObject origin);

    public abstract void override(@Nonnull JsonObject jsonObject);
}
