package model.effects;

// import exceptions.ChampionDisarmedException;
import model.abilities.*;
import model.world.Champion;

public class Disarm extends Effect {

	public Disarm(int duration) {
		super("Disarm", duration, EffectType.DEBUFF);
	}

	public void apply(Champion c) { // Already validated in game class to not perform normal attacks when disarmed. Should we throw an exception here?
		// c.getAppliedEffects().add(this);
		c.getAbilities().add(new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50));
	}

	public void remove(Champion c) {
		// c.getAppliedEffects().remove(this);
		for (Ability a : c.getAbilities()) {
			if (a.getName().equals("Punch")) {
				c.getAbilities().remove(a);
				break;
			}
		}
	}
}