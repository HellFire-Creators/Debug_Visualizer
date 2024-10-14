package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/* Created by Conor on 02.10.2024 */
public class Shape {

    // Conor-03.10.2024: Me not likey!
    protected final Map<Class<? extends ImplOptions<?>>, ImplOptions<?>> optionsMap = new HashMap<>();
    protected final Function<DebugVisualizer, Collection<VisualizerElement>> visFunc;

    private Shape(ImplOptions<?>[] options, Function<DebugVisualizer, Collection<VisualizerElement>> visFunc) {
        this.visFunc = visFunc;
        for (ImplOptions<?> option : options) {
            optionsMap.put((Class<? extends ImplOptions<?>>) option.getClass(), option);
        }
    }

    public static Shape createBlock(final @NotNull Vec position, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createBlock(position))
        );
    }

    public static Shape createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createArea(cornerA, cornerB))
        );
    }

    public static Shape createPlane(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @NotNull Vec cornerC, final @NotNull Vec cornerD, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createPlane(cornerA, cornerB, cornerC, cornerD))
        );
    }

    public static Shape createLine(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createLine(cornerA, cornerB))
        );
    }

    public static Shape createPolygon(final @NotNull Vec[] points, @Nullable ImplOptions<?>... options) {
        if (points.length <= 1) throw new IllegalArgumentException("At least two points are required");

        return new Shape(
                options,
                (vis) -> createPolygonElements(points, vis)
        );

    }

    private static List<VisualizerElement> createPolygonElements(Vec[] points, DebugVisualizer vis) {
        final List<VisualizerElement> elems = new ArrayList<>(points.length - 1);
        Vec start = points[0];
        for (int i = 1; i < points.length; i++) {
            elems.add(vis.createLine(start, points[i]));
            start = points[i];
        }

        return elems;
    }
}

