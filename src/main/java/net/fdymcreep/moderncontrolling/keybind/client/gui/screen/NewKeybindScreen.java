package net.fdymcreep.moderncontrolling.keybind.client.gui.screen;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fdymcreep.moderncontrolling.core.ControllingCore;
import net.fdymcreep.moderncontrolling.core.client.gui.button.ImageButton;
import net.fdymcreep.moderncontrolling.core.client.gui.button.RadioButton;
import net.fdymcreep.moderncontrolling.core.client.gui.button.TooltipButton;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.ErrorScreen;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.keybind.compat.MoCKCompatCheck;
import net.fdymcreep.moderncontrolling.keybind.compat.ToolkitCompat;
import net.fdymcreep.moderncontrolling.keybind.util.KeybindingFilterHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.fdymcreep.moderncontrolling.core.client.gui.button.TooltipButton.TOOLTIP_MAX_WIDTH;
import static net.fdymcreep.moderncontrolling.keybind.ControllingKeybind.MODID;
import static net.fdymcreep.moderncontrolling.keybind.ControllingKeybind.logger;

@SideOnly(Side.CLIENT)
public class NewKeybindScreen extends GuiScreen {
    protected final GuiScreen parentScreen;
    protected String screenTitle;
    protected final GameSettings options;
    protected NewKeybindList keyBindingList;
    protected GuiButton buttonReset;
    protected GuiTextField searchBar;
    protected RadioButton.Group group;
    protected RadioButton radioButtonName;
    protected RadioButton radioButtonCategory;
    protected RadioButton radioButtonKey;
    protected TooltipButton conflictsFilterButton;
    protected boolean conflictsFilterEnabled = false;
    protected TooltipButton unboundFilterButton;
    protected boolean unboundFilterEnabled = false;
    protected ImageButton sortButton;
    protected KeybindingFilterHelper.sorts sort = KeybindingFilterHelper.sorts.DEFAULT;
    protected HoverChecker sortButtonHoverChecker;
    protected ImageButton configButton;
    protected TooltipButton syncFromKeybindingsButton;
    protected TooltipButton overrideKeybindingsButton;
    protected TooltipButton rereadButton;
    protected TooltipButton saveButton;
    public KeyBinding buttonId;
    public long time;

    public NewKeybindScreen(GuiScreen screen, GameSettings settings) {
        this.parentScreen = screen;
        this.options = settings;
    }

    public void search() {
        this.keyBindingList.listEntries.clear();
        boolean withCategory = true;
        List<KeyBinding> keyBindings = new ArrayList<>(Arrays.asList(this.options.keyBindings));

        keyBindings = sort.func.sort(keyBindings);
        if (!sort.name().equals("DEFAULT")) withCategory = false;

        if (!searchBar.getText().isEmpty()) {
            switch (group.getIndex()) {
                case 0:
                    keyBindings = KeybindingFilterHelper.nameFilter(keyBindings, searchBar.getText());
                    withCategory = false;
                    break;
                case 1:
                    keyBindings = KeybindingFilterHelper.categoryFilter(keyBindings, searchBar.getText());
                    break;
                case 2:
                    keyBindings = KeybindingFilterHelper.keyFilter(keyBindings, searchBar.getText());
                    withCategory = false;
                    break;
            }
        }

        if (conflictsFilterEnabled) {
            keyBindings = KeybindingFilterHelper.conflictsFilter(keyBindings);
            withCategory = false;
        } else if (unboundFilterEnabled) {
            keyBindings = KeybindingFilterHelper.unboundFilter(keyBindings);
            withCategory = false;
        }

        if (MoCKCompatCheck.moCTCanCampat()) ToolkitCompat.insertCloneKeybindings(keyBindings);
        this.keyBindingList.setListEntries(keyBindings, withCategory);
    }

    @Override
    public void initGui() {
        this.keyBindingList = new NewKeybindList(this, this.mc);
        this.buttonList.add(new GuiButton(200, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("gui.done")));
        this.buttonReset = this.addButton(new GuiButton(201, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
        this.screenTitle = I18n.format("gui." + MODID + ".control.title");

        Keyboard.enableRepeatEvents(true);
        this.searchBar = new GuiTextField(0, this.fontRenderer, this.width / 2 - 155, 18, 150, 20);
        this.searchBar.setMaxStringLength(50);

        this.group = new RadioButton.Group(0);
        this.radioButtonName = new RadioButton(
                0,
                this.width / 2 - 155,
                42,
                75,
                10,
                I18n.format("gui." + MODID + ".control.name"),
                0,
                this.group
        ) {
            @Override
            public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
                if (super.mousePressed(mc, mouseX, mouseY)) {
                    search();
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.group.add(this.radioButtonName);
        this.buttonList.add(this.radioButtonName);
        this.radioButtonCategory = new RadioButton(
                0,
                this.width / 2 - 155,
                52,
                75,
                10,
                I18n.format("gui." + MODID + ".control.category"),
                1,
                this.group
        ) {
            @Override
            public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
                if (super.mousePressed(mc, mouseX, mouseY)) {
                    search();
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.group.add(this.radioButtonCategory);
        this.buttonList.add(this.radioButtonCategory);
        this.radioButtonKey = new RadioButton(
                0,
                this.width / 2 - 80,
                42,
                75,
                10,
                I18n.format("gui." + MODID + ".control.key"),
                2,
                this.group
        ) {
            @Override
            public boolean mousePressed(@Nonnull Minecraft mc, int mouseX, int mouseY) {
                if (super.mousePressed(mc, mouseX, mouseY)) {
                    search();
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.group.add(this.radioButtonKey);
        this.buttonList.add(this.radioButtonKey);

        this.conflictsFilterButton = new TooltipButton(0, this.width / 2 - 180, 18, 20, 20, "\u00a7cA", I18n.format("gui." + MODID + ".control.conflictsFilterButton", ""));
        this.buttonList.add(this.conflictsFilterButton);
        this.unboundFilterButton = new TooltipButton(0, this.width / 2 - 180, 42, 20, 20, "/", I18n.format("gui." + MODID + ".control.unboundFilterButton", ""));
        this.buttonList.add(this.unboundFilterButton);
        this.sortButton = new ImageButton(0, this.width / 2 - 205, 18, 20, 20, 0, 0, 20, 20, new ResourceLocation(MODID, "textures/gui/widgets.png"));
        this.sortButtonHoverChecker = new HoverChecker(this.sortButton, 0);
        this.buttonList.add(this.sortButton);
        this.configButton = new ImageButton(0, this.width - 20, 0, 20, 20, 20, 0, 20, 20, new ResourceLocation(ControllingCore.MODID, "textures/gui/widgets.png"));
        this.buttonList.add(this.configButton);

        this.syncFromKeybindingsButton = new TooltipButton(
                0, this.width / 2 + 5, 18, 70, 20,
                I18n.format("gui." + MODID + ".control.syncFromKeybindings"),
                ControllingKeybind.keybindingsFile == null ? I18n.format("gui." + MODID + ".config.disableKeybindingsFile") : I18n.format("gui." + MODID + ".control.syncFromKeybindings.tooltip")
        );
        this.overrideKeybindingsButton = new TooltipButton(
                0, this.width / 2 + 85, 18, 70, 20,
                I18n.format("gui." + MODID + ".control.overrideKeybindings"),
                ControllingKeybind.keybindingsFile == null ? I18n.format("gui." + MODID + ".config.disableKeybindingsFile") : I18n.format("gui." + MODID + ".control.overrideKeybindings.tooltip")
        );
        this.rereadButton = new TooltipButton(
                0, this.width / 2 + 5, 42, 70, 20,
                I18n.format("gui." + MODID + ".control.rereadFileContent.short"),
                ControllingKeybind.keybindingsFile == null ? I18n.format("gui." + MODID + ".config.disableKeybindingsFile") : I18n.format("gui." + MODID + ".control.rereadFileContent.tooltip")
        );
        this.saveButton = new TooltipButton(
                0, this.width / 2 + 85, 42, 70, 20,
                I18n.format("gui." + MODID + ".control.saveFileContent.short"),
                ControllingKeybind.keybindingsFile == null ? I18n.format("gui." + MODID + ".config.disableKeybindingsFile") : I18n.format("gui." + MODID + ".control.saveFileContent.tooltip")
        );
        this.buttonList.add(this.syncFromKeybindingsButton);
        this.buttonList.add(this.overrideKeybindingsButton);
        this.buttonList.add(this.saveButton);
        this.buttonList.add(this.rereadButton);
        if (ControllingKeybind.keybindingsFile == null) {
            this.syncFromKeybindingsButton.enabled = false;
            this.overrideKeybindingsButton.enabled = false;
            this.rereadButton.enabled = false;
            this.saveButton.enabled = false;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.keyBindingList.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 201) {
            for (KeyBinding keybinding : this.mc.gameSettings.keyBindings) {
                keybinding.setToDefault();
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (button.equals(configButton)) {
            this.mc.displayGuiScreen(new MoCKConfigScreen(this));
        } else if (button.equals(this.syncFromKeybindingsButton)) {
            this.mc.displayGuiScreen(new GuiYesNo(
                    (result, id) -> {
                        this.mc.displayGuiScreen(this);
                        if (result) {
                            ControllingKeybind.keybindingsFile.syncFromKeybinding();
                        }
                    },
                    I18n.format("gui." + MODID + ".syncWarning"),
                    I18n.format("gui." + MODID + ".continue"),
                    0
            ));
        } else if (button.equals(this.overrideKeybindingsButton)) {
            this.mc.displayGuiScreen(new GuiYesNo(
                    (result, id) -> {
                        this.mc.displayGuiScreen(this);
                        if (result) {
                            ControllingKeybind.keybindingsFile.overrideKeybindings();
                        }
                    },
                    I18n.format("gui." + MODID + ".overrideWarning"),
                    I18n.format("gui." + MODID + ".continue"),
                    0
            ));
        } else if (button.equals(this.rereadButton)) {
            this.mc.displayGuiScreen(this);
            try {
                ControllingKeybind.keybindingsFile.rereadContent();
            } catch (IOException e) {
                String s = I18n.format("gui." + ControllingCore.MODID + ".error.couldNotOpen", new File(
                        new File(
                                Minecraft.getMinecraft().mcDataDir,
                                "keybindings"
                        ),
                        ControllingKeybind.keybindingsFile.name
                ).getPath());
                logger.error(s, e);
                this.mc.displayGuiScreen(new ErrorScreen(this) {
                    @Override
                    public String getErrorMessage() {
                        return s;
                    }
                });
            } catch (JsonSyntaxException e) {
                String s = I18n.format("gui." + ControllingCore.MODID + ".error.jsonSyntax", new File(
                        new File(
                                Minecraft.getMinecraft().mcDataDir,
                                "keybindings"
                        ),
                        ControllingKeybind.keybindingsFile.name
                ).getPath());
                logger.error(s, e);
                this.mc.displayGuiScreen(new ErrorScreen(this) {
                    @Override
                    public String getErrorMessage() {
                        return s;
                    }
                });
            } catch (JsonIOException e) {
                String s = I18n.format("gui." + ControllingCore.MODID + ".error.jsonIO", new File(
                        new File(
                                Minecraft.getMinecraft().mcDataDir,
                                "keybindings"
                        ),
                        ControllingKeybind.keybindingsFile.name
                ).getPath());
                logger.error(s, e);
                this.mc.displayGuiScreen(new ErrorScreen(this) {
                    @Override
                    public String getErrorMessage() {
                        return s;
                    }
                });
            }
        } else if (button.equals(this.saveButton)) {
            try {
                ControllingKeybind.keybindingsFile.saveContent();
            } catch (IOException e) {
                String s = I18n.format("gui." + ControllingCore.MODID + ".error.couldNotWrite", new File(
                        new File(
                                Minecraft.getMinecraft().mcDataDir,
                                "keybindings"
                        ),
                        ControllingKeybind.keybindingsFile.name
                ).getPath());
                logger.error(s, e);
                this.mc.displayGuiScreen(new ErrorScreen(this) {
                    @Override
                    public String getErrorMessage() {
                        return s;
                    }
                });
            }
        } else {
            if (button.equals(this.conflictsFilterButton)) {
                this.conflictsFilterEnabled = !this.conflictsFilterEnabled;
                this.conflictsFilterButton.tooltipText = I18n.format("gui." + MODID + ".control.conflictsFilterButton", conflictsFilterEnabled ? "\u00a7e\u00a7l" : "");
                this.unboundFilterButton.enabled = !this.conflictsFilterEnabled;
            } else if (button.equals(this.unboundFilterButton)) {
                this.unboundFilterEnabled = !this.unboundFilterEnabled;
                this.unboundFilterButton.tooltipText = I18n.format("gui." + MODID + ".control.unboundFilterButton", unboundFilterEnabled ? "\u00a7e\u00a7l" : "");
                this.conflictsFilterButton.enabled = !this.unboundFilterEnabled;
            } else if (button.equals(this.sortButton)) {
                this.sort = this.sort.next();
                if (this.sortButton.u == 80) {
                    this.sortButton.u = 0;
                } else {
                    this.sortButton.u += 20;
                }
            }
            this.search();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.buttonId != null) {
            this.buttonId.setKeyCode(-100 + mouseButton);
            this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        searchBar.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.buttonId != null) {
            if (keyCode == 1) {
                this.buttonId.setKeyCode(0);
                this.options.setOptionKeyBinding(this.buttonId, 0);
            } else if (keyCode != 0) {
                this.buttonId.setKeyCode(keyCode);
                this.options.setOptionKeyBinding(this.buttonId, keyCode);
            } else if (typedChar > 0) {
                this.buttonId.setKeyCode(typedChar + 256);
                this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
            }
            this.time = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
            this.buttonId = null;
        } else {
            super.keyTyped(typedChar, keyCode);
        }
        if (searchBar.textboxKeyTyped(typedChar, keyCode)) {
            this.search();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 8, 16777215);
        boolean flag = false;

        for (KeyBinding keybinding : this.options.keyBindings) {
            if (!keybinding.isSetToDefaultValue()) {
                flag = true;
                break;
            }
        }

        this.buttonReset.enabled = flag;
        this.searchBar.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.keyBindingList.drawForeground(mouseX, mouseY, partialTicks);
        this.conflictsFilterButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.unboundFilterButton.drawButtonForegroundLayer(mouseX, mouseY);
        if (this.sortButtonHoverChecker.checkHover(mouseX, mouseY)) {
            GuiUtils.drawHoveringText(
                    Arrays.asList(I18n.format(this.sort.name).split("\n")),
                    mouseX + 5, mouseY,
                    this.mc.currentScreen.width,
                    this.mc.currentScreen.height,
                    TOOLTIP_MAX_WIDTH,
                    this.mc.fontRenderer
            );
        }
        this.syncFromKeybindingsButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.overrideKeybindingsButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.rereadButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.saveButton.drawButtonForegroundLayer(mouseX, mouseY);
    }
}

