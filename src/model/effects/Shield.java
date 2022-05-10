package model.effects;

import model.world.Champion;

public class Shield extends Effect {

	public Shield(int duration) {
		super("Shield", duration, EffectType.BUFF);
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
