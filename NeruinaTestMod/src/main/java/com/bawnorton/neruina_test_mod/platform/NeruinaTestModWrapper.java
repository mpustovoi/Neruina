package com.bawnorton.neruina_test_mod.platform;

/*? if fabric {*//*
import net.fabricmc.api.ModInitializer;

public class NeruinaTestModWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
    }
}
*//*? } elif forge {*/
import net.minecraftforge.fml.common.Mod;

@Mod("neruina_test_mod")
public class NeruinaTestModWrapper {
    public NeruinaTestModWrapper() {
    }
}
/*? } elif neoforge {*//*
import net.neoforged.fml.common.Mod;

@Mod("neruina_test_mod")
public class NeruinaTestModWrapper {
    public NeruinaTestModWrapper() {
    }
}
*//*? }*/
