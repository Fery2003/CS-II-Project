package model.abilities;

public class Ability {

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

    public Ability() { // Empty constructor just in case
        
        this.name = "";
        this.manaCost = 0;
        this.baseCooldown = 0;
        this.castRange = 0;
        // this.castArea = ???;
        this.requiredActionPoints = 0;
        
    }

    public String getName() {
        return this.name;
    }

    public int getCost() {
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

}
