package model.world;

import java.awt.Point;

public class Cover {

    private int currentHP; // READ AND WRITE
    private Point location;

    public Cover(int x, int y) {
        location.x = x;
        location.y = y;
        int currentHP = (int) (Math.random() * 900) + 100;
    }

    public void test(){
        
    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public void setCurrentHP(int hp) { // since it's READ & WRITE not READ ONLY
        this.currentHP = hp;
    }

    public Point getLocation() {
        return this.location;
    }
}
