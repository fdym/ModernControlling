package net.fdymcreep.moderncontrolling.toolkit.api;

import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybinding;
import net.fdymcreep.moderncontrolling.toolkit.util.CloneKeybindingWrapper;

import java.util.List;

public interface IKeyBinding {
    void press();

    List<CloneKeybinding> getClones();

    List<CloneKeybindingWrapper> getCloneWrappers();
}
