package model.abilities;

import java.util.ArrayList;
import model.world.Damageable;

public abstract class Ability {

    private String name;
    private int manaCost;
    private int baseCooldown;
    private int currentCooldown; // READ AND WRITE
    private int castRange;
    private int requiredActionPoints;
    private AreaOfEffect castArea;

    public Ability(String name, int cost, int baseCooldown, int castRange, AreaOfEffect area, int required) {
        this.name = name;
        this.manaCost = cost;
        this.baseCooldown = baseCooldown;
        this.castRange = castRange;
        this.castArea = area;
        this.requiredActionPoints = required;
    }

    abstract public void execute(ArrayList<Damageable> targets) throws CloneNotSupportedException;

    // #region Getters/Setters

    public String getName() {
        return this.name;
    }

    public int getManaCost() {
        return this.manaCost;
    }

    public int getBaseCooldown() {
        return this.baseCooldown;
    }

    public int getCurrentCooldown() {
        return this.currentCooldown;
    }

    public void setCurrentCooldown(int c) { // since it's READ & WRITE not READ ONLY
        this.currentCooldown = c;
        if (c > this.baseCooldown) // if the input cooldown is greater than the base cooldown then set it to the base cooldown
            this.currentCooldown = baseCooldown;
    }

    public int getCastRange() {
        return this.castRange;
    }

    public int getRequiredActionPoints() {
        return this.requiredActionPoints;
    }

    public AreaOfEffect getCastArea() {
        return this.castArea;
    }

    // #endregion

}
