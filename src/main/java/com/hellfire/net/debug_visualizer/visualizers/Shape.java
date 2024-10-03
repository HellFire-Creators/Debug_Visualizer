package com.hellfire.net.debug_visualizer.visualizers;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/* Created by Conor on 02.10.2024 */
public class Shape {

    // Conor-03.10.2024: Me not likey!
    protected final Map<Class<? extends ImplOptions<?>>, ImplOptions<?>> optionsMap;
    protected final Function<DebugVisualizer, VisualizerElement> visFunc;

    private Shape(@Nullable Consumer<ConfigurableOptions> configurer, Function<DebugVisualizer, VisualizerElement> visFunc) {
        this.visFunc = visFunc;
        if (configurer == null) this.optionsMap = new HashMap<>();
        else {
            final ConfigurableOptions options = new ConfigurableOptions();
            configurer.accept(options);
            this.optionsMap = options.optionsMap;
        }
    }

    public static Shape createBlock(final @NotNull Vec position, final @Nullable Consumer<ConfigurableOptions> configurer) {
        return new Shape(
                configurer,
                (vis) -> vis.createBlock(position)
        );
    }

    public static Shape createArea(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable Consumer<ConfigurableOptions> configurer) {
        return new Shape(
                configurer,
                (vis) -> vis.createArea(cornerA, cornerB)
        );
    }

    public static Shape createPlane(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @NotNull Vec cornerC, final @Nullable Consumer<ConfigurableOptions> configurer) {
        return new Shape(
                configurer,
                (vis) -> vis.createPlane(cornerA, cornerB, cornerC)
        );
    }

    public static Shape createLine(final @NotNull Vec cornerA, final @NotNull Vec cornerB, final @Nullable Consumer<ConfigurableOptions> configurer) {
        return new Shape(
                configurer,
                (vis) -> vis.createLine(cornerA, cornerB)
        );
    }


    public static final class ConfigurableOptions {

        private final Map<Class<? extends ImplOptions<?>>, ImplOptions<?>> optionsMap = new HashMap<>();

        private ConfigurableOptions() { /* EMPTY */ }

        public <T extends ImplOptions<T>> ConfigurableOptions withOption(final Class<T> optionClazz, final T option) {
            optionsMap.put(optionClazz, option);
            return this;
        }
    }
}
