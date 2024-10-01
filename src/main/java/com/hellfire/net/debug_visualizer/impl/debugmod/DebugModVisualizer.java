package com.hellfire.net.debug_visualizer.impl.debugmod;

import com.hellfire.net.debug_visualizer.impl.particles.DebugParticleOptions;
import com.hellfire.net.debug_visualizer.visualizers.IDebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.shape.BoxShape;
import com.mattworzala.debug.shape.LineShape;
import com.mattworzala.debug.shape.Shape;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;

/**
 * The DebugModVisualizer class implements the DebugVisualizer interface and provides
 * methods for drawing visual elements in mattw's debug mod. It generates random
 * colors and NamespaceIDs for each drawn element.
 */
public class DebugModVisualizer implements IDebugVisualizer {

    /**
     * Generates a random NamespaceID based on the given typeName. <br>
     * The NamespaceID is in the format "type-uuid:domain" <br>
     * Therefore "type-uuid" will be the NameSpace string, making it unique.
     * */
    private static NamespaceID getRandomNamespaceID(String typeName) {
        return NamespaceID.from(String.format(
                "%s:%s_%s",
                "debug_vis",
                typeName,
                UUID.randomUUID().toString().replaceAll("-", ".")
        ));
    }

    private static void sendBoxShape(NamespaceID ns, Audience player, DebugModOptions op, Vec posA, Vec posB) {
        final BoxShape.Builder builder = Shape.box()
                .faceColor(op.getPrimaryColor().getHexCode(op.getPrimaryAlpha()))
                .edgeColor(op.getSecondaryColor().getHexCode(op.getSecondaryAlpha()))
                .start(posA)
                .end(posB);

        DebugMessage.builder().set(ns, builder.build()).build().sendTo(player);
    }

    @Override
    public VisualizerElement createLine(@NotNull Vec posA, @NotNull Vec posB) {
        return new VisualizerElement() {
            private final NamespaceID ns = getRandomNamespaceID("line");

            @Override
            public void draw(@NotNull Player player) {
                final DebugModOptions op = (DebugModOptions) options.computeIfAbsent(DebugModOptions.class, (k) -> DebugModOptions.createStd());

                final LineShape.Builder builder = Shape.line()
                        .color(op.getPrimaryColor().getHexCode(op.getPrimaryAlpha()))
                        .point(posA)
                        .point(posB);


                DebugMessage.builder().set(ns, builder.build()).build().sendTo(player);
            }

            @Override
            public void clear (@NotNull Player player) {
                DebugMessage.builder().remove(ns).build().sendTo(player);
            }
        };
    }

    @Override
    public VisualizerElement createBlock(@NotNull Vec position) {
        final NamespaceID ns = getRandomNamespaceID("block");

        return new VisualizerElement() {
            final DebugModOptions op = (DebugModOptions) options.computeIfAbsent(DebugModOptions.class, (k) -> DebugModOptions.createStd());

            @Override
            public void draw(@NotNull Player player) {
                sendBoxShape(ns, player, op, position, position.add(1));
            }

            @Override
            public void clear(@NotNull Player player) {
                DebugMessage.builder().remove(ns).build().sendTo(player);
            }
        };
    }

    @Override
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB) {
        final NamespaceID ns = getRandomNamespaceID("area");

        return new VisualizerElement() {
            final DebugModOptions op = (DebugModOptions) options.computeIfAbsent(DebugModOptions.class, (k) -> DebugModOptions.createStd());

            @Override
            public void draw(@NotNull Player player) {
                sendBoxShape(ns, player, op, cornerA, cornerB);
            }

            @Override
            public void clear(@NotNull Player player) {
                DebugMessage.builder().remove(ns).build().sendTo(player);
            }
        };
    }

    @Override
    public VisualizerElement createPlane(@NotNull Direction dir, @NotNull Vec cornerA, @NotNull Vec cornerB) {
        final NamespaceID ns = getRandomNamespaceID("plane");
        final Vec otherCorner = switch (dir) {
            case EAST, WEST ->      cornerB.withX(cornerA.x());
            case UP, DOWN ->        cornerB.withY(cornerA.y());
            case NORTH, SOUTH ->    cornerB.withZ(cornerA.z());
        };

        return new VisualizerElement() {
            final DebugModOptions op = (DebugModOptions) options.computeIfAbsent(DebugModOptions.class, (k) -> DebugModOptions.createStd());

            @Override
            public void draw(@NotNull Player player) {
                sendBoxShape(ns, player, op, cornerA, otherCorner);
            }

            @Override
            public void clear(@NotNull Player player) {
                DebugMessage.builder().remove(ns).build().sendTo(player);
            }
        };
    }

}
