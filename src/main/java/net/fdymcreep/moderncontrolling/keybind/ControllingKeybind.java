package net.fdymcreep.moderncontrolling.keybind;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fdymcreep.moderncontrolling.core.ControllingCore;
import net.fdymcreep.moderncontrolling.keybind.client.event.KeybindClientEventHandler;
import net.fdymcreep.moderncontrolling.keybind.util.KeybindingsFile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

@Mod(
        modid = ControllingKeybind.MODID,
        name = ControllingKeybind.NAME,
        version = ControllingKeybind.VERSION,
        dependencies = "required-after-client:moderncontrolling_core",
        clientSideOnly = true,
        guiFactory = "net.fdymcreep.moderncontrolling.keybind.MoCKGuiFactory"
)
public class ControllingKeybind {
    public static final String MODID = "moderncontrolling_keybind";
    public static final String NAME = "Modern Controlling Keybind";
    public static final String VERSION = "12.1.1";

    public static KeybindingsFile keybindingsFile;
    public static Logger logger;

    @Nullable
    public static String[] reloadKeybindingsFile(String newName) {
        String[] result = new String[2];
        try {
            keybindingsFile = new KeybindingsFile(newName);
        } catch (IOException e) {
            result[0] = "gui." + ControllingCore.MODID + ".error.couldNotOpen";
            result[1] = new File(
                    new File(
                            Minecraft.getMinecraft().mcDataDir,
                            "keybindings"
                    ),
                    newName
            ).getPath();
            logger.error(I18n.format(result[0], result[1]), e);
            keybindingsFile = null;
            return result;
        } catch (JsonSyntaxException e) {
            result[0] = "gui." + ControllingCore.MODID + ".error.jsonSyntax";
            result[1] = new File(
                    new File(
                            Minecraft.getMinecraft().mcDataDir,
                            "keybindings"
                    ),
                    newName
            ).getPath();
            logger.error(I18n.format(result[0], result[1]), e);
            keybindingsFile = null;
            return result;
        } catch (JsonIOException e) {
            result[0] = "gui." + ControllingCore.MODID + ".error.jsonIO";
            result[1] = new File(
                    new File(
                            Minecraft.getMinecraft().mcDataDir,
                            "keybindings"
                    ),
                    newName
            ).getPath();
            logger.error(I18n.format(result[0], result[1]), e);
            keybindingsFile = null;
            return result;
        }
        return null;
    }

    @Mod.EventHandler
    private void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(new KeybindClientEventHandler());
    }

    @Mod.EventHandler
    private void postInit(FMLPostInitializationEvent event) {
        if (ControllingKeybindConfig.enableKeybindingsFile) {
            reloadKeybindingsFile(ControllingKeybindConfig.keybindingsFilename);
        } else {
            keybindingsFile = null;
        }
    }
}
