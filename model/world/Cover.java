package model.world;

import java.awt.Point;

public class Cover {

    private int currentHP; // READ AND WRITE
    private Point location = new Point(0, 0);

    public Cover(int x, int y) {
        this.location.x = x;
        this.location.y = y;
        this.currentHP = (int) (Math.random() * 900) + 100; // 1000 being exclusive
    }

    // #region Getters/Setters

    public int getCurrentHP() {
        return this.currentHP;
    }

    public void setCurrentHP(int hp) { // since it's READ & WRITE not READ ONLY
        if (currentHP >= 0) // Validation check, make sure it's >= 0 before setting it
            this.currentHP = hp;
        if (currentHP < 0)
            this.currentHP = 0;
    }

    public Point getLocation() {
        return this.location;
    }

    // #endregion

}
