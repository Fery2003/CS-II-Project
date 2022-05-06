package model.world;

import java.util.ArrayList;

import model.effects.*;

public class Hero extends Champion {

    public Hero(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActions, speed, attackRange, attackDamage);
    }

    public void useLeaderAbility(ArrayList<Champion> targets) {
        for (Champion target : targets) {
            for (Effect effect : target.getAppliedEffects()) {
                if (effect.getType() == EffectType.DEBUFF)
                    effect.remove(target);
            }
            target.getAppliedEffects().add(new Embrace(2));
        }
    }

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        return 0;
    }
}
