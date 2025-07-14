package net.fdymcreep.moderncontrolling.keybind.client.gui.screen;

import net.fdymcreep.moderncontrolling.core.client.gui.button.ITooltipList;
import net.fdymcreep.moderncontrolling.core.client.gui.button.ITooltipListEntry;
import net.fdymcreep.moderncontrolling.core.client.gui.button.TooltipButton;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.keybind.util.KeybindingFilterHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class NewKeybindList extends GuiListExtended implements ITooltipList {
    protected final NewKeybindScreen controlsScreen;
    protected final Minecraft mc;
    protected int maxListLabelWidth;
    public List<ITooltipListEntry> listEntries;

    public NewKeybindList(NewKeybindScreen controls, Minecraft mcIn) {
        super(mcIn, controls.width + 45, controls.height, 63, controls.height - 32, 20);
        this.controlsScreen = controls;
        this.mc = mcIn;
        this.listEntries = new ArrayList<>();

        List<KeyBinding> keyBindings = new ArrayList<>(Arrays.asList(controls.options.keyBindings));
        keyBindings = KeybindingFilterHelper.defaultSort(keyBindings);
        this.setListEntries(keyBindings, true);
    }

    public static String getModifierString(KeyModifier modifier) {
        switch (modifier) {
            case CONTROL:
                return "CTRL(CMD)+";
            case ALT:
                return "ALT(OPT)+";
            case SHIFT:
                return "SHIFT+";
            default:
                return "/";

        }
    }

    public void setListEntries(List<KeyBinding> sortedKeyBindings, boolean withCategory) {
        this.listEntries.clear();
        int i = 0;
        String s = null;
        for (KeyBinding keybinding : sortedKeyBindings) {
            String s1 = keybinding.getKeyCategory();

            if (!s1.equals(s) && withCategory) {
                s = s1;
                this.listEntries.add(i++, new CategoryEntry(s1));
            }

            int j = this.mc.fontRenderer.getStringWidth(I18n.format(keybinding.getKeyDescription()));

            if (j > this.maxListLabelWidth) {
                this.maxListLabelWidth = j;
            }

            this.listEntries.add(i++, new KeyEntry(keybinding));
        }
    }

    protected int getSize() {
        return this.listEntries.size();
    }

    @Nonnull
    public ITooltipListEntry getListEntry(int index) {
        return this.listEntries.get(index);
    }

    @Override
    protected int getScrollBarX() {
        return super.getScrollBarX() + 35;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 50;
    }

    public void drawForeground(int mouseXIn, int mouseYIn, float partialTicks) {
        int left = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
        int top = this.top + 4 - (int)this.amountScrolled;
        for (int i = 0; i < this.getSize(); i++) {
            drawSlotForeground(
                    i,
                    left,
                    top + i * this.slotHeight + this.headerPadding,
                    this.slotHeight - 4,
                    mouseXIn,
                    mouseYIn,
                    partialTicks
            );
        }
    }

    public void drawSlotForeground(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
        this.getListEntry(slotIndex).drawEntryForeground(slotIndex, xPos, yPos, this.getListWidth(), heightIn, mouseXIn, mouseYIn, this.isMouseYWithinSlotBounds(mouseYIn) && this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == slotIndex, partialTicks);
    }

    @SideOnly(Side.CLIENT)
    public class CategoryEntry implements ITooltipListEntry {
        protected final String labelText;
        protected final int labelWidth;

        public CategoryEntry(String name) {
            this.labelText = I18n.format(name);
            this.labelWidth = NewKeybindList.this.mc.fontRenderer.getStringWidth(this.labelText);
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            NewKeybindList.this.mc.fontRenderer.drawString(this.labelText, NewKeybindList.this.mc.currentScreen.width / 2 - this.labelWidth / 2, y + slotHeight - NewKeybindList.this.mc.fontRenderer.FONT_HEIGHT - 1, 16777215);}

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            return false;
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {}

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}

        @Override
        public void drawEntryForeground(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {}
    }

    @SideOnly(Side.CLIENT)
    public class KeyEntry implements ITooltipListEntry {
        protected final KeyBinding keybinding;
        protected final String keyDesc;
        protected final TooltipButton btnChangeKeyModifier;
        protected final TooltipButton btnChangeKeyBinding;
        protected final GuiButton btnReset;
        protected String tooltip;

        protected KeyEntry(@Nonnull KeyBinding keyBinding) {
            this.keybinding = keyBinding;
            this.keyDesc = I18n.format(keyBinding.getKeyDescription());
            this.btnChangeKeyModifier = new TooltipButton(1, 0, 0, 50, 20, "/");
            this.btnChangeKeyBinding = new TooltipButton(0, 0, 0, 95, 20, I18n.format(keyBinding.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            boolean flag = NewKeybindList.this.controlsScreen.buttonId == this.keybinding;
            NewKeybindList.this.mc.fontRenderer.drawString(this.keyDesc, x + 10 - NewKeybindList.this.maxListLabelWidth, y + slotHeight / 2 - NewKeybindList.this.mc.fontRenderer.FONT_HEIGHT / 2, 16777215);
            this.btnReset.x = x + 215;
            this.btnReset.y = y;
            this.btnReset.enabled = !this.keybinding.isSetToDefaultValue();
            this.btnReset.drawButton(NewKeybindList.this.mc, mouseX, mouseY, partialTicks);

            this.btnChangeKeyBinding.x = x + 110;
            this.btnChangeKeyBinding.y = y;
            this.btnChangeKeyBinding.displayString = new KeyBinding("", this.keybinding.getKeyCode(), "").getDisplayName();
            List<KeyBinding> conflictsKeybinding = new ArrayList<>();
            StringBuilder builder;
            if (this.keybinding.getKeyCode() != 0) {
                for (KeyBinding keybinding : NewKeybindList.this.mc.gameSettings.keyBindings) {
                    if (keybinding != this.keybinding && keybinding.conflicts(this.keybinding)) {
                        conflictsKeybinding.add(keybinding);
                    }
                }
            }
            if (flag) {
                this.btnChangeKeyBinding.displayString = TextFormatting.WHITE + "> " + TextFormatting.YELLOW + this.btnChangeKeyBinding.displayString + TextFormatting.WHITE + " <";
            } else {
                if (!conflictsKeybinding.isEmpty()) {
                    builder = new StringBuilder(I18n.format("gui." + ControllingKeybind.MODID + ".control.conflicts"));
                    builder.append('\n');
                    for (int i = 0; i < conflictsKeybinding.size(); i++) {
                        KeyBinding keyBinding = conflictsKeybinding.get(i);
                        builder.append(I18n.format(keyBinding.getKeyDescription()));
                        if (i != conflictsKeybinding.size() - 1) builder.append('\n');
                    }
                    this.tooltip = builder.toString();
                    this.btnChangeKeyBinding.displayString = TextFormatting.RED + this.btnChangeKeyBinding.displayString;
                } else {
                    this.tooltip = null;
                }
            }
            this.btnChangeKeyBinding.tooltipText = this.tooltip;
            this.btnChangeKeyBinding.drawButton(NewKeybindList.this.mc, mouseX, mouseY, partialTicks);

            this.btnChangeKeyModifier.x = x + 50;
            this.btnChangeKeyModifier.y = y;
            builder = !conflictsKeybinding.isEmpty() ? new StringBuilder("\u00a7c") : new StringBuilder();
            builder.append(getModifierString(this.keybinding.getKeyModifier()));
            this.btnChangeKeyModifier.displayString = builder.toString();
            this.btnChangeKeyModifier.tooltipText = tooltip;
            this.btnChangeKeyModifier.drawButton(mc, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (this.btnChangeKeyBinding.mousePressed(NewKeybindList.this.mc, mouseX, mouseY)) {
                NewKeybindList.this.controlsScreen.buttonId = this.keybinding;
                return true;
            } else if (this.btnReset.mousePressed(NewKeybindList.this.mc, mouseX, mouseY)) {
                this.keybinding.setToDefault();
                NewKeybindList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else if (this.btnChangeKeyModifier.mousePressed(NewKeybindList.this.mc, mouseX, mouseY)) {
                KeyModifier nextModifier = keybinding.getKeyModifier();
                switch (nextModifier) {
                    case CONTROL:
                        nextModifier = KeyModifier.ALT;
                        this.btnChangeKeyModifier.displayString = "ALT(OPT)+";
                        break;
                    case ALT:
                        nextModifier = KeyModifier.SHIFT;
                        this.btnChangeKeyModifier.displayString = "SHIFT+";
                        break;
                    case SHIFT:
                        nextModifier = KeyModifier.NONE;
                        this.btnChangeKeyModifier.displayString = "/";
                        break;
                    default:
                        nextModifier = KeyModifier.CONTROL;
                        this.btnChangeKeyModifier.displayString = "CTRL(CMD)+";
                        break;
                }
                keybinding.setKeyModifierAndCode(nextModifier, keybinding.getKeyCode());
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
            this.btnChangeKeyBinding.mouseReleased(x, y);
            this.btnReset.mouseReleased(x, y);
            this.btnChangeKeyModifier.mouseReleased(x, y);
        }

        @Override
        public void updatePosition(int slotIndex, int x, int y, float partialTicks) {}

        @Override
        public void drawEntryForeground(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            this.btnChangeKeyBinding.drawButtonForegroundLayer(mouseX, mouseY);
            this.btnChangeKeyModifier.drawButtonForegroundLayer(mouseX, mouseY);
        }
    }
}

