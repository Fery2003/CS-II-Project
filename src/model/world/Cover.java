package model.world;

import java.awt.Point;

public class Cover {

    private int currentHP; // READ AND WRITE
    private Point location;

    public Cover(int x, int y) {
        location.x = x;
        location.y = y;
        int currentHP = (int) (Math.random() * 900) + 100; // 1000 being exclusive
    }

    // #region Getters/Setters

    public int getCurrentHP() {
        return this.currentHP;
    }

    public void setCurrentHP(int hp) { // since it's READ & WRITE not READ ONLY
        if (currentHP >= 0) { // Validation check, make sure it's >= 0.
            this.currentHP = hp;
        }
    }

    public Point getLocation() {
        return this.location;
    }

    // #endregion

}
