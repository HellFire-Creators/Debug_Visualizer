package com.hellfire.net.debug_visualizer.transformations;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/* Created by Conor on 10.01.2025 */
public class ObjTransformation {

    private final Queue<TransformationOperation> operations = new LinkedList<>();

    public ObjTransformation() { }

    public ObjTransformation add(final @NotNull ObjTransformation trans) {
        operations.addAll(trans.operations);
        return this;
    }

    public ObjTransformation translate(final @NotNull Vec delta) {
        operations.add(new TransformationOperation.Translation(delta));
        return this;
    }

    public ObjTransformation scale(double scale) {
        operations.add(new TransformationOperation.Scale(scale, scale, scale));
        return this;
    }

    public ObjTransformation scale(final @NotNull Vec scale) {
        operations.add(new TransformationOperation.Scale(scale.x(), scale.y(), scale.z()));
        return this;
    }

    public ObjTransformation scaleX(double scale) {
        operations.add(new TransformationOperation.Scale(scale, 1, 1));
        return this;
    }

    public ObjTransformation scaleY(double scale) {
        operations.add(new TransformationOperation.Scale(1, scale, 1));
        return this;
    }

    public ObjTransformation scaleZ(double scale) {
        operations.add(new TransformationOperation.Scale(1, 1, scale));
        return this;
    }

    public ObjTransformation skewX(double k_xy, double k_xz) {
        operations.add(new TransformationOperation.SkewingX(k_xy, k_xz));
        return this;
    }

    public ObjTransformation skewY(double k_xy, double k_yz) {
        operations.add(new TransformationOperation.SkewingY(k_xy, k_yz));
        return this;
    }

    public ObjTransformation skewZ(double k_xz, double k_yz) {
        operations.add(new TransformationOperation.SkewingZ(k_xz, k_yz));
        return this;
    }

    public ObjTransformation rotate(final @NotNull Vec axis, double angle) {
        operations.add(new TransformationOperation.Rotation(axis, angle));
        return this;
    }

    public ObjTransformation rotateX(double angle) {
        operations.add(new TransformationOperation.RotateX(angle));
        return this;
    }

    public ObjTransformation rotateY(double angle) {
        operations.add(new TransformationOperation.RotateY(angle));
        return this;
    }

    public ObjTransformation rotateZ(double angle) {
        operations.add(new TransformationOperation.RotateZ(angle));
        return this;
    }

    public ObjTransformation faceTowards(final @NotNull Vec dir) {
        operations.add(new TransformationOperation.FaceDirection(dir));
        return this;
    }

    public ObjTransformation faceAndRotate(final @NotNull Vec dir, double angle) {
        faceTowards(dir);
        rotate(Direction.UP.vec(), angle);
        return this;
    }

    public ObjTransformation reset() {
        operations.clear();
        return this;
    }

    public List<TransformationOperation> getOperations() {
        return Collections.unmodifiableList(new LinkedList<>(operations));
    }

    public Matrix matrixFromOperations() {
        Matrix m = new Matrix();
        for (TransformationOperation operation : operations)
            m = operation.transform().mult(m);  // Mult new matrix to the left => transform happens "later" => Order is preserved
        return m;
    }

    @Override
    public String toString() {
        return String.format("ObjTransformation[transforms=%s]", operations);
    }

}
