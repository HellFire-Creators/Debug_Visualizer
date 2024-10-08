package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/* Created by Conor on 02.10.2024 */
public class Shape {

    // Conor-03.10.2024: Me not likey!
    protected final Map<Class<? extends ImplOptions<?>>, ImplOptions<?>> optionsMap = new HashMap<>();
    protected final Function<DebugVisualizer, VisualizerElement> visFunc;

    private Shape(ImplOptions<?>[] options, Function<DebugVisualizer, VisualizerElement> visFunc) {
        this.visFunc = visFunc;
        for (ImplOptions<?> option : options) {
            optionsMap.put((Class<? extends ImplOptions<?>>) option.getClass(), option);
        }
    }

    public static Shape createBlock(final @NotNull Vec position, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> vis.createBlock(position)
        );
    }

    public static Shape createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> vis.createArea(cornerA, cornerB)
        );
    }

    public static Shape createPlane(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @NotNull Vec cornerC, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> vis.createPlane(cornerA, cornerB, cornerC)
        );
    }

    public static Shape createLine(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> vis.createLine(cornerA, cornerB)
        );
    }
}
