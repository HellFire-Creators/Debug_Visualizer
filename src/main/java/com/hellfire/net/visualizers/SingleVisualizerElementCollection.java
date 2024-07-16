package com.hellfire.net.visualizers;


import com.hellfire.net.VisualSupervisor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a VisualizerElementCollection which once it's called again will clear the old instance and add the new one.
 */
/* Created by Conor on 12.03.2024 */
@Getter
public class SingleVisualizerElementCollection extends VisualizerElementCollection {

    private final String name;  // A name to identify vis collections from another

    protected SingleVisualizerElementCollection(List<VisualizerElement> elements, String name) {
        super(elements);
        this.name = name;
    }

    @Override
    public void draw(@NotNull VisualSupervisor supervisor) {
        // Remove already existing single vis collections
        supervisor.getCurrentVisibleElements().stream()
                .filter(vs -> vs instanceof SingleVisualizerElementCollection)
                .filter(e -> ((SingleVisualizerElementCollection) e).name.equals(this.name))
                .forEach((c) -> {
                    c.clear(supervisor);
                    supervisor.removeVisibleElements(c);
                });

        super.draw(supervisor);
    }
}
