package model.effects;

import model.abilities.*;
import model.world.Champion;

public class PowerUp extends Effect {

	public PowerUp(int duration) {
		super("PowerUp", duration, EffectType.BUFF);
	}

	public void apply(Champion c) {
		c.getAppliedEffects().add(this);
		for (Ability a : c.getAbilities())
			if (a instanceof DamagingAbility)
				((DamagingAbility) a).setDamageAmount((int) (((DamagingAbility) a).getDamageAmount() * 1.2));
			else if (a instanceof HealingAbility)
				((HealingAbility) a).setHealAmount((int) (((HealingAbility) a).getHealAmount() * 1.2));
	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		for (Ability a : c.getAbilities())
			if (a instanceof DamagingAbility)
				((DamagingAbility) a).setDamageAmount((int) (((DamagingAbility) a).getDamageAmount() * (100 / 120)));
			else if (a instanceof HealingAbility)
				((HealingAbility) a).setHealAmount((int) (((HealingAbility) a).getHealAmount() * (100 / 120)));
	}

}
