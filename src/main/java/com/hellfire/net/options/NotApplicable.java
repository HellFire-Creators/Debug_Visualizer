package com.hellfire.net.options;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used as a "marker" to denote if a setting is only applicable to a certain implementation. <br>
 * Potential usage:
 * <pre>{@code
 *  @NotApplicable(implementation = "vanilla") private DebugColor fillColor;
 * }</pre>
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NotApplicable {

    String implementation();

}
