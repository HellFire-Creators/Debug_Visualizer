package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a visual element that can be drawn in a player's debug view.
 */
public abstract class VisualizerElement {

    /**
     * It is discouraged to call this function manually. Use a {@link VisualizerElementCollection} instead. <br>
     * If you manually call this function, you also have to manually call {@link #clear(Player)} as well.
     *
     * @param player Player to display the debug element to
     */
    protected abstract void draw(final @NotNull Player player, final ImplOptions<?> options);

    /**
     * Clears the visual element from the players view.
     *
     * @param player Player to remove the debug element from
     */
    protected abstract void clear(final @NotNull Player player);

}
