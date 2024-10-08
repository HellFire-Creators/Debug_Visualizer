package com.hellfire.net.debug_visualizer.impl.displayblock;

import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/* Created by Conor on 03.10.2024 */
@Getter
public class DebugDisplayOptions extends ImplOptions<DebugDisplayOptions> {

    private DebugColor debugColor   = DebugColor.getRandomColor();
    private StateOption state       = StateOption.GLASS;
    private @Nullable Block block   = null;     // Null indicates the usage of the state option!
    private boolean zFighting       = true;     // When set to true, the size of the entities will be slightly increased to remove z-fighting

    private DebugDisplayOptions() { /* EMPTY */ }

    public static DebugDisplayOptions createStd() {
        return new DebugDisplayOptions();
    }

    @Override
    public DebugDisplayOptions getStd() {
        return createStd();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ///////////////////////////////////////////////////////////////////////////

    public static DebugDisplayOptions createWithColor(@NotNull DebugColor color) {
        final DebugDisplayOptions options = createStd();

        options.debugColor = color;
        return options;
    }

    public static DebugDisplayOptions createWithState(@NotNull StateOption state) {
        final DebugDisplayOptions options = createStd();

        options.state = state;
        return options;
    }

    public static DebugDisplayOptions createWithBlock(@NotNull Block block) {
        final DebugDisplayOptions options = createStd();

        options.block = block;
        return options;
    }

    public static DebugDisplayOptions createWithZFighting() {
        final DebugDisplayOptions options = createStd();

        options.zFighting = false;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////

    public DebugDisplayOptions setDebugColor(@NotNull DebugColor color) {
        this.debugColor = color;
        return this;
    }

    public DebugDisplayOptions setState(@NotNull StateOption state) {
        this.state = state;
        return this;
    }

    public DebugDisplayOptions setBlock(@Nullable Block block) {
        this.block = block;
        return this;
    }

    public DebugDisplayOptions setZFighting(boolean zFighting) {
        this.zFighting = zFighting;
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Block state options
    ///////////////////////////////////////////////////////////////////////////

    @AllArgsConstructor
    public enum StateOption {
        WOOL(StateOption::convertWool),
        CONCRETE(StateOption::convertConcrete),
        GLASS(StateOption::convertGlass),
        TERRACOTTA(StateOption::convertTerracotta);

        public final Function<DebugColor, Block> converter;

        private static Block convertWool(DebugColor color) {
            return switch (color) {
                case WHITE -> Block.WHITE_WOOL;
                case GRAY -> Block.LIGHT_GRAY_WOOL;
                case DARK_GRAY -> Block.GRAY_WOOL;
                case BLACK -> Block.BLACK_WOOL;
                case BROWN -> Block.BROWN_WOOL;
                case DARK_RED -> Block.RED_WOOL;
                case GOLD -> Block.ORANGE_WOOL;
                case YELLOW -> Block.YELLOW_WOOL;
                case GREEN -> Block.LIME_WOOL;
                case DARK_GREEN -> Block.GREEN_WOOL;
                case AQUA -> Block.CYAN_WOOL;
                case BLUE -> Block.LIGHT_BLUE_WOOL;
                case DARK_BLUE -> Block.BLUE_WOOL;
                case DARK_PURPLE -> Block.PURPLE_WOOL;
                case LIGHT_PURPLE -> Block.MAGENTA_WOOL;
                case RED -> Block.PINK_WOOL;    // Bruh
            };
        }

        private static Block convertConcrete(DebugColor color) {
            return switch (color) {
                case WHITE -> Block.WHITE_CONCRETE;
                case GRAY -> Block.LIGHT_GRAY_CONCRETE;
                case DARK_GRAY -> Block.GRAY_CONCRETE;
                case BLACK -> Block.BLACK_CONCRETE;
                case BROWN -> Block.BROWN_CONCRETE;
                case DARK_RED -> Block.RED_CONCRETE;
                case GOLD -> Block.ORANGE_CONCRETE;
                case YELLOW -> Block.YELLOW_CONCRETE;
                case GREEN -> Block.LIME_CONCRETE;
                case DARK_GREEN -> Block.GREEN_CONCRETE;
                case AQUA -> Block.CYAN_CONCRETE;
                case BLUE -> Block.LIGHT_BLUE_CONCRETE;
                case DARK_BLUE -> Block.BLUE_CONCRETE;
                case DARK_PURPLE -> Block.PURPLE_CONCRETE;
                case LIGHT_PURPLE -> Block.MAGENTA_CONCRETE;
                case RED -> Block.PINK_CONCRETE;
            };
        }

        private static Block convertGlass(DebugColor color) {
            return switch (color) {
                case WHITE -> Block.WHITE_STAINED_GLASS;
                case GRAY -> Block.LIGHT_GRAY_STAINED_GLASS;
                case DARK_GRAY -> Block.GRAY_STAINED_GLASS;
                case BLACK -> Block.BLACK_STAINED_GLASS;
                case BROWN -> Block.BROWN_STAINED_GLASS;
                case DARK_RED -> Block.RED_STAINED_GLASS;
                case GOLD -> Block.ORANGE_STAINED_GLASS;
                case YELLOW -> Block.YELLOW_STAINED_GLASS;
                case GREEN -> Block.LIME_STAINED_GLASS;
                case DARK_GREEN -> Block.GREEN_STAINED_GLASS;
                case AQUA -> Block.CYAN_STAINED_GLASS;
                case BLUE -> Block.LIGHT_BLUE_STAINED_GLASS;
                case DARK_BLUE -> Block.BLUE_STAINED_GLASS;
                case DARK_PURPLE -> Block.PURPLE_STAINED_GLASS;
                case LIGHT_PURPLE -> Block.MAGENTA_STAINED_GLASS;
                case RED -> Block.PINK_STAINED_GLASS;
            };
        }

        private static Block convertTerracotta(DebugColor color) {
            return switch (color) {
                case WHITE -> Block.WHITE_TERRACOTTA;
                case GRAY -> Block.LIGHT_GRAY_TERRACOTTA;
                case DARK_GRAY -> Block.GRAY_TERRACOTTA;
                case BLACK -> Block.BLACK_TERRACOTTA;
                case BROWN -> Block.BROWN_TERRACOTTA;
                case DARK_RED -> Block.RED_TERRACOTTA;
                case GOLD -> Block.ORANGE_TERRACOTTA;
                case YELLOW -> Block.YELLOW_TERRACOTTA;
                case GREEN -> Block.LIME_TERRACOTTA;
                case DARK_GREEN -> Block.GREEN_TERRACOTTA;
                case AQUA -> Block.CYAN_TERRACOTTA;
                case BLUE -> Block.LIGHT_BLUE_TERRACOTTA;
                case DARK_BLUE -> Block.BLUE_TERRACOTTA;
                case DARK_PURPLE -> Block.PURPLE_TERRACOTTA;
                case LIGHT_PURPLE -> Block.MAGENTA_TERRACOTTA;
                case RED -> Block.PINK_STAINED_GLASS;
            };
        }
    }
}
