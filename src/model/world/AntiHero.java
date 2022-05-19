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
        Stun temp = new Stun(2);
        for (Champion target : targets) { // && target != leader validation check in game class
            target.getAppliedEffects().add(temp);
            temp.apply(target);
        }
    }

    // public int heroTypeChecker(Champion targetChampion) { // HELPER METHOD, 0 -> no extra dmg, 1 -> extra dmg, -1 -> cover
    //     if (this instanceof AntiHero && targetChampion instanceof AntiHero)
    //         return 0;
    //     else if ((this instanceof AntiHero && targetChampion instanceof Villain) || (this instanceof AntiHero && targetChampion instanceof Hero))
    //         return 1;
    //     return -1;
    // }
}