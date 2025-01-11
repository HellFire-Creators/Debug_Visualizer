package com.hellfire.net.debug_visualizer;

import net.minestom.server.coordinate.Vec;

import static java.lang.Math.*;

/* Created by Conor on 03.10.2024 */
public final class MathUtil {

    public static final double PI_OVER_2 = PI / 2;     // 90°
    public static final double PI_TIMES_2 = PI * 2;    // 360°

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

    public static boolean pointLiesOnPlane(Vec p1, Vec p2, Vec p3, Vec point) {
        // Calc normal of plane
        final Vec v1 = p2.sub(p1);
        final Vec v2 = p3.sub(p1);
        final Vec planeNormal = v1.cross(v2);

        return point.sub(p1).dot(planeNormal) == 0;
    }

}
