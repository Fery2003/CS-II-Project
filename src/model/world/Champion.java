package model.world;

import model.abilities.Ability;
import model.effects.Effect;

import java.awt.Point;
import java.util.ArrayList;

public abstract class Champion implements Damageable, Comparable {

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
    private ArrayList<Effect> appliedEffects;
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
        this.abilities = new ArrayList<Ability>();
        this.appliedEffects = new ArrayList<Effect>();
        this.currentHP = maxHP; // set the current HP to the max HP at the start of the game
        this.currentActionPoints = maxActions; // set the current action points to the max action points at the start of the game
    }

    abstract public void useLeaderAbility(ArrayList<Champion> targets);

    // #region Getters/Setters

    public String getName() {
        return this.name;
    }

    public int getMaxHP() {
        return this.maxHP;
    }

    public int getCurrentHP() {
        return this.currentHP;
    }

    public void setCurrentHP(int hp) {
        if (hp > this.maxHP)
            this.currentHP = this.maxHP;
        else if (hp < 0)
            this.currentHP = 0;
        else
            this.currentHP = hp;
    }

    public int getMana() {
        return this.mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getMaxActionPointsPerTurn() {
        return this.maxActionPointsPerTurn;
    }

    public void setMaxActionPointsPerTurn(int m) {
        this.maxActionPointsPerTurn = m;
    }

    public int getCurrentActionPoints() {
        return this.currentActionPoints;
    }

    public void setCurrentActionPoints(int a) {
        this.currentActionPoints = a;
        if (this.currentActionPoints > this.maxActionPointsPerTurn)
            this.currentActionPoints = this.maxActionPointsPerTurn;
        if (this.currentActionPoints < 0)
            this.currentActionPoints = 0;
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

    // #endregion

}
