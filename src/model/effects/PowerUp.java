package model.effects;

import model.world.Champion;

public class PowerUp extends Effect {

	public PowerUp(int duration) {
		super("PowerUp", duration, EffectType.BUFF);
	}

	@Override
	public void apply(Champion c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Champion c) {
		// TODO Auto-generated method stub
		
	}

}
