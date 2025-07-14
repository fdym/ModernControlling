package net.fdymcreep.moderncontrolling.keybind.client.gui.screen;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fdymcreep.moderncontrolling.core.client.gui.button.ImageButton;
import net.fdymcreep.moderncontrolling.core.client.gui.button.TooltipButton;
import net.fdymcreep.moderncontrolling.core.client.gui.screen.ErrorScreen;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybindConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;

import static net.fdymcreep.moderncontrolling.keybind.ControllingKeybind.MODID;
import static net.fdymcreep.moderncontrolling.keybind.ControllingKeybind.logger;

public class MCKConfigScreen extends GuiScreen {
    protected GuiScreen parent;
    protected GuiTextField filenameInputBar;
    protected ImageButton chooseFileButton;
    protected TooltipButton reloadFileButton;
    protected GuiButton switchButton;
    protected TooltipButton syncFromKeybindingsButton;
    protected TooltipButton overrideKeybindingsButton;
    protected TooltipButton rereadButton;
    protected TooltipButton saveButton;

    public MCKConfigScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.buttonList.add(new GuiButton(200, 20, this.height - 40, 200, 20, I18n.format("gui.done")));
        this.filenameInputBar = new GuiTextField(0, this.fontRenderer, 19, 39, 179, 19);
        this.filenameInputBar.setMaxStringLength(50);
        this.filenameInputBar.setText(ControllingKeybindConfig.keybindingsFilename);
        this.chooseFileButton = new ImageButton(0, 200, 40, 20, 20, 100, 0, 20, 20, new ResourceLocation(MODID, "textures/gui/keybind_widget.png"));
        this.reloadFileButton = new TooltipButton(
                0, 20, 60, 100, 20,
                I18n.format("gui." + MODID + ".config.reloadFile"),
                I18n.format("gui." + MODID + ".config.reloadFile.tooltip")
        );
        this.switchButton = new GuiButton(
                0, 120, 60, 100, 20,
                ControllingKeybind.keybindingsFile == null ? I18n.format("addServer.resourcePack.enabled") : I18n.format("addServer.resourcePack.disabled")
        );
        this.buttonList.add(this.chooseFileButton);
        this.buttonList.add(this.reloadFileButton);
        this.buttonList.add(this.switchButton);

        this.syncFromKeybindingsButton = new TooltipButton(
                0, 20, 100, 100, 20,
                I18n.format("gui." + MODID + ".control.syncFromKeybindings"),
                I18n.format("gui." + MODID + ".control.syncFromKeybindings.tooltip")
        );
        this.overrideKeybindingsButton = new TooltipButton(
                0, 120, 100, 100, 20,
                I18n.format("gui." + MODID + ".control.overrideKeybindings"),
                I18n.format("gui." + MODID + ".control.overrideKeybindings.tooltip")
        );
        this.rereadButton = new TooltipButton(
                0, 20, 120, 100, 20,
                I18n.format("gui." + MODID + ".control.rereadFileContent"),
                I18n.format("gui." + MODID + ".control.rereadFileContent.tooltip")
        );
        this.saveButton = new TooltipButton(
                0, 120, 120, 100, 20,
                I18n.format("gui." + MODID + ".control.saveFileContent"),
                I18n.format("gui." + MODID + ".control.saveFileContent.tooltip")
        );
        this.buttonList.add(this.syncFromKeybindingsButton);
        this.buttonList.add(this.overrideKeybindingsButton);
        this.buttonList.add(this.saveButton);
        this.buttonList.add(this.rereadButton);

        if (ControllingKeybind.keybindingsFile == null) {
            this.chooseFileButton.enabled = false;
            this.reloadFileButton.enabled = false;
            this.syncFromKeybindingsButton.enabled = false;
            this.overrideKeybindingsButton.enabled = false;
            this.rereadButton.enabled = false;
            this.saveButton.enabled = false;
            this.switchButton.displayString = I18n.format("addServer.resourcePack.enabled");
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.filenameInputBar.drawTextBox();
        if (this.reloadFileButton.enabled) {
            this.drawString(this.fontRenderer, I18n.format("gui." + MODID + ".config.tip1", ControllingKeybind.keybindingsFile.name), 20, 80, 0xFFFF55);
            this.drawString(this.fontRenderer, I18n.format("gui." + MODID + ".config.tip2", ControllingKeybind.keybindingsFile.name), 20, 90, 0xFFFF55);
        }

        this.reloadFileButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.syncFromKeybindingsButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.overrideKeybindingsButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.rereadButton.drawButtonForegroundLayer(mouseX, mouseY);
        this.saveButton.drawButtonForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if (button.id == 200) {
            this.mc.displayGuiScreen(this.parent);
        } else if (button.equals(this.chooseFileButton)) {
            File dir = new File(
                    Minecraft.getMinecraft().mcDataDir,
                    "keybindings"
            );
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Minecraft.getMinecraft().displayGuiScreen(new ErrorScreen(this) {
                        @Override
                        public String getErrorMessage() {
                            return I18n.format("gui." + MODID + ".error.couldNotCreate", dir.getPath());
                        }
                    });
                    return;
                }
            }
            JFileChooser fileChooser = new JFileChooser(dir);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
                }

                @Override
                public String getDescription() {
                    return "Keybindings File (*.json)";
                }
            });
            int result = fileChooser.showOpenDialog(new JFrame());
            if (result == JFileChooser.APPROVE_OPTION) {
                ControllingKeybindConfig.keybindingsFilename = fileChooser.getSelectedFile().getName();
                ConfigManager.sync(MODID, Config.Type.INSTANCE);
            }
        } else if (button.equals(this.reloadFileButton)) {
            if (ControllingKeybind.keybindingsFile != null) {
                String[] result = ControllingKeybind.reloadKeybindingsFile(ControllingKeybindConfig.keybindingsFilename);
                this.filenameInputBar.setText(ControllingKeybindConfig.keybindingsFilename);
                if (result != null) {
                    this.mc.displayGuiScreen(new ErrorScreen(this) {
                        @Override
                        public String getErrorMessage() {
                            return I18n.format(result[0], result[1]);
                        }
                    });
                }
                if (ControllingKeybind.keybindingsFile == null) {
                    this.chooseFileButton.enabled = false;
                    this.reloadFileButton.enabled = false;
                    this.syncFromKeybindingsButton.enabled = false;
                    this.overrideKeybindingsButton.enabled = false;
                    this.rereadButton.enabled = false;
                    this.saveButton.enabled = false;
                    this.switchButton.displayString = I18n.format("addServer.resourcePack.enabled");
                }
            }
        } else if (button.equals(this.switchButton)) {
            if (ControllingKeybind.keybindingsFile != null) {
                ControllingKeybind.keybindingsFile = null;
                this.chooseFileButton.enabled = false;
                this.reloadFileButton.enabled = false;
                this.syncFromKeybindingsButton.enabled = false;
                this.overrideKeybindingsButton.enabled = false;
                this.rereadButton.enabled = false;
                this.saveButton.enabled = false;
                this.switchButton.displayString = I18n.format("addServer.resourcePack.enabled");
            } else {
                String[] result = ControllingKeybind.reloadKeybindingsFile(ControllingKeybindConfig.keybindingsFilename);
                this.filenameInputBar.setText(ControllingKeybindConfig.keybindingsFilename);
                if (result != null) {
                    this.mc.displayGuiScreen(new ErrorScreen(this) {
                        @Override
                        public String getErrorMessage() {
                            return I18n.format(result[0], result[1]);
                        }
                    });
                } else {
                    this.chooseFileButton.enabled = true;
                    this.reloadFileButton.enabled = true;
                    this.syncFromKeybindingsButton.enabled = true;
                    this.overrideKeybindingsButton.enabled = true;
                    this.rereadButton.enabled = true;
                    this.saveButton.enabled = true;
                    this.switchButton.displayString = I18n.format("addServer.resourcePack.disabled");
                }
            }

            ControllingKeybindConfig.enableKeybindingsFile = !ControllingKeybindConfig.enableKeybindingsFile;
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
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
                String s = I18n.format("gui." + MODID + ".error.couldNotOpen", new File(
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
                String s = I18n.format("gui." + MODID + ".error.jsonSyntax", new File(
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
                String s = I18n.format("gui." + MODID + ".error.jsonIO", new File(
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
                String s = I18n.format("gui." + MODID + ".error.couldNotWrite", new File(
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
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.filenameInputBar.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.filenameInputBar.textboxKeyTyped(typedChar, keyCode)) {
            ControllingKeybindConfig.keybindingsFilename = filenameInputBar.getText();
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }
}
