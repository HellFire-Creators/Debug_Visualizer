package com.hellfire.net.options.impl;

import com.hellfire.net.options.DebugColor;
import com.hellfire.net.options.ImplOptions;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/* Created by Conor on 18.07.2024 */
@Getter
public class DebugModOptions implements ImplOptions {

    private DebugColor color            = DebugColor.getRandomColor();
    private DebugColor textColor        = DebugColor.BLACK;
    private String text                 = "";
    private float alpha                 = 1.0f;

    private DebugModOptions() { }

    public static DebugModOptions createStd() {
        return new DebugModOptions();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ///////////////////////////////////////////////////////////////////////////

    public static DebugModOptions createWithColor(@NotNull DebugColor color) {
        final DebugModOptions options = new DebugModOptions();
        options.color = color;
        return options;
    }

    public static DebugModOptions createWithTextColor(@NotNull DebugColor color) {
        final DebugModOptions options = new DebugModOptions();
        options.textColor = color;
        return options;
    }

    public static DebugModOptions createWithText(@NotNull String text) {
        final DebugModOptions options = new DebugModOptions();
        options.text = text;
        return options;
    }

    public static DebugModOptions createWithAlpha(@Range(from = 0, to = 1) float alpha) {
        final DebugModOptions options = new DebugModOptions();
        options.alpha = alpha;
        return options;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Setters
    ///////////////////////////////////////////////////////////////////////////

    public DebugModOptions setColor(@NotNull DebugColor color) {
        this.color = color;
        return this;
    }

    public DebugModOptions setTextColor(@NotNull DebugColor textColor) {
        this.textColor = textColor;
        return this;
    }

    public DebugModOptions setText(@NotNull String text) {
        this.text = text;
        return this;
    }

    public DebugModOptions setAlpha(@Range(from = 0, to = 1) float alpha) {
        this.alpha = alpha;
        return this;
    }
}
