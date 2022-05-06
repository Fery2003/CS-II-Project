package model.abilities;

import java.util.ArrayList;

import model.world.Damageable;

public class DamagingAbility extends Ability {

    private int damageAmount; // READ AND WRITE

    public DamagingAbility(String name, int cost, int baseCooldown, int castRange, AreaOfEffect area, int required, int damageAmount) {
        super(name, cost, baseCooldown, castRange, area, required);
        this.damageAmount = damageAmount;
    }

    public int getDamageAmount() {
        return this.damageAmount;
    }

    public void setDamageAmount(int d) { // since it's READ & WRITE not READ ONLY
        this.damageAmount = d;
    }

    public void execute(ArrayList<Damageable> targets) {
        for (Damageable target : targets) {
            target.setCurrentHP(target.getCurrentHP() - this.damageAmount);
        }
    }
}
