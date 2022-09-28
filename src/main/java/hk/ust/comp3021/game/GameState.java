package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * The state of the Sokoban Game.
 * Each game state represents an ongoing game.
 * As the game goes, the game state changes while players are moving
 * while the original game map stays the unmodified.
 * <b>The game state should not modify the original game map.</b>
 * <p>
 * GameState consists of things changing as the game goes, such as:
 * <li>Current locations of all crates.</li>
 * <li>A move history.</li>
 * <li>Current location of player.</li>
 * <li>Undo quota left.</li>
 */
public class GameState {
    private GameMap gameMap;
    private Map<Position, Character> currentBoxLocations;
    private Map<Character, Position> currentPlayerLocations;
    private Stack<Entity[][]> moveHistoryOfEntity;
    private int undoQuota;


    /**
     * Create a running game state from a game map.
     *
     * @param map the game map from which to create this game state.
     */
    public GameState(@NotNull GameMap map) {
        this.gameMap = map;
        this.currentBoxLocations = new HashMap<>();
        this.currentPlayerLocations = new HashMap<>();
        this.moveHistoryOfEntity = new Stack<>();
        this.undoQuota = map.getUndoLimit().get();

        // all the current locations come from GameMap at first
        for (int i = 0; i < getMapMaxHeight(); i++) {
            for (int j = 0; j < getMapMaxWidth(); j++)  {
                Entity entity = map.getEntity(new Position(j, i));
                if (entity instanceof Box) {
                    currentBoxLocations.put(new Position(j, i), (char)(((Box)entity).getPlayerId()+97));
                } else if (entity instanceof Player) {
                    currentPlayerLocations.put((char)(((Player)entity).getId()+65), new Position(j, i));
                }
            }
        }
        checkpoint(); // record the init state
    }

    /**
     * Get the current position of the player with the given id.
     *
     * @param id player id.
     * @return the current position of the player.
     */
    public @Nullable Position getPlayerPositionById(int id) {
        Character key = (char)(id+65);
        if (currentPlayerLocations.containsKey(key)) {
            return currentPlayerLocations.get(key);
        }
        return null;
    }

    /**
     * Get current positions of all players in the game map.
     *
     * @return a set of positions of all players.
     */
    public @NotNull Set<Position> getAllPlayerPositions() {
        Set<Position> allPositions = new HashSet<>();
        for (int i = 0; i < 26; i++) {
            Character key = (char)(i+65);
            if (currentPlayerLocations.containsKey(key)) {
                allPositions.add(currentPlayerLocations.get(key));
            }
        }
        return allPositions;
    }

    /**
     * Get the entity that is currently at the given position.
     *
     * @param position the position of the entity.
     * @return the entity object.
     */
    public @Nullable Entity getEntity(@NotNull Position position) {
        return gameMap.entityArray[position.y()][position.x()];
    }

    /**
     * Get all box destination positions as a set in the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return gameMap.getDestinations();
    }

    /**
     * Get the undo quota currently left, i.e., the maximum number of undo actions that can be performed from now on.
     * If undo is unlimited,
     *
     * @return the undo quota left (using {@link Optional#of(Object)}) if the game has an undo limit;
     * {@link Optional#empty()} if the game has unlimited undo.
     */
    public Optional<Integer> getUndoQuota() {
        if (undoQuota == -1) {
            return Optional.empty();
        } else {
            return Optional.of(undoQuota);
        }
    }

    /**
     * Check whether the game wins or not.
     * The game wins only when all box destinations have been occupied by boxes.
     *
     * @return true is the game wins.
     */
    public boolean isWin() {
        for (int i = 0; i < getMapMaxHeight(); i++) {
            for (int j = 0; j < getMapMaxWidth(); j++) {
                // if there is a box not in box des. -> not winning
                Entity entity = getEntity(new Position(j, i));
                if ((entity instanceof Box) && (!getDestinations().contains(new Position(j, i)))) {
                    return false;
                }
            }
        }
        // if all boxes in the box des. -> win
        return true;
    }

    /**
     * Move the entity from one position to another.
     * This method assumes the validity of this move is ensured.
     * <b>The validity of the move of the entity in one position to another need not to check.</b>
     *
     * @param from The current position of the entity to move.
     * @param to   The position to move the entity to.
     */
    public void move(Position from, Position to) {
        Entity entity = getEntity(from);
        if (entity instanceof Player) {
            int id = ((Player)entity).getId();
            char charID = (char)(id+65);
            currentPlayerLocations.remove(charID);
            currentPlayerLocations.put(charID, new Position(to.x(), to.y()));
        } else if (entity instanceof Box) {
            int id = ((Box)entity).getPlayerId();
            char charID = (char)(id+97);
            currentBoxLocations.remove(from);
            currentBoxLocations.put(to, charID);

        }
        GameMap.entityArray[to.y()][to.x()] = GameMap.entityArray[from.y()][from.x()];
        GameMap.entityArray[from.y()][from.x()] = new Empty();
    }

    /**
     * Record a checkpoint of the game state, including:
     * <li>All current positions of entities in the game map.</li>
     * <li>Current undo quota</li>
     * <p>
     * Checkpoint is used in {@link GameState#undo()}.
     * Every undo actions reverts the game state to the last checkpoint.
     */
    public void checkpoint() {
        // perform deep copy to store the current state
        Entity[][] entityArrayCheckpoint = new Entity[getMapMaxHeight()][getMapMaxWidth()];
        for (int i = 0; i < entityArrayCheckpoint.length; i++) {
            for (int j = 0; j < entityArrayCheckpoint[0].length; j++) {
                Entity entity = getEntity(new Position(j, i));
                if (entity instanceof Player) {
                    entityArrayCheckpoint[i][j] = new Player(((Player)entity).getId());
                } else if (entity instanceof Box) {
                    entityArrayCheckpoint[i][j] = new Box(((Box)entity).getPlayerId());
                } else if (entity instanceof Wall) {
                    entityArrayCheckpoint[i][j] = new Wall();
                } else if (entity instanceof Empty) {
                    entityArrayCheckpoint[i][j] = new Empty();
                } else { // outside the wall
                    entityArrayCheckpoint[i][j] = null;
                }
            }
        }
        moveHistoryOfEntity.push(entityArrayCheckpoint);
    }

    /**
     * Revert the game state to the last checkpoint in history.
     * This method assumes there is still undo quota left, and decreases the undo quota by one.
     * <p>
     * If there is no checkpoint recorded, i.e., before moving any box when the game starts,
     * revert to the initial game state.
     */
    public void undo() {
        if (moveHistoryOfEntity.size() > 1) { // have move history (not only init state)
            moveHistoryOfEntity.pop(); // remove the recent move
            // perform deep copy to get back previous state
            Entity[][] entityArrayCheckpoint = moveHistoryOfEntity.peek(); // get the last move
            for (int i = 0; i < entityArrayCheckpoint.length; i++) {
                for (int j = 0; j < entityArrayCheckpoint[0].length; j++) {
                    Entity entity = entityArrayCheckpoint[i][j];
                    if (entity instanceof Player) {
                        int id = ((Player) entity).getId();
                        char charID = (char) (id + 65);
                        gameMap.entityArray[i][j] = new Player(id);
                        currentPlayerLocations.remove(charID);
                        currentPlayerLocations.put(charID, new Position(j, i));
                    } else if (entity instanceof Box) {
                        int id = ((Box) entity).getPlayerId();
                        char charID = (char) (id + 97);
                        gameMap.entityArray[i][j] = new Box(id);
                        currentBoxLocations.remove(new Position(j, i));
                        currentBoxLocations.put(new Position(j, i), charID);
                    } else if (entity instanceof Wall) {
                        gameMap.entityArray[i][j] = new Wall();
                    } else if (entity instanceof Empty) {
                        gameMap.entityArray[i][j] = new Empty();
                    } else { // outside the wall
                        gameMap.entityArray[i][j] = null;
                    }
                }
            }
            if (undoQuota != -1) {
                undoQuota--;
            }
        }
    }

    /**
     * Get the maximum width of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum width.
     */
    public int getMapMaxWidth() {
        return gameMap.getMaxWidth();
    }

    /**
     * Get the maximum height of the game map.
     * This should be the same as that in {@link GameMap} class.
     *
     * @return maximum height.
     */
    public int getMapMaxHeight() {
        return gameMap.getMaxHeight();
    }
}
