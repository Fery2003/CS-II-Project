package engine;

import java.io.*;
import java.util.ArrayList;
import java.awt.*;

import model.abilities.*;
import model.effects.*;
import model.world.*;

public class Game {

    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstLeaderAbilityUsed = false;
    private boolean secondLeaderAbilityUsed = false;
    private Object[][] board;
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
    private PriorityQueue turnOrder;
    private final static int BOARDHEIGHT = 5;
    private final static int BOARDWIDTH = 5;

    public Game(Player first, Player second) throws IOException {
        this.firstPlayer = first;
        this.secondPlayer = second;

        this.board = new Object[BOARDHEIGHT][BOARDWIDTH];

        availableChampions = new ArrayList<Champion>();
        availableAbilities = new ArrayList<Ability>();

        placeChampions();
        placeCovers();

        this.turnOrder = new PriorityQueue(firstPlayer.getTeam().size() + secondPlayer.getTeam().size());
    }

    private void placeChampions() {

        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
            board[0][i + 1] = firstPlayer.getTeam().get(i);
            firstPlayer.getTeam().get(i).setLocation(new Point(0, i + 1)); // supposed to be (i + 1, 0)?
        }

        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
            board[BOARDHEIGHT - 1][i + 1] = secondPlayer.getTeam().get(i);
            secondPlayer.getTeam().get(i).setLocation(new Point(BOARDHEIGHT - 1, i + 1)); // x and y are flipped so this makes sense
        }

    }

    private void placeCovers() { // place cover randomly unless on corners
        int placedCovers = 0;

        while (placedCovers < 5) {
            int x = (int) (Math.random() * 3) + 1; // to make sure it's not placed on first and/or last rows
            int y = (int) (Math.random() * 5); // any column in the rest of the rows

            if (board[x][y] == null) { // if the spot is empty
                board[x][y] = new Cover(x, y);
                placedCovers++;
            }
        }
    }

    public static void loadAbilities(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String currentLine = "";
        Effect temp = null;

        // Abilities.csv line format:-
        // Type, name, manaCost, castRange, baseCooldown, AreaOfEffect,
        // requiredActionsPerTurn, damageAmount/healAmount/effect name,
        // effect duration (in case the ability is a CrowdControl ability)

        while ((currentLine = br.readLine()) != null) {
            String[] abilitiesLine = currentLine.split(",");
            if (abilitiesLine[0].equals("CC")) {
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
                        temp)); // effect applied
            } else if (abilitiesLine[0].equals("DMG"))
                availableAbilities.add(new DamagingAbility(abilitiesLine[1], // name
                        Integer.parseInt(abilitiesLine[2]), // manaCost
                        Integer.parseInt(abilitiesLine[4]), // baseCooldown
                        Integer.parseInt(abilitiesLine[3]), // castRange
                        AreaOfEffect.valueOf(abilitiesLine[5]), // AreaOfEffect
                        Integer.parseInt(abilitiesLine[6]), // requiredActionPointsPerTurn
                        Integer.parseInt(abilitiesLine[7]))); // damageAmount
            else if (abilitiesLine[0].equals("HEL"))
                availableAbilities.add(new HealingAbility(abilitiesLine[1], // name
                        Integer.parseInt(abilitiesLine[2]), // manaCost
                        Integer.parseInt(abilitiesLine[4]), // baseCooldown
                        Integer.parseInt(abilitiesLine[3]), // castRange
                        AreaOfEffect.valueOf(abilitiesLine[5]), // AreaOfEffect
                        Integer.parseInt(abilitiesLine[6]), // requiredActionPointsPerTurn
                        Integer.parseInt(abilitiesLine[7]))); // healAmount
        }
        br.close();
    }

    public static void loadChampions(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String currentLine = "";
        while ((currentLine = br.readLine()) != null) {
            String[] championsLine = currentLine.split(",");

            // Champions.csv line format:-
            // Type, name, maxHP, mana, actions, speed, attackRange, attackDamage,
            // ability1 name, ability2 name, ability3 name

            if (championsLine[0].equals("V")) {
                Villain tempVillain = new Villain(championsLine[1], // name
                        Integer.parseInt(championsLine[2]), // maxHP
                        Integer.parseInt(championsLine[3]), // mana
                        Integer.parseInt(championsLine[4]), // actions
                        Integer.parseInt(championsLine[5]), // speed
                        Integer.parseInt(championsLine[6]), // attackRange
                        Integer.parseInt(championsLine[7])); // attackDamage

                Ability a1 = null;
                Ability a2 = null;
                Ability a3 = null;

                for (int i = 0; i < availableAbilities.size(); i++) {
                    if (championsLine[8].equals(availableAbilities.get(i).getName())) {
                        a1 = availableAbilities.get(i);
                        tempVillain.getAbilities().add(a1);
                    }
                    if (championsLine[9].equals(availableAbilities.get(i).getName())) {
                        a2 = availableAbilities.get(i);
                        tempVillain.getAbilities().add(a2);
                    }
                    if (championsLine[10].equals(availableAbilities.get(i).getName())) {
                        a3 = availableAbilities.get(i);
                        tempVillain.getAbilities().add(a3);
                    }
                }
                availableChampions.add(tempVillain);

            }

            else if (championsLine[0].equals("H")) {
                Hero tempHero = new Hero(championsLine[1], // name
                        Integer.parseInt(championsLine[2]), // maxHP
                        Integer.parseInt(championsLine[3]), // mana
                        Integer.parseInt(championsLine[4]), // actions
                        Integer.parseInt(championsLine[5]), // speed
                        Integer.parseInt(championsLine[6]), // attackRange
                        Integer.parseInt(championsLine[7])); // attackDamage

                Ability a1 = null;
                Ability a2 = null;
                Ability a3 = null;

                for (int i = 0; i < availableAbilities.size(); i++) {
                    if (championsLine[8].equals(availableAbilities.get(i).getName())) {
                        a1 = availableAbilities.get(i);
                        tempHero.getAbilities().add(a1);
                    }
                    if (championsLine[9].equals(availableAbilities.get(i).getName())) {
                        a2 = availableAbilities.get(i);
                        tempHero.getAbilities().add(a2);
                    }
                    if (championsLine[10].equals(availableAbilities.get(i).getName())) {
                        a3 = availableAbilities.get(i);
                        tempHero.getAbilities().add(a3);
                    }
                }
                availableChampions.add(tempHero);

            } else if (championsLine[0].equals("A")) {
                AntiHero tempAntiHero = new AntiHero(championsLine[1], // name
                        Integer.parseInt(championsLine[2]), // maxHP
                        Integer.parseInt(championsLine[3]), // mana
                        Integer.parseInt(championsLine[4]), // actions
                        Integer.parseInt(championsLine[5]), // speed
                        Integer.parseInt(championsLine[6]), // attackRange
                        Integer.parseInt(championsLine[7])); // attackDamage

                Ability a1 = null;
                Ability a2 = null;
                Ability a3 = null;

                for (int i = 0; i < availableAbilities.size(); i++) {
                    if (championsLine[8].equals(availableAbilities.get(i).getName())) {
                        a1 = availableAbilities.get(i);
                        tempAntiHero.getAbilities().add(a1);
                    }
                    if (championsLine[9].equals(availableAbilities.get(i).getName())) {
                        a2 = availableAbilities.get(i);
                        tempAntiHero.getAbilities().add(a2);
                    }
                    if (championsLine[10].equals(availableAbilities.get(i).getName())) {
                        a3 = availableAbilities.get(i);
                        tempAntiHero.getAbilities().add(a3);
                    }
                }
                availableChampions.add(tempAntiHero);
            }
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
