package com.hellfire.net.debug_visualizer;

import com.hellfire.net.debug_visualizer.transformations.Mat3;
import com.hellfire.net.debug_visualizer.transformations.Quaternion;
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

    // https://stackoverflow.com/a/4901243
    public static Vec perpendicular(Vec v) {
        Vec perp = v.cross(new Vec(-1, 0, 0));
        if(perp.length() == 0) {
            // If v is too close to -x try -y
            perp = v.cross(new Vec(0, -1, 0));
        }
        return perp.normalize();
    }

    public static double invsqrt(double r) {
        return 1.0f / sqrt(r);
    }

    public static double cosFromSin(double sin, double angle) {
        double cos = sqrt(1.0f - sin * sin);
        double a = angle + PI / 2;
        double b = a - (int) (a / PI / 2) * PI / 2;
        if (b < 0.0)
            b = PI / 2 + b;
        if (b >= PI / 2)
            return -cos;
        return cos;
    }

    public static boolean pointLiesOnPlane(Vec p1, Vec p2, Vec p3, Vec point) {
        // Calc normal of plane
        final Vec v1 = p2.sub(p1);
        final Vec v2 = p3.sub(p1);
        final Vec planeNormal = v1.cross(v2);

        return point.sub(p1).dot(planeNormal) == 0;
    }

    // https://github.com/misode/misode.github.io/blob/96278cbbe4850b0c7e4f88a3c92486cd6df18787/src/app/Utils.ts#L469
    public static SvdResult svdDecomposition(Mat3 m) {
        Quaternion q = new Quaternion();
        Quaternion r = new Quaternion();
        Mat3 n = m.transpose().mult(m);

        for (int i = 0; i < 5; i++) {
            final Tuple<Mat3, Quaternion> step = stepJacobi(n);
            n = step.l();
            r = r.mult(step.r());
        }

        r = r.normalize();

        // Rotations in all 3 axis
        final Mat3 p0 = m.mult(new Mat3(r));
        final Tuple<Double, Double> qr = m.m00 < 1e-6
                ? qrGivensQuat(p0.m11, -p0.m10)
                : qrGivensQuat(p0.m00, p0.m01);
        final double a1 = qr.l(), b1 = qr.r();

        final double c1 = b1 * b1 - a1 * a1;
        final double d1 = -2 * a1 * b1;
        final Quaternion s1 = new Quaternion(0, 0, a1, b1);
        q = q.mult(s1);
        final Mat3 p1 = new Mat3(
                c1, d1, 0,
                -d1, c1, 0,
                0, 0, 1
        ).mult(p0);

        final Tuple<Double, Double> moreQR = m.m00 < 1e-6
                ? qrGivensQuat(p1.m22, -p1.m20)
                : qrGivensQuat(p1.m00, p1.m02);
        final double a2 = -moreQR.l(), b2 = moreQR.r();
        final double c2 = b2 * b2 - a2 * a2;
        final double d2 = -2 * a2 * b2;
        final Quaternion s2 = new Quaternion(0, a2, 0, b2);
        q = q.mult(s2);
        final Mat3 p2 = new Mat3(
                c2, 0, -d2,
                0, 1, 0,
                d2, 0, c2
        ).mult(p1);

        final Tuple<Double, Double> evenMoreQR = m.m11 < 1e-6
                ? qrGivensQuat(p2.m22, -p2.m21)
                : qrGivensQuat(p2.m11, p2.m12);
        final double a3 = evenMoreQR.l(), b3 = evenMoreQR.r();
        final double c3 = b3 * b3 - a3 * a3;
        final double d3 = -2 * a3 * b3;
        final Quaternion s3 = new Quaternion(a3, 0, 0, b3);
        q = q.mult(s3);
        final Mat3 p3 = new Mat3(
                1, 0, 0,
                0, c3, d3,
                0, -d3, c3
        ).mult(p2);

        // So close, almost done
        r = r.conjugate();
        return new SvdResult(q, new Vec(p3.m00, p3.m11, p3.m22), r);
    }

    private static Tuple<Mat3, Quaternion> stepJacobi(Mat3 m) {
        Quaternion q = new Quaternion();

        if (m.m01 * m.m01 + m.m10 * m.m10 > 1e-6) {
            final Tuple<Double, Double> approx = approxGivensQuat(m.m00, 0.5 * (m.m01 + m.m10), m.m11);
            final double a = approx.l(), b = approx.r();
            final Quaternion r = new Quaternion(0, 0, a, b);
            final double c = b * b - a * a;
            final double d = -2 * a * b;
            final Mat3 n = new Mat3(
                    c, -d, 0,
                    d, c, 0,
                    0, 0, 1
            );

            m = m.mult(n);
            return new Tuple<>(
                    n.transpose().mult(m),
                    q.mult(r)
            );
        }
        if (m.m02 * m.m02 + m.m20 * m.m20 > 1e-6) {
            final Tuple<Double, Double> approx = approxGivensQuat(m.m00, 0.5 * (m.m02 + m.m20), m.m22);
            final double a = -approx.l(), b = approx.r();
            final Quaternion r = new Quaternion(0, a, 0, b);
            final double c = b * b - a * a;
            final double d = -2 * a * b;
            final Mat3 n = new Mat3(
                    c, 0, d,
                    0, 1, 0,
                    -d, 0, c
            );

            m = m.mult(n);
            return new Tuple<>(
                    n.transpose().mult(m),
                    q.mult(r)
            );
        }
        if (m.m12 * m.m12 + m.m21 * m.m21 > 1e-6) {
            final Tuple<Double, Double> approx = approxGivensQuat(m.m11, 0.5 * (m.m12 + m.m21), m.m22);
            final double a = approx.l(), b = approx.r();
            final Quaternion r = new Quaternion(a, 0, 0, b);
            final double c = b * b - a * a;
            final double d = -2 * a * b;
            final Mat3 n = new Mat3(
                    1, 0, 0,
                    0, c, -d,
                    0, d, c
            );

            m = m.mult(n);
            return new Tuple<>(
                    n.transpose().mult(m),
                    q.mult(r)
            );
        }

        return new Tuple<>(m, q);
    }

    private static final double CS = Math.cos(Math.PI / 8);
    private static final double SS = Math.sin(Math.PI / 8);
    private static final double G = 3 + 2 * Math.sqrt(2);

    private static Tuple<Double, Double> approxGivensQuat(double a, double b, double c) {
        final double d = 2 * (a - c);
        if (G * b * b < d * d) {
            final double e = 1 / Math.sqrt(b * b + d * d);
            return new Tuple<>(e * b, e * d);
        } else {
            return new Tuple<>(SS, CS);
        }
    }

    // I actually had this in uni, crazy that there's an application XD
    private static Tuple<Double, Double> qrGivensQuat(double a, double b) {
        final double c = Math.hypot(a, b);
        double d = c > 1e-6 ? b : 0;
        double e = Math.abs(a) + Math.max(c, 1e-6);
        if (a < 0) {
            double temp = d;
            d = e;
            e = temp;
        }
        final double f = 1 / Math.sqrt(e * e + d * d);
        return new Tuple<>(d * f, e * f);
    }

    public record SvdResult(Quaternion left, Vec scale, Quaternion right) { }

    private record Tuple<L, R>(L l, R r) { }
}
