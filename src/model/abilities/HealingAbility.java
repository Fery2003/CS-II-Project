package model.abilities;

import java.util.ArrayList;

import model.world.Champion;
import model.world.Damageable;

public class HealingAbility extends Ability {

    private int healAmount; // READ AND WRITE

    public HealingAbility(String name, int cost, int baseCooldown, int castRange, AreaOfEffect area, int required, int healAmount) {
        super(name, cost, baseCooldown, castRange, area, required);
        this.healAmount = healAmount;
    }

    public int getHealAmount() {
        return this.healAmount;
    }

    public void setHealAmount(int h) { // since it's READ & WRITE not READ ONLY
        this.healAmount = h;
    }

    public void execute(ArrayList<Damageable> targets) {
        for (Damageable target : targets)
            if (target instanceof Champion) {
                Champion c = (Champion) target;
                c.setCurrentHP(target.getCurrentHP() + this.healAmount);
            }
    }

}
