package model.effects;

// import exceptions.ChampionDisarmedException;
import model.abilities.*;
import model.world.Champion;

public class Disarm extends Effect {

	private DamagingAbility Punch = new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50);

	public Disarm(int duration) {
		super("Disarm", duration, EffectType.DEBUFF);
	}

	public void apply(Champion c) {
		c.getAppliedEffects().add(this);
		c.getAbilities().add(Punch);
		// throw new ChampionDisarmedException("You can't use normal attacks while disarmed!");
	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		c.getAbilities().remove(Punch);
	}

	// TODO: come back to this after completing section 6

}