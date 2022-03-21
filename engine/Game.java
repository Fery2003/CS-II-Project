package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.world.Champion;
import model.world.Cover;

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
            if (!availableAbilitiesEntries[0].equals("CC"))
                availableAbilities.add(new Ability(availableAbilitiesEntries[1], Integer.parseInt(availableAbilitiesEntries[2]), Integer.parseInt(availableAbilitiesEntries[4]), Integer.parseInt(availableAbilitiesEntries[3]), AreaOfEffect.valueOf(availableAbilitiesEntries[5]), Integer.parseInt(availableAbilitiesEntries[6])));
            else {
                availableAbilities.add(new Ability(availableAbilitiesEntries[1], Integer.parseInt(availableAbilitiesEntries[2]), Integer.parseInt(availableAbilitiesEntries[4]), Integer.parseInt(availableAbilitiesEntries[3]), AreaOfEffect.valueOf(availableAbilitiesEntries[5]), Integer.parseInt(availableAbilitiesEntries[6])));
                // CC duration
            }
        }
    }
}