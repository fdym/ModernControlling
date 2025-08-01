package net.fdymcreep.moderncontrolling.toolkit.client.event;

import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybindingWrapper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PostRemoveCloneKeybindingEvent extends Event {
    public final CloneKeybindingWrapper wrapper;

    public PostRemoveCloneKeybindingEvent(CloneKeybindingWrapper wrapper) {
        this.wrapper = wrapper;
    }
}
