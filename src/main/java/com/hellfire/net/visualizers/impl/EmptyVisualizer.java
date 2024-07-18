package com.hellfire.net.visualizers.impl;

import com.hellfire.net.options.ImplOptions;
import com.hellfire.net.visualizers.IDebugVisualizer;
import com.hellfire.net.visualizers.VisualizerElement;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* Created by Conor on 16.07.2024 */
public class EmptyVisualizer implements IDebugVisualizer<EmptyVisualizer.EmptyOptions> {

    private static final VisualizerElement EMPTY = new VisualizerElement() {
        @Override
        public void draw(@NotNull Player player) { }

        @Override
        public void clear(@NotNull Player player) { }
    };

    @Override
    public VisualizerElement createBlock(@NotNull Vec position, @Nullable EmptyVisualizer.EmptyOptions options) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB, @Nullable EmptyVisualizer.EmptyOptions options) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createPlane(@NotNull Direction dir, @NotNull Vec cornerA, @NotNull Vec cornerB, @Nullable EmptyVisualizer.EmptyOptions options) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB, @Nullable EmptyVisualizer.EmptyOptions options) {
        return EMPTY;
    }

    public static final class EmptyOptions implements ImplOptions { }
}
