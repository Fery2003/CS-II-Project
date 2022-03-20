package engine;

import java.util.ArrayList;
import java.util.PriorityQueue;

import model.abilities.Ability;
import model.world.*;

public class Game {

    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstLeaderAbilityUsed;
    private boolean secondLeaderAbilityUsed;
    private Object[][] board = new Object[5][5];
    private static ArrayList<Champion> availableChampions;
    private static ArrayList<Ability> availableAbilities;
    private PriorityQueue turnOrder;
    private static int BOARDHEIGHT;
    private static int BOARDWIDTH;

    public Game(Player first, Player second) {
        this.firstPlayer = first;
        this.secondPlayer = second;
        placeChampions();
        placeCovers();
    }

    private void placeChampions() {

    }

    private void placeCovers() {
        for (int i = 0; i <= 5; i++) {
            for (int j = 0; j <= 5; j++) {
                // place cover if not on Object[0][0] or Object[4][4] or Object[0][4] or
                // Object[4][0] (aka don't place on corners of board)
                if (!(i == 0 && j == 4) || !(i == 4 && j == 0) || !(i == 4 && j == 4) || !(i == 0 && j == 0))
                    if (board[i][j] == null)
                        board[i][j] = new Cover(i, j);
            }
        }
    }
}