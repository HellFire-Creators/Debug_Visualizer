package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.transformations.ObjTransformation;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/* Created by Conor on 02.10.2024 */
public final class Shape {

    private static final double POINT_SIZE = 0.1;

    // Conor-03.10.2024: Me not likey!
    final Map<Class<? extends ImplOptions<?>>, ImplOptions<?>> optionsMap = new HashMap<>();
    final Function<DebugVisualizer, Collection<VisualizerElement>> visFunc;

    public Shape(ImplOptions<?>[] options, Function<DebugVisualizer, Collection<VisualizerElement>> visFunc) {
        this.visFunc = visFunc;
        for (ImplOptions<?> option : options) {
            optionsMap.put((Class<? extends ImplOptions<?>>) option.getClass(), option);
        }
    }

    public static Shape createPoint(final @NotNull Vec pos, final @NotNull ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createArea(
                        pos.sub(POINT_SIZE / 2), pos.add(POINT_SIZE / 2),
                        null
                ))
        );
    }

    public static Shape createBlock(final @NotNull Vec position,
                                    final @Nullable ObjTransformation trans,
                                    final @Nullable ImplOptions<?>... options) {
        return createBlockArea(position, position, trans, options);
    }

    public static Shape createBlockArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB,
                                        final @Nullable ObjTransformation trans,
                                        final @Nullable ImplOptions<?>... options) {
        final Vec cA = cornerA.min(cornerB);
        final Vec cB = cornerA.max(cornerB).add(1);

        return createArea(cA, cB, trans, options);
    }

    public static Shape createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB,
                                   final @Nullable ObjTransformation trans,
                                   final @Nullable ImplOptions<?>... options) {

        return new Shape(
                options,
                (vis) -> List.of(vis.createArea(cornerA, cornerB, trans))
        );
    }

    public static Shape createPlane(final double width, final double height, final @NotNull Vec center, final @NotNull ObjTransformation trans,
                                    final @Nullable ImplOptions<?>... options) {
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("The width and height must be positive!");
        final double hw = width / 2, hh = height / 2;
        final Vec[] initPoints = new Vec[]{
                new Vec(hw, 0, hh),
                new Vec(-hw, 0, -hh),
                new Vec(hw, 0, -hh),
                new Vec(-hw, 0, hh),
        };

        final Vec[] rotatedPoints = new ObjTransformation()
                .translate(center)
                .add(trans)
                .matrixFromOperations()
                .transformPoints(initPoints);

        return new Shape(
                options,
                (vis) -> List.of(vis.createPlane(rotatedPoints[0], rotatedPoints[2], rotatedPoints[1], rotatedPoints[3], trans))
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

