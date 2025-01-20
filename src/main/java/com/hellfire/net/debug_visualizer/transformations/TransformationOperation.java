package com.hellfire.net.debug_visualizer.transformations;

import com.hellfire.net.debug_visualizer.MathUtil;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;

import static java.lang.Math.*;

/* Created by Conor on 11.01.2025 */
public interface TransformationOperation {

    Matrix transform();

    record Translation(Vec delta) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    1, 0, 0, delta.x(),
                    0, 1, 0, delta.y(),
                    0, 0, 1, delta.z(),
                    0, 0, 0, 1
            );
        }

    }

    record Scale(double scaleX, double scaleY, double scaleZ) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return TransformationOperation.scaleMatrix(scaleX, scaleY, scaleZ);
        }
    }

    record SkewingX(double k_xy, double k_xz) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    1, k_xy, k_xz, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            );
        }

    }

    record SkewingY(double k_xy, double k_yz) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    1, 0, 0, 0,
                    k_xy, 1, k_yz, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            );
        }

    }

    record SkewingZ(double k_xz, double k_yz) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    1, 0, 0, 0,
                    0, 1, 0, 0,
                    k_xz, k_yz, 1, 0,
                    0, 0, 0, 1
            );
        }

    }

    record Rotation(Vec axis, double angle) implements TransformationOperation {

        @Override
        public Matrix transform() {
            final Vec v = axis.normalize();
            final double sin = sin(angle), cos = cos(angle);

            final double m11 = v.x() * v.x() * (1 - cos) + cos;
            final double m12 = v.x() * v.y() * (1 - cos) + v.z() * sin;
            final double m13 = v.z() * v.x() * (1 - cos) - v.y() * sin;
            final double m21 = v.x() * v.y() * (1 - cos) - v.z() * sin;
            final double m22 = v.y() * v.y() * (1 - cos) + cos;
            final double m23 = v.z() * v.y() * (1 - cos) + v.x() * sin;
            final double m31 = v.z() * v.x() * (1 - cos) + v.y() * sin;
            final double m32 = v.z() * v.y() * (1 - cos) - v.x() * sin;
            final double m33 = v.z() * v.z() * (1 - cos) + cos;

            return new Matrix(
                    m11, m12, m13, 0,
                    m21, m22, m23, 0,
                    m31, m32, m33, 0,
                    0, 0, 0, 1
            );
        }

    }

    record RotateX(double angle) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    1, 0, 0, 0,
                    0, cos(angle), sin(angle), 0,
                    0, -sin(angle), cos(angle), 0,
                    0, 0, 0, 1
            );
        }
    }

    record RotateY(double angle) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    cos(angle), 0, -sin(angle), 0,
                    0, 1, 0, 0,
                    sin(angle), 0, cos(angle), 0,
                    0, 0, 0, 1
            );
        }
    }

    record RotateZ(double angle) implements TransformationOperation {

        @Override
        public Matrix transform() {
            return new Matrix(
                    cos(angle), sin(angle), 0, 0,
                    -sin(angle), cos(angle), 0, 0,
                    0, 0, 1, 0,
                    0, 0, 0, 1
            );
        }
    }

    record FaceDirection(Vec direction) implements TransformationOperation {

        @Override
        // https://stackoverflow.com/a/4901243
        public Matrix transform() {
            final Vec dir = direction.normalize();

            if (dir.equals(Direction.UP.vec()))     return new Matrix();
            if (dir.equals(Direction.DOWN.vec()))   return new Rotation(new Vec(1, 0, 0), PI).transform();

            final Vec forward = MathUtil.perpendicular(dir);
            final Vec right = dir.cross(forward);

            return new Matrix(
                    forward.x(), dir.x(), right.x(), 0,
                    forward.y(), dir.y(), right.y(), 0,
                    forward.z(), dir.z(), right.z(), 0,
                    0, 0, 0, 1
            );
        }
    }


    private static Matrix scaleMatrix(double scaleX, double scaleY, double scaleZ) {
        return new Matrix(
                scaleX, 0, 0, 0,
                0, scaleY, 0, 0,
                0, 0, scaleZ, 0,
                0, 0, 0, 1
        );
    }
}
