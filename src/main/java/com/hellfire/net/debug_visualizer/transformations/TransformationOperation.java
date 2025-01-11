package com.hellfire.net.debug_visualizer.transformations;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

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

    record FaceDirection(Vec dir) implements TransformationOperation {

        public static final Vec DEFAULT = Direction.UP.vec();

        @Override
        public Matrix transform() {
            final boolean isSame = dir.abs().equals(DEFAULT);
            final Vec rotationAxis = isSame ? dir : dir.cross(DEFAULT);
            final double rotationAngle = isSame ? 0 : dir.angle(DEFAULT);

            return new Rotation(rotationAxis, rotationAngle).transform();
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
