package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.*;

import model.abilities.*;
import model.effects.*;
import model.world.*;
import exceptions.*;

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

    public Champion getCurrentChampion() {
        return (Champion) turnOrder.peekMin();
    }

    public Player checkGameOver() {
        if (firstPlayer.getTeam().size() == 0)
            return secondPlayer;
        else if (secondPlayer.getTeam().size() == 0)
            return firstPlayer;
        else
            return null;
    }

    public boolean isMoveable(Champion c, Direction d) throws UnallowedMovementException { // HELPER METHOD
        if (getCurrentChampion().getCondition() != Condition.ACTIVE) {
            throw new UnallowedMovementException("Champion is inactive, knocked out or rooted");
        } else if (getCurrentChampion().getCurrentActionPoints() <= 0)
            throw new UnallowedMovementException("Not enough action points");
        else
            switch (d) {
            case RIGHT:
                return c.getLocation().x + 1 < BOARDWIDTH || board[(int) c.getLocation().y][(int) (c.getLocation().x + 1)] != null;
            case LEFT:
                return c.getLocation().x - 1 >= 0 || board[(int) c.getLocation().y][(int) (c.getLocation().x - 1)] != null;
            case UP:
                return c.getLocation().y + 1 < BOARDHEIGHT || board[(int) (c.getLocation().y + 1)][(int) c.getLocation().x] != null;
            case DOWN:
                return c.getLocation().y - 1 >= 0 || board[(int) (c.getLocation().y - 1)][(int) c.getLocation().x] != null;
            default:
                throw new UnallowedMovementException();
            }

    }

    public Damageable normalAttackChecker(Champion c, Direction d) throws InvalidTargetException, ChampionDisarmedException, NotEnoughResourcesException { // HELPER METHOD
        Damageable tempDamageable = null;
        for (Effect effect : c.getAppliedEffects())
            if (effect instanceof Disarm)
                throw new ChampionDisarmedException("Champion is disarmed");
        if (c.getCondition() != Condition.ACTIVE)
            throw new InvalidTargetException("Champion is inactive, knocked out or rooted");
        else if (c.getCurrentActionPoints() <= 1)
            throw new InvalidTargetException("Not enough action points");
        else
            switch (d) {
            case RIGHT:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().x + i < BOARDWIDTH)
                        if (board[c.getLocation().y][(int) (c.getLocation().x + i)] != null) {
                            tempDamageable = (Damageable) board[(int) c.getLocation().y][(int) (c.getLocation().x + i)];
                            if (tempDamageable instanceof Champion)
                                if ((firstPlayer.getTeam().contains(tempDamageable) && firstPlayer.getTeam().contains(c)) || (secondPlayer.getTeam().contains(tempDamageable) && secondPlayer.getTeam().contains(c)))
                                    throw new InvalidTargetException("Champion is on the same team");
                            break;
                        } else
                            continue;
                break;
            case LEFT:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().x - i < BOARDWIDTH)
                        if (board[c.getLocation().y][(int) (c.getLocation().x + i)] != null) {
                            tempDamageable = (Damageable) board[(int) c.getLocation().y][(int) (c.getLocation().x - i)];
                            if (tempDamageable instanceof Champion)
                                if ((firstPlayer.getTeam().contains(tempDamageable) && firstPlayer.getTeam().contains(c)) || (secondPlayer.getTeam().contains(tempDamageable) && secondPlayer.getTeam().contains(c)))
                                    throw new InvalidTargetException("Champion is on the same team");
                            break;
                        } else
                            continue;
                break;
            case UP:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().y + i < BOARDHEIGHT)
                        if (board[c.getLocation().y - i][(int) (c.getLocation().x)] != null) {
                            tempDamageable = (Damageable) board[(int) c.getLocation().y + i][(int) (c.getLocation().x)];
                            if (tempDamageable instanceof Champion)
                                if ((firstPlayer.getTeam().contains(tempDamageable) && firstPlayer.getTeam().contains(c)) || (secondPlayer.getTeam().contains(tempDamageable) && secondPlayer.getTeam().contains(c)))
                                    throw new InvalidTargetException("Champion is on the same team");
                            break;
                        } else
                            continue;
                break;
            case DOWN:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().y - i < BOARDHEIGHT)
                        if (board[c.getLocation().y - i][(int) (c.getLocation().x)] != null) {
                            tempDamageable = (Damageable) board[(int) c.getLocation().y - i][(int) (c.getLocation().x)];
                            if (tempDamageable instanceof Champion)
                                if ((firstPlayer.getTeam().contains(tempDamageable) && firstPlayer.getTeam().contains(c)) || (secondPlayer.getTeam().contains(tempDamageable) && secondPlayer.getTeam().contains(c)))
                                    throw new InvalidTargetException("Champion is on the same team");
                            break;
                        } else
                            continue;
                break;
            default:
            }

        if (tempDamageable instanceof Champion)
            for (Effect effect : ((Champion) tempDamageable).getAppliedEffects())
                if (effect instanceof Dodge) {
                    int dodgeChance = (int) Math.random() * 2; // 0 if dodged, 1 if not dodged
                    if (dodgeChance == 0)
                        tempDamageable = null;
                } else if (effect instanceof Shield) {
                    tempDamageable = null;
                }
        return tempDamageable;
    }

    public void move(Direction d) throws UnallowedMovementException {
        // check if the cell we wanna move to doesn't contain a champion, cover or isn't
        // out of board bounds (is an empty cell)

        if (isMoveable(getCurrentChampion(), d)) {
            switch (d) {
            case RIGHT:
                getCurrentChampion().getLocation().x++;
                break;
            case LEFT:
                getCurrentChampion().getLocation().x--;
                break;
            case UP:
                getCurrentChampion().getLocation().y++;
                break;
            case DOWN:
                getCurrentChampion().getLocation().y--;
                break;
            default:
            }
            getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 1);
        } else
            throw new UnallowedMovementException();
    }

    @SuppressWarnings("all")
    public void attack(Direction d) throws ChampionDisarmedException, InvalidTargetException, NotEnoughResourcesException {
        Damageable tempDamageable = normalAttackChecker(getCurrentChampion(), d);
        // remove action points if attack is successful or target dodged
        if (tempDamageable != null) {
            if (((((Comparable) tempDamageable).compareTo(getCurrentChampion()))) <= 0)
                tempDamageable.setCurrentHP(tempDamageable.getCurrentHP() - getCurrentChampion().getAttackDamage());
            else if (((((Comparable) tempDamageable).compareTo(getCurrentChampion()))) > 0)
                tempDamageable.setCurrentHP((int) (tempDamageable.getCurrentHP() - (getCurrentChampion().getAttackDamage() * 1.5)));
            else
                tempDamageable.setCurrentHP(tempDamageable.getCurrentHP() - getCurrentChampion().getAttackDamage());
        }
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 2);
    }

    public Boolean isShielded(Damageable target) { // HELPER METHOD
        for (Effect effect : ((Champion) target).getAppliedEffects())
            if (!(effect instanceof Shield))
                return false;
            else
                ((Champion) target).getAppliedEffects().remove(effect);
        return true;
    }

    public Boolean isSilenced(Champion c) { // HELPER METHOD
        for (Effect effect : c.getAppliedEffects())
            if (!(effect instanceof Silence))
                return true;
        return false;
    }

    public Boolean isSameTeam(Champion c1, Champion c2) { // HELPER METHOD
        if (firstPlayer.getTeam().contains(c1) && firstPlayer.getTeam().contains(c2))
            return true;
        else if (secondPlayer.getTeam().contains(c1) && secondPlayer.getTeam().contains(c2))
            return true;
        else
            return false;
    }

    public int getCurrentChampionTeam() { // HELPER METHOD
        if (firstPlayer.getTeam().contains(getCurrentChampion()))
            return 1;
        else
            return 2;
    }

    public ArrayList<Damageable> surrounding(Point championLocation) { // HELPER METHOD
        ArrayList<Damageable> targets = new ArrayList<Damageable>();
        HashMap<Integer, Point> surroundMap = new HashMap<Integer, Point>();
        surroundMap.put(0, new Point(championLocation.getLocation().x--,championLocation.getLocation().y++));
        surroundMap.put(1, new Point(championLocation.getLocation().x,championLocation.getLocation().y++));
        surroundMap.put(2, new Point(championLocation.getLocation().x++,championLocation.getLocation().y++));
        surroundMap.put(3, new Point(championLocation.getLocation().x--,championLocation.getLocation().y));
        surroundMap.put(4, new Point(championLocation.getLocation().x++,championLocation.getLocation().y));
        surroundMap.put(5, new Point(championLocation.getLocation().x--,championLocation.getLocation().y--));
        surroundMap.put(6, new Point(championLocation.getLocation().x,championLocation.getLocation().y--));
        surroundMap.put(7, new Point(championLocation.getLocation().x++,championLocation.getLocation().y--));

        for (int i = 0; i < 8; i++) {
            if (surroundMap.get(i).x >= 0 && surroundMap.get(i).x < BOARDWIDTH && surroundMap.get(i).y >= 0 && surroundMap.get(i).y < BOARDHEIGHT)
                if (board[surroundMap.get(i).y][surroundMap.get(i).x] != null)
                    targets.add((Damageable) board[surroundMap.get(i).y][surroundMap.get(i).x]);
        }

        return targets;
    }

    public void castAbilityChecker(Ability a) throws NotEnoughResourcesException, AbilityUseException {
        if (getCurrentChampion().getMana() < a.getManaCost())
            throw new NotEnoughResourcesException("Not enough mana!");
        else if (a.getCurrentCooldown() > 0)
            throw new AbilityUseException("Ability is on cooldown!");
        else if (isSilenced(getCurrentChampion()))
            throw new AbilityUseException("Champion is silenced!");
    }

    public void castAbility(Ability a) throws InvalidTargetException, CloneNotSupportedException, NotEnoughResourcesException, AbilityUseException { // SURROUND, SELFTARGET, TEAMTARGET
        ArrayList<Damageable> targets = new ArrayList<Damageable>();

        // x++,y / x--,y / x,y++ / x,y-- / x++,y++ / x--,y-- / x++,y-- / x--,y++
        // RIGHT / LEFT / UP / DOWN / UPRIGHT / DOWNLEFT / DOWNRIGHT / UPLEFT

        // HashMap<Integer, Object> surroundMap = new HashMap<Integer, Object>();
        // try {
        //     surroundMap.put(0, board[getCurrentChampion().getLocation().x--][getCurrentChampion().getLocation().y++]);
        //     surroundMap.put(1, board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y++]);
        //     surroundMap.put(2, board[getCurrentChampion().getLocation().x++][getCurrentChampion().getLocation().y++]);
        //     surroundMap.put(3, board[getCurrentChampion().getLocation().x--][getCurrentChampion().getLocation().y]);
        //     surroundMap.put(4, board[getCurrentChampion().getLocation().x++][getCurrentChampion().getLocation().y]);
        //     surroundMap.put(5, board[getCurrentChampion().getLocation().x--][getCurrentChampion().getLocation().y--]);
        //     surroundMap.put(6, board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y--]);
        //     surroundMap.put(7, board[getCurrentChampion().getLocation().x++][getCurrentChampion().getLocation().y--]);
        // } catch (ArrayIndexOutOfBoundsException e) {
        //     return;
        // } // do we need to handle this specific exception?

        castAbilityChecker(a);
        if (a instanceof HealingAbility) {
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                for (Damageable d : surrounding(getCurrentChampion().getLocation()))
                    if (d != null && d instanceof Champion)
                        if (isSameTeam(getCurrentChampion(), (Champion) d))
                            targets.add((Damageable) d);
            } else if (a.getCastArea() == AreaOfEffect.SELFTARGET) {
                targets.add(getCurrentChampion());
            } else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (getCurrentChampionTeam() == 1)
                    targets.add((Damageable) firstPlayer.getTeam()); // TODO: Does this work or do we have to go through the arraylist Team one by one?
                else
                    targets.add((Damageable) secondPlayer.getTeam());
            }

        } else if (a instanceof DamagingAbility) {
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                for (Damageable d : surrounding(getCurrentChampion().getLocation()))
                    if (d != null)
                        if (d instanceof Champion) {
                            if (!isShielded((Damageable) d)) {
                                if (!isSameTeam(getCurrentChampion(), (Champion) d))
                                    targets.add((Damageable) d);
                            }
                        } else
                            targets.add((Damageable) d);
            } else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (getCurrentChampionTeam() == 1) {
                    for (Champion c : secondPlayer.getTeam()) {
                        if (!isShielded(c))
                            targets.add((Damageable) c);
                    }
                } else {
                    for (Champion c : firstPlayer.getTeam()) {
                        if (!isShielded(c))
                            targets.add((Damageable) c);
                    }
                }
            }
        } else if (a instanceof CrowdControlAbility) {
            if (a.getCastArea() == AreaOfEffect.SURROUND) {
                // for (Object d : surroundMap.values()) // TODO: this gives less errors???
                for (Damageable d : surrounding(getCurrentChampion().getLocation()))
                    if (d != null && d instanceof Champion)
                        if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                            if (!isSameTeam(getCurrentChampion(), (Champion) d))
                                targets.add((Damageable) d);
                        } else {
                            if (isSameTeam(getCurrentChampion(), (Champion) d))
                                targets.add((Damageable) d);
                        }
            } else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                    if (getCurrentChampionTeam() == 1) {
                        targets.add((Damageable) secondPlayer.getTeam());
                    } else {
                        targets.add((Damageable) firstPlayer.getTeam());
                    }
                } else {
                    if (getCurrentChampionTeam() == 1) {
                        targets.add((Damageable) firstPlayer.getTeam());
                    } else {
                        targets.add((Damageable) secondPlayer.getTeam());
                    }
                }
            } else if (a.getCastArea() == AreaOfEffect.SELFTARGET)
                targets.add(getCurrentChampion());
        }

        if (targets.isEmpty())
            throw new InvalidTargetException("No targets found");
        else
            a.execute(targets);
    }

    public void castAbility(Ability a, Direction d) { // DIRECTIONAL

    }

    public void castAbility(Ability a, int x, int y) { // SINGLETARGET

    }

    private void placeChampions() {

        for (int i = 0; i < firstPlayer.getTeam().size(); i++) {
            board[0][i + 1] = firstPlayer.getTeam().get(i);
            firstPlayer.getTeam().get(i).setLocation(new Point(0, i + 1));
        }

        for (int i = 0; i < secondPlayer.getTeam().size(); i++) {
            board[BOARDHEIGHT - 1][i + 1] = secondPlayer.getTeam().get(i);
            secondPlayer.getTeam().get(i).setLocation(new Point(BOARDHEIGHT - 1, i + 1));
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
        // Type, name, manaCost, castRange, baseCooldownreaOfEffect,
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
            // Type, name, maxHP, manactions, speedttackRange,
            // attackDamagebility1 namebility2 namebility3 name

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
