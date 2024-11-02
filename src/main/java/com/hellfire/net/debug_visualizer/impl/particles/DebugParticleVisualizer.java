package com.hellfire.net.debug_visualizer.impl.particles;

import com.hellfire.net.debug_visualizer.MathUtil;
import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.Shape;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElementCollection;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Created by Conor on 20.07.2024 */
public class DebugParticleVisualizer extends DebugVisualizer {

    private static final int PARTICLE_LIFE_DURATION = 5;    // Time until new particles are spawned, in ticks

    @Override
    public VisualizerElement createArea(@NotNull Vec bottomCenter, @NotNull Vec dim, @NotNull Vec dir, double angle) {
        final Vec STD_AREA_DIR = Direction.UP.vec();
        final double hW = dim.x() / 2, hL = dim.z() / 2;
        final double height = dim.y();

        // Calc bottom points
        // ALL RELATIVE TO Vec.ZERO!
        // Not facing dir or rotated:
        final Vec sA = new Vec(-hW, 0, -hL), sB = new Vec(hW, 0, -hL);
        final Vec sC = new Vec(hW, 0, hL), sD = new Vec(-hW, 0, hL);

        // Make them face dir
        final double normRot = STD_AREA_DIR.angle(dir);
        final Vec normRotAxis = (dir.abs().equals(STD_AREA_DIR)) ? STD_AREA_DIR : STD_AREA_DIR.cross(dir).normalize();
        final Vec dA = sA.rotateAroundAxis(normRotAxis, normRot), dB = sB.rotateAroundAxis(normRotAxis, normRot);
        final Vec dC = sC.rotateAroundAxis(normRotAxis, normRot), dD = sD.rotateAroundAxis(normRotAxis, normRot);

        // Determine rotation correction factor
        // The prior rotation messes up the positioning of the corners, so we have to correct them
        final Vec bbc = dB.sub(dA).div(2).add(dA);
        final Vec tbc = MathUtil.planeLineIntersection(
                Vec.ZERO, dA, dB,
                dir, STD_AREA_DIR
        );

        final double correctionRot = (dir.abs().equals(STD_AREA_DIR)) ? 0 : bbc.normalize().angle(tbc.normalize());

        // In this step, we also apply the actual/final rotation
        final Vec fA = dA.rotateAroundAxis(dir, Math.toRadians(angle)).add(bottomCenter);
        final Vec fB = dB.rotateAroundAxis(dir, Math.toRadians(angle)).add(bottomCenter);
        final Vec fC = dC.rotateAroundAxis(dir, Math.toRadians(angle)).add(bottomCenter);
        final Vec fD = dD.rotateAroundAxis(dir, Math.toRadians(angle)).add(bottomCenter);

        // Calc other side
        final Vec fE = fA.add(dir.mul(height)), fF = fB.add(dir.mul(height));
        final Vec fG = fC.add(dir.mul(height)), fH = fD.add(dir.mul(height));

        return new VisualizerElement() {
            private Task task;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugParticleOptions op = (DebugParticleOptions) options;
                final ParticlePacket[] particles = Arrays.stream(new Vec[][]{
                                // frame
                                calcParticlePositions(fA, fB, op),
                                calcParticlePositions(fB, fC, op),
                                calcParticlePositions(fC, fD, op),
                                calcParticlePositions(fD, fA, op),

                                calcParticlePositions(fE, fF, op),
                                calcParticlePositions(fF, fG, op),
                                calcParticlePositions(fG, fH, op),
                                calcParticlePositions(fH, fE, op),

                                calcParticlePositions(fA, fE, op),
                                calcParticlePositions(fB, fF, op),
                                calcParticlePositions(fC, fG, op),
                                calcParticlePositions(fD, fH, op),

                                // fill
                                calcPlanePositions(fA, fB, fC, fD, op),
                                calcPlanePositions(fE, fF, fG, fH, op),

                                calcPlanePositions(fA, fE, fF, fB, op),
                                calcPlanePositions(fB, fF, fG, fC, op),
                                calcPlanePositions(fC, fG, fH, fD, op),
                                calcPlanePositions(fD, fH, fE, fA, op),
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
    public VisualizerElement createPlane(Vec a, Vec b, Vec c, Vec d, double rot) {
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
