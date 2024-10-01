package com.mattworzala.debug.shape;

import com.mattworzala.debug.Layer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.network.NetworkBuffer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public record SplineShape(
        @NotNull Type type,
        @NotNull List<Point> points,
        boolean loop,
        int color,
        @NotNull Layer layer,
        float lineWidth
) implements Shape {
    public enum Type {
        CATMULL_ROM,
        BEZIER,
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public void write(@NotNull NetworkBuffer buffer) {
        buffer.writeEnum(Type.class, type);
        buffer.writeCollection(points, (buf, point) -> {
            buf.write(NetworkBuffer.DOUBLE, point.x());
            buf.write(NetworkBuffer.DOUBLE, point.y());
            buf.write(NetworkBuffer.DOUBLE, point.z());
        });
        buffer.write(NetworkBuffer.BOOLEAN, loop);
        buffer.write(NetworkBuffer.INT, color);
        buffer.writeEnum(Layer.class, layer);
        buffer.write(NetworkBuffer.FLOAT, lineWidth);
    }

    public static class Builder {
        private Type type = Type.CATMULL_ROM;
        private final List<Point> points = new ArrayList<>();
        private boolean loop = false;
        private int color = 0xFFFFFFFF;
        private Layer layer = Layer.INLINE;
        private float lineWidth = 3.0f;

        public @NotNull Builder type(@NotNull Type type) {
            this.type = type;
            return this;
        }

        public @NotNull Builder point(@NotNull Point point) {
            this.points.add(point);
            return this;
        }

        public @NotNull Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public @NotNull Builder color(int color) {
            this.color = color;
            return this;
        }

        public @NotNull Builder layer(@NotNull Layer layer) {
            this.layer = layer;
            return this;
        }

        public @NotNull Builder lineWidth(float lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        public @NotNull SplineShape build() {
            return new SplineShape(type, points, loop, color, layer, lineWidth);
        }
    }
}
