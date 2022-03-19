package model.world;

import java.util.ArrayList;
import java.awt.Point;
import model.abilities.Ability;
import model.effects.Effect;

public class Champion {

    String name;
    int maxHP;
    int currentHP;
    int mana;
    int maxActionPointsPerTurn;
    int currentActionPoints;
    int attackRange;
    int attackDamage;
    int speed;
    ArrayList<Ability> abilities;
    ArrayList<Effect> appliedEffects;
    Condition condition;
    Point location;

    public Champion(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        
    }
}
