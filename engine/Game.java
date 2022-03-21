package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

import model.abilities.*;
import model.effects.*;
import model.world.*;

public class Game {

    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;
    private Object[][] board = new Object[BOARDHEIGHT][BOARDWIDTH];
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
    private PriorityQueue turnOrder;
    private static int BOARDHEIGHT = 5;
    private static int BOARDWIDTH = 5;

    public Game(Player first, Player second) {
        this.firstPlayer = first;
        this.secondPlayer = second;
        placeChampions();
        placeCovers();
    }

    private void placeChampions() {
        for (int i = 1; i <= 3; i++)
            board[i][0] = firstPlayer.getTeam().get(i);

        for (int i = 1; i <= 3; i++)
            board[i][BOARDHEIGHT - 1] = secondPlayer.getTeam().get(i);
    }

    private void placeCovers() {
        for (int i = 0; i <= BOARDWIDTH; i++)
            for (int j = 0; j <= BOARDHEIGHT; j++) // place cover if not on corners
                if (!(i == 0 && j == BOARDHEIGHT - 1) || !(i == BOARDWIDTH - 1 && j == 0)
                        || !(i == BOARDWIDTH - 1 && j == BOARDHEIGHT - 1) || !(i == 0 && j == 0))
                    if (board[i][j] == null)
                        board[i][j] = new Cover(i, j);
    }

    public static void loadAbilities(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while (br.readLine() != null) {
            String[] availableAbilitiesEntries = br.readLine().split(",");
            // Abilities.csv line format:-
            // Type, name, manaCost, castRange, baseCooldown, AreaOfEffect,
            // requiredActionsPerTurn, damageAmount/healAmount/effect name,
            // effect duration (in case the ability is a CrowdControl ability)
            if (!availableAbilitiesEntries[0].equals("CC"))
                availableAbilities.add(new Ability(availableAbilitiesEntries[1], // name
                        Integer.parseInt(availableAbilitiesEntries[2]), // manaCost
                        Integer.parseInt(availableAbilitiesEntries[4]), // baseCooldown
                        Integer.parseInt(availableAbilitiesEntries[3]), // castRange
                        AreaOfEffect.valueOf(availableAbilitiesEntries[5]), // AreaOfEffect
                        Integer.parseInt(availableAbilitiesEntries[6]))); // requiredActionPointsPerTurn

            else {
                Effect tempEffect = new Effect(availableAbilitiesEntries[7], // name
                        Integer.parseInt(availableAbilitiesEntries[8]), // duration
                        EffectType.valueOf(availableAbilitiesEntries[0])); // type

                availableAbilities.add(new CrowdControlAbility(availableAbilitiesEntries[1], // name
                        Integer.parseInt(availableAbilitiesEntries[2]), // manaCost
                        Integer.parseInt(availableAbilitiesEntries[4]), // baseCooldown
                        Integer.parseInt(availableAbilitiesEntries[3]), // castRange
                        AreaOfEffect.valueOf(availableAbilitiesEntries[5]), // AreaOfEffect
                        Integer.parseInt(availableAbilitiesEntries[6]), // requiredActionPointsPerTurn
                        tempEffect)); // effect applied due to the type (availableAbilitesEntries[0]) being "CC"
            }
        }
    }

    public static void loadChampions(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        while (br.readLine() != null) {
            String[] availableChampionsEntries = br.readLine().split(",");
            // Champions.csv line format:-
            // Type, name, maxHP, mana, actions, speed, attackRange, attackDamage,
            // ability1 name, ability2 name, ability3 name
            availableChampions.add(new Champion(availableChampionsEntries[1], // name
                    Integer.parseInt(availableChampionsEntries[2]), // maxHP
                    Integer.parseInt(availableChampionsEntries[3]), // mana
                    Integer.parseInt(availableChampionsEntries[4]), // actions
                    Integer.parseInt(availableChampionsEntries[5]), // speed
                    Integer.parseInt(availableChampionsEntries[6]), // attackRange
                    Integer.parseInt(availableChampionsEntries[7]))); // attackDamage
        }
    }
}
