package com.hellfire.net.debug_visualizer;

import net.minestom.server.coordinate.Vec;

/* Created by Conor on 03.10.2024 */
public final class MathUtil {

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

    private static void normalize(float[] q) {
        final float x = q[0], y = q[1], z = q[2], w = q[3];
        final float len = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        q[0] /= len;
        q[1] /= len;
        q[2] /= len;
        q[3] /= len;
    }

}
