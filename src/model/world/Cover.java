package model.world;

import java.awt.Point;

public class Cover {

	private int currentHP;
	private Point location;

	public Cover(int x, int y) {
		this.location = new Point(x, y);
		this.currentHP = (int) (Math.random() * 900) + 100;
	}

	public int getCurrentHP() {
		return currentHP;
	}

	public void setCurrentHP(int currentHP) {
		this.currentHP = currentHP;
		if (this.currentHP < 0)
			this.currentHP = 0;
	}

	public Point getLocation() {
		return location;
	}

}
