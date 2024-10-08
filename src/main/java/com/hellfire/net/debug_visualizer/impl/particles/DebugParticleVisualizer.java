package com.hellfire.net.debug_visualizer.impl.particles;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/* Created by Conor on 20.07.2024 */
public class DebugParticleVisualizer extends DebugVisualizer {

    private static final int PARTICLE_LIFE_DURATION = 5;    // Time until new particles are spawned, in ticks

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB) {
        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> option) {
                final DebugParticleOptions op = (DebugParticleOptions) option;
                final Vec offset = cornerB.sub(cornerA);

                final Vec[][] allPositions = {
                        calcParticlePositions(cornerA, cornerA.add(offset.x(), 0, 0), op),
                        calcParticlePositions(cornerA, cornerA.add(0, offset.y(),0), op),
                        calcParticlePositions(cornerA, cornerA.add(0, 0, offset.z()), op),

                        calcParticlePositions(cornerB, cornerB.sub(offset.x(), 0, 0), op),
                        calcParticlePositions(cornerB, cornerB.sub(0, offset.y(), 0), op),
                        calcParticlePositions(cornerB, cornerB.sub(0, 0, offset.z()), op),

                        calcParticlePositions(cornerA.add(offset.x(), 0, 0), cornerB.sub(0, offset.y(), 0), op),
                        calcParticlePositions(cornerA.add(offset.x(), 0, 0), cornerB.sub(0, 0, offset.z()), op),
                        calcParticlePositions(cornerA.add(0, offset.y(), 0), cornerB.sub(0, 0, offset.z()), op),
                        calcParticlePositions(cornerA.add(0, offset.y(), 0), cornerB.sub(offset.x(), 0, 0), op),
                        calcParticlePositions(cornerA.add(0, 0, offset.z()), cornerB.sub(0, offset.y(), 0), op),
                        calcParticlePositions(cornerA.add(0, 0, offset.z()), cornerB.sub(offset.x(), 0, 0), op),
                };

                final ParticlePacket[] particles = Arrays.stream(allPositions)
                        .flatMap(Arrays::stream)
                        .map((vec) -> convertToPacket(vec, op))
                        .toArray(ParticlePacket[]::new);

                task = startParticleScheduler(player, particles);
            }

            @Override
            protected void clear(@NotNull Player player) {
                if (task == null) return;
                task.cancel();
            }
        };
    }

    @Override
    public VisualizerElement createPlaneImpl(@NotNull Vec cornerA, @NotNull Vec cornerB, @NotNull Vec cornerC, @NotNull Vec cornerD) {
        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> option) {
                final DebugParticleOptions op = (DebugParticleOptions) option;
                final Vec[][] allPositions = {
                        calcParticlePositions(cornerA, cornerB, op),
                        calcParticlePositions(cornerB, cornerC, op),
                        calcParticlePositions(cornerC, cornerD, op),
                        calcParticlePositions(cornerA, cornerD, op),
                };

                final ParticlePacket[] particles = Arrays.stream(allPositions)
                        .flatMap(Arrays::stream)
                        .map((vec) -> convertToPacket(vec, op))
                        .toArray(ParticlePacket[]::new);

                task = startParticleScheduler(player, particles);
            }

            @Override
            public void clear(@NotNull Player player) {
                if (task == null) return;
                task.cancel();
            }
        };
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> option) {
                final DebugParticleOptions op = (DebugParticleOptions) option;
                final ParticlePacket[] particles = Arrays.stream(calcParticlePositions(posA, posB, op))
                        .map((vec) -> convertToPacket(vec, op))
                        .toArray(ParticlePacket[]::new);

                task = startParticleScheduler(player, particles);
            }

            @Override
            public void clear(@NotNull Player player) {
                if (task == null) return;
                task.cancel();
            }
        };
    }

    @Override
    public Class<? extends ImplOptions<?>> getOptionsClass() {
        return DebugParticleOptions.class;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper functions
    ///////////////////////////////////////////////////////////////////////////

    // Calcs te particles from posA to posB
    private static Vec[] calcParticlePositions(Vec posA, Vec posB, DebugParticleOptions options) {
        final double density = options.getDensity();

        final Vec aB = posB.sub(posA);
        final double dist = aB.length();

        Vec dirStep = aB.normalize().mul(density);   // From A to B

        // Ensures that the vector is only ever as long as the points are apart
        if (dirStep.length() > dist) dirStep = aB.normalize().mul(dist);
        final int stepCount = (int) Math.max(1, (aB.length() / density));

        final Vec[] positions = new Vec[stepCount + 1];
        for (int i = 0; i < stepCount; i++) {
            positions[i] = dirStep.mul(i).add(posA);
        }
        positions[positions.length - 1] = posB;

        return positions;
    }

    private static Task startParticleScheduler(Player player, ParticlePacket[] particles) {
        return MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (!player.isOnline()) return TaskSchedule.stop();
            player.sendPackets(particles);
            return TaskSchedule.tick(PARTICLE_LIFE_DURATION);
        }, TaskSchedule.tick(PARTICLE_LIFE_DURATION));
    }

    private static @NotNull ParticlePacket convertToPacket(Vec vec, DebugParticleOptions op) {
        if (op.getParticle().id() == Particle.DUST.id()) {
            final Particle dus = Particle.DUST.withColor(op.getColor().asRGBLike());
            return new ParticlePacket(dus, vec.x(), vec.y(), vec.z(), 0, 0, 0, 0, 1);
        }

        return new ParticlePacket(op.getParticle(), vec.x(), vec.y(), vec.z(), 0, 0, 0, 0, 1);
    }

}
