package com.hellfire.net.debug_visualizer.impl.display;

import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import lombok.Getter;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.hellfire.net.debug_visualizer.options.DebugColor.*;

/* Created by Conor on 11.01.2025 */
@Getter
public class DebugDisplayOptions extends ImplOptions<DebugDisplayOptions> {

    private State state         = State.CONCRETE;
    private DebugColor color    = DebugColor.getRandomColor();
    private Block block         = null;     // Null indicates that we should determine it later
    private int viewRange       = 60;
    private boolean glow        = false;

    private DebugDisplayOptions() { }

    public static DebugDisplayOptions createSTD() {
        return new DebugDisplayOptions();
    }

    @Override
    public DebugDisplayOptions getStd() {
        return createSTD();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ///////////////////////////////////////////////////////////////////////////

    public static DebugDisplayOptions createWithBlock(final @Nullable Block block) {
        final DebugDisplayOptions options = createSTD();
        options.block = block;
        return options;
    }

    public static DebugDisplayOptions createWithState(final @NotNull State state) {
        final DebugDisplayOptions options = createSTD();
        options.state = state;
        return options;
    }

    public static DebugDisplayOptions createWithColor(final @NotNull DebugColor color) {
        final DebugDisplayOptions options = createSTD();
        options.color = color;
        return options;
    }

    public static DebugDisplayOptions createWithViewRange(final int viewRange) {
        final DebugDisplayOptions options = createSTD();
        options.viewRange = viewRange;
        return options;
    }

    public static DebugDisplayOptions createWithGlow(final boolean glow) {
        final DebugDisplayOptions options = createSTD();
        options.glow = glow;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////

    public DebugDisplayOptions withBlock(final @Nullable Block block) {
        this.block = block;
        return this;
    }

    public DebugDisplayOptions withState(final @NotNull State state) {
        this.state = state;
        return this;
    }

    public DebugDisplayOptions withColor(final @NotNull DebugColor color) {
        this.color = color;
        return this;
    }

    public DebugDisplayOptions withViewRange(final int viewRange) {
        this.viewRange = viewRange;
        return this;
    }

    public DebugDisplayOptions withGlow(final boolean glow) {
        this.glow = glow;
        return this;
    }

    public DebugColor getColor() {
        return hasOverrideColor() ? getOverrideColor() : color;
    }

    public boolean isCustomBlock() {
        return block != null;
    }

    public Block getBlock() {
        if (hasOverrideColor()) this.block = null;  // Set block to null => isCustomBlock() = false => getBlock via state
        return isCustomBlock() ? block : state.getFromColor(getColor());
    }

    /*                    Helper stuff                    */

    public enum State {
        WOOL     ( () -> Wool.rand().block,      (col) -> Wool.fromColor(col).block     ),
        GLASS    ( () -> Glass.rand().block,     (col) -> Glass.fromColor(col).block    ),
        CONCRETE ( () -> Concrete.rand().block,  (col) -> Concrete.fromColor(col).block );

        private final Supplier<Block> randBlockSup;
        private final Function<DebugColor, Block> blockFromColFunc;

        State(Supplier<Block> randBlockSup, Function<DebugColor, Block> blockFromColFunc) {
            this.randBlockSup = randBlockSup;
            this.blockFromColFunc = blockFromColFunc;
        }

        private Block getRandomBlock() {
            return randBlockSup.get();
        }

        private Block getFromColor(DebugColor color) {
            return blockFromColFunc.apply(color);
        }
    }

    public enum Wool {
        W_DARK_RED(DARK_RED, Block.RED_WOOL),
        W_RED(RED, Block.RED_WOOL),
        W_GOLD(GOLD, Block.ORANGE_WOOL),
        W_YELLOW(YELLOW, Block.YELLOW_WOOL),
        W_GREEN(GREEN, Block.LIME_WOOL),
        W_DARK_GREEN(DARK_GREEN, Block.GREEN_WOOL),
        W_AQUA(AQUA, Block.CYAN_WOOL),
        W_BLUE(BLUE, Block.LIGHT_BLUE_WOOL),
        W_DARK_BLUE(DARK_BLUE, Block.BLUE_WOOL),
        W_LIGHT_PURPLE(LIGHT_PURPLE, Block.MAGENTA_WOOL),
        W_DARK_PURPLE(DARK_PURPLE, Block.PURPLE_WOOL),
        W_WHITE(WHITE, Block.WHITE_WOOL),
        W_GRAY(GRAY, Block.LIGHT_GRAY_WOOL),
        W_DARK_GRAY(DARK_GRAY, Block.GRAY_WOOL),
        W_BLACK(BLACK, Block.BLACK_WOOL),
        W_BROWN(BROWN, Block.BROWN_WOOL)
        ;

        private final DebugColor color;
        private final Block block;

        Wool(DebugColor color, Block block) {
            this.color = color;
            this.block = block;
        }

        public static Wool rand() {
            return values()[new Random().nextInt(values().length)];
        }

        public static Wool fromColor(final @NotNull DebugColor color) {
            return Arrays.stream(values())
                    .filter((c) -> c.color.equals(color))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Missing color implementation"));
        }
    }

    public enum Glass {
        G_DARK_RED(DARK_RED, Block.RED_STAINED_GLASS),
        G_RED(RED, Block.RED_STAINED_GLASS),
        G_GOLD(GOLD, Block.ORANGE_STAINED_GLASS),
        G_YELLOW(YELLOW, Block.YELLOW_STAINED_GLASS),
        G_GREEN(GREEN, Block.LIME_STAINED_GLASS),
        G_DARK_GREEN(DARK_GREEN, Block.GREEN_STAINED_GLASS),
        G_AQUA(AQUA, Block.CYAN_STAINED_GLASS),
        G_BLUE(BLUE, Block.LIGHT_BLUE_STAINED_GLASS),
        G_DARK_BLUE(DARK_BLUE, Block.BLUE_STAINED_GLASS),
        G_LIGHT_PURPLE(LIGHT_PURPLE, Block.MAGENTA_STAINED_GLASS),
        G_DARK_PURPLE(DARK_PURPLE, Block.PURPLE_STAINED_GLASS),
        G_WHITE(WHITE, Block.WHITE_STAINED_GLASS),
        G_GRAY(GRAY, Block.LIGHT_GRAY_STAINED_GLASS),
        G_DARK_GRAY(DARK_GRAY, Block.GRAY_STAINED_GLASS),
        G_BLACK(BLACK, Block.BLACK_STAINED_GLASS),
        G_BROWN(BROWN, Block.BROWN_STAINED_GLASS)
        ;

        private final DebugColor color;
        private final Block block;

        Glass(DebugColor color, Block block) {
            this.color = color;
            this.block = block;
        }

        public static Glass rand() {
            return values()[new Random().nextInt(values().length)];
        }

        public static Glass fromColor(final @NotNull DebugColor color) {
            return Arrays.stream(values())
                    .filter((c) -> c.color.equals(color))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Missing color implementation"));
        }
    }

    public enum Concrete {
        C_DARK_RED(DARK_RED, Block.RED_CONCRETE),
        C_RED(RED, Block.RED_CONCRETE),
        C_GOLD(GOLD, Block.ORANGE_CONCRETE),
        C_YELLOW(YELLOW, Block.YELLOW_CONCRETE),
        C_GREEN(GREEN, Block.LIME_CONCRETE),
        C_DARK_GREEN(DARK_GREEN, Block.GREEN_CONCRETE),
        C_AQUA(AQUA, Block.CYAN_CONCRETE),
        C_BLUE(BLUE, Block.LIGHT_BLUE_CONCRETE),
        C_DARK_BLUE(DARK_BLUE, Block.BLUE_CONCRETE),
        C_LIGHT_PURPLE(LIGHT_PURPLE, Block.MAGENTA_CONCRETE),
        C_DARK_PURPLE(DARK_PURPLE, Block.PURPLE_CONCRETE),
        C_WHITE(WHITE, Block.WHITE_CONCRETE),
        C_GRAY(GRAY, Block.LIGHT_GRAY_CONCRETE),
        C_DARK_GRAY(DARK_GRAY, Block.GRAY_CONCRETE),
        C_BLACK(BLACK, Block.BLACK_CONCRETE),
        C_BROWN(BROWN, Block.BROWN_CONCRETE),
        ;

        private final DebugColor color;
        private final Block block;

        Concrete(DebugColor color, Block block) {
            this.color = color;
            this.block = block;
        }

        public static Concrete rand() {
            return values()[new Random().nextInt(values().length)];
        }

        public static Concrete fromColor(final @NotNull DebugColor color) {
            return Arrays.stream(values())
                    .filter((c) -> c.color.equals(color))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Missing color implementation"));
        }
    }
}
