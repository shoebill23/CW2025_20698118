package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class RandomBrickGenerator implements BrickGenerator { //Generates the Bricks in random implementing a 7-Bag system

    private final Deque<Brick> bag = new ArrayDeque<>();

    public RandomBrickGenerator() {
        refillBag();
    }

    private void refillBag() { //Refills the bag with 7 random bricks
        List<Brick> fresh = new ArrayList<>(7);
        fresh.add(new IBrick());
        fresh.add(new JBrick());
        fresh.add(new LBrick());
        fresh.add(new OBrick());
        fresh.add(new SBrick());
        fresh.add(new TBrick());
        fresh.add(new ZBrick());
        Collections.shuffle(fresh);
        bag.addAll(fresh);
    }

    @Override
    public void reset() {
        bag.clear();
        refillBag();
    }

    @Override
    public Brick getBrick() { 
        if (bag.isEmpty()) refillBag();
        Brick next = bag.poll();
        if (bag.isEmpty()) refillBag();
        return next;
    }

    @Override
    public Brick getNextBrick() {
        if (bag.isEmpty()) refillBag();
        return bag.peek();
    }
}
