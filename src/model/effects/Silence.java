package model.effects;

import model.world.Champion;

public class Silence extends Effect {

	public Silence(int duration) {
		super("Silence", duration, EffectType.DEBUFF);
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
