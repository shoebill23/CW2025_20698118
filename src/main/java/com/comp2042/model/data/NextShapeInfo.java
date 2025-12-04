package com.comp2042.model.data;

import com.comp2042.model.MatrixOperations;

import java.util.Arrays;
import java.util.Objects;

/**
 * Immutable description of the next rotation state: matrix and rotation index.
 */
public record NextShapeInfo (int[][] shape, int position) {

    @Override
    public boolean equals(Object o) {
        return o instanceof NextShapeInfo(int[][] s, int p)
                && this.position == p
                && Arrays.deepEquals(this.shape, s);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(position);
        result = 31 * result + Arrays.deepHashCode(shape);
        return result;
    }

    @Override
    public String toString() {
        return "NextShapeInfo{" +
                "shape=" + Arrays.deepToString(shape) +
                ", position=" + position +
                '}';
    }

    /**
     * Defensive copy of the shape matrix.
     * @return shape matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Rotation index to apply as the next state.
     * @return rotation index
     */
    public int getPosition() {
        return position;
    }
}