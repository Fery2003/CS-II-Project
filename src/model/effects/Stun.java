package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Stun extends Effect {

	public Stun(int duration) {
		super("Stun", duration, EffectType.DEBUFF);
	}

	public void apply(Champion c) {
		c.getAppliedEffects().add(this);
		c.setCondition(Condition.INACTIVE);
	}

	public void remove(Champion c) {
		c.getAppliedEffects().add(this);
		c.setCondition(Condition.ACTIVE);
	}
	// TODO: check if this is correct after completing section 6.9 & 6.10
}
