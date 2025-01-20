package com.hellfire.net.debug_visualizer.transformations;

import com.hellfire.net.debug_visualizer.MathUtil;
import org.jetbrains.annotations.NotNull;

/* Created by Conor on 12.01.2025 */
public record Quaternion(double x, double y, double z, double w) {

    public Quaternion() {
        this(0, 0, 0, 1);
    }

    public Quaternion mult(final @NotNull Quaternion q) {
        return new Quaternion(
                Math.fma(w, q.x(), Math.fma(x, q.w(), Math.fma(y, q.z(), -z * q.y()))),
                Math.fma(w, q.y(), Math.fma(-x, q.z(), Math.fma(y, q.w(), z * q.x()))),
                Math.fma(w, q.z(), Math.fma(x, q.y(), Math.fma(-y, q.x(), z * q.w()))),
                Math.fma(w, q.w(), Math.fma(-x, q.x(), Math.fma(-y, q.y(), -z * q.z())))
        );
    }

    public Quaternion normalize() {
        double invNorm = MathUtil.invsqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
        return new Quaternion(x * invNorm, y * invNorm, z * invNorm, w * invNorm);
    }

    // https://glmatrix.net/docs/vec4.js.html#line239
    public Quaternion scale(double factor) {
        return new Quaternion(
                this.x * factor,
                this.y * factor,
                this.z * factor,
                this.w * factor
        );
    }

    // https://glmatrix.net/docs/quat.js.html#line397
    public Quaternion conjugate() {
        return new Quaternion(
                -this.x,
                -this.y,
                -this.z,
                this.w
        );
    }

    // https://github.com/JOML-CI/JOML/blob/5886cdfcabf6225780ff39784bb90a48f9e14b7c/src/main/java/org/joml/Quaterniond.java#L494
    public static Quaternion fromMatrix(final @NotNull Matrix m) {
        double[] q = new double[4];
        
        // Normalize matrix
        double m00 = m.m00, m01 = m.m01, m02 = m.m02;
        double m10 = m.m10, m11 = m.m11, m12 = m.m12;
        double m20 = m.m20, m21 = m.m21, m22 = m.m22;
        double lenX = MathUtil.invsqrt(m00 * m00 + m01 * m01 + m02 * m02);
        double lenY = MathUtil.invsqrt(m10 * m10 + m11 * m11 + m12 * m12);
        double lenZ = MathUtil.invsqrt(m20 * m20 + m21 * m21 + m22 * m22);
        m00 *= lenX; m01 *= lenX; m02 *= lenX;
        m10 *= lenY; m11 *= lenY; m12 *= lenY;
        m20 *= lenZ; m21 *= lenZ; m22 *= lenZ;
        
        // Calc quat
        double t;
        double tr = m00 + m11 + m22;
        if (tr >= 0.0) {
            t = Math.sqrt(tr + 1.0);
            q[3] = t * 0.5;
            t = 0.5 / t;
            q[0] = (m12 - m21) * t;
            q[1] = (m20 - m02) * t;
            q[2] = (m01 - m10) * t;
        } else {
            if (m00 >= m11 && m00 >= m22) {
                t = Math.sqrt(m00 - (m11 + m22) + 1.0);
                q[0] = t * 0.5;
                t = 0.5 / t;
                q[1] = (m10 + m01) * t;
                q[2] = (m02 + m20) * t;
                q[3] = (m12 - m21) * t;
            } else if (m11 > m22) {
                t = Math.sqrt(m11 - (m22 + m00) + 1.0);
                q[1] = t * 0.5;
                t = 0.5 / t;
                q[2] = (m21 + m12) * t;
                q[0] = (m10 + m01) * t;
                q[3] = (m20 - m02) * t;
            } else {
                t = Math.sqrt(m22 - (m00 + m11) + 1.0);
                q[2] = t * 0.5;
                t = 0.5 / t;
                q[0] = (m02 + m20) * t;
                q[1] = (m21 + m12) * t;
                q[3] = (m01 - m10) * t;
            }
        }

        return new Quaternion(q[0], q[1], q[2], q[3]);
    }
    
}
