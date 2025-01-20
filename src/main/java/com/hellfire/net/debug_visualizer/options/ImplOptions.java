package com.hellfire.net.debug_visualizer.options;

import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;

/**
 * This interface represents options that are present in specific implementations.
 * Since different implementations might have vastly different options,
 * this interface is empty to allow for any options to be specified.
 */
/* Created by Conor on 18.07.2024 */
public abstract class ImplOptions<T extends ImplOptions<T>> {

    private DebugVisualizer debugVis = null;   // If null => show no debug data; !null => show extra data with vis
    private DebugColor overrideColor = null;

    public ImplOptions() { /* EMPTY CONSTRUCTOR */ }

    public T withExtraDebug(final @Nullable DebugVisualizer vis) {
        this.debugVis = vis;
        return (T) this;
    }

    public T withOverrideColor(final @Nullable DebugColor color) {
        this.overrideColor = color;
        return (T) this;
    }

    public boolean showExtraDebugInfo() {
        return this.debugVis != null;
    }

    @Nullable
    public DebugVisualizer getExtraDebugVis() {
        return debugVis;
    }

    public boolean hasOverrideColor() {
        return this.overrideColor != null;
    }

    @Nullable
    public DebugColor getOverrideColor() {
        return overrideColor;
    }

    public abstract T getStd();

    @ApiStatus.Experimental
    @Nullable
    public static ImplOptions<?> getStdFromClass(final @NotNull Class<? extends ImplOptions<?>> clazz) {
        try {
            final Optional<Constructor<?>> opCon = Arrays.stream(clazz.getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 0)
                    .findFirst();

            if (opCon.isEmpty()) return null;   // Conor-02.10.2024: HOW?!
            final Constructor<?> con = opCon.get();
            con.setAccessible(true);
            return ((ImplOptions<?>) con.newInstance()).getStd();
        }
        catch(Exception e) { return null; }
    }

}
