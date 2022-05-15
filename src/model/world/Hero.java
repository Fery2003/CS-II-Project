package model.world;

import java.util.ArrayList;

import model.effects.*;

public class Hero extends Champion {

    public Hero(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActions, speed, attackRange, attackDamage);
    }

    public void useLeaderAbility(ArrayList<Champion> targets) {
        for (Champion target : targets) { // targets = own team validation check in game class
            for (Effect effect : target.getAppliedEffects()) {
                if (effect.getType() == EffectType.DEBUFF)
                    effect.remove(target);
            }
            target.getAppliedEffects().add(new Embrace(2));
        }
    }

    public int isOppositeType(Object o) {
        if (o instanceof Hero)
            return 0;
        else if (o instanceof Villain || o instanceof AntiHero)
            return 1;
        return -1; // o instanceof Cover
    }

}
