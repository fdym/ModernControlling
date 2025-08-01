package net.fdymcreep.moderncontrolling.keybind.compat;

import com.google.gson.JsonObject;
import net.fdymcreep.moderncontrolling.keybind.ControllingKeybind;
import net.fdymcreep.moderncontrolling.toolkit.client.event.PostRemoveCloneKeybindingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PostRemoveHandler {
    @SubscribeEvent
    public void postRemove(PostRemoveCloneKeybindingEvent event) {
        JsonObject jsonObject = ControllingKeybind.keybindingsFile
                .getContent()
                .extrasMap
                .get(CloneKeybindingExtra.staticGetName());
        StringBuilder builder = new StringBuilder(
                event.wrapper.instance.origin.getKeyDescription()
        );
        builder.append("$");
        builder.append(event.wrapper.instance.cloneID);
        jsonObject.remove(builder.toString());
    }
}
