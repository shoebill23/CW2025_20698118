package com.comp2042.model;

import com.comp2042.model.data.NextShapeInfo;
import com.comp2042.logic.bricks.Brick;


/**
 * Tracks the current rotation of a brick and provides the next rotation state.
 */
public class BrickRotator { //Class that rotates the brick

    private Brick brick;
    private int currentShape = 0;

    /** Compute the next rotation index and matrix for the current brick. */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /** Current rotation matrix of the brick. */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /** Set the active rotation index. */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /** Assign a new brick and reset rotation to initial state. */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
    
    /** Access the currently assigned brick. */
    public Brick getBrick() {
        return brick;
    }

}