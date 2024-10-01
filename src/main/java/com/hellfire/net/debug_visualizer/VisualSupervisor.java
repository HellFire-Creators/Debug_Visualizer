package com.hellfire.net.debug_visualizer;

import com.hellfire.net.debug_visualizer.visualizers.IDebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.SingleVisualizerElementCollection;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElementCollection;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Used for managing the elements a Player can currently see. <br>
 * If you have already implemented a custom implementation of {@link net.minestom.server.entity.Player}, then implement this interface;
 * Otherwise use {@link STD}. However, keep in mind that you will have to keep the reference to the object if visuals
 * like {@link SingleVisualizerElementCollection} should work correctly!
 */
/* Created by Conor on 16.07.2024 */
public interface VisualSupervisor {

    /**
     * @return Player to display visuals too
     */
    Player getPlayer();

    /**
     * @return Type of visualizer
     */
    IDebugVisualizer getVisualizer();

    /**
     * @return Currently displayed visuals
     */
    Collection<VisualizerElementCollection> getCurrentVisibleElements();

    /**
     * NOTE: Adding elements doesn't draw them!
     * @param elements Elements to have been drawn
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    void addVisibleElements(final @NotNull VisualizerElementCollection elements);

    /**
     * NOTE: Removing elements doesn't un-draw them!
     * @param elements Elements that have been cleared
     */
    @ApiStatus.Internal
    @Deprecated(forRemoval = false)
    void removeVisibleElements(final @NotNull VisualizerElementCollection elements);

    ///////////////////////////////////////////////////////////////////////////
    // "Dummy" Impl
    ///////////////////////////////////////////////////////////////////////////

    class STD implements VisualSupervisor {

        // Conor-16.07.2024: Concurrent, just in case
        private final Queue<VisualizerElementCollection> visibleElements = new ConcurrentLinkedQueue<>();
        @Getter @Setter private IDebugVisualizer visualizer;
        private final Player player;

        private STD(@NotNull Player player, @NotNull IDebugVisualizer visualizer) {
            this.player = player;
            this.visualizer = visualizer;
        }

        public static STD create(@NotNull Player player, @NotNull IDebugVisualizer visualizer) {
            return new STD(player, visualizer);
        }

        public static STD create(@NotNull Player player) {
            return create(player, IDebugVisualizer.STD_VISUALIZER);
        }

        @Override
        public Player getPlayer() {
            return player;
        }

        @Override
        public Collection<VisualizerElementCollection> getCurrentVisibleElements() {
            return Collections.unmodifiableCollection(visibleElements);
        }

        @Override
        public void addVisibleElements(@NotNull VisualizerElementCollection elements) {
            visibleElements.add(elements);
        }

        @Override
        public void removeVisibleElements(@NotNull VisualizerElementCollection elements) {
            visibleElements.remove(elements);
        }
    }
}