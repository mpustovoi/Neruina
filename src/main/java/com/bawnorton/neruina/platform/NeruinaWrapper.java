package com.bawnorton.neruina.platform;

import com.bawnorton.neruina.Neruina;

/*? if fabric {*//*
import net.fabricmc.api.ModInitializer;

public class NeruinaWrapper implements ModInitializer {
    @Override
    public void onInitialize() {
        Neruina.init();
        Neruina.getInstance().setup();
    }
}
*//*? } elif forge {*//*
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Neruina.MOD_ID)
public class NeruinaWrapper {
    public NeruinaWrapper() {
        Neruina.init();
    }

    @SubscribeEvent
    public void onSetup(FMLCommonSetupEvent event) {
        Neruina.getInstance().setup();
    }
}
*//*? } elif neoforge {*/
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Neruina.MOD_ID)
public class NeruinaWrapper {
    public NeruinaWrapper() {
        Neruina.init();
    }

    @SubscribeEvent
    public void onSetup(RegisterEvent event) {
        Neruina.getInstance().setup();
    }
}
/*? }*/
