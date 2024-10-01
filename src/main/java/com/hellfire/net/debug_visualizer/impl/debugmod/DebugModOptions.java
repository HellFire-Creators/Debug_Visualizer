package com.hellfire.net.debug_visualizer.impl.debugmod;

import com.hellfire.net.debug_visualizer.options.DebugColor;
import com.hellfire.net.debug_visualizer.options.ImplOptions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/* Created by Conor on 18.07.2024 */
@Getter
public class DebugModOptions implements ImplOptions {

    private DebugColor primaryColor   = DebugColor.getRandomColor();
    private DebugColor secondaryColor = primaryColor;
    private float primaryAlpha        = 1.0f;
    private float secondaryAlpha      = 1.0f;

    private DebugModOptions() { }

    public static DebugModOptions createStd() {
        return new DebugModOptions();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ///////////////////////////////////////////////////////////////////////////

    public static DebugModOptions createPrimaryColor(@NotNull DebugColor color) {
        final DebugModOptions options = new DebugModOptions();
        options.primaryColor = color;
        return options;
    }

    public static DebugModOptions createSecondaryColor(@NotNull DebugColor color) {
        final DebugModOptions options = new DebugModOptions();
        options.secondaryColor = color;
        return options;
    }

    public static DebugModOptions createPrimaryAlpha(@Range(from = 0, to = 1) float alpha) {
        final DebugModOptions options = new DebugModOptions();
        options.primaryAlpha = alpha;
        return options;
    }

    public static DebugModOptions createSecondaryAlpha(@Range(from = 0, to = 1) float alpha) {
        final DebugModOptions options = new DebugModOptions();
        options.secondaryAlpha = alpha;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////

    public DebugModOptions setPrimaryColor(@NotNull DebugColor primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    public DebugModOptions setSecondaryColor(@NotNull DebugColor secondaryColor) {
        this.secondaryColor = secondaryColor;
        return this;
    }

    public DebugModOptions setPrimaryAlpha(@Range(from = 0, to = 1) float primaryAlpha) {
        this.primaryAlpha = primaryAlpha;
        return this;
    }

    public DebugModOptions setSecondaryAlpha(@Range(from = 0, to = 1) float secondaryAlpha) {
        this.secondaryAlpha = secondaryAlpha;
        return this;
    }
}
