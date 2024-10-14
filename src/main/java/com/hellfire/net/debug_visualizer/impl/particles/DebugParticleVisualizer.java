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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Created by Conor on 20.07.2024 */
public class DebugParticleVisualizer extends DebugVisualizer {

    private static final int PARTICLE_LIFE_DURATION = 5;    // Time until new particles are spawned, in ticks

    @Override
    public VisualizerElement createArea(@NotNull Vec a, @NotNull Vec b) {
        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> option) {
                final DebugParticleOptions op = (DebugParticleOptions) option;
                final Vec o = b.sub(a);

                final ParticlePacket[] particles = Arrays.stream(new Vec[][]{
                        // frame
                        calcParticlePositions(a, a.add(o.x(), 0, 0), op),
                        calcParticlePositions(a, a.add(0, o.y(), 0), op),
                        calcParticlePositions(a, a.add(0, 0, o.z()), op),
                        calcParticlePositions(b, b.sub(o.x(), 0, 0), op),
                        calcParticlePositions(b, b.sub(0, o.y(), 0), op),
                        calcParticlePositions(b, b.sub(0, 0, o.z()), op),
                        calcParticlePositions(a.add(0, o.y(), 0), a.add(o.x(), o.y(), 0), op),
                        calcParticlePositions(a.add(0, o.y(), 0), a.add(0, o.y(), o.z()), op),
                        calcParticlePositions(b.sub(0, o.y(), 0), b.sub(o.x(), o.y(), 0), op),
                        calcParticlePositions(b.sub(0, o.y(), 0), b.sub(0, o.y(), o.z()), op),
                        calcParticlePositions(a.add(o.x(), 0, 0), a.add(o.x(), o.y(), 0), op),
                        calcParticlePositions(a.add(0, 0, o.z()), a.add(0, o.y(), o.z()), op),

                        // fill
                        calcPlanePositions(a, a.add(o.x(), 0, 0), a.add(o.x(), o.y(), 0), a.add(0, o.y(), 0), op),
                        calcPlanePositions(a, a.add(0, 0, o.z()), a.add(0, o.y(), o.z()), a.add(0, o.y(), 0), op),
                        calcPlanePositions(a, a.add(o.x(), 0, 0), a.add(o.x(), 0, o.z()), a.add(0, 0, o.z()), op),

                        calcPlanePositions(b, b.add(-o.x(), 0, 0), b.add(-o.x(), -o.y(), 0), b.add(0, -o.y(), 0), op),
                        calcPlanePositions(b, b.add(0, 0, -o.z()), b.add(0, -o.y(), -o.z()), b.add(0, -o.y(), 0), op),
                        calcPlanePositions(b, b.add(-o.x(), 0, 0), b.add(-o.x(), 0, -o.z()), b.add(0, 0, -o.z()), op),
                        })
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
    protected VisualizerElement createPlane(Vec a, Vec b, Vec c, Vec d) {
        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugParticleOptions op = (DebugParticleOptions) options;
                final ParticlePacket[] particles = Arrays.stream(new Vec[][] {
                        // frame
                        calcParticlePositions(a, b, op),
                        calcParticlePositions(b, c, op),
                        calcParticlePositions(c, d, op),
                        calcParticlePositions(d, a, op),

                        // fill
                        calcPlanePositions(a, b, c, d, op),
                        })
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

    // Only calcs the points INSIDE the plane
    private static Vec[] calcPlanePositions(Vec a, Vec b, Vec c, Vec d, DebugParticleOptions op) {
        if  (op.getFillDensity() == 0) return new Vec[0];
        final List<Vec> positions = new ArrayList<>();

        final Vec ad = d.sub(a);
        final Vec ab = b.sub(a);

        final double abLen = ab.length();
        final double abSteps = abLen / op.getFillDensity();
        final double adLen = ad.length();
        final double adSteps = adLen / op.getFillDensity();

        final Vec xStepSize = ab.normalize().mul(op.getFillDensity());
        final Vec yStepSize = ad.normalize().mul(op.getFillDensity());

        // Create filled area
        for (int y = 1; y < adSteps; y++) {
            final Vec start = a.add(yStepSize.mul(y));

            for (int x = 1; x < abSteps; x++)
                positions.add(start.add(xStepSize.mul(x)));
        }

        return positions.toArray(Vec[]::new);
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
