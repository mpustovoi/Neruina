package com.bawnorton.neruina.mixin.errorable;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.extend.Errorable;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.UUID;

/*? if >=1.20.2 {*/
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
/*? }*/

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements Errorable {
    /*? if >=1.20.2 {*/
    @Shadow @Final
    ComponentMapImpl components;
    /*? } else {*//*
    @Shadow public abstract NbtCompound getOrCreateNbt();

    @Shadow @Nullable
    public abstract NbtCompound getNbt();

    @Shadow public abstract boolean hasNbt();
    *//*? }*/

    @Unique
    private boolean neruina$errored = false;

    @Unique
    private UUID neruina$tickingEntryId = null;

    @Override
    public boolean neruina$isErrored() {
        return neruina$errored;
    }

    @Override
    public void neruina$setErrored() {
        neruina$errored = true;
        neruina$updateData();
    }

    @Override
    public void neruina$clearErrored() {
        neruina$errored = false;
        neruina$updateData();
    }

    @Override
    public void neruina$setTickingEntryId(UUID uuid) {
        neruina$tickingEntryId = uuid;
        neruina$updateData();
    }

    @Override
    public UUID neruina$getTickingEntryId() {
        return neruina$tickingEntryId;
    }

    /*? if >=1.20.2 {*/
    @Unique
    private void neruina$updateData() {
        ComponentChanges.Builder builder = ComponentChanges.builder()
                .add(Neruina.getInstance().getErroredComponent(), neruina$errored);
        if (neruina$tickingEntryId != null) {
            builder.add(Neruina.getInstance().getTickingEntryIdComponent(), neruina$tickingEntryId);
        }
        components.applyChanges(builder.build());
    }

    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;ILnet/minecraft/component/ComponentMapImpl;)V", at = @At("TAIL"))
    private void readErroredFromComponents(ItemConvertible item, int count, ComponentMapImpl components, CallbackInfo ci) {
        neruina$errored = components.getOrDefault(Neruina.getInstance().getErroredComponent(), false);
        neruina$tickingEntryId = components.getOrDefault(Neruina.getInstance().getTickingEntryIdComponent(), null);
    }
    /*? } else {*//*
    @Unique
    private void neruina$updateData() {
        NbtCompound nbt = getOrCreateNbt();
        if (neruina$errored) {
            nbt.putBoolean("neruina$errored", true);
            if (neruina$tickingEntryId != null) {
                nbt.putUuid("neruina$tickingEntryId", neruina$tickingEntryId);
            }
        } else {
            nbt.remove("neruina$errored");
            nbt.remove("neruina$tickingEntryId");
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    private void readErroredFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if(hasNbt()) {
            NbtCompound tag = getNbt();
            assert tag != null;
            if (tag.contains("neruina$errored")) {
                neruina$errored = tag.getBoolean("neruina$errored");
            }
            if (tag.contains("neruina$tickingEntryId")) {
                neruina$tickingEntryId = tag.getUuid("neruina$tickingEntryId");
            }
        }
    }
    *//*? }*/
}
