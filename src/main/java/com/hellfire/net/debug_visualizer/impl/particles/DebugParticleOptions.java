package com.hellfire.net.debug_visualizer.impl.particles;

import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/* Created by Conor on 20.07.2024 */
@Getter
public class DebugParticleOptions implements ImplOptions {

    private DebugColor color    = DebugColor.getRandomColor();
    private double density      = 0.5f;  // # of particles per distance (0.5 => 2 particles per block)

    private DebugParticleOptions() { }

    public static DebugParticleOptions getStd() {
        return new DebugParticleOptions();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ///////////////////////////////////////////////////////////////////////////

    public static DebugParticleOptions createWithColor(@NotNull DebugColor color) {
        final DebugParticleOptions options = new DebugParticleOptions();
        options.color = color;

        return options;
    }

    public static DebugParticleOptions createWithDensity(double density) {
        if (density <= 0) throw new IllegalArgumentException("Density must be greater than zero!");

        final DebugParticleOptions options = new DebugParticleOptions();
        options.density = density;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////

    public DebugParticleOptions setColor(@NotNull DebugColor color) {
        this.color = color;
        return this;
    }

    public DebugParticleOptions setDensity(double density) {
        if (density <= 0) throw new IllegalArgumentException("Density must be greater than zero!");
        this.density = density;
        return this;
    }

}
