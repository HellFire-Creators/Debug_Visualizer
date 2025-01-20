package com.hellfire.net.debug_visualizer.impl;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.transformations.ObjTransformation;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

/* Created by Conor on 16.07.2024 */
public class EmptyVisualizer extends DebugVisualizer {

    private static final VisualizerElement EMPTY = new VisualizerElement() {

        @Override
        protected void draw(@NotNull Player player, ImplOptions<?> options) { }

        @Override
        public void clear(@NotNull Player player) { }
    };

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB, @NotNull ObjTransformation trans) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createPlane(@NotNull Vec cornerA, @NotNull Vec cornerB, @NotNull Vec cornerC, @NotNull Vec cornerD, ObjTransformation trans) {
        return EMPTY;
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        return EMPTY;
    }

    @Override
    public @NotNull Class<? extends ImplOptions<?>> getOptionsClass() {
        return EmptyOptions.class;
    }

    public static final class EmptyOptions extends ImplOptions<EmptyOptions> {
        @Override
        public EmptyOptions getStd() { return new EmptyOptions(); }
    }
}
