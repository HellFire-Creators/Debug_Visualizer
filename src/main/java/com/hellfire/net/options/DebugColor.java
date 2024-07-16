package com.hellfire.net.options;

import lombok.Getter;
import org.jetbrains.annotations.Range;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that represents different debug colors. <br>
 * More importantly, these colors "conform" with Minecrafts dye and standard particle colors.
 */
public enum DebugColor {

    DARK_RED(0x00AA0000),
    RED(0x00FF5555),
    GOLD(0x00FFAA00),
    YELLOW(0x00FFFF55),
    GREEN(0x0055FF55),
    DARK_GREEN(0x0000AA00),
    AQUA(0x0055FFFF),
    DARK_AQUA(0x0000AAAA),
    BLUE(0x005555FF),
    DARK_BLUE(0x000000AA),
    LIGHT_PURPLE(0x00FF55FF),
    DARK_PURPLE(0x00AA00AA),
    WHITE(0x00FFFFFF),
    GRAY(0x00AAAAAA),
    DARK_GRAY(0x00555555),
    BLACK(0x00000000);

    public static final int STD_ALPHA_VAL = 0xFF000000;

    private final int hexCode;  // aRGB
    @Getter private final float[] particleRGB;

    DebugColor(int hexCode) {
        this.hexCode = hexCode;
        this.particleRGB = new float[] {
                ((hexCode >> 24) & 0xFF) / 255.0f,
                ((hexCode >> 16) & 0xFF) / 255.0f,
                ((hexCode >> 8) & 0xFF) / 255.0f
        };
    }

   public int getHexCode() {
        return getHexCode(1f);
   }

   public int getHexCode(@Range(from=0, to=1) Float alpha) {
        if (alpha == null) return (hexCode | STD_ALPHA_VAL);

        int alphaMask = (int) (alpha * 255.0f) << 24;
        return (hexCode | alphaMask);
   }

    public static DebugColor getRandomColor() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }

}
