package com.hellfire.net.debug_visualizer.impl.particles;

import com.hellfire.net.debug_visualizer.MathUtil;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.transformations.ObjTransformation;
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
import java.util.HashSet;
import java.util.List;

/* Created by Conor on 20.07.2024 */
public class DebugParticleVisualizer extends DebugVisualizer {

    private static final int PARTICLE_LIFE_DURATION = 5;    // Time until new particles are spawned, in ticks

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB, ObjTransformation trans) {
        final Vec min = cornerA.min(cornerB);
        final Vec max = cornerA.max(cornerB);
        final Vec mid = min.sub(max);
        // mid is now "0 0"

        // All 8 corners of the cube
        final Vec[] corners = Arrays.stream(new Vec[]{
                        new Vec(0, 0, 0), new Vec(1, 0, 0), new Vec(1, 0, 1), new Vec(0, 0, 1),
                        new Vec(0, 1, 0), new Vec(1, 1, 0), new Vec(1, 1, 1), new Vec(0, 1, 1),
                }).map((v) -> v.mul(mid).sub(mid.div(2)))
                .toArray(Vec[]::new);

        final Vec[] points = new ObjTransformation()
                .translate(min.sub(mid.div(2)))
                .add((trans == null) ? new ObjTransformation() : trans)
                .matrixFromOperations()
                .affine()
                .transformPoints(corners);

        final Vec a = points[0], b = points[1], c = points[2], d = points[3];
        final Vec e = points[4], f = points[5], g = points[6], h = points[7];

        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugParticleOptions op = (DebugParticleOptions) options;
                final ParticlePacket[] particles = Arrays.stream(new Vec[][]{
                                // frame
                                calcParticlePositions(a, b, op),
                                calcParticlePositions(b, c, op),
                                calcParticlePositions(c, d, op),
                                calcParticlePositions(d, a, op),

                                calcParticlePositions(e, f, op),
                                calcParticlePositions(f, g, op),
                                calcParticlePositions(g, h, op),
                                calcParticlePositions(h, e, op),

                                calcParticlePositions(a, e, op),
                                calcParticlePositions(b, f, op),
                                calcParticlePositions(c, g, op),
                                calcParticlePositions(d, h, op),

                                // fill
                                calcPlanePositions(a, b, c, d, op),
                                calcPlanePositions(e, f, g, h, op),

                                calcPlanePositions(a, e, f, b, op),
                                calcPlanePositions(b, f, g, c, op),
                                calcPlanePositions(c, g, h, d, op),
                                calcPlanePositions(d, h, e, a, op),
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
    public VisualizerElement createPlane(Vec cornerA, Vec cornerB, Vec cornerC, Vec cornerD, ObjTransformation trans) {
        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                if (!allDifferent(cornerA, cornerB, cornerC, cornerD)) throw new Error("All corners must be unique; Cannot construct plane");
                final boolean allOnPlane = MathUtil.pointLiesOnPlane(cornerA, cornerB, cornerC, cornerD);
//                if (!allOnPlane) throw new Error("All points must lie on a plane; Cannot construct plane");

                final DebugParticleOptions op = (DebugParticleOptions) options;
                final ParticlePacket[] particles = Arrays.stream(new Vec[][]{
                                // frame
                                calcParticlePositions(cornerA, cornerB, op),
                                calcParticlePositions(cornerB, cornerC, op),
                                calcParticlePositions(cornerC, cornerD, op),
                                calcParticlePositions(cornerD, cornerA, op),

                                // fill
                                calcPlanePositions(cornerA, cornerB, cornerC, cornerD, op),
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
    public @NotNull Class<? extends ImplOptions<?>> getOptionsClass() {
        return DebugParticleOptions.class;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper functions
    ///////////////////////////////////////////////////////////////////////////

    private static boolean allDifferent(Vec... vs) {
        return new HashSet<>(List.of(vs)).size() == vs.length;
    }

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
        if (op.getFillDensity() == 0) return new Vec[0];
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
