package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;

/* Created by Conor on 02.10.2024 */
public class Shape {

    private static final double PLANE_DIR_DIFF_THRESHOLD = 0.02f;   // Used to determine when the direction should no longer affect the calculation

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

    public static Shape createPlane(final double width, final double height, final @NotNull Vec center, final @NotNull Vec dir, double rot, final @Nullable ImplOptions<?>... options) {
        if (dir.equals(Vec.ZERO)) throw new InvalidParameterException("The direction must not be zero!");
        rot = Math.toRadians(rot);
        final double hW = width / 2, hH = height / 2;
        // Corners before rotation
        final Vec nA = center.add(-hW, hH, 0), nB = center.add(hW, hH, 0);
        final Vec nC = center.add(hW, -hH, 0), nD = center.add(-hW, -hH, 0);

        // Vec from center to corner
        final Vec dA = nA.sub(center), dB = nB.sub(center);
        final Vec dC = nC.sub(center), dD = nD.sub(center);

        // Corners after rotating around plane axis
        final Vec rA = dA.rotateAroundZ(rot).add(center);
        final Vec rB = dB.rotateAroundZ(rot).add(center);
        final Vec rC = dC.rotateAroundZ(rot).add(center);
        final Vec rD = dD.rotateAroundZ(rot).add(center);

        // https://stackoverflow.com/a/23699458
        final Vec dirA = new Vec(0, 0, 1);
        final Vec dirB = dir.normalize();

        if (dirA.normalize().sub(dirB.normalize()).length() < PLANE_DIR_DIFF_THRESHOLD) {
            return new Shape(
                    options,
                    (vis) -> List.of(vis.createPlane(rA, rB, rC, rD))
            );
        }

        final float posRot = (float) Math.acos(dirA.dot(dirB));
        final Vec rotAxis = dirA.cross(dirB).normalize();

        // Conor-16.10.2024: A bit stupid with subtracting and then adding back, but... meh
        final Vec a = rA.sub(center).rotateAroundAxis(rotAxis, posRot).add(center);
        final Vec b = rB.sub(center).rotateAroundAxis(rotAxis, posRot).add(center);
        final Vec c = rC.sub(center).rotateAroundAxis(rotAxis, posRot).add(center);
        final Vec d = rD.sub(center).rotateAroundAxis(rotAxis, posRot).add(center);

        return new Shape(
                options,
                (vis) -> List.of(vis.createPlane(a, b, c, d))
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

