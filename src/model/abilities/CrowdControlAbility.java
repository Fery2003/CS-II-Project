package model.abilities;

import java.util.ArrayList;

import model.world.*;

import model.effects.Effect; // to be able to access the "Effect" enum from the "abilities" package

public class CrowdControlAbility extends Ability {
    private Effect effect;

    public CrowdControlAbility(String name, int cost, int baseCooldown, int castRange, AreaOfEffect area, int required, Effect effect) {
        super(name, cost, baseCooldown, castRange, area, required);
        this.effect = effect;
    }

    public Effect getEffect() {
        return this.effect;
    }

    public void execute(ArrayList<Damageable> targets) {
        for (Damageable target : targets)
            if (target instanceof Champion) {
                Champion c = (Champion) target;
                c.getAppliedEffects().add(this.effect);
            }
    }
}
