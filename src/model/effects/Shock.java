package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
	}

	public void apply(Champion c) {
		// c.getAppliedEffects().add(this);
		c.setSpeed((int) (c.getSpeed() * 0.9));
		c.setAttackDamage((int) (c.getAttackDamage() * 0.9));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 1);
		c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
	}

	public void remove(Champion c) {
		// c.getAppliedEffects().remove(this);
		c.setSpeed((int) (c.getSpeed() * (100.0 / 90.0)));
		c.setAttackDamage((int) (c.getAttackDamage() * (100.0 / 90.0)));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 1);
		c.setCurrentActionPoints(c.getCurrentActionPoints() + 1);
	}
}
