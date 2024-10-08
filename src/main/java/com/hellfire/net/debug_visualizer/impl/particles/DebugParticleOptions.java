package com.hellfire.net.debug_visualizer.impl.particles;

import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import lombok.Getter;
import net.minestom.server.particle.Particle;
import org.jetbrains.annotations.NotNull;

/* Created by Conor on 20.07.2024 */
@Getter
public class DebugParticleOptions extends ImplOptions<DebugParticleOptions> {

    private DebugColor color    = DebugColor.getRandomColor();
    private double density      = 0.5f;  // # of particles per distance (0.5 => 2 particles per block)
    private Particle particle   = Particle.DUST;

    private DebugParticleOptions() { }

    public static DebugParticleOptions createStd() {
        return new DebugParticleOptions();
    }

    public DebugParticleOptions getStd() {
        return createStd();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ///////////////////////////////////////////////////////////////////////////

    public static DebugParticleOptions createWithColor(@NotNull DebugColor color) {
        final DebugParticleOptions options = createStd();
        options.color = color;

        return options;
    }

    public static DebugParticleOptions createWithDensity(double density) {
        if (density <= 0) throw new IllegalArgumentException("Density must be greater than zero!");

        final DebugParticleOptions options = createStd();
        options.density = density;
        return options;
    }

    public static DebugParticleOptions createWithParticle(@NotNull Particle particle) {
        final DebugParticleOptions options = createStd();
        options.particle = particle;
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

    public DebugParticleOptions setParticle(@NotNull Particle particle) {
        this.particle = particle;
        return this;
    }

}
