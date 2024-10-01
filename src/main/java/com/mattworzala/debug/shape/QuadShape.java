package com.mattworzala.debug.shape;

import com.mattworzala.debug.Layer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public record QuadShape(
        @NotNull Point a,
        @NotNull Point b,
        @NotNull Point c,
        @NotNull Point d,
        int color,
        @NotNull Layer renderLayer
) implements Shape {

    @Override
    public int id() {
        return 2;
    }

    @Override
    public void write(@NotNull NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.DOUBLE, a.x());
        buffer.write(NetworkBuffer.DOUBLE, a.y());
        buffer.write(NetworkBuffer.DOUBLE, a.z());
        buffer.write(NetworkBuffer.DOUBLE, b.x());
        buffer.write(NetworkBuffer.DOUBLE, b.y());
        buffer.write(NetworkBuffer.DOUBLE, b.z());
        buffer.write(NetworkBuffer.DOUBLE, c.x());
        buffer.write(NetworkBuffer.DOUBLE, c.y());
        buffer.write(NetworkBuffer.DOUBLE, c.z());
        buffer.write(NetworkBuffer.DOUBLE, d.x());
        buffer.write(NetworkBuffer.DOUBLE, d.y());
        buffer.write(NetworkBuffer.DOUBLE, d.z());
        buffer.write(NetworkBuffer.INT, color);
        buffer.writeEnum(Layer.class, renderLayer);
    }

    public static class Builder {
        private Point a;
        private Point b;
        private Point c;
        private Point d;
        private int color = 0xFFFFFFFF;
        private Layer renderLayer = Layer.INLINE;

        public @NotNull Builder a(@NotNull Point a) {
            this.a = a;
            return this;
        }

        public @NotNull Builder b(@NotNull Point b) {
            this.b = b;
            return this;
        }

        public @NotNull Builder c(@NotNull Point c) {
            this.c = c;
            return this;
        }

        public @NotNull Builder d(@NotNull Point d) {
            this.d = d;
            return this;
        }

        public @NotNull Builder color(int color) {
            this.color = color;
            return this;
        }

        public @NotNull Builder renderLayer(@NotNull Layer renderLayer) {
            this.renderLayer = renderLayer;
            return this;
        }

        public @NotNull QuadShape build() {
            Check.notNull(a, "a");
            Check.notNull(b, "b");
            Check.notNull(c, "c");
            Check.notNull(d, "d");
            return new QuadShape(a, b, c, d, color, renderLayer);
        }
    }
}
