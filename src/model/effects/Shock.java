package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
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
