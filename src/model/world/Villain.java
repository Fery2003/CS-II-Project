package model.world;

import java.util.ArrayList;

public class Villain extends Champion {

    public Villain(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
        super(name, maxHP, mana, maxActions, speed, attackRange, attackDamage);
        super.setCurrentHP(maxHP);
        super.setCurrentActionPoints(maxActions);
    }

    public void useLeaderAbility(ArrayList<Champion> targets) {
        for (Champion target : targets) // targets = enemy team validation check in game class
            if (target.getCurrentHP() < target.getMaxHP() * (30.0 / 100.0)) {
                target.setCondition(Condition.KNOCKEDOUT);
                target.setCurrentHP(0); // set the current HP to 0?
            }
    }

    public int compareTo(Object o) {

        return 0;
    }
}