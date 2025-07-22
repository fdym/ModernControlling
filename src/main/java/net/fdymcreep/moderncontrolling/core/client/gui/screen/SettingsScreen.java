package net.fdymcreep.moderncontrolling.core.client.gui.screen;

import akka.japi.Pair;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SettingsScreen extends GuiScreen {
    public final GuiScreen parent;
    @Nullable
    public final GuiListExtended.IGuiListEntry parentListEntry;
    public final String domain;
    public final List<String> names;
    public final List<Pair<Gui, Integer>> options = new ArrayList<>();

    public final List<GuiButton> buttons = new ArrayList<>();
    public final List<GuiTextField> inputBars = new ArrayList<>();

    public SettingsScreen(GuiScreen parent, String domain, List<String> names) {
        this.parent = parent;
        this.parentListEntry = null;
        this.domain = domain;
        this.names = names;
    }

    public SettingsScreen(GuiScreen parent, @Nullable GuiListExtended.IGuiListEntry parentListEntry, String domain, List<String> names) {
        this.parent = parent;
        this.parentListEntry = parentListEntry;
        this.domain = domain;
        this.names = names;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int i = 0;
        options.sort(Comparator.comparing(Pair::second));
        for (Pair<Gui, Integer> pair : this.options) {
            if (pair.first() instanceof GuiButton) {
                GuiButton gui = (GuiButton) pair.first();
                gui.x = this.width / 2 - 155 + i % 2 * 160;
                gui.y = 42 + 24 * (i >> 1);
                this.buttonList.add(gui);
                this.buttons.add(gui);
            } else if (pair.first() instanceof GuiTextField) {
                GuiTextField gui = (GuiTextField) pair.first();
                gui.x = this.width / 2 - 155 + i % 2 * 160;
                gui.y = 42 + 24 * (i >> 1);
                this.inputBars.add(gui);
            }
            i++;
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 75, this.mc.currentScreen.height - 60, 150, 20, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, String.join(" > ", this.names), this.width / 2, 15, 16777215);
        for (GuiTextField inputBar : this.inputBars) {
            inputBar.drawTextBox();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiTextField inputBar : this.inputBars) {
            inputBar.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (GuiTextField inputBar : this.inputBars) {
            inputBar.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onGuiClosed() {
        buttons.clear();
        inputBars.clear();
        Keyboard.enableRepeatEvents(false);
    }
}
