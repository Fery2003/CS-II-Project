package model.effects;
//Package : model.effects 
public class Effect { 
	private String name;
	private int duration ;
	private EffectType type;
	public Effect(String name , int duration , EffectType type) {
		this.name = name;
		this.duration = duration;
		this.type = type;
	}
	public String getname() {
		return this.name;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getDuration() {
		return this.duration;
	}
	public EffectType getEffectType() {
		return this.type ;
	}
	

}
