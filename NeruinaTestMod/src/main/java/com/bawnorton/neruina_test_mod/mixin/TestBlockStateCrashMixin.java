package com.bawnorton.neruina_test_mod.mixin;

import net.minecraft.block.FarmlandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public abstract class TestBlockStateCrashMixin {
    @Inject(method = "randomTick", at = @At("HEAD"))
    private void throwTestException(CallbackInfo ci) {
        throw new RuntimeException("Test exception");
    }
}
