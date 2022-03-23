package model.world;

import model.abilities.Ability;
import model.effects.Effect;

import java.awt.Point;
import java.util.ArrayList;

public class Champion {

    private String name;
    private int maxHP;
    private int currentHP;
    private int mana;
    private int maxActionPointsPerTurn;
    private int currentActionPoints;
    private int attackRange;
    private int attackDamage;
    private int speed;
    private ArrayList<Ability> abilities;
    private ArrayList<Effect> appliedEffects; // What would these default attributes be?
    private Condition condition;
    private Point location;

    public Champion(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        this.name = name;
        this.maxHP = maxHP;
        this.mana = mana;
        this.maxActionPointsPerTurn = maxActions;
        this.attackRange = attackRange;
        this.attackDamage = attackDamage;
        this.speed = speed;
        this.condition = Condition.ACTIVE;
    }

    public Champion() {
        this.name = "";
        this.maxHP = 0;
        this.mana = 0;
        this.maxActionPointsPerTurn = 0;
        this.attackRange = 0;
        this.attackDamage = 0;
        this.speed = 0;
        this.condition = Condition.ACTIVE;
    }

    public String getName() {
        return this.name;
    }

    public int getMaxHP() {
        return this.maxHP;
    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public int getMana() {
        return this.mana;
    }

    public int getMaxActionPointsPerTurn() {
        return this.maxActionPointsPerTurn;
    }

    public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
        this.maxActionPointsPerTurn = maxActionPointsPerTurn;
    }

    public int getCurrentActionPoints() {
        return this.currentActionPoints;
    }

    public int getAttackRange() {
        return this.attackRange;
    }

    public int getAttackDamage() {
        return this.attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public ArrayList<Ability> getAbilities() {
        return this.abilities;
    }

    public ArrayList<Effect> getAppliedEffects() {
        return this.appliedEffects;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Point getLocation() {
        return this.location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

}
