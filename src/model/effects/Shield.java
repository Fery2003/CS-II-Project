package model.effects;

// import model.abilities.*;
import model.world.Champion;

public class Shield extends Effect {

	public Shield(int duration) {
		super("Shield", duration, EffectType.BUFF);
	}

	public void apply(Champion c) {
		// c.getAppliedEffects().add(this);
		c.setSpeed((int) (c.getSpeed() * 1.02));
	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		c.setSpeed((int) (c.getSpeed() * (100.0 / 102.0)));
	}
}
