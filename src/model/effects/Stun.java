package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Stun extends Effect {

	public Stun(int duration) {
		super("Stun", duration, EffectType.DEBUFF);
	}

	public Boolean isRooted(Champion c) { // HELPER METHOD
		for (Effect effect : c.getAppliedEffects())
			if (effect instanceof Root)
				return true;
		return false;
	}

	public void apply(Champion c) {
		c.setCondition(Condition.INACTIVE);
	}

	public void remove(Champion c) {

		if (isRooted(c))
			c.setCondition(Condition.ROOTED);
		else
			c.setCondition(Condition.ACTIVE);
	}
}
