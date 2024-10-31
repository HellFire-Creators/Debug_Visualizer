package com.hellfire.net.debug_visualizer;

import net.minestom.server.coordinate.Vec;

/* Created by Conor on 03.10.2024 */
public final class MathUtil {

    public static final double PI_OVER_2 = Math.PI / 2;     // 90°
    public static final double PI_TIMES_2 = Math.PI * 2;    // 360°

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

    /**
     * Compute two arbitrary vectors perpendicular to the given normalized vector <code>(x, y, z)</code>, and store them in <code>dest1</code> and <code>dest2</code>,
     * respectively.
     * <p>
     * The computed vectors will themselves be perpendicular to each another and normalized. So the tree vectors <code>(x, y, z)</code>, <code>dest1</code> and
     * <code>dest2</code> form an orthonormal basis.
     * @return Tuple of perpendicular normalized vectors
     */
    // Source: https://github.com/JOML-CI/JOML/blob/main/src/main/java/org/joml/GeometryUtils.java
    public static Vec[] perpendicular(Vec dir) {
        final double x = dir.x(), y = dir.y(), z = dir.z();
        final Vec[] rets = new Vec[2];

        double magX = z * z + y * y;
        double magY = z * z + x * x;
        double magZ = y * y + x * x;
        double mag;
        if (magX > magY && magX > magZ) {
            rets[0] = new Vec(0, z, -y);
            mag = magX;
        } else if (magY > magZ) {
            rets[0] = new Vec(-z, 0, x);
            mag = magY;
        } else {
            rets[0] = new Vec(y, -x, 0);
            mag = magZ;
        }
        double len = 1.0 / Math.sqrt(mag);
        rets[0] = rets[0].mul(len);
        rets[1] = new Vec(
                y * rets[0].z() - z * rets[0].y(),
                z * rets[0].x() - x * rets[0].z(),
                x * rets[0].y() - y * rets[0].x()
        );

        return rets;
    }

}
