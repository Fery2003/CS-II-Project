package model.world;

import java.util.ArrayList;

import model.effects.*;

public class Hero extends Champion {
    
    public Hero(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActions, speed, attackRange, attackDamage);
    }

    public void useLeaderAbility(ArrayList<Champion> targets) {
        Embrace temp = new Embrace(2);
        ArrayList<Effect> debuffsToRemove = new ArrayList<Effect>();
        for (Champion target : targets) { // targets = own team validation check in game class
            target.getAppliedEffects().add(temp);
            temp.apply(target);
            for (Effect e : target.getAppliedEffects()) {
                if (e.getType() == EffectType.DEBUFF) {
                    debuffsToRemove.add(e);
                }
            }
            target.getAppliedEffects().removeAll(debuffsToRemove);
        }
    }

    // public int heroTypeChecker(Champion targetChampion) { // HELPER METHOD, 0 -> no extra dmg, 1 -> extra dmg, -1 -> cover
    //     if (this instanceof Hero && targetChampion instanceof Hero)
    //         return 0;
    //     else if (this instanceof Hero && targetChampion instanceof Villain)
    //         return 1;
    //     return -1;
    // }

}
