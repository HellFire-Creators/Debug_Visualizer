package com.mattworzala.debug.shape;

import com.mattworzala.debug.Layer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public record BoxShape(
        Point start,
        Point end,
        int faceColor,
        Layer faceLayer,
        int edgeColor,
        Layer edgeLayer,
        float edgeWidth
) implements com.mattworzala.debug.shape.Shape {

    @Override
    public int id() {
        return 3;
    }

    @Override
    public void write(@NotNull NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.DOUBLE, start.x());
        buffer.write(NetworkBuffer.DOUBLE, start.y());
        buffer.write(NetworkBuffer.DOUBLE, start.z());
        buffer.write(NetworkBuffer.DOUBLE, end.x());
        buffer.write(NetworkBuffer.DOUBLE, end.y());
        buffer.write(NetworkBuffer.DOUBLE, end.z());
        buffer.write(NetworkBuffer.INT, faceColor);
        buffer.writeEnum(Layer.class, faceLayer);
        buffer.write(NetworkBuffer.INT, edgeColor);
        buffer.writeEnum(Layer.class, edgeLayer);
        buffer.write(NetworkBuffer.FLOAT, edgeWidth);
    }

    public static class Builder {

        private Point start;
        private Point end;
        private int faceColor = 0xFFFFFFFF;
        private Layer faceLayer = Layer.INLINE;
        private int edgeColor = 0xFFFFFFFF;
        private Layer edgeLayer = Layer.INLINE;
        private float edgeWidth = 4f;

        /**
         * The starting corner of the box. Must be set.
         *
         * @param start The position.
         * @return The builder.
         */
        public @NotNull Builder start(@NotNull Point start) {
            this.start = start;
            return this;
        }

        /**
         * The ending corner of the box. Must be set.
         *
         * @param end The position.
         * @return The builder.
         */
        public @NotNull Builder end(@NotNull Point end) {
            this.end = end;
            return this;
        }

        public @NotNull Builder faceColor(int color) {
            this.faceColor = color;
            return this;
        }

        /**
         * The {@link Layer} of the box.
         * <p>
         * Defaults to {@link Layer#INLINE} if not set.
         *
         * @param layer The layer.
         * @return The builder.
         */
        public @NotNull Builder faceLayer(@NotNull Layer layer) {
            this.faceLayer = layer;
            return this;
        }

        public @NotNull Builder edgeColor(int color) {
            this.edgeColor = color;
            return this;
        }

        public @NotNull Builder edgeLayer(@NotNull Layer layer) {
            this.edgeLayer = layer;
            return this;
        }

        public @NotNull Builder edgeWidth(float width) {
            this.edgeWidth = width;
            return this;
        }

        public @NotNull Shape build() {
            Check.notNull(start, "start");
            Check.notNull(end, "end");
            return new BoxShape(start, end, faceColor, faceLayer, edgeColor, edgeLayer, edgeWidth);
        }

    }

}
