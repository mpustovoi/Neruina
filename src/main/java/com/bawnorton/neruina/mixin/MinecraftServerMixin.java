package com.bawnorton.neruina.mixin;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.report.Storage;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    /*? if >=1.19.3 {*/
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;createMetadata()Lnet/minecraft/server/ServerMetadata;", ordinal = 0))
    private void onServerStart(CallbackInfo ci) {
        Neruina.getInstance().getAutoReportHandler().init((MinecraftServer) (Object) this);
        Neruina.getInstance().getTickHandler().init();
        Neruina.getInstance().getPersitanceHandler((MinecraftServer) (Object) this);
        Storage.init((MinecraftServer) (Object) this);
    }
    /*?} elif >=1.19 {*/
    /*@Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0))
    private void onServerStart(CallbackInfo ci) {
        Neruina.getInstance().getAutoReportHandler().init((MinecraftServer) (Object) this);
        Neruina.getInstance().getTickHandler().init();
        Neruina.getInstance().getPersitanceHandler((MinecraftServer) (Object) this);
        Storage.init((MinecraftServer) (Object) this);
    }
    *//*?} else {*//*
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V", ordinal = 0))
    private void onServerStart(CallbackInfo ci) {
        Neruina.getInstance().getTickHandler().init();
        Neruina.getInstance().getPersitanceHandler((MinecraftServer) (Object) this);
    }
    *//*?}*/

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onServerStop(CallbackInfo ci) {
        Neruina.getInstance().getPersitanceHandler((MinecraftServer) (Object) this).markDirty();
    }
}
