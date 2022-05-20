package engine;

import java.util.ArrayList;
import model.world.Champion;

public class Player {

    private String name;
    private Champion leader; // READ AND WRITE
    private ArrayList<Champion> team;

    public Player(String name) {
        this.name = name;
        this.team = new ArrayList<Champion>();
    }

    // #region Getters/Setters

    public String getName() {
        return this.name;
    }

    public Champion getLeader() {
        return this.leader;
    }

    public void setLeader(Champion leader) { // since it's READ & WRITE not READ ONLY
        this.leader = leader;
    }

    public ArrayList<Champion> getTeam() {
        return team;
    }
    
    // #endregion
}
