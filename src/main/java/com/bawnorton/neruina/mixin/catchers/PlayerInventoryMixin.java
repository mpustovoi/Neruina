package com.bawnorton.neruina.mixin.catchers;

import com.bawnorton.neruina.Neruina;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Shadow @Final public DefaultedList<ItemStack> main;

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void removeErroredStatusOnInvInit(CallbackInfo ci) {
        /*? if >=1.20.2 {*//*
        main.forEach(stack -> {
            if(stack.getOrDefault(Neruina.getInstance().getErroredComponent(), false)) {
                Neruina.getInstance().getTickHandler().removeErrored(stack);
            }
        });
        *//*? } else {*/
        main.forEach(stack -> {
            if(stack.hasNbt()) {
                NbtCompound nbt = stack.getNbt();
                if(nbt.getBoolean("neruina$errored")) {
                    Neruina.getInstance().getTickHandler().removeErrored(stack);
                }
            }
        });
        /*? }*/
    }
}
