package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.transformations.ObjTransformation;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DebugVisualizer {

    /**
     * Creates an area marker between two specified corners.
     *
     * @param cornerA the first corner of the area
     * @param cornerB the second corner of the area
     * @param trans
     * @return a VisualizerElement representing the area marker
     */
    // Conor-02.11.2024: This is a very inconvenient way of defining an area...
    public abstract VisualizerElement createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable ObjTransformation trans);

    /**
     * Creates a plane centered on a point facing a specified direction.
     *
     * @param cornerA corner of the plane
     * @param cornerB corner of the plane
     * @param cornerC corner of the plane
     * @param cornerD corner of the plane
     * @param trans   rotation around the normal vec of the plane
     * @return a {@link VisualizerElement} representing the plane
     * @implNote Assuming that <code>rot</code> is equal to <code>0</code>, then following statements must hold: <br>
     * - a and b / c and d will have the same y-value <br>
     * - a.y = b.y > c.y = d.y <br>
     * If the plane is facing up/down-wards, then following holds: <br>
     * - a.z = b.z < c.z = d.z
     */
    public abstract VisualizerElement createPlane(Vec cornerA, Vec cornerB, Vec cornerC, Vec cornerD, ObjTransformation trans);

    /**
     * Creates a line between two specified positions.
     *
     * @param posA     the starting position of the line
     * @param posB     the end position of the line
     * @return a VisualizerElement representing the line marker
     */
    public abstract VisualizerElement createLine(final @NotNull Vec posA, final @NotNull Vec posB);

    @NotNull
    public abstract Class<? extends ImplOptions<?>> getOptionsClass();
}