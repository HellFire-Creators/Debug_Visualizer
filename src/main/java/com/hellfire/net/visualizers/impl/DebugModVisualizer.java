package com.hellfire.net.visualizers.impl;

import com.hellfire.net.options.impl.DebugModOptions;
import com.hellfire.net.visualizers.IDebugVisualizer;
import com.hellfire.net.visualizers.VisualizerElement;
import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.shape.Box;
import com.mattworzala.debug.shape.Line;
import com.mattworzala.debug.shape.OutlineBox;
import com.mattworzala.debug.shape.Shape;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The DebugModVisualizer class implements the DebugVisualizer interface and provides
 * methods for drawing visual elements in mattw's debug mod. It generates random
 * colors and NamespaceIDs for each drawn element.
 */
public class DebugModVisualizer implements IDebugVisualizer<DebugModOptions> {

    /**
     * Generates a random NamespaceID based on the given typeName. <br>
     * The NamespaceID is in the format "type-uuid:domain" <br>
     * Therefore "type-uuid" will be the NameSpace string, making it unique.
     * */
    private static NamespaceID getRandomNamespaceID(String typeName) {
        return NamespaceID.from(String.format(
                "%s-%s:%s",
                typeName,
                UUID.randomUUID(),
                "debug_vis"
        ));
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB, @Nullable DebugModOptions options) {
        final DebugModOptions op = (options == null) ? DebugModOptions.createStd() : options;

        return new VisualizerElement() {
            private final NamespaceID ns = getRandomNamespaceID("line");

            @Override
            public void draw(@NotNull Player player) {
                final Line.Builder builder = Shape.line()
                        .color(op.getColor().getHexCode(op.getAlpha()))
                        .point(posA)
                        .point(posB);

                player.sendPacket(DebugMessage.builder().set(ns, builder.build()).build().getPacket());
            }

            @Override
            public void clear (@NotNull Player player) {
                player.sendPacket(DebugMessage.builder().clear(ns.namespace()).build().getPacket());
            }
        };
    }

    @Override
    public VisualizerElement createBlock(@NotNull Vec position, @Nullable DebugModOptions options) {
        final DebugModOptions op = (options == null) ? DebugModOptions.createStd() : options;

        return new VisualizerElement() {
            private final NamespaceID ns = getRandomNamespaceID("block");

            @Override
            public void draw(@NotNull Player player) {
                final Box.Builder builder = Shape.box()
                        .color(op.getColor().getHexCode(op.getAlpha()))
                        .start(position)
                        .end(position.add(1));

                player.sendPacket(DebugMessage.builder().set(ns, builder.build()).build().getPacket());
            }

            @Override
            public void clear(@NotNull Player player) {
                player.sendPacket(DebugMessage.builder().clear(ns.namespace()).build().getPacket());
            }
        };
    }

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB, @Nullable DebugModOptions options) {
        final DebugModOptions op = (options == null) ? DebugModOptions.createStd() : options;

        return new VisualizerElement() {
            private final NamespaceID ns = getRandomNamespaceID("area");

            @Override
            public void draw(@NotNull Player player) {
                final OutlineBox.Builder builder = new OutlineBox.Builder()
                        .colorLine(op.getColor().getHexCode(op.getAlpha()))
                        .color(op.getColor().getHexCode(op.getAlpha()))
                        .colorText(op.getTextColor().getHexCode())
                        .text(op.getText())
                        .start(cornerA)
                        .end(cornerB);

                player.sendPacket(DebugMessage.builder().set(ns, builder.build()).build().getPacket());
            }

            @Override
            public void clear(@NotNull Player player) {
                player.sendPacket(DebugMessage.builder().clear(ns.namespace()).build().getPacket());
            }
        };
    }

    @Override
    public VisualizerElement createPlane(@NotNull Direction dir, @NotNull Vec cornerA, @NotNull Vec cornerB, @Nullable DebugModOptions options) {
        final DebugModOptions op = (options == null) ? DebugModOptions.createStd() : options;
        final Vec otherCorner = switch (dir) {
            case EAST, WEST ->      cornerB.withX(cornerA.x());
            case UP, DOWN ->        cornerB.withY(cornerA.y());
            case NORTH, SOUTH ->    cornerB.withZ(cornerA.z());
        };

        return new VisualizerElement() {
            private final NamespaceID ns = getRandomNamespaceID("plane");

            @Override
            public void draw(@NotNull Player player) {
                final OutlineBox.Builder builder = new OutlineBox.Builder()
                        .colorLine(op.getColor().getHexCode())
                        .color(op.getColor().getHexCode(op.getAlpha()))
                        .colorText(op.getTextColor().getHexCode())
                        .text(op.getText())
                        .start(cornerA)
                        .end(otherCorner);

                player.sendPacket(DebugMessage.builder().set(ns, builder.build()).build().getPacket());
            }

            @Override
            public void clear(@NotNull Player player) {
                player.sendPacket(DebugMessage.builder().clear(ns.namespace()).build().getPacket());
            }
        };
    }

}
