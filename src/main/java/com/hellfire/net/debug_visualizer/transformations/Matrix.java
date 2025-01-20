package com.hellfire.net.debug_visualizer.transformations;

import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/* Created by Conor on 11.01.2025 */
public class Matrix {

    public final double m00, m01, m02, m03;
    public final double m10, m11, m12, m13;
    public final double m20, m21, m22, m23;
    public final double m30, m31, m32, m33;

    public Matrix(
            double m00, double m01, double m02, double m03,
            double m10, double m11, double m12, double m13,
            double m20, double m21, double m22, double m23,
            double m30, double m31, double m32, double m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public Matrix() {
        this(1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public Matrix mult(final @NotNull Matrix right) {
        double nm00 = Math.fma(m00, right.m00, Math.fma(m10, right.m01, Math.fma(m20, right.m02, m30 * right.m03)));
        double nm01 = Math.fma(m01, right.m00, Math.fma(m11, right.m01, Math.fma(m21, right.m02, m31 * right.m03)));
        double nm02 = Math.fma(m02, right.m00, Math.fma(m12, right.m01, Math.fma(m22, right.m02, m32 * right.m03)));
        double nm03 = Math.fma(m03, right.m00, Math.fma(m13, right.m01, Math.fma(m23, right.m02, m33 * right.m03)));
        double nm10 = Math.fma(m00, right.m10, Math.fma(m10, right.m11, Math.fma(m20, right.m12, m30 * right.m13)));
        double nm11 = Math.fma(m01, right.m10, Math.fma(m11, right.m11, Math.fma(m21, right.m12, m31 * right.m13)));
        double nm12 = Math.fma(m02, right.m10, Math.fma(m12, right.m11, Math.fma(m22, right.m12, m32 * right.m13)));
        double nm13 = Math.fma(m03, right.m10, Math.fma(m13, right.m11, Math.fma(m23, right.m12, m33 * right.m13)));
        double nm20 = Math.fma(m00, right.m20, Math.fma(m10, right.m21, Math.fma(m20, right.m22, m30 * right.m23)));
        double nm21 = Math.fma(m01, right.m20, Math.fma(m11, right.m21, Math.fma(m21, right.m22, m31 * right.m23)));
        double nm22 = Math.fma(m02, right.m20, Math.fma(m12, right.m21, Math.fma(m22, right.m22, m32 * right.m23)));
        double nm23 = Math.fma(m03, right.m20, Math.fma(m13, right.m21, Math.fma(m23, right.m22, m33 * right.m23)));
        double nm30 = Math.fma(m00, right.m30, Math.fma(m10, right.m31, Math.fma(m20, right.m32, m30 * right.m33)));
        double nm31 = Math.fma(m01, right.m30, Math.fma(m11, right.m31, Math.fma(m21, right.m32, m31 * right.m33)));
        double nm32 = Math.fma(m02, right.m30, Math.fma(m12, right.m31, Math.fma(m22, right.m32, m32 * right.m33)));
        double nm33 = Math.fma(m03, right.m30, Math.fma(m13, right.m31, Math.fma(m23, right.m32, m33 * right.m33)));

        return new Matrix(
                nm00, nm01, nm02, nm03,
                nm10, nm11, nm12, nm13,
                nm20, nm21, nm22, nm23,
                nm30, nm31, nm32, nm33
        );
    }

    public Matrix multScalar(double scale) {
        return new Matrix(
                m00 * scale, m01 * scale, m02 * scale, m03 * scale,
                m10 * scale, m11 * scale, m12 * scale, m13 * scale,
                m20 * scale, m21 * scale, m22 * scale, m23 * scale,
                m30 * scale, m31 * scale, m32 * scale, m33 * scale
        );
    }

    public Matrix affine() {
        return multScalar(1 / m33);
    }

    public Vec getTranslation() {
        return new Vec(m03, m13, m23);
    }

    public Vec getScale() {
        return new Vec(m00, m11, m22);
    }

    public Vec transform(final @NotNull Vec v) {
        final double x = v.x(), y = v.y(), z = v.z();

        return new Vec(
                m00 * x + m01 * y + m02 * z + m03,
                m10 * x + m11 * y + m12 * z + m13,
                m20 * x + m21 * y + m22 * z + m23
        );
    }

    public Vec[] transformPoints(final @NotNull Vec... vecs) {
        return Arrays.stream(vecs)
                .map(this::transform)
                .toArray(Vec[]::new);
    }


    @Override

    public String toString() {
        return String.format(
                """
                        %.2f %.2f %.2f %.2f
                        %.2f %.2f %.2f %.2f
                        %.2f %.2f %.2f %.2f
                        %.2f %.2f %.2f %.2f
                        """,
                m00, m01, m02, m03,
                m10, m11, m12, m13,
                m20, m21, m22, m23,
                m30, m31, m32, m33);
    }

}
