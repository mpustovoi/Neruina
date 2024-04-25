package com.bawnorton.neruina_test_mod.mixin;

import net.minecraft.block.DaylightDetectorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DaylightDetectorBlock.class)
public abstract class TestBlockEntityCrashMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private static void throwTestException(CallbackInfo ci) {
        throw new RuntimeException("Test exception");
    }
}
