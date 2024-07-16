package com.hellfire.net.visualizers;

import com.hellfire.net.options.DebugAreaOptions;
import com.hellfire.net.options.DebugColorOptions;
import com.hellfire.net.visualizers.impl.EmptyVisualizer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IDebugVisualizer {

    IDebugVisualizer STD_VISUALIZER = new EmptyVisualizer();

    /**
     * Creates a block marker at the specified position.
     *
     * @param position the position at which the block marker should be drawn
     * @param options  the optional debug block options
     * @return a VisualizerElement representing the block marker
     */
    VisualizerElement createBlock(final @NotNull Vec position, final @Nullable DebugColorOptions options);

    /**
     * Creates an area marker between two specified corners.
     *
     * @param cornerA  the first corner of the area
     * @param cornerB  the second corner of the area
     * @param options  the optional debug area options
     * @return a VisualizerElement representing the area marker
     */
    VisualizerElement createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable DebugAreaOptions options);


    /**
     * Creates a plane marker between two specified corners which lies on a specific axis.
     *
     * @param dir       the axis in which the plane lies
     * @param cornerA   the first corner of the plane
     * @param cornerB   the second corner of the plane
     * @param options   the optional debug area options
     * @return a VisualizerElement representing the plane marker
     */
    VisualizerElement createPlane(final @NotNull Direction dir, final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable DebugAreaOptions options);

    /**
     * Creates a line between two specified positions.
     *
     * @param posA     the starting position of the line
     * @param posB     the end position of the line
     * @param options  the optional debug block options
     * @return a VisualizerElement representing the line marker
     */
    VisualizerElement createLine(final @NotNull Vec posA, final @NotNull Vec posB, final @Nullable DebugColorOptions options);
}