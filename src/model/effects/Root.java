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
		int rootCounter = 0;
		for (Effect e : c.getAppliedEffects())
			if (e instanceof Root)
				rootCounter++;
		if (c.getCondition() != Condition.INACTIVE && rootCounter < 1)
			c.setCondition(Condition.ACTIVE);
	}
}
