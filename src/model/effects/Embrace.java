package model.effects;

import model.world.Champion;

public class Embrace extends Effect {

	public Embrace(int duration) {
		super("Embrace", duration, EffectType.BUFF);
	}

	public void apply(Champion c) {
		c.getAppliedEffects().add(this);
		c.setCurrentHP(c.getCurrentHP() + ((int) (c.getMaxHP() * (20.0 / 100.0))));
		c.setMana((int) (c.getMana() * 1.2));
		c.setSpeed((int) (c.getSpeed() * 1.2));
		c.setAttackDamage((int) (c.getAttackDamage() * 1.2));
	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		c.setSpeed((int) (c.getSpeed() * (100 / 120)));
		c.setAttackDamage((int) (c.getAttackDamage() * (100 / 120)));
	}

}
