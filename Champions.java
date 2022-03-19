public class Champions {

    int effectDuration;
    String name;
    String type;
    int healthPoints;
    int mana;
    int attackRange;
    int speed;
    String condition;
    int numOfActions;
    int attackDamage;

    public Champions(String name, String type, int healthPoints, int mana, int attackRange, int speed, String condition,
            int numOfActions, int attackDamage) {
        this.name = name;
        this.type = type;
        this.healthPoints = healthPoints;
        this.mana = mana;
        this.attackRange = attackRange;
        this.speed = speed;
        this.condition = condition;
        this.numOfActions = numOfActions;
        this.attackDamage = attackDamage;
    }

    public void attack(Champions c) {
        // if(distance(this, c) <= 1) then attack

        int damageGiven;
        if (this.type != c.type)
            damageGiven = (int) (this.attackDamage * 1.25);
        else
            damageGiven = this.attackDamage;

        c.healthPoints -= damageGiven;
        numOfActions--;
        c.condition();
    }

    public void condition() {
        if (this.healthPoints < 0)
            condition = "Knocked Out";
        else if (healthPoints > 0 && numOfActions <= 0)
            condition = "Inactive";
        else
            condition = "Active";
    }

    // Range Calculation:-
    
    public int distance(Champions c1, Champions c2){
        // x = Math.abs(c2.x - c1.x);
        // y = Math.abs(c2.y - c1.y);
        // return x + y;
    }

}