package net.fdymcreep.moderncontrolling.core.client.gui.screen;

import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class NewControlScreen extends GuiScreen {
    protected static final GameSettings.Options[] OPTIONS_ARR = new GameSettings.Options[] {GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN, GameSettings.Options.AUTO_JUMP};
    protected GuiScreen parent;
    protected GuiButton returnButton;
    public GameSettings options;
    public List<GuiButton> buttons = new ArrayList<>();

    public NewControlScreen(GuiScreen parent, GameSettings options) {
        this.parent = parent;
        this.options = options;
    }

    @Override
    public void initGui() {
        int i = 0;

        for (GameSettings.Options gamesettings$options : OPTIONS_ARR) {
            if (gamesettings$options.isFloat()) {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 42 + 24 * (i >> 1), gamesettings$options));
            } else {
                this.buttonList.add(new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, 42 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
            }

            i++;
        }

        for (GuiButton button : this.buttons) {
            button.x = this.width / 2 - 155 + i % 2 * 160;
            button.y = 42 + 24 * (i >> 1);
            this.buttonList.add(button);
            i++;
        }

        this.returnButton = new GuiButton(0, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done"));
        this.buttonList.add(this.returnButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("controls.title"), this.width / 2, 15, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if (button.equals(this.returnButton)) {
            buttons.clear();
            mc.displayGuiScreen(parent);
        } else if (button.id < 100 && button instanceof GuiOptionButton) {
            this.options.setOptionValue(((GuiOptionButton)button).getOption(), 1);
            button.displayString = this.options.getKeyBinding(GameSettings.Options.byOrdinal(button.id));
        }
    }
}
