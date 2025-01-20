package com.hellfire.net.debug_visualizer.impl.display;

import com.hellfire.net.debug_visualizer.MathUtil;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.transformations.*;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/* Created by Conor on 11.01.2025 */
public class DebugDisplayVisualizer extends DebugVisualizer {

    public static final double LINE_WIDTH = 0.1;

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB, @Nullable ObjTransformation trans) {
        final Vec min = cornerA.min(cornerB);
        final Vec max = cornerA.max(cornerB);
        final Vec minToMax = max.sub(min);
//        final Vec offset = new Vec(0.5).mul(minToMax);
        final Vec offset = Vec.ZERO;
        final Vec mid = min.add(offset);

        final ObjTransformation objTrans = new ObjTransformation()
                .translate(offset.neg())
                .add((trans == null) ? new ObjTransformation() : trans);

        final Matrix transformation = intersectTransformation(objTrans).affine();
        final MathUtil.SvdResult svdResult = MathUtil.svdDecomposition(new Mat3(transformation));

        return new VisualizerElement() {

            final int entId = ThreadLocalRandom.current().nextInt();

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugDisplayOptions op = ((DebugDisplayOptions) options).withBlock(Block.FURNACE);

                final Entity ent = getStdEnt(op);
                ent.editEntityMeta(BlockDisplayMeta.class, meta -> {
                    meta.setTranslation(transformation.getTranslation());
                    meta.setLeftRotation(quatToArr(svdResult.left()));
                    meta.setScale(svdResult.scale().mul(minToMax));
                    meta.setRightRotation(quatToArr(svdResult.right()));
                });

                // Packet stuff
                SpawnEntityPacket packet = (SpawnEntityPacket) ent.getEntityType().registry().spawnType().getSpawnPacket(ent);
                EntityMetaDataPacket metadataPacket = ent.getMetadataPacket();

                SpawnEntityPacket fakePacket = new SpawnEntityPacket(entId, ent.getUuid(), packet.type(),
                        mid.asPosition(), 0f, packet.data(), (short) 0, (short) 0, (short) 0);

                EntityMetaDataPacket fakeMetadataPacket = new EntityMetaDataPacket(entId, metadataPacket.entries());

                player.sendPacket(fakePacket);
                player.sendPacket(fakeMetadataPacket);
                final TeamsPacket fakeTeam = createFakeTeamAddEnt(ent, op);
                if (fakeTeam == null) return;
                player.sendPacket(fakeTeam);
            }

            @Override
            protected void clear(@NotNull Player player) {
                player.sendPacket(new DestroyEntitiesPacket(entId));
            }
        };
    }

    @Override
    public VisualizerElement createPlane(Vec cornerA, Vec cornerB, Vec cornerC, Vec cornerD, ObjTransformation trans) {
        return new VisualizerElement() {
            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {

            }

            @Override
            protected void clear(@NotNull Player player) {

            }
        };
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        return new VisualizerElement() {
            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
            }

            @Override
            protected void clear(@NotNull Player player) {
            }
        };
    }

    @Override
    public @NotNull Class<? extends ImplOptions<?>> getOptionsClass() {
        return DebugDisplayOptions.class;
    }

    private static Entity getStdEnt(DebugDisplayOptions op) {
        final Entity entity = new Entity(EntityType.BLOCK_DISPLAY);
        entity.editEntityMeta(BlockDisplayMeta.class, (meta) -> {
                    meta.setHasNoGravity(true);
                    meta.setBrightnessOverride(-1);
                    meta.setGlowColorOverride(-1);
                    meta.setBlockState(op.getBlock());
                    meta.setViewRange(op.getViewRange());
                    meta.setHasGlowingEffect(op.isGlow());
                    meta.setShadowStrength(-1);
                    meta.setShadowRadius(0);
                }
        );

        return entity;
    }

    @Nullable
    private static TeamsPacket createFakeTeamAddEnt(Entity entity, DebugDisplayOptions op) {
        // If glow and not a custom block => set glow color
        if (op.isGlow() && !op.isCustomBlock()) {
            return new TeamsPacket(entity.getUuid().toString(), new TeamsPacket.CreateTeamAction(
                    Component.text(entity.getEntityId()),
                    (byte) 0,
                    TeamsPacket.NameTagVisibility.ALWAYS,
                    TeamsPacket.CollisionRule.NEVER,
                    NamedTextColor.nearestTo(TextColor.color(op.getColor().asRGBLike())),
                    Component.text(""),
                    Component.text(""),
                    List.of(entity.getUuid().toString())
            ));
        }

        return null;
    }

    private static float[] quatToArr(Quaternion q) {
        return new float[]{(float) q.x(), (float) q.y(), (float) q.z(), (float) q.w()};
    }

    // This is kind of a "hook" into the transformation process.
    private static Matrix intersectTransformation(ObjTransformation trans) {
        Matrix result = new Matrix();
//        return trans.matrixFromOperations();
        final List<TransformationOperation> operations = trans.getOperations();
        for (TransformationOperation operation : operations) {
            if (!(operation instanceof TransformationOperation.FaceDirection))  result = operation.transform().mult(result);   // "Normal" procedure
            else                                                                result = intersectFaceDir(operation, result).mult(result);
        }

        return result;
    }

    private static Matrix intersectFaceDir(TransformationOperation operation, Matrix prevM) {
        final Vec dir = ((TransformationOperation.FaceDirection) operation).direction().normalize();
        final Vec DEFAULT = Direction.UP.vec();

        // Conor-18.01.2025
        // Todo:
        //  Fix this, doesn't work for DIRECTION.DOWN!
        if (dir.equals(DEFAULT))        return new Matrix();

        // left = up x direction
        double leftX, leftY, leftZ;
        leftX = DEFAULT.y() * dir.z() - DEFAULT.z() * dir.y();
        leftY = DEFAULT.z() * dir.x() - DEFAULT.x() * dir.z();
        leftZ = DEFAULT.x() * dir.y() - DEFAULT.y() * dir.x();
        // normalize left
        double invLeftLength = MathUtil.invsqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
        leftX *= invLeftLength;
        leftY *= invLeftLength;
        leftZ *= invLeftLength;
        // up = direction x left
        double upnX = dir.y() * leftZ - dir.z() * leftY;
        double upnY = dir.z() * leftX - dir.x() * leftZ;
        double upnZ = dir.x() * leftY - dir.y() * leftX;
        double rm00 = leftX;
        double rm01 = leftY;
        double rm02 = leftZ;
        double rm10 = upnX;
        double rm11 = upnY;
        double rm12 = upnZ;
        double rm20 = dir.x();
        double rm21 = dir.y();
        double rm22 = dir.z();
        double nm00 = prevM.m00 * rm00 + prevM.m10 * rm01 + prevM.m20 * rm02;
        double nm01 = prevM.m01 * rm00 + prevM.m11 * rm01 + prevM.m21 * rm02;
        double nm02 = prevM.m02 * rm00 + prevM.m12 * rm01 + prevM.m22 * rm02;
        double nm03 = prevM.m03 * rm00 + prevM.m13 * rm01 + prevM.m23 * rm02;
        double nm10 = prevM.m00 * rm10 + prevM.m10 * rm11 + prevM.m20 * rm12;
        double nm11 = prevM.m01 * rm10 + prevM.m11 * rm11 + prevM.m21 * rm12;
        double nm12 = prevM.m02 * rm10 + prevM.m12 * rm11 + prevM.m22 * rm12;
        double nm13 = prevM.m03 * rm10 + prevM.m13 * rm11 + prevM.m23 * rm12;

        double nm20 = prevM.m00 * rm20 + prevM.m10 * rm21 + prevM.m20 * rm22;
        double nm21 = prevM.m01 * rm20 + prevM.m11 * rm21 + prevM.m21 * rm22;
        double nm22 = prevM.m02 * rm20 + prevM.m12 * rm21 + prevM.m22 * rm22;
        double nm23 = prevM.m03 * rm20 + prevM.m13 * rm21 + prevM.m23 * rm22;

        double nm30 = prevM.m30;
        double nm31 = prevM.m31;
        double nm32 = prevM.m32;
        double nm33 = prevM.m33;

        final Matrix faceMat = new Matrix(
                nm00, nm01, nm02, nm03,
                nm10, nm11, nm12, nm13,
                nm20, nm21, nm22, nm23,
                nm30, nm31, nm32, nm33
        );

//        // This code points the -z (North) axis towards dir
//        // So we rotate 90° by the x (East) axis to make y face towards dir

        final Matrix xRot = new TransformationOperation.Rotation(Direction.EAST.vec(), Math.PI / 2).transform();

        return faceMat.mult(xRot);
    }
}
