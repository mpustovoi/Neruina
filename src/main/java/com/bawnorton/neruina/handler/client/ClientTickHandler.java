package com.bawnorton.neruina.handler.client;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.version.VersionedText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.player.PlayerEntity;

public class ClientTickHandler {
    public static void handleTickingClient(PlayerEntity player, Throwable e) {
        Neruina.LOGGER.warn("Neruina caught an exception, see below for cause", e);
        player.getWorld().disconnect();
        MinecraftClient client = MinecraftClient.getInstance();
        /*? if >=1.19 {*/
        client.disconnect(new net.minecraft.client.gui.screen.MessageScreen(VersionedText.translatable("menu.savingLevel")));
        /*?} else {*//*
        client.disconnect(new net.minecraft.client.gui.screen.SaveLevelScreen(VersionedText.translatable("menu.savingLevel")));
        *//*?}*/
        client.setScreen(new TitleScreen());
        client.getToastManager().add(SystemToast.create(client,
                SystemToast.Type.WORLD_ACCESS_FAILURE,
                VersionedText.translatable("neruina.toast.title"),
                VersionedText.translatable("neruina.toast.desc")
        ));
    }
}
