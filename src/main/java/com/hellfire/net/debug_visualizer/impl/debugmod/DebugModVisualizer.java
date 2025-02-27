package com.hellfire.net.debug_visualizer.impl.debugmod;

import com.hellfire.net.debug_visualizer.options.ImplOptions;
import com.hellfire.net.debug_visualizer.transformations.ObjTransformation;
import com.hellfire.net.debug_visualizer.visualizers.DebugVisualizer;
import com.hellfire.net.debug_visualizer.visualizers.VisualizerElement;
import com.mattworzala.debug.DebugMessage;
import com.mattworzala.debug.shape.BoxShape;
import com.mattworzala.debug.shape.LineShape;
import com.mattworzala.debug.shape.QuadShape;
import com.mattworzala.debug.shape.Shape;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * The DebugModVisualizer class implements the DebugVisualizer interface and provides
 * methods for drawing visual elements in mattw's debug mod. It generates random
 * colors and NamespaceIDs for each drawn element.
 */
public class DebugModVisualizer extends DebugVisualizer {

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
            public void draw(@NotNull Player player, @NotNull ImplOptions<?> options) {
                final DebugModOptions op = (DebugModOptions) options;

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
    public VisualizerElement createArea(@NotNull Vec cornerA, @NotNull Vec cornerB, @NotNull ObjTransformation trans) {
        return null;
    }

    @Override
    public VisualizerElement createPlane(Vec cornerA, Vec cornerB, Vec cornerC, Vec cornerD, ObjTransformation trans) {
        final NamespaceID ns = getRandomNamespaceID("plane");
        return new VisualizerElement() {

            @Override
            protected void draw(@NotNull Player player, ImplOptions<?> options) {
                final DebugModOptions op = (DebugModOptions) options;
                final QuadShape plane = Shape.quad()
                        .a(cornerA)
                        .b(cornerB)
                        .c(cornerC)
                        .d(cornerD)
                        .color(op.getPrimaryColor().getHexCode(op.getPrimaryAlpha()))
                        .build();

                DebugMessage.builder().set(ns, plane).build().sendTo(player);
            }

            @Override
            protected void clear(@NotNull Player player) {
                DebugMessage.builder().remove(ns).build().sendTo(player);
            }
        };
    }

    @Override
    public Class<? extends ImplOptions<?>> getOptionsClass() {
        return DebugModOptions.class;
    }

}
