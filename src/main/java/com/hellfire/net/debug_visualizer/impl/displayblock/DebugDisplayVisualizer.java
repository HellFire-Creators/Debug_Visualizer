package com.hellfire.net.debug_visualizer.impl.displayblock;

import com.hellfire.net.debug_visualizer.MathUtil;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

/* Created by Conor on 03.10.2024 */
public class DebugDisplayVisualizer extends DebugVisualizer {

    private static final double LINE_THICKNESS = 0.1d;

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB) {
        final Vec cA = cornerA.min(cornerB), cB = cornerA.max(cornerB);

        return new VisualizerElement() {
            int entId;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugDisplayOptions op = (DebugDisplayOptions) options;
                final Block block = getBlock(op);

                final Entity ent = getEntity((meta) -> {
                    meta.setBlockState(block);
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
    protected VisualizerElement createPlaneImpl(@NotNull Vec cornerA, @NotNull Vec cornerB, @NotNull Vec cornerC, @NotNull Vec cornerD) {
        return null;
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
                                                                                                     // These checks are stupid...
        double pitch = Math.atan((posA.x() - posB.x()) / (posA.z() - posB.z()))                * ((posB.x() < posA.x()) ? -1 : 1) * ((posB.y() > posA.y()) ? -1 : 1); // Y
        double roll = Math.atan((posA.distance(posB.withY(posA.y()))) / (posB.y() - posA.y())) * ((posB.z() < posA.z()) ? -1 : 1) * ((posB.y() > posA.y()) ? -1 : 1); // X
        double yaw = calcYaw(posA, posB);

        return new VisualizerElement() {
            int entId;

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugDisplayOptions op = (DebugDisplayOptions) options;
                final Block block = getBlock(op);

                final Entity ent = getEntity((meta) -> {
                    meta.setBlockState(block);

                    meta.setScale(new Vec(LINE_THICKNESS, posA.distance(posB), LINE_THICKNESS));
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
    public Class<? extends ImplOptions<?>> getOptionsClass() {
        return DebugDisplayOptions.class;
    }

    private static Block getBlock(DebugDisplayOptions op) {
        Block b = op.getBlock();
        if (b != null) return b;

        // Create via state
        return op.getState().converter.apply(op.getDebugColor());
    }

    private static Entity getEntity(Consumer<BlockDisplayMeta> edit) {
        final Entity ent = new Entity(EntityType.BLOCK_DISPLAY);
        ent.setVelocity(Vec.ZERO);
        ent.editEntityMeta(BlockDisplayMeta.class, (meta) -> {
            meta.setBrightnessOverride(-1);
            meta.setHasNoGravity(true);

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
        return  -2 * theta;
    }

    private static double mirrorXZ(Vec posA, Vec posB) {
        double theta = Math.atan((posB.y() - posA.y()) / (posB.x() - posA.x()));    // Mirror on XZ-Plane
        return  2 * theta;
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
            case   0 -> Math.PI;

            default -> 0;
        };
    }

}
