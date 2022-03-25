package engine;

import java.io.*;
import java.util.ArrayList;

import model.abilities.*;
import model.effects.*;
import model.world.*;

public class Game {

    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;
    private Object[][] board = new Object[BOARDHEIGHT][BOARDWIDTH];
    private static ArrayList<Champion> availableChampions = new ArrayList<Champion>();
    private static ArrayList<Ability> availableAbilities = new ArrayList<Ability>();
    private PriorityQueue turnOrder;
    private final static int BOARDHEIGHT = 5;
    private final static int BOARDWIDTH = 5;

    public Game(Player first, Player second) throws IOException {
        this.firstPlayer = first;
        this.secondPlayer = second;

        loadAbilities("D:\\University Files\\Programming II Project\\CS-II-Project\\Abilities.csv");
        loadChampions("D:\\University Files\\Programming II Project\\CS-II-Project\\Champions.csv");

        placeChampions();
        placeCovers();
    }

    private void placeChampions() {
        for (int i = 1; i <= 3; i++)
            board[i][0] = firstPlayer.getTeam().get(i);

        for (int i = 1; i <= 3; i++)
            board[i][BOARDHEIGHT - 1] = secondPlayer.getTeam().get(i);
    }

    private void placeCovers() { // place cover randomly unless on corners
        for (int i = 0; i <= 5; i++) {

            int x = (int) (Math.random() * 5);
            int y = (int) (Math.random() * 5);

            if (!(x <= 0 && y >= BOARDHEIGHT - 1) || !(x >= BOARDWIDTH - 1 && y <= 0)
                    || !(x >= BOARDWIDTH - 1 && y >= BOARDHEIGHT - 1) || !(x <= 0 && y <= 0))
                if (board[x][y] == null)
                    board[x][y] = new Cover(x, y);
        }
    }

    public static void loadAbilities(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        Object temp = new Object();
        while (br.readLine() != null) {
            String[] abilitiesLine = br.readLine().split(",");
            if (abilitiesLine[0] == "CC") {
                switch (abilitiesLine[7]) {
                    case "Disarm":
                        temp = new Disarm(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Dodge":
                        temp = new Dodge(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Embrace":
                        temp = new Embrace(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "PowerUp":
                        temp = new PowerUp(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Root":
                        temp = new Root(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Shield":
                        temp = new Shield(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Shock":
                        temp = new Shock(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Silence":
                        temp = new Silence(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "SpeedUp":
                        temp = new SpeedUp(Integer.parseInt(abilitiesLine[8]));
                        break;
                    case "Stun":
                        temp = new Stun(Integer.parseInt(abilitiesLine[8]));
                        break;
                    default:
                        break;
                }
                availableAbilities.add(new CrowdControlAbility(abilitiesLine[1], // name
                        Integer.parseInt(abilitiesLine[2]), // manaCost
                        Integer.parseInt(abilitiesLine[4]), // baseCooldown
                        Integer.parseInt(abilitiesLine[3]), // castRange
                        AreaOfEffect.valueOf(abilitiesLine[5]), // AreaOfEffect
                        Integer.parseInt(abilitiesLine[6]), // requiredActionPointsPerTurn
                        (Effect) temp));
            } else
                availableAbilities.add(new Ability(abilitiesLine[1], // name
                        Integer.parseInt(abilitiesLine[2]), // manaCost
                        Integer.parseInt(abilitiesLine[4]), // baseCooldown
                        Integer.parseInt(abilitiesLine[3]), // castRange
                        AreaOfEffect.valueOf(abilitiesLine[5]), // AreaOfEffect
                        Integer.parseInt(abilitiesLine[6]))); // requiredActionPointsPerTurn

            // Abilities.csv line format:-
            // Type, name, manaCost, castRange, baseCooldown, AreaOfEffect,
            // requiredActionsPerTurn, damageAmount/healAmount/effect name,
            // effect duration (in case the ability is a CrowdControl ability)
        }
        br.close();
    }

    public static void loadChampions(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        while (br.readLine() != null) {
            String[] championsLine = br.readLine().split(",");

            // Champions.csv line format:-
            // Type, name, maxHP, mana, actions, speed, attackRange, attackDamage,
            // ability1 name, ability2 name, ability3 name

            availableChampions.add(new Champion(championsLine[1], // name
                    Integer.parseInt(championsLine[2]), // maxHP
                    Integer.parseInt(championsLine[3]), // mana
                    Integer.parseInt(championsLine[4]), // actions
                    Integer.parseInt(championsLine[5]), // speed
                    Integer.parseInt(championsLine[6]), // attackRange
                    Integer.parseInt(championsLine[7]))); // attackDamage
        }
        br.close();
    }

    // #region Getters/Setters

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public boolean isFirstLeaderAbilityUsed() {
        return firstLeaderAbilityUsed;
    }

    public boolean isSecondLeaderAbilityUsed() {
        return secondLeaderAbilityUsed;
    }

    public Object[][] getBoard() {
        return board;
    }

    public static ArrayList<Champion> getAvailableChampions() {
        return availableChampions;
    }

    public static ArrayList<Ability> getAvailableAbilities() {
        return availableAbilities;
    }

    public PriorityQueue getTurnOrder() {
        return turnOrder;
    }

    public int getBoardheight() {
        return BOARDHEIGHT;
    }

    public int getBoardwidth() {
        return BOARDWIDTH;
    }

    // #endregion
}
