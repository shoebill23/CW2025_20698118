package com.comp2042;

import java.util.Arrays;
import java.util.Objects;

public record ClearRow (int linesRemoved, int[][] newMatrix, int scoreBonus) {

    @Override
    public boolean equals(Object o) {
        return o instanceof ClearRow(int l, int[][] m, int s)
                && linesRemoved == l
                && scoreBonus == s
                && Arrays.deepEquals(newMatrix, m);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(linesRemoved, scoreBonus);
        result = 31 * result + Arrays.deepHashCode(newMatrix);
        return result;
    }

    @Override
    public String toString() {
        return "ClearRow{" +
                "linesRemoved=" + linesRemoved +
                ", newMatrix=" + Arrays.deepToString(newMatrix) +
                ", scoreBonus=" + scoreBonus +
                '}';
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }
}
