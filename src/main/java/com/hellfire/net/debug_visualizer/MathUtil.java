package com.hellfire.net.debug_visualizer;

import net.minestom.server.coordinate.Vec;

/* Created by Conor on 03.10.2024 */
public final class MathUtil {

    private static final double PI_OVER_2 = Math.PI / 2;
    private static final double PI_TIMES_2 = Math.PI * 2;

    public static float[] eulerAngleToQuaternion(double roll, double pitch, double yaw) {
        double cr = Math.cos(roll * 0.5);
        double sr = Math.sin(roll * 0.5);
        double cp = Math.cos(pitch * 0.5);
        double sp = Math.sin(pitch * 0.5);
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);

        float[] q = new float[4];
        q[0] = (float) (sr * cp * cy - cr * sp * sy);
        q[1] = (float) (cr * sp * cy + sr * cp * sy);
        q[2] = (float) (cr * cp * sy - sr * sp * cy);
        q[3] = (float) (cr * cp * cy + sr * sp * sy);

        normalize(q);
        return q;
    }

    public static Vec planeLineIntersection(Vec p1, Vec p2, Vec p3, Vec dir, Vec start) {
        // Calc normal of plane
        final Vec v1 = p2.sub(p1);
        final Vec v2 = p3.sub(p1);
        final Vec planeNormal = v1.cross(v2);

        if (planeNormal.dot(dir.normalize()) == 0) {
            return null;
        }

        double t = (planeNormal.dot(p2) - planeNormal.dot(start)) / planeNormal.dot(dir.normalize());
        return start.add(dir.normalize().mul(t));
    }

    // https://github.com/JOML-CI/JOML/blob/main/src/main/java/org/joml/Quaterniond.java
    public static float[] rotationYXZ(double angleY, double angleX, double angleZ) {
        double sx = Math.sin(angleX * 0.5);
        double cx = cosFromSin(sx, angleX * 0.5);
        double sy = Math.sin(angleY * 0.5);
        double cy = cosFromSin(sy, angleY * 0.5);
        double sz = Math.sin(angleZ * 0.5);
        double cz = cosFromSin(sz, angleZ * 0.5);

        double cysx = cy * sx;
        double sycy = sy * cx;
        double sysz = sy * sx;
        double cycw = cy * cx;
        double x = cysx * cz + sycy * sz;
        double y = sycy * cz - cysx * sz;
        double z = cycw * sz - sysz * cz;
        double w = cycw * cz + sysz * sz;

        return new float[]{(float) x, (float) y, (float) z, (float) w};
    }

    // https://github.com/JOML-CI/JOML/blob/main/src/main/java/org/joml/Math.java
    private static double cosFromSin(double sin, double angle) {
        // sin(x)^2 + cos(x)^2 = 1
        double cos = Math.sqrt(1.0 - sin * sin);
        double a = angle + PI_OVER_2;
        double b = a - (int) (a / PI_TIMES_2) * PI_TIMES_2;
        if (b < 0.0)
            b = PI_TIMES_2 + b;
        if (b >= Math.PI)
            return -cos;
        return cos;
    }

    // https://github.com/JOML-CI/JOML/blob/main/src/main/java/org/joml/Quaterniond.java#L1446
    public static Vec applyQuaternion(float[] q, Vec v) {
        final double x = v.x(), y = v.y(), z = v.z();
        double xx = q[0] * q[0], yy = q[1] * q[1], zz = q[2] * q[2], ww = q[3] * q[3];
        double xy = q[0] * q[1], xz = q[0] * q[2], yz = q[1] * q[2], xw = q[0] * q[3];
        double zw = q[2] * q[3], yw = q[1] * q[3], k = 1 / (xx + yy + zz + ww);
        return new Vec(
                Math.fma((xx - yy - zz + ww) * k, x, Math.fma(2 * (xy - zw) * k, y, (2 * (xz + yw) * k) * z)),
                Math.fma(2 * (xy + zw) * k, x, Math.fma((yy - xx - zz + ww) * k, y, (2 * (yz - xw) * k) * z)),
                Math.fma(2 * (xz - yw) * k, x, Math.fma(2 * (yz + xw) * k, y, ((zz - xx - yy + ww) * k) * z))
        );
    }

    private static void normalize(float[] q) {
        final float x = q[0], y = q[1], z = q[2], w = q[3];
        final float len = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        q[0] /= len;
        q[1] /= len;
        q[2] /= len;
        q[3] /= len;
    }
}
