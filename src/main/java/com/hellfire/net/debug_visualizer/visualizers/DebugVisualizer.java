package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;

public abstract class DebugVisualizer {

    /**
     * Creates a block marker at the specified position.
     *
     * @param position the position at which the block marker should be drawn
     * @return a VisualizerElement representing the block marker
     */
    public abstract VisualizerElement createBlock(final @NotNull Vec position);

    /**
     * Creates an area marker between two specified corners.
     *
     * @param cornerA  the first corner of the area
     * @param cornerB  the second corner of the area
     * @return a VisualizerElement representing the area marker
     */
    public abstract VisualizerElement createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB);

    /**
     * Creates a plane using three points, each of which act as a corner of the plane. The fourth corner is then inferred by the other 3. <br>
     * If all three corners are placed in a line spanned by these three points, a {@link java.security.InvalidParameterException} will be thrown.
     *
     * @implNote The last corner is determined by taking the offset between corners B and C and applying it to cornerA.
     *
     * @param cornerA the first corner of the plane
     * @param cornerB the second corner of the plane
     * @param cornerC the third corner of the plane
     * @return a VisualizerElement representing the plane marker
     */
    public VisualizerElement createPlane(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @NotNull Vec cornerC) {
        // Conor-03.10.2024: Check the corners and only then call the impl

        final Vec v = cornerB.sub(cornerA).abs().normalize();
        final Vec d = cornerC.sub(cornerA).abs().normalize();
        if (d.equals(v)) throw new InvalidParameterException("All three points lie on a line!");

        final Vec cornerD = cornerC.sub(cornerB).add(cornerA);
        return createPlaneImpl(cornerA, cornerB, cornerC, cornerD);
    }

    protected abstract VisualizerElement createPlaneImpl(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @NotNull Vec cornerC, final @NotNull Vec cornerD);

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