package net.fdymcreep.moderncontrolling.keybind.util;

import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.keybind.client.gui.screen.NewKeybindList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KeybindingFilterHelper {
    public static List<KeyBinding> defaultSort(List<KeyBinding> origin) {
        List<KeyBinding> list = new ArrayList<>(origin);
        Collections.sort(list);
        return list;
    }

    public static List<KeyBinding> StringSort(List<KeyBinding> origin) {
        List<KeyBinding> list = new ArrayList<>(origin);
        list.sort(Comparator.comparing(o -> I18n.format(o.getKeyDescription())));
        return list;
    }

    public static List<KeyBinding> StringReverseSort(List<KeyBinding> origin) {
        List<KeyBinding> list = new ArrayList<>(origin);
        list.sort((o1, o2) -> I18n.format(o2.getKeyDescription()).compareTo(I18n.format(o1.getKeyDescription())));
        return list;
    }

    public static List<KeyBinding> KeySort(List<KeyBinding> origin) {
        List<KeyBinding> list = new ArrayList<>(origin);
        list.sort(Comparator.comparing(o -> (NewKeybindList.getModifierString(o.getKeyModifier()) + GameSettings.getKeyDisplayString(o.getKeyCode()))));
        return list;
    }

    public static List<KeyBinding> KeyReverseSort(List<KeyBinding> origin) {
        List<KeyBinding> list = new ArrayList<>(origin);
        list.sort((o1, o2) ->
                (NewKeybindList.getModifierString(o2.getKeyModifier()) + GameSettings.getKeyDisplayString(o2.getKeyCode()))
                        .compareTo((NewKeybindList.getModifierString(o1.getKeyModifier()) + GameSettings.getKeyDisplayString(o1.getKeyCode()))));
        return list;
    }

    public enum sorts {
        DEFAULT(KeybindingFilterHelper::defaultSort, "gui." + ControllingKeybind.MODID + ".defaultSort.tooltip") {
            @Override
            public sorts next() {
                return sorts.STRING;
            }
        },

        STRING(KeybindingFilterHelper::StringSort, "gui." + ControllingKeybind.MODID + ".stringSort.tooltip") {
            @Override
            public sorts next() {
                return sorts.STRING_REVERSE;
            }
        },

        STRING_REVERSE(KeybindingFilterHelper::StringReverseSort, "gui." + ControllingKeybind.MODID + ".stringReverseSort.tooltip") {
            @Override
            public sorts next() {
                return sorts.KEY;
            }
        },

        KEY(KeybindingFilterHelper::KeySort, "gui." + ControllingKeybind.MODID + ".keySort.tooltip") {
            @Override
            public sorts next() {
                return sorts.KEY_REVERSE;
            }
        },

        KEY_REVERSE(KeybindingFilterHelper::KeyReverseSort, "gui." + ControllingKeybind.MODID + ".keyReverseSort.tooltip") {
            @Override
            public sorts next() {
                return sorts.DEFAULT;
            }
        };

        public interface ISort {
            List<KeyBinding> sort(List<KeyBinding> origin);
        }

        public final ISort func;
        public final String name;

        sorts(ISort func, String name) {
            this.func = func;
            this.name = name;
        }

        public abstract sorts next();
    }

    public static List<KeyBinding> nameFilter(List<KeyBinding> origin, String name) {
        return origin
                .stream()
                .filter(keyBinding ->
                        I18n.format(keyBinding.getKeyDescription())
                                .toLowerCase()
                                .contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<KeyBinding> categoryFilter(List<KeyBinding> origin, String category) {
        return origin
                .stream()
                .filter(keyBinding ->
                        I18n.format(keyBinding.getKeyCategory())
                                .toLowerCase()
                                .contains(category.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<KeyBinding> keyFilter(List<KeyBinding> origin, String key) {
        return origin
                .stream()
                .filter(keyBinding ->
                        (NewKeybindList.getModifierString(keyBinding.getKeyModifier()) + GameSettings.getKeyDisplayString(keyBinding.getKeyCode()))
                                .toLowerCase()
                                .contains(key.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<KeyBinding> conflictsFilter(List<KeyBinding> origin) {
        List<KeyBinding> list = new ArrayList<>();
        for (KeyBinding keyBinding : origin) {
            if (keyBinding.getKeyCode() == 0) continue;
            for (KeyBinding other : origin) {
                if (keyBinding != other && keyBinding.conflicts(other)) {
                    list.add(keyBinding);
                    break;
                }
            }
        }
        return list;
    }

    public static List<KeyBinding> unboundFilter(List<KeyBinding> origin) {
        return origin
                .stream()
                .filter(keyBinding -> keyBinding.getKeyCode() == 0)
                .collect(Collectors.toList());
    }
}
