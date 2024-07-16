package com.hellfire.net.options;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents options for debugging area markers.
 */
public class DebugAreaOptions {

    private final DebugColor sharedColor = DebugColor.getRandomColor(); // A "fallback" value, if both colors are null

    private DebugColor outlineColor;
    @NotApplicable(implementation = "vanilla") private DebugColor fillColor;
    @NotApplicable(implementation = "vanilla") private DebugColor textColor;
    @Getter @NotApplicable(implementation = "vanilla") private String text = "";
    @Getter @NotApplicable(implementation = "vanilla") @Range(from = 0, to = 1) private Float alpha = 1.0f;

    private DebugAreaOptions() { }

    public static DebugAreaOptions createStd() {
        final DebugAreaOptions options = new DebugAreaOptions();
        options.outlineColor = DebugColor.getRandomColor();
        options.fillColor = options.outlineColor;
        options.textColor = DebugColor.BLACK;
        options.text = "";
        options.alpha = 1.0f;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Static Creation
    ///////////////////////////////////////////////////////////////////////////

    public static DebugAreaOptions createOutlineColor(@NotNull DebugColor color) {
        DebugAreaOptions debugAreaOptions = new DebugAreaOptions();
        debugAreaOptions.outlineColor = color;
        return debugAreaOptions;
    }

    public static DebugAreaOptions createFillColor(@NotNull DebugColor color) {
        DebugAreaOptions debugAreaOptions = new DebugAreaOptions();
        debugAreaOptions.fillColor = color;
        return debugAreaOptions;
    }

    public static DebugAreaOptions createAlpha(float alpha) {
        DebugAreaOptions debugAreaOptions = new DebugAreaOptions();
        debugAreaOptions.alpha = alpha;
        return debugAreaOptions;
    }

    public static DebugAreaOptions createText(@NotNull String text) {
        DebugAreaOptions debugAreaOptions = new DebugAreaOptions();
        debugAreaOptions.text = text;
        return debugAreaOptions;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Method creation
    ///////////////////////////////////////////////////////////////////////////

    public DebugAreaOptions setOutlineColor(@NotNull DebugColor color) {
        final DebugAreaOptions debugAreaOptions = clone();
        debugAreaOptions.outlineColor = color;
        return debugAreaOptions;
    }

    public DebugAreaOptions setFillColor(@NotNull DebugColor color) {
        final DebugAreaOptions debugAreaOptions = clone();
        debugAreaOptions.fillColor = color;
        return debugAreaOptions;
    }

    public DebugAreaOptions setAlpha(float alpha) {
        final DebugAreaOptions debugAreaOptions = clone();
        debugAreaOptions.alpha = alpha;
        return debugAreaOptions;
    }

    public DebugAreaOptions setText(@NotNull String text) {
        final DebugAreaOptions debugAreaOptions = clone();
        debugAreaOptions.text = text;
        return debugAreaOptions;
    }

    public DebugAreaOptions setTextColor(@NotNull DebugColor color) {
        final DebugAreaOptions debugAreaOptions = clone();
        debugAreaOptions.textColor = color;
        return debugAreaOptions;
    }

    @NotNull
    public DebugColor getFillColor() {
        if (fillColor != null && outlineColor == null) return fillColor;
        if (fillColor == null && outlineColor != null) return outlineColor;
        return sharedColor;
    }

    @NotNull
    public DebugColor getOutlineColor() {
        if (outlineColor != null && fillColor == null) return outlineColor;
        if (outlineColor == null && fillColor != null) return fillColor;
        return sharedColor;
    }

    @NotNull
    public DebugColor getTextColor() {
        if (textColor == null) return getOutlineColor();
        return textColor;
    }

    public DebugAreaOptions clone() {
        final DebugAreaOptions debugAreaOptions = new DebugAreaOptions();
        debugAreaOptions.outlineColor = outlineColor;
        debugAreaOptions.fillColor = fillColor;
        debugAreaOptions.textColor = textColor;
        debugAreaOptions.alpha = alpha;
        debugAreaOptions.text = text;
        return debugAreaOptions;
    }
}