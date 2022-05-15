package model.effects;

// import model.abilities.*;
import model.world.Champion;

public class Shield extends Effect {

	public Shield(int duration) {
		super("Shield", duration, EffectType.BUFF);
	}

	public void apply(Champion c) {
		c.getAppliedEffects().add(this);
		c.setSpeed((int) (c.getSpeed() * 1.2));
	}

	public void remove(Champion c) {
		c.getAppliedEffects().remove(this);
		// listen to any added abilities to appliedAbilities arraylist and check if they're instanceof DamagingAbility
		// c.getAbilities().forEach(a -> {
		// 	if (a instanceof DamagingAbility) {
		// 		((DamagingAbility) a).setDamageAmount((int) (((DamagingAbility) a).getDamageAmount() * (100 / 120)));
		// };
		// });
		c.setSpeed((int) (c.getSpeed() * (100 / 120)));
	}
}
