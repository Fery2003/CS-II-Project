package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root(int duration) {
		super("Root", duration, EffectType.DEBUFF);
	}

	public void apply(Champion c) {
		// c.getAppliedEffects().add(this);
		if (c.getCondition() == Condition.INACTIVE)
			c.setCondition(Condition.INACTIVE);
		else
			c.setCondition(Condition.ROOTED);
	}

	public void remove(Champion c) {

		c.getAppliedEffects().remove(this);
		// if (c.getAppliedEffects().contains(instanceof Stun))
	}
	// TODO: check if this is correct after completing section 6.9 & 6.10
}
