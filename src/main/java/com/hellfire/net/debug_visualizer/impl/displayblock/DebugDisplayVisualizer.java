package com.hellfire.net.debug_visualizer.impl.displayblock;

import com.hellfire.net.debug_visualizer.MathUtil;
import com.hellfire.net.debug_visualizer.VisualSupervisor;
import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.Shape;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElementCollection;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

/* Created by Conor on 03.10.2024 */
public class DebugDisplayVisualizer extends DebugVisualizer {

    private static final double MIN_THICKNESS = 0.1d;

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB) {
        final Vec cA = cornerA.min(cornerB), cB = cornerA.max(cornerB);

        return new VisualizerElement() {
            int entId;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugDisplayOptions op = (DebugDisplayOptions) options;

                final Entity ent = getEntity(op, (meta) -> {
                    meta.setScale(cB.sub(cA).add((op.isZFighting()) ? 0.02f : 0));
                });

                entId = ent.getEntityId();
                spawnEntity(player, ent, cA.sub((op.isZFighting()) ? 0.01f : 0).asPosition());
            }

            @Override
            protected void clear(@NotNull Player player) {
                player.sendPacket(new DestroyEntitiesPacket(entId));
            }
        };
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        // These checks are stupid...
        double pitch = Math.atan((posA.x() - posB.x()) / (posA.z() - posB.z())) * ((posB.x() < posA.x()) ? -1 : 1) * ((posB.y() > posA.y()) ? -1 : 1); // Y
        double roll = Math.atan((posA.distance(posB.withY(posA.y()))) / (posB.y() - posA.y())) * ((posB.z() < posA.z()) ? -1 : 1) * ((posB.y() > posA.y()) ? -1 : 1); // X
        double yaw = calcYaw(posA, posB);

        return new VisualizerElement() {
            int entId;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugDisplayOptions op = (DebugDisplayOptions) options;

                final Entity ent = getEntity(op, (meta) -> {
                    meta.setScale(new Vec(MIN_THICKNESS, posA.distance(posB), MIN_THICKNESS));
                    meta.setLeftRotation(MathUtil.eulerAngleToQuaternion(roll, pitch + Math.PI, yaw));
                });

                spawnEntity(player, ent, posA.asPosition());
                entId = ent.getEntityId();
            }

            @Override
            protected void clear(@NotNull Player player) {
                player.sendPacket(new DestroyEntitiesPacket(entId));
            }
        };
    }

    @Override
    protected VisualizerElement createPlane(Vec a, Vec b, Vec c, Vec d) {
        final double width = a.distance(b), height = a.distance(d);

        // Calc normal of plane / dir plane is facing
        final Vec ba = b.sub(a), ca = c.sub(a);
        final Vec dir = ba.cross(ca).normalize().abs();
        System.out.println(dir);

        // Get angles to rotate for Y and X
        final boolean isUp = dir.normalize().abs().equals(Direction.UP.vec());
        final double angleY = isUp ? 0 : Direction.SOUTH.vec().angle(dir.withY(0));
        final double angleX = isUp ? Math.PI / 2 : dir.withY(0).angle(dir);

        // Calc angle of plane
        final Vec center = c.sub(a).div(2).add(a);
        final Vec bottomCenter = d.sub(c).div(2).add(c).sub(center);
        final Vec currDir = (dir.normalize().abs().equals(Direction.UP.vec())) ? new Vec(0, 0, 1) : new Vec(0, 1, 0);
        final double rot = -currDir.angle(bottomCenter);
        System.out.printf("Y: %.2f >> %.2f°\n", angleY, Math.toDegrees(angleY));
        System.out.printf("X: %.2f >> %.2f°\n", angleX, Math.toDegrees(angleX));
        System.out.printf("Z: %.2f >> %.2f°\n", rot, Math.toDegrees(rot));

        return new VisualizerElement() {
            int entId;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugDisplayOptions op = (DebugDisplayOptions) options;

//                VisualizerElementCollection.builder()
//                        .addElement(Shape.createPoint(a, DebugDisplayOptions.createWithColor(DebugColor.WHITE)))
//                        .addElement(Shape.createPoint(b, DebugDisplayOptions.createWithColor(DebugColor.GREEN)))
//                        .addElement(Shape.createPoint(c, DebugDisplayOptions.createWithColor(DebugColor.YELLOW)))
//                        .addElement(Shape.createPoint(d, DebugDisplayOptions.createWithColor(DebugColor.RED)))
//                        .build().draw(VisualSupervisor.STD.create("Conorsmine", new DebugDisplayVisualizer()));

                spawnEntity(player, getEntity(op, (m) -> {
                    m.setBlockState(Block.PURPLE_CONCRETE);
                    m.setScale(new Vec(width, height, MIN_THICKNESS));
                    m.setLeftRotation(MathUtil.rotationYXZ(angleY, angleX, rot));
                }), a.asPosition());
//                entId = ent.getEntityId();
//                spawnEntity(player, ent, c.asPosition());
            }

            @Override
            protected void clear(@NotNull Player player) {
                player.sendPacket(new DestroyEntitiesPacket(entId));
            }
        };
    }

    @Override
    public Class<? extends ImplOptions<?>> getOptionsClass() {
        return DebugDisplayOptions.class;
    }

    private static Block getBlock(DebugDisplayOptions op) {
        Block b = op.getBlock();
        if (b != null) return b;

        // Create via state
        return op.getState().converter.apply(op.getDebugColor());
    }

    private static Entity getEntity(DebugDisplayOptions op, Consumer<BlockDisplayMeta> edit) {
        final Entity ent = new Entity(EntityType.BLOCK_DISPLAY);
        ent.setVelocity(Vec.ZERO);

        // Default values
        ent.editEntityMeta(BlockDisplayMeta.class, (meta) -> {
            meta.setBrightnessOverride(-1);
            meta.setHasNoGravity(true);
            meta.setBlockState(getBlock(op));
            meta.setHasGlowingEffect(op.isGlowing());
            meta.setGlowColorOverride(op.getDebugColor().getHexCode());
            meta.setShadowStrength(0);

            edit.accept(meta);
        });
        return ent;
    }

    private static void spawnEntity(Player player, Entity entity, Pos pos) {
        final SpawnEntityPacket packet = (SpawnEntityPacket) entity.getEntityType().registry().spawnType().getSpawnPacket(entity);
        final EntityMetaDataPacket metadataPacket = entity.getMetadataPacket();

        final SpawnEntityPacket fakePacket = new SpawnEntityPacket(entity.getEntityId(), UUID.randomUUID(), packet.type(),
                pos, packet.headRot(), packet.data(), (short) 0, (short) 0, (short) 0);

        final EntityMetaDataPacket fakeMetadataPacket = new EntityMetaDataPacket(entity.getEntityId(), metadataPacket.entries());

        player.sendPacket(fakePacket);
        player.sendPacket(fakeMetadataPacket);
    }

    private static double mirrorYZ(Vec posA, Vec posB) {
        double theta = Math.atan((posB.x() - posA.x()) / (posB.y() - posA.y()));    // Mirror on YZ-Plane
        return -2 * theta;
    }

    private static double mirrorXZ(Vec posA, Vec posB) {
        double theta = Math.atan((posB.y() - posA.y()) / (posB.x() - posA.x()));    // Mirror on XZ-Plane
        return 2 * theta;
    }

    private static double calcYaw(Vec posA, Vec posB) {
        final Vec o = posB.sub(posA);
        byte i = 0;
        if (o.x() > 0) i += 100;
        if (o.y() > 0) i += 10;
        if (o.z() > 0) i += 1;

        return switch (i) {
            case 111, 110 -> mirrorYZ(posA, posB);
            case 101, 100 -> mirrorXZ(posA, posB);
            case 0 -> Math.PI;

            default -> 0;
        };
    }

}
