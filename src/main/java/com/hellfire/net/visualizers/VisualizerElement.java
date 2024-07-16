package com.hellfire.net.visualizers;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a visual element that can be drawn in a player's debug view.
 */
public interface VisualizerElement {

    /**
     * It is discouraged to call this function manually. Use a {@link VisualizerElementCollection} instead. <br>
     * If you manually call this function, you also have to manually call {@link #clear(Player)} as well.
     *
     * @param player Player to display the debug element to
     */
    void draw(final @NotNull Player player);

    /**
     * Clears the visual element from the players view.
     *
     * @param player Player to remove the debug element from
     */
    void clear(final @NotNull Player player);

    default VisualizerElementCollection toCollection() {
        return VisualizerElementCollection.builder()
                .addElement(this)
                .build();
    }

    default SingleVisualizerElementCollection toSingleVisCollection(final @NotNull String key) {
        return VisualizerElementCollection.builder()
                .addElement(this)
                .buildSingleVis(key);
    }
}
