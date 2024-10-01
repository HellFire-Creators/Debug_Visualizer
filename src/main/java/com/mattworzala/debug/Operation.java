package com.mattworzala.debug;

import com.mattworzala.debug.shape.Shape;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public interface Operation {

    void write(@NotNull NetworkBuffer buffer);

    record Set(
            NamespaceID id,
            Shape shape
    ) implements Operation {
        private static final int ID = 0;

        @Override
        public void write(@NotNull NetworkBuffer buffer) {
            buffer.write(NetworkBuffer.VAR_INT, ID);
            buffer.write(NetworkBuffer.STRING, id.asString());
            buffer.write(NetworkBuffer.VAR_INT, shape.id());
            shape.write(buffer);
        }
    }

    record Remove(
            @NotNull NamespaceID id
    ) implements Operation {
        private static final int ID = 1;

        @Override
        public void write(@NotNull NetworkBuffer buffer) {
            buffer.write(NetworkBuffer.VAR_INT, ID);
            buffer.write(NetworkBuffer.STRING, id.asString());
        }
    }

    record ClearNS(
            @NotNull String namespace
    ) implements Operation {
        private static final int ID = 2;

        @Override
        public void write(@NotNull NetworkBuffer buffer) {
            buffer.write(NetworkBuffer.VAR_INT, ID);
            buffer.write(NetworkBuffer.STRING, namespace);
        }
    }

    final class Clear implements Operation {
        private static final int ID = 3;

        @Override
        public void write(@NotNull NetworkBuffer buffer) {
            buffer.write(NetworkBuffer.VAR_INT, ID);
        }
    }


}
