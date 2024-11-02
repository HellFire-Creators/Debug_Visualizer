package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

public abstract class DebugVisualizer {

    /**
     * Creates an area marker between two specified corners.
     *
     * @param cornerA  the first corner of the area
     * @param cornerB  the second corner of the area
     * @return a VisualizerElement representing the area marker
     */
    public abstract VisualizerElement createArea(final @NotNull Vec bottomCenter, final @NotNull Vec dim, final @NotNull Vec dir, final double angle);

    /**
     * Creates a plane centered on a point facing a specified direction.
     *
     * @param a corner of the plane
     * @param b corner of the plane
     * @param c corner of the plane
     * @param d corner of the plane
     * @param rot rotation around the normal vec of the plane
     * @return a {@link VisualizerElement} representing the plane
     * @implNote Assuming that <code>rot</code> is equal to <code>0</code>, then following statements must hold: <br>
     * - a and b / c and d will have the same y-value <br>
     * - a.y = b.y > c.y = d.y <br>
     * If the plane is facing up/down-wards, then following holds: <br>
     * - a.z = b.z < c.z = d.z
     */
    public abstract VisualizerElement createPlane(Vec a, Vec b, Vec c, Vec d, double rot);

    /**
     * Creates a line between two specified positions.
     *
     * @param posA     the starting position of the line
     * @param posB     the end position of the line
     * @return a VisualizerElement representing the line marker
     */
    public abstract VisualizerElement createLine(final @NotNull Vec posA, final @NotNull Vec posB);

    public abstract Class<? extends ImplOptions<?>> getOptionsClass();
}