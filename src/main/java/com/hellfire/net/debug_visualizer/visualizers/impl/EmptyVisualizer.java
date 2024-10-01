package com.hellfire.net.debug_visualizer.visualizers.impl;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.visualizers.IDebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

/* Created by Conor on 16.07.2024 */
public class EmptyVisualizer implements IDebugVisualizer {

    private static final VisualizerElement EMPTY = new VisualizerElement() {
        @Override
        public void draw(@NotNull Player player) { }

        @Override
        public void clear(@NotNull Player player) { }
    };

    @Override
    public VisualizerElement createBlock(@NotNull Vec position) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createPlane(@NotNull Direction dir, @NotNull Vec cornerA, @NotNull Vec cornerB) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        return EMPTY;
    }

    public static final class EmptyOptions implements ImplOptions { }
}
