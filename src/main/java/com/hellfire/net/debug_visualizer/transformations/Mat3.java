package com.hellfire.net.debug_visualizer.transformations;

import org.jetbrains.annotations.NotNull;

/* Created by Conor on 16.01.2025 */
public class Mat3 {

    public final double m00, m01, m02;
    public final double m10, m11, m12;
    public final double m20, m21, m22;

    public Mat3(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public Mat3(final @NotNull Matrix m) {
        this(
                m.m00, m.m01, m.m02,
                m.m10, m.m11, m.m12,
                m.m20, m.m21, m.m22
        );
    }

    public Mat3() {
        this(
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        );
    }

    // https://glmatrix.net/docs/mat3.js.html#line542
    public Mat3(final @NotNull Quaternion q) {
        final double x = q.x(), y = q.y(), z = q.z(), w = q.w();

        final double x2 = x + x;
        final double y2 = y + y;
        final double z2 = z + z;
        final double xx = x * x2;
        final double yx = y * x2;
        final double yy = y * y2;
        final double zx = z * x2;
        final double zy = z * y2;
        final double zz = z * z2;
        final double wx = w * x2;
        final double wy = w * y2;
        final double wz = w * z2;

        this.m00 = 1 - yy - zz;
        this.m10 = yx - wz;
        this.m20 = zx + wy;
        this.m01 = yx + wz;
        this.m11 = 1 - xx - zz;
        this.m21 = zy - wx;
        this.m02 = zx - wy;
        this.m12 = zy + wx;
        this.m22 = 1 - xx - yy;
    }

    public Mat3 transpose() {
        return new Mat3(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }

    public Mat3 mult(final @NotNull Mat3 right) {
        double nm00 = Math.fma(m00, right.m00, Math.fma(m10, right.m01, m20 * right.m02));
        double nm01 = Math.fma(m01, right.m00, Math.fma(m11, right.m01, m21 * right.m02));
        double nm02 = Math.fma(m02, right.m00, Math.fma(m12, right.m01, m22 * right.m02));
        double nm10 = Math.fma(m00, right.m10, Math.fma(m10, right.m11, m20 * right.m12));
        double nm11 = Math.fma(m01, right.m10, Math.fma(m11, right.m11, m21 * right.m12));
        double nm12 = Math.fma(m02, right.m10, Math.fma(m12, right.m11, m22 * right.m12));
        double nm20 = Math.fma(m00, right.m20, Math.fma(m10, right.m21, m20 * right.m22));
        double nm21 = Math.fma(m01, right.m20, Math.fma(m11, right.m21, m21 * right.m22));
        double nm22 = Math.fma(m02, right.m20, Math.fma(m12, right.m21, m22 * right.m22));

        return new Mat3(
                nm00, nm01, nm02,
                nm10, nm11, nm12,
                nm20, nm21, nm22
        );
    }
}
