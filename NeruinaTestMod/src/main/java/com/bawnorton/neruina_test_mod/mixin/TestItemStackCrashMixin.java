package com.bawnorton.neruina_test_mod.mixin;

import net.minecraft.item.CompassItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CompassItem.class)
public abstract class TestItemStackCrashMixin {
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void throwTestException(CallbackInfo ci) {
        throw new RuntimeException("Test exception");
    }
}
