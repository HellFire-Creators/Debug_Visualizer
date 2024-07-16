package com.hellfire.net.visualizers;

import com.hellfire.net.VisualSupervisor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The VisualizerElementCollection class represents a collection of VisualizerElement objects.
 * It provides methods to add, remove, and retrieve elements from the collection. <br>
 * Additionally it's used to cache many {@link VisualizerElement} before displaying them via {@link #draw(VisualSupervisor)}
 * or conveniently removing them all via {@link #clear(VisualSupervisor)}.
 */
public class VisualizerElementCollection {

    private final List<VisualizerElement> elements;

    protected VisualizerElementCollection(List<VisualizerElement> elements) {
        this.elements = List.copyOf(elements);
    }

    public void draw(final @NotNull VisualSupervisor supervisor) {
        elements.forEach((e) -> e.draw(supervisor.getPlayer()));
        supervisor.addVisibleElements(this);
    }

    public void clear(final @NotNull VisualSupervisor supervisor) {
        elements.forEach((e) -> e.clear(supervisor.getPlayer()));
        supervisor.removeVisibleElements(this);
    }

    public List<VisualizerElement> getImmutableElements() {
        // List is immutable due to List#copyOf in the constructor
        return elements;
    }

    /**
     * Removes a {@link SingleVisualizerElementCollection} in case you've lost reference to the object.
     * @param supervisor Player to remove the visual from
     * @param key Key of the {@link SingleVisualizerElementCollection}
     */
    public static void removeSingleVisFromPlayer(final @NotNull VisualSupervisor supervisor, final @NotNull String key) {
        supervisor.getCurrentVisibleElements().stream()
                .filter((c) -> c instanceof SingleVisualizerElementCollection)
                .filter((s) -> ((SingleVisualizerElementCollection) s).getName().equals(key))
                .forEach((s) -> s.clear(supervisor));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    public static Builder builder() {
        return new Builder();
    }

    @Getter
    public static class Builder {

        private final List<VisualizerElement> elements = new ArrayList<>();

        private Builder() { }

        public Builder addElement(VisualizerElement element) {
            elements.add(element);
            return this;
        }

        public Builder removeElement(VisualizerElement element) {
            elements.remove(element);
            return this;
        }

        public VisualizerElementCollection build() {
            return new VisualizerElementCollection(elements);
        }

        public SingleVisualizerElementCollection buildSingleVis(final @NotNull String name) {
            return new SingleVisualizerElementCollection(elements, name);
        }

    }
}
