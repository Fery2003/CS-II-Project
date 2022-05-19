package engine;

import java.io.*;
import java.util.ArrayList;
import java.util.SplittableRandom;
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
    // private static ArrayList<Damageable> targets = new ArrayList<Damageable>();
    // // TODO: this is allowed

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

    // #region HELPER METHODS

    public Damageable normalAttackChecker(Champion c, Direction d) throws InvalidTargetException, ChampionDisarmedException, NotEnoughResourcesException { // HELPER METHOD
        Damageable tempDamageable = null;
        for (Effect effect : c.getAppliedEffects())
            if (effect instanceof Disarm)
                throw new ChampionDisarmedException("Champion is disarmed");
        if (c.getCondition() != Condition.ACTIVE)
            throw new InvalidTargetException("Champion is inactive or knocked out.");
        else if (c.getCurrentActionPoints() < 2)
            throw new NotEnoughResourcesException("Not enough action points");
        else
            switch (d) {
            case RIGHT:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().y + i < BOARDHEIGHT && board[c.getLocation().x][c.getLocation().y + i] != null) {
                        tempDamageable = (Damageable) board[c.getLocation().x][c.getLocation().y + i];
                        if (!(tempDamageable instanceof Champion) && !(isSameTeam(c, (Champion) tempDamageable)))
                            return tempDamageable;
                    } else
                        continue;
                break;
            case LEFT:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().y - i >= 0 && board[c.getLocation().x][c.getLocation().y - i] != null) {
                        tempDamageable = (Damageable) board[c.getLocation().x][c.getLocation().y - i];
                        if (tempDamageable instanceof Champion && isSameTeam(c, (Champion) tempDamageable))
                            return tempDamageable;
                    } else
                        continue;
                break;
            case UP:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().x + i < BOARDWIDTH && board[c.getLocation().x + i][c.getLocation().y] != null) {
                        tempDamageable = (Damageable) board[c.getLocation().x + i][c.getLocation().y];
                        if (tempDamageable instanceof Champion && isSameTeam(c, (Champion) tempDamageable))
                            return tempDamageable;
                    } else
                        continue;
                break;
            case DOWN:
                for (int i = 1; i <= c.getAttackRange(); i++)
                    if (c.getLocation().x - i >= 0 && board[c.getLocation().x - i][c.getLocation().y] != null) {
                        tempDamageable = (Damageable) board[c.getLocation().x - i][c.getLocation().y];
                        if (tempDamageable instanceof Champion && isSameTeam(c, (Champion) tempDamageable))
                            return tempDamageable;
                    } else
                        continue;
                break;
            default:
                throw new InvalidTargetException("Champion is on the same team!");
            }
        return tempDamageable;
    }

    public Boolean willDodgeAttack(Champion c) { // HELPER METHOD
        SplittableRandom random = new SplittableRandom();
        return random.nextInt(1, 101) <= 50 ? true : false;
    }

    public Boolean isShielded(Champion target) { // HELPER METHOD
        for (Effect effect : target.getAppliedEffects())
            if (effect instanceof Shield) {
                effect.remove(target);
                target.getAppliedEffects().remove(effect);
                return true;
            }
        return false;
    }

    public Boolean isSilenced(Champion c) { // HELPER METHOD
        for (Effect effect : c.getAppliedEffects())
            if (effect instanceof Silence)
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

    public int getChampionTeam() { // HELPER METHOD
        if (firstPlayer.getTeam().contains(getCurrentChampion()))
            return 1;
        else
            return 2;
    }

    public int getChampionTeam(Champion c) {
        if (firstPlayer.getTeam().contains(c))
            return 1;
        else
            return 2;
    }

    public ArrayList<Damageable> surrounding(Point championLocation) { // HELPER METHOD
        ArrayList<Damageable> targets = new ArrayList<Damageable>();
        for (int i = -1; i <= 1; i++)
            if (championLocation.y + i >= 0 && championLocation.y + i < BOARDHEIGHT)
                for (int j = -1; j <= 1; j++)
                    if (championLocation.x + j >= 0 && championLocation.x + j < BOARDWIDTH)
                        if (board[championLocation.y + i][championLocation.x + j] != null && !(i == 0 && j == 0))
                            targets.add((Damageable) board[championLocation.y + i][championLocation.x + j]);
        return targets;
    }

    public void castAbilityChecker(Ability a) throws NotEnoughResourcesException, AbilityUseException { // HELPER METHOD

        if (getCurrentChampion().getMana() < a.getManaCost() || getCurrentChampion().getCurrentActionPoints() < a.getRequiredActionPoints())
            throw new NotEnoughResourcesException("Not enough resources!");
        else if (a.getCurrentCooldown() > 0)
            throw new AbilityUseException("Ability is on cooldown!");
        else if (isSilenced(getCurrentChampion()))
            throw new AbilityUseException("Champion is silenced!");
    }

    public ArrayList<Damageable> directionalTileChecker(Direction d, int castRange) { // HELPER METHOD
        ArrayList<Damageable> targets = new ArrayList<Damageable>();
        for (int i = 1; i <= castRange; i++)
            switch (d) {
            case UP:
                if (getCurrentChampion().getLocation().x + i < BOARDHEIGHT)
                    if (board[getCurrentChampion().getLocation().x + i][getCurrentChampion().getLocation().y] != null)
                        targets.add((Damageable) board[getCurrentChampion().getLocation().x + i][getCurrentChampion().getLocation().y]);
                break;
            case DOWN:
                if (getCurrentChampion().getLocation().x - i >= 0)
                    if (board[getCurrentChampion().getLocation().x - i][getCurrentChampion().getLocation().y] != null)
                        targets.add((Damageable) board[getCurrentChampion().getLocation().x - i][getCurrentChampion().getLocation().y]);
                break;

            case RIGHT:
                if (getCurrentChampion().getLocation().y + i < BOARDWIDTH)
                    if (board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y + i] != null)
                        targets.add((Damageable) board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y + i]);
                break;

            case LEFT:
                if (getCurrentChampion().getLocation().y - i >= 0)
                    if (board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y - i] != null)
                        targets.add((Damageable) board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y - i]);
                break;

            default:

            }
        return targets;
    }

    public void postAbility(Ability a, ArrayList<Damageable> d) { // HELPER METHOD
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
        a.setCurrentCooldown(a.getBaseCooldown());

        if (a instanceof DamagingAbility) {
            for (Damageable target : d) {
                if (target.getCurrentHP() <= 0) {
                    if (target instanceof Champion) {
                        if (getChampionTeam((Champion) target) == 1)
                            firstPlayer.getTeam().remove(target);
                        else
                            secondPlayer.getTeam().remove(target);
                    }
                    board[target.getLocation().x][target.getLocation().y] = null;
                }
            }
        }
    }

    public void postAbility(Ability a) {
        getCurrentChampion().setMana(getCurrentChampion().getMana() - a.getManaCost());
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - a.getRequiredActionPoints());
        a.setCurrentCooldown(a.getBaseCooldown());
    }

    public Boolean isInCastRange(Damageable target, int range) { // HELPER METHOD, return true if in range
        return Math.abs(target.getLocation().x - getCurrentChampion().getLocation().x) + Math.abs(target.getLocation().y - getCurrentChampion().getLocation().y) <= range;
    }

    public ArrayList<Effect> effectsToRemove() { // HELPER METHOD
        ArrayList<Effect> effectsToRemove = new ArrayList<Effect>();
        for (Effect effect : getCurrentChampion().getAppliedEffects())
            if (effect.getDuration() <= 0)
                effectsToRemove.add(effect);
        return effectsToRemove;
    }

    // #endregion

    public Champion getCurrentChampion() {
        // prepareChampionTurns();
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

    public void move(Direction d) throws UnallowedMovementException, NotEnoughResourcesException {
        // check if the cell we wanna move to doesn't contain a champion, cover or isn't
        // out of board bounds (is an empty cell)

        if (getCurrentChampion().getCurrentActionPoints() < 1)
            throw new NotEnoughResourcesException("Not enough action points!");
        if (getCurrentChampion().getCondition() == Condition.ROOTED)
            throw new UnallowedMovementException("Champion is rooted!");

        if (d == Direction.UP) {
            if (++getCurrentChampion().getLocation().x >= BOARDHEIGHT)
                throw new UnallowedMovementException("Out of board bounds!");
            else if (board[getCurrentChampion().getLocation().x++][getCurrentChampion().getLocation().y] != null)
                throw new UnallowedMovementException("Cell is not empty!");
            else {
                board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y] = null;
                board[++getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y] = getCurrentChampion();
                getCurrentChampion().setLocation(new Point(getCurrentChampion().getLocation().x - 2, getCurrentChampion().getLocation().y));
            }
        } else if (d == Direction.DOWN) {
            if (--getCurrentChampion().getLocation().x < 0)
                throw new UnallowedMovementException("Out of board bounds!");
            else if (board[getCurrentChampion().getLocation().x--][getCurrentChampion().getLocation().y] != null)
                throw new UnallowedMovementException("Cell is not empty!");
            else {
                board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y] = null;
                board[--getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y] = getCurrentChampion();
                getCurrentChampion().setLocation(new Point(getCurrentChampion().getLocation().x + 2, getCurrentChampion().getLocation().y));
            }
        } else if (d == Direction.LEFT) {
            if (--getCurrentChampion().getLocation().y < 0)
                throw new UnallowedMovementException("Out of board bounds!");
            else if (board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y--] != null)
                throw new UnallowedMovementException("Cell is not empty!");
            else {
                board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y] = null;
                board[getCurrentChampion().getLocation().x][--getCurrentChampion().getLocation().y] = getCurrentChampion();
                getCurrentChampion().setLocation(new Point(getCurrentChampion().getLocation().x, getCurrentChampion().getLocation().y + 2));
            }
        } else if (d == Direction.RIGHT) {
            if (++getCurrentChampion().getLocation().y >= BOARDWIDTH)
                throw new UnallowedMovementException("Out of board bounds!");
            else if (board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y++] != null)
                throw new UnallowedMovementException("Cell is not empty!");
            else {
                board[getCurrentChampion().getLocation().x][getCurrentChampion().getLocation().y] = null;
                board[getCurrentChampion().getLocation().x][++getCurrentChampion().getLocation().y] = getCurrentChampion();
                getCurrentChampion().setLocation(new Point(getCurrentChampion().getLocation().x, getCurrentChampion().getLocation().y - 2));
            }
        }
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 1);
    }

    public void attack(Direction d) throws ChampionDisarmedException, InvalidTargetException, NotEnoughResourcesException {
        Damageable tempDamageable = normalAttackChecker(getCurrentChampion(), d);
        // remove action points if attack is successful or target dodged
        if (tempDamageable != null && tempDamageable instanceof Champion) {
            if (!isShielded((Champion) tempDamageable) && !willDodgeAttack((Champion) tempDamageable))
                if (getCurrentChampion().heroTypeChecker((Champion) tempDamageable) == 0)
                    tempDamageable.setCurrentHP(tempDamageable.getCurrentHP() - getCurrentChampion().getAttackDamage());
                else if (getCurrentChampion().heroTypeChecker((Champion) tempDamageable) == 1)
                    tempDamageable.setCurrentHP(tempDamageable.getCurrentHP() - ((int) (getCurrentChampion().getAttackDamage() * 1.5)));
        } else
            tempDamageable.setCurrentHP(tempDamageable.getCurrentHP() - getCurrentChampion().getAttackDamage());

        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getCurrentActionPoints() - 2);

        if (tempDamageable.getCurrentHP() <= 0) {
            if (tempDamageable instanceof Champion && ((Champion) tempDamageable).getCondition() == Condition.KNOCKEDOUT)
                turnOrder.remove();
            board[tempDamageable.getLocation().y][tempDamageable.getLocation().x] = null;
        }
    }

    public void castAbility(Ability a) throws InvalidTargetException, CloneNotSupportedException, NotEnoughResourcesException, AbilityUseException { // SURROUND, SELFTARGET, TEAMTARGET

        // x++,y / x--,y / x,y++ / x,y-- / x++,y++ / x--,y-- / x++,y-- / x--,y++
        // RIGHT / LEFT / UP / DOWN / UPRIGHT / DOWNLEFT / DOWNRIGHT / UPLEFT
        castAbilityChecker(a);
        ArrayList<Damageable> targets = new ArrayList<Damageable>();
        if (a instanceof HealingAbility) {
            if (a.getCastArea().equals(AreaOfEffect.SURROUND)) {
                for (Damageable d : surrounding(getCurrentChampion().getLocation()))
                    if (d != null && d instanceof Champion)
                        if (isSameTeam(getCurrentChampion(), (Champion) d))
                            targets.add(d);
            } else if (a.getCastArea().equals(AreaOfEffect.SELFTARGET)) {
                targets.add(getCurrentChampion());
            } else if (a.getCastArea().equals(AreaOfEffect.TEAMTARGET)) {
                if (getChampionTeam() == 1) {
                    for (Champion c : firstPlayer.getTeam())
                        if (isInCastRange(c, a.getCastRange()))
                            targets.add(c);
                } else
                    for (Champion c : secondPlayer.getTeam())
                        if (isInCastRange(c, a.getCastRange()))
                            targets.add(c);
            }

        } else if (a instanceof DamagingAbility) {
            if (a.getCastArea().equals(AreaOfEffect.SURROUND)) {
                for (Damageable d : surrounding(getCurrentChampion().getLocation()))
                    if (d != null && d instanceof Champion)
                        if (!isSameTeam(getCurrentChampion(), (Champion) d) && !isShielded((Champion) d))
                            targets.add(d);
            } else if (a.getCastArea().equals(AreaOfEffect.TEAMTARGET)) {
                if (getChampionTeam() == 1) {
                    for (Champion c : secondPlayer.getTeam())
                        if (isInCastRange(c, a.getCastRange()))
                            targets.add(c);
                } else {
                    for (Champion c : firstPlayer.getTeam())
                        if (isInCastRange(c, a.getCastRange()))
                            targets.add(c);
                }
            }
        } else if (a instanceof CrowdControlAbility) {
            if (a.getCastArea().equals(AreaOfEffect.SURROUND)) {
                for (Damageable d : surrounding(getCurrentChampion().getLocation()))
                    if (d != null && d instanceof Champion)
                        if (((CrowdControlAbility) a).getEffect().getType().equals(EffectType.DEBUFF)) {
                            if (!isSameTeam(getCurrentChampion(), (Champion) d))
                                targets.add((Damageable) d);
                        } else {
                            if (isSameTeam(getCurrentChampion(), (Champion) d))
                                targets.add((Damageable) d);
                        }
            } else if (a.getCastArea().equals(AreaOfEffect.TEAMTARGET)) {
                if (((CrowdControlAbility) a).getEffect().getType().equals(EffectType.DEBUFF)) {
                    if (getChampionTeam() == 1) {
                        for (Champion c : secondPlayer.getTeam()) {
                            if (isInCastRange(c, a.getCastRange()))
                                targets.add((Damageable) c);
                        }
                    } else {
                        for (Champion c : firstPlayer.getTeam()) {
                            if (isInCastRange(c, a.getCastRange()))
                                targets.add((Damageable) c);
                        }
                    }
                } else {
                    if (getChampionTeam() == 1) {
                        for (Champion c : firstPlayer.getTeam())
                            if (isInCastRange(c, a.getCastRange()))
                                targets.add((Damageable) c);
                    } else {
                        for (Champion c : secondPlayer.getTeam())
                            if (isInCastRange(c, a.getCastRange()))
                                targets.add((Damageable) c);
                    }
                }
            } else if (a.getCastArea().equals(AreaOfEffect.SELFTARGET)) {
                targets.add(getCurrentChampion());
            }
        }

        a.execute(targets);
        postAbility(a, targets);

    }

    public void castAbility(Ability a, Direction d) throws InvalidTargetException, CloneNotSupportedException, NotEnoughResourcesException, AbilityUseException { // DIRECTIONAL
        castAbilityChecker(a);
        ArrayList<Damageable> targets = new ArrayList<Damageable>();

        if (a instanceof HealingAbility) {
            for (Damageable c : directionalTileChecker(d, a.getCastRange()))
                if (c instanceof Champion && isSameTeam(getCurrentChampion(), (Champion) c))
                    targets.add(c);
        } else if (a instanceof DamagingAbility) {
            for (Damageable c : directionalTileChecker(d, a.getCastRange()))
                if (c instanceof Champion) {
                    if (!isShielded((Champion) c) && !isSameTeam(getCurrentChampion(), (Champion) c))
                        targets.add(c);
                } else
                    targets.add(c);

        } else if (a instanceof CrowdControlAbility) {
            for (Damageable c : directionalTileChecker(d, a.getCastRange()))
                if (c instanceof Champion)
                    if (((CrowdControlAbility) a).getEffect().getType().equals(EffectType.DEBUFF)) {
                        if (!isSameTeam(getCurrentChampion(), (Champion) c)) // if debuff add to enemy team
                            targets.add(c);
                    } else {
                        if (isSameTeam(getCurrentChampion(), (Champion) c)) // else if buff add to own team
                            targets.add(c);
                    }
        }

        a.execute(targets);
        postAbility(a, targets);

    }

    public void castAbility(Ability a, int x, int y) throws InvalidTargetException, CloneNotSupportedException, NotEnoughResourcesException, AbilityUseException { // SINGLETARGET

        castAbilityChecker(a);
        ArrayList<Damageable> targets = new ArrayList<Damageable>();
        // int distance = Math.abs(x1-x0) + Math.abs(y1-y0);
        int distance = Math.abs(x - getCurrentChampion().getLocation().x) + Math.abs(y - getCurrentChampion().getLocation().y);
        if (board[x][y] == null) {
            postAbility(a);
            throw new InvalidTargetException();
        }
        if (distance > a.getCastRange())
            throw new AbilityUseException();
        if (a instanceof HealingAbility) {
            if (board[x][y] instanceof Champion) {
                if (isSameTeam(getCurrentChampion(), (Champion) board[x][y]))
                    targets.add((Champion) board[x][y]);
            } else if (board[x][y] instanceof Cover)
                throw new InvalidTargetException();

        } else if (a instanceof DamagingAbility) {
            if (board[x][y].equals(getCurrentChampion()))
                throw new InvalidTargetException();

            if (board[x][y] instanceof Champion) {
                if (!isShielded((Champion) board[x][y]) && !isSameTeam(getCurrentChampion(), (Champion) board[x][y]))
                    targets.add((Damageable) board[x][y]);
            } else if (board[x][y] instanceof Cover)
                targets.add((Damageable) board[x][y]);

        } else if (a instanceof CrowdControlAbility) {
            if (board[x][y] instanceof Champion)
                if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF) {
                    if (!isSameTeam(getCurrentChampion(), (Champion) board[x][y]))
                        targets.add((Damageable) board[x][y]);
                    else
                        throw new InvalidTargetException();
                } else {
                    if (isSameTeam(getCurrentChampion(), (Champion) board[x][y]))
                        targets.add((Damageable) board[x][y]);
                    else
                        throw new InvalidTargetException();
                }
            else
                throw new InvalidTargetException();
        }

        a.execute(targets);
        postAbility(a, targets);

    }

    public void useLeaderAbility() throws LeaderNotCurrentException {
        ArrayList<Champion> targets = new ArrayList<Champion>();

        if (!firstPlayer.getLeader().equals(getCurrentChampion()) && !secondPlayer.getLeader().equals(getCurrentChampion()))
            throw new LeaderNotCurrentException();

        // if(getChampionTeam() == 1) //TODO STOPPED HERE
    }

    public void endTurn() {
        // update current champion's ability cooldowns, ability durations and remove all
        // expired effects
        // if current champion is stunned (Condition.INACTIVE), update their stun
        // duration and remove if expired
        for (Effect effect : getCurrentChampion().getAppliedEffects())
            effect.setDuration(effect.getDuration() - 1);
        for (Effect effect : getCurrentChampion().getAppliedEffects())
            if (effect.getDuration() <= 0)
                effect.remove(getCurrentChampion());

        for (Effect e : effectsToRemove())
            getCurrentChampion().getAppliedEffects().remove(e);

        for (Ability ability : getCurrentChampion().getAbilities())
            ability.setCurrentCooldown(ability.getCurrentCooldown() - 1);
        getCurrentChampion().setCurrentActionPoints(getCurrentChampion().getMaxActionPointsPerTurn());
        turnOrder.remove();

        if (turnOrder.isEmpty()) // prepare a new turnOrder if the current one is empty (aka all turns have
                                 // ended)
            prepareChampionTurns();

        while (getCurrentChampion().getCondition().equals(Condition.INACTIVE)) { // decrement all abilities for current
                                                                                 // too
            for (Effect effect : getCurrentChampion().getAppliedEffects())
                effect.setDuration(effect.getDuration() - 1);
            for (Effect effect : getCurrentChampion().getAppliedEffects()) {
                if (effect.getDuration() <= 0)
                    effect.remove(getCurrentChampion());
            }
            for (Effect e : effectsToRemove())
                getCurrentChampion().getAppliedEffects().remove(e);

            for (Ability ability : getCurrentChampion().getAbilities())
                ability.setCurrentCooldown(ability.getCurrentCooldown() - 1);
            turnOrder.remove();
        }
    }

    private void prepareChampionTurns() {
        for (int i = 0; i <= firstPlayer.getTeam().size(); i++)
            if (firstPlayer.getTeam().get(i).getCondition() != Condition.KNOCKEDOUT)
                turnOrder.insert(firstPlayer.getTeam().get(i));
        for (int i = 0; i <= secondPlayer.getTeam().size(); i++)
            if (secondPlayer.getTeam().get(i).getCondition() != Condition.KNOCKEDOUT)
                turnOrder.insert(secondPlayer.getTeam().get(i));
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
            int y = (int) (Math.random() * 3) + 1; // to make sure it's not placed on first and/or last rows
            int x = (int) (Math.random() * 5); // any column in the rest of the rows

            if (board[y][x] == null) { // if the spot is empty
                board[y][x] = new Cover(x, y);
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
