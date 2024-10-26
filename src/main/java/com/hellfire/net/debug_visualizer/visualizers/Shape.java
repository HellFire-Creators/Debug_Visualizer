package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.MathUtil;
import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.impl.displayblock.DebugDisplayOptions;
import com.hellfire.net.debug_visualizer.impl.displayblock.DebugDisplayVisualizer;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleOptions;
import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleVisualizer;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;

/* Created by Conor on 02.10.2024 */
public class Shape {

    private static final double PLANE_DIR_DIFF_THRESHOLD = 0.01f;           // Used to determine when the direction should no longer affect the calculation
    private static final Vec STD_PLANE_DIR = new Vec(0, 0, 1);    // Dir plane should face wehen "spawned" in

    // Conor-03.10.2024: Me not likey!
    protected final Map<Class<? extends ImplOptions<?>>, ImplOptions<?>> optionsMap = new HashMap<>();
    protected final Function<DebugVisualizer, Collection<VisualizerElement>> visFunc;

    private Shape(ImplOptions<?>[] options, Function<DebugVisualizer, Collection<VisualizerElement>> visFunc) {
        this.visFunc = visFunc;
        for (ImplOptions<?> option : options) {
            optionsMap.put((Class<? extends ImplOptions<?>>) option.getClass(), option);
        }
    }

    public static Shape createPoint(final @NotNull Vec pos, final @NotNull ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createArea(pos.sub(0.1), pos.add(0.1)))
        );
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

    public static Shape createPlane(final double width, final double height, final @NotNull Vec center, final @NotNull Vec dir, double rot, final @Nullable ImplOptions<?>... options) {
        if (dir.equals(Vec.ZERO)) throw new IllegalArgumentException("The direction must not be zero!");
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("The width and height must be positive!");
        rot = Math.toRadians(rot % 360);
        final double hW = width / 2, hH = height / 2;
        // Corners before rotation
        final Vec nA = center.add(-hW, hH, 0), nB = center.add(hW, hH, 0);
        final Vec nC = center.add(hW, -hH, 0), nD = center.add(-hW, -hH, 0);

        // Vec from center to corner
        final Vec dA = nA.sub(center), dB = nB.sub(center);
        final Vec dC = nC.sub(center), dD = nD.sub(center);

        // Mathematically unstable otherwise!
        if (dir.abs().normalize().angle(STD_PLANE_DIR) < PLANE_DIR_DIFF_THRESHOLD) {
            return createPlaneDef(
                    dA.rotateAroundAxis(dir, rot),
                    dB.rotateAroundAxis(dir, rot),
                    dC.rotateAroundAxis(dir, rot),
                    dD.rotateAroundAxis(dir, rot),
                    center,
                    options
            );
        }

        // Rotate plane, so that normal = dir
        final Vec[] initRot = initialPlaneRot(dA, dB, dC, dD, dir);

        // Correct to expected std (a and b / c and d should have the same y value)
        final Vec iA = initRot[0], iB = initRot[1], iC = initRot[2], iD = initRot[3];
        final double correctionRot = correctRotation(iC, iD, dir, center);

        // Correct and rotate
        final Vec a = iA.rotateAroundAxis(dir, correctionRot + rot);
        final Vec b = iB.rotateAroundAxis(dir, correctionRot + rot);
        final Vec c = iC.rotateAroundAxis(dir, correctionRot + rot);
        final Vec d = iD.rotateAroundAxis(dir, correctionRot + rot);

        return createPlaneDef(a, b, c, d, center, options);
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

    /*                    Plane code                    */
    // Conor-19.10.2024: SO MUCH MATH

    private static Shape createPlaneDef(Vec a, Vec b, Vec c, Vec d, Vec center, ImplOptions<?>... options) {
        return new Shape(
                options,
                (vis) -> List.of(vis.createPlane(
                        a.add(center),
                        b.add(center),
                        c.add(center),
                        d.add(center)
                ))
        );
    }

    private static Vec[] initialPlaneRot(Vec a, Vec b, Vec c, Vec d, Vec dir) {
        final double posRot = dir.angle(STD_PLANE_DIR);
        final Vec rotAxis = STD_PLANE_DIR.cross(dir).normalize();

        final Vec[] res = new Vec[4];
        res[0] = a.rotateAroundAxis(rotAxis, posRot);
        res[1] = b.rotateAroundAxis(rotAxis, posRot);
        res[2] = c.rotateAroundAxis(rotAxis, posRot);
        res[3] = d.rotateAroundAxis(rotAxis, posRot);
        return res;
    }

    private static double correctRotation(Vec c, Vec d, Vec dir, Vec center) {
        // Assuming rot = 0:
        // a.x = b.x > c.x = d.x
        if (dir.abs().normalize().equals(Direction.UP.vec()))
            return calcRotForDir(center, dir, c, d, new Vec(0,0, 1));

        // a.y = b.y > c.y = d.y
        else
            return calcRotForDir(center, dir, c, d, new Vec(0, 1, 0));
    }

    private static double calcRotForDir(Vec center, Vec dir, Vec c, Vec d, Vec dirOffset) {
        final Vec bottomCenter = d.sub(c).div(2).add(c);            // Vec from center to bottom center

        final boolean isBelow = (bottomCenter.normalize().abs().equals(dirOffset));   // If bottomcenter is facing the direction of orientation
        final Vec vec = MathUtil.planeLineIntersection(
                center, center.add(dir), center.sub(dirOffset),   // Plane
                d.sub(c), c.add(center)  // Line
        );

        return (isBelow) ? 0 : bottomCenter.angle(vec.sub(center));
    }
}

