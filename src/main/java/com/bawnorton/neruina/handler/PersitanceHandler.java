package com.bawnorton.neruina.handler;

import com.bawnorton.neruina.Neruina;
import com.bawnorton.neruina.thread.ConditionalRunnable;
import com.bawnorton.neruina.util.TickingEntry;
import com.bawnorton.neruina.version.VersionedText;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;

public final class PersitanceHandler extends PersistentState {
    private static ServerWorld world;

    /*? if >=1.20.2 {*//*
    private static final Type<PersitanceHandler> type = new Type<>(
            PersitanceHandler::new,
            PersitanceHandler::fromNbt,
            null
    );
    *//*? } */

    public static PersitanceHandler getServerState(MinecraftServer server) {
        world = server.getWorld(World.OVERWORLD);
        assert world != null;
        PersistentStateManager manager = world.getPersistentStateManager();
        /*? if >=1.20.2 {*//*
        PersitanceHandler handler = manager.getOrCreate(type, Neruina.MOD_ID);
        *//*? } else {*/
        PersitanceHandler handler = manager.getOrCreate(PersitanceHandler::fromNbtInternal, PersitanceHandler::new, Neruina.MOD_ID);
        /*? }*/
        handler.markDirty();
        return handler;
    }

    /*? if >=1.20.2 {*//*
    private static PersitanceHandler fromNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        return fromNbtInternal(nbt);
    }
    *//*? } */

    private static PersitanceHandler fromNbt(NbtCompound nbt) {
        return fromNbtInternal(nbt);
    }

    private static PersitanceHandler fromNbtInternal(NbtCompound nbt) {
        PersitanceHandler handler = new PersitanceHandler();
        TickHandler tickHandler = Neruina.getInstance().getTickHandler();
        NbtList tickingEntries = nbt.getList("tickingEntries", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < tickingEntries.size(); i++) {
            tickHandler.addTickingEntryUnsafe(TickingEntry.fromNbt(world, tickingEntries.getCompound(i)));
        }
        return handler;
    }

    /*? if >=1.20.2 {*//*
    public NbtCompound writeNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        return writeNbtInternal(nbt);
    }
    *//*? } */

    public NbtCompound writeNbt(NbtCompound nbt) {
        return writeNbtInternal(nbt);
    }

    private NbtCompound writeNbtInternal(NbtCompound nbt) {
        NbtList tickingEntries = new NbtList();
        Neruina.getInstance()
                .getTickHandler()
                .getTickingEntries()
                .stream()
                .filter(TickingEntry::isPersitent)
                .forEach(entry -> tickingEntries.add(entry.writeNbt()));
        nbt.put("tickingEntries", tickingEntries);
        return nbt;
    }
}
