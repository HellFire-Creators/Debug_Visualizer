package com.hellfire.net.debug_visualizer.impl.particles;

import com.hellfire.net.debug_visualizer.visualizers.IDebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/* Created by Conor on 20.07.2024 */
public class DebugParticleVisualizer implements IDebugVisualizer {

    private static final int PARTICLE_LIFE_DURATION = 5;    // Time until new particles are spawned, in ticks

    @Override
    public VisualizerElement createBlock(@NotNull Vec position) {
        return new VisualizerElement() {
            private Task task;

            @Override
            public void draw(@NotNull Player player) {
                final DebugParticleOptions op = (DebugParticleOptions) options.computeIfAbsent(DebugParticleOptions.class, (k) -> DebugParticleOptions.getStd());
                final Vec cA = position, cB = position.add(1, 0, 1), cC = position.add(1, 1, 0), cD = position.add(0, 1, 1);

                final Vec[][] allPositions = {
                        calcParticlePositions(cA, cA.add(1, 0, 0), op),
                        calcParticlePositions(cA, cA.add(0, 1, 0), op),
                        calcParticlePositions(cA, cA.add(0, 0, 1), op),

                        calcParticlePositions(cB, cB.add(-1, 0, 0), op),
                        calcParticlePositions(cB, cB.add(0, 1, 0), op),
                        calcParticlePositions(cB, cB.add(0, 0, -1), op),

                        calcParticlePositions(cC, cC.add(-1, 0, 0), op),
                        calcParticlePositions(cC, cC.add(0, -1, 0), op),
                        calcParticlePositions(cC, cC.add(0, 0, 1), op),

                        calcParticlePositions(cD, cD.add(1, 0, 0), op),
                        calcParticlePositions(cD, cD.add(0, -1, 0), op),
                        calcParticlePositions(cD, cD.add(0, 0, -1), op),
                };

                final List<ParticlePacket> particles = Arrays.stream(allPositions)
                        .flatMap(Arrays::stream)
                        .map((vec) -> convertToPacket(vec, op))
                        .toList();

                task = startParticleScheduler(player, particles.toArray(ParticlePacket[]::new));
            }

            @Override
            public void clear(@NotNull Player player) {
                if (task == null) return;
                task.cancel();
            }
        };
    }

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB) {
        return null;
    }

    @Override
    public VisualizerElement createPlane(@NotNull Direction dir, @NotNull Vec cornerA, @NotNull Vec cornerB) {
        return null;
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        return new VisualizerElement() {
            private Task task;

            @Override
            public void draw(@NotNull Player player) {
                final DebugParticleOptions op = (DebugParticleOptions) options.computeIfAbsent(DebugParticleOptions.class, (k) -> DebugParticleOptions.getStd());
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
        final Particle dus = Particle.DUST.withColor(op.getColor().asRGBLike());
        return new ParticlePacket(dus, vec.x(), vec.y(), vec.z(), 0, 0, 0, 0, 1);
    }

}
