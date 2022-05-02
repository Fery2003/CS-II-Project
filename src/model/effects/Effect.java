package model.effects;

public class Effect {
	private String name;
	private int duration;
	private EffectType type;

	public Effect(String name, int duration, EffectType type) {
		this.name = name;
		this.duration = duration;
		this.type = type;
	}

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
