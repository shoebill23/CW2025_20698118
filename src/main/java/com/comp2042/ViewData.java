package com.comp2042;

import java.util.Arrays;
import java.util.Objects;

//Using record instead of class since it is immutable by default
public record ViewData (int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {

    @Override //Checks if the numbers in the array are same
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ViewData that)) {
            return false;
        }

        return this.xPosition == that.xPosition;
    }

    @Override //Creates a consistent ID based on the deep content of the arrays
    public int hashCode() {
        int result = Objects.hash(xPosition, yPosition);
        result = 31 * result + Arrays.deepHashCode(brickData);
        result = 31 * result + Arrays.deepHashCode(nextBrickData);
        return result;
    }

    @Override //returns a readable string showing the array values for debugging
    public String toString() {
        return "ViewData{" +
                "brickData=" + Arrays.deepToString(brickData) +
                ", xPosition=" + xPosition +
                ", yPosition=" + yPosition +
                ", nextBrickData=" + Arrays.deepToString(nextBrickData) +
                '}';
    }


    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
