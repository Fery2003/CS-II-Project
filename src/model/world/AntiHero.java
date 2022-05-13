package model.world;

import java.util.ArrayList;

import model.effects.*;

public class AntiHero extends Champion {

    public AntiHero(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActions, speed, attackRange, attackDamage);
        super.setCurrentHP(maxHP);
        super.setCurrentActionPoints(maxActions);
    }

    public void useLeaderAbility(ArrayList<Champion> targets) {
        for (Champion target : targets) { // && target != leader validation check in game class
            target.getAppliedEffects().add(new Stun(2));
        }
    }

    public int compareTo(Object o) {
        if (o instanceof AntiHero)
            return 0;
        else if (o instanceof Villain || o instanceof Hero)
            return 1;
        return -1; // o instanceof Cover
    }
}