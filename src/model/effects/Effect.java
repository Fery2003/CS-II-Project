package model.effects;

// import exceptions.GameActionException;
import model.world.Champion;

public abstract class Effect implements Cloneable {
	private String name;
	private int duration;
	private EffectType type;

	public Effect(String name, int duration, EffectType type) {
		this.name = name;
		this.duration = duration;
		this.type = type;
	}

	public Object clone() throws CloneNotSupportedException{
		try {
			return (Effect) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	abstract public void apply(Champion c);

	abstract public void remove(Champion c);

	// #region Getters/Setters

	public String getName() {
		return this.name;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return this.duration;
	}

	public EffectType getType() {
		return this.type;
	}

	// #endregion
}
