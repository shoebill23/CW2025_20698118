package com.comp2042;

import java.util.Arrays;
import java.util.Objects;

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

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }
}