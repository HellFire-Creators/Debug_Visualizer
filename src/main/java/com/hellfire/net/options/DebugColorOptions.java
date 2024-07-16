package com.hellfire.net.options;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents options for the color of a DebugVisualizer.
 */
public class DebugColorOptions {

    private DebugColor color;
    @Getter @NotApplicable(implementation = "vanilla") @Range(from = 0, to = 1) private Float alpha;

    private DebugColorOptions() { }

    /**
     * Mainly used in the case, a DebugColorOptions was never set by the user.
     * @return Returns a DebugColorOptions with a random color
     */
    public static DebugColorOptions createStd() {
        DebugColorOptions options = new DebugColorOptions();
        options.color = DebugColor.getRandomColor();
        options.alpha = 1.0f;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Static Creation
    ///////////////////////////////////////////////////////////////////////////

    public static DebugColorOptions createColor(@NotNull DebugColor color) {
        DebugColorOptions debugColorOptions = new DebugColorOptions();
        debugColorOptions.color = color;
        return debugColorOptions;
    }

    public static DebugColorOptions createAlpha(float alpha) {
        DebugColorOptions debugColorOptions = new DebugColorOptions();
        debugColorOptions.alpha = alpha;
        return debugColorOptions;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Method creation
    ///////////////////////////////////////////////////////////////////////////

   public DebugColorOptions setColor(@NotNull DebugColor color) {
        DebugColorOptions debugColorOptions = new DebugColorOptions();
        debugColorOptions.color = color;
        debugColorOptions.alpha = alpha;
        return debugColorOptions;
   }

   public DebugColorOptions setAlpha(float alpha) {
        DebugColorOptions debugColorOptions = new DebugColorOptions();
        debugColorOptions.alpha = alpha;
        debugColorOptions.color = color;
        return debugColorOptions;
   }

    @NotNull
    public DebugColor getColor() {
        return (color != null) ? color : DebugColor.getRandomColor();
    }

}
