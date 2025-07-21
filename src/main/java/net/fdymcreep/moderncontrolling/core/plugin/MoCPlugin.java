package net.fdymcreep.moderncontrolling.core.plugin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoCPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {return new String[0];}

    @Override
    public String getModContainerClass() {return null;}

    @Nullable
    @Override
    public String getSetupClass() {return null;}

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {return null;}

    @Override
    public List<String> getMixinConfigs() {
        List<String> result = new ArrayList<>();
        result.add("mixins.moderncontrolling_toolkit.json");
        return result;
    }
}
