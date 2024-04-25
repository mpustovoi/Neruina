package com.bawnorton.neruina;

import com.bawnorton.neruina.config.ConfigManager;
import com.bawnorton.neruina.handler.MessageHandler;
import com.bawnorton.neruina.handler.PersitanceHandler;
import com.bawnorton.neruina.handler.TickHandler;
import com.bawnorton.neruina.report.AutoReportHandler;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*? if >=1.20.2 {*//*
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import com.mojang.serialization.Codec;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodecs;
*//*? }*/

public class Neruina {
    public static final String MOD_ID = "neruina";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Neruina INSTANCE = new Neruina();

    private final TickHandler tickHandler;
    private final MessageHandler messageHandler;
    private final AutoReportHandler autoReportHandler;

    /*? if >=1.20.2 {*//*
    private static DataComponentType<Boolean> ERRORED;
    *//*? }*/

    public Neruina() {
        this.tickHandler = new TickHandler();
        this.messageHandler = new MessageHandler();
        this.autoReportHandler = new AutoReportHandler();
    }

    public static void init() {
        ConfigManager.loadConfig();
    }

    public void setup() {
        /*? if >=1.20.2 {*//*
        ERRORED = Registry.register(Registries.DATA_COMPONENT_TYPE, MOD_ID + ":errored", DataComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build());
        *//*? }*/
    }

    public static Neruina getInstance() {
        return INSTANCE;
    }

    public TickHandler getTickHandler() {
        return tickHandler;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public AutoReportHandler getAutoReportHandler() {
        return autoReportHandler;
    }

    public PersitanceHandler getPersitanceHandler(MinecraftServer server) {
        return PersitanceHandler.getServerState(server);
    }

    /*? if >=1.20.2 {*//*
    public DataComponentType<Boolean> getErroredComponent() {
        return ERRORED;
    }
    *//*? }*/
}
