package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * The VisualizerElementCollection class represents a collection of VisualizerElement objects.
 * It provides methods to add, remove, and retrieve elements from the collection. <br>
 * Additionally it's used to cache many {@link VisualizerElement} before displaying them via {@link #draw(VisualSupervisor)}
 * or conveniently removing them all via {@link #clear(VisualSupervisor)}.
 */
public class VisualizerElementCollection {

    private final List<Shape> elements;

    protected VisualizerElementCollection(List<Shape> elements) {
        this.elements = List.copyOf(elements);
    }

    public void draw(final @NotNull VisualSupervisor supervisor) {
        elements.forEach((e) -> {
            final Class<? extends ImplOptions<?>> optionsClass = supervisor.getVisualizer().getOptionsClass();
            final ImplOptions<?> option = e.optionsMap.computeIfAbsent(optionsClass, (k) -> createStdOption(optionsClass));

            if (option == null) throw new RuntimeException("Could not instantiate a default visualizer option for " + optionsClass.getSimpleName());
            e.visFunc.apply(supervisor.getVisualizer()).forEach(v -> v.draw(supervisor.getPlayer(),  option));
        });
        supervisor.addVisibleElements(this);
    }

    public void clear(final @NotNull VisualSupervisor supervisor) {
        for (Shape shape : elements) {
            shape.visFunc.apply(supervisor.getVisualizer()).forEach(v -> v.clear(supervisor.getPlayer()));
        }
        supervisor.removeVisibleElements(this);
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

    // Create std options instance, in case no option was defined
    @Nullable
    private static ImplOptions<?> createStdOption(Class<? extends ImplOptions<?>> implClass) {
        try {
            final Optional<Constructor<?>> opCon = Arrays.stream(implClass.getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 0)
                    .findFirst();
            if (opCon.isEmpty()) return null;   // Conor-02.10.2024: HOW?!
            final Constructor<?> con = opCon.get();
            con.setAccessible(true);
            return ((ImplOptions<?>) con.newInstance()).getStd();
        }
        catch(Exception e) { return null; }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    public static Builder builder() {
        return new Builder();
    }

    @Getter
    public static class Builder {

        private final List<Shape> elements = new ArrayList<>();

        private Builder() { }

        public Builder addElement(Shape element) {
            elements.add(element);
            return this;
        }

        public Builder removeElement(Shape element) {
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
