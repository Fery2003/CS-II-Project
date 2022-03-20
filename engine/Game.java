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
        
    }

    private void placeCovers() {
        for (int i = 0; i <= BOARDWIDTH; i++) {
            for (int j = 0; j <= BOARDHEIGHT; j++) {
                // place cover if not on Object[0][0] or Object[4][4] or Object[0][4] or
                // Object[4][0] (aka don't place on corners of board)
                if (!(i == 0 && j == BOARDHEIGHT - 1) || !(i == BOARDWIDTH - 1 && j == 0)
                        || !(i == BOARDWIDTH - 1 && j == BOARDHEIGHT - 1) || !(i == 0 && j == 0))
                    if (board[i][j] == null)
                        board[i][j] = new Cover(i, j);
            }
        }
    }
}