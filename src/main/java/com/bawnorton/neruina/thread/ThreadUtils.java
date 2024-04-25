package com.bawnorton.neruina.thread;

import net.minecraft.util.thread.ThreadExecutor;
import java.util.function.Supplier;

public final class ThreadUtils {
    public static <T> T onThread(ThreadExecutor<?> executor, Supplier<T> supplier) {
        return executor.submit(supplier).join();
    }
}
