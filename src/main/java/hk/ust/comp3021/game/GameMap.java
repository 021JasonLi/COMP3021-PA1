package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A Sokoban game board.
 * GameBoard consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
 * <p/>
 * GameBoard is capable to create many GameState instances, each representing an ongoing game.
 */
public class GameMap {
    private int maxWidth;
    private int maxHeight;
    private Set<Position> walls;
    private Set<Position> destinations;
    private Map<Position, Character> boxLocations;
    private Map<Character, Position> playerLocations;
    private Optional<Integer> undoLimit;
    private static Set<Integer> playerIDs = new HashSet<Integer>();
    static Entity[][] entityArray;
    private static String[] mapTextSplited;


    /**
     * Create a new GameMap with width, height, set of box destinations and undo limit.
     *
     * @param maxWidth     Width of the game map.
     * @param maxHeight    Height of the game map.
     * @param destinations Set of box destination positions.
     * @param undoLimit    Undo limit.
     *                     Positive numbers specify the maximum number of undo actions.
     *                     0 means undo is not allowed.
     *                     -1 means unlimited. Other negative numbers are not allowed.
     */
    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, Optional<Integer> undoLimit) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = destinations;
        this.undoLimit = undoLimit;
        this.walls = new HashSet<Position>();
        this.boxLocations = new HashMap<>();
        this.playerLocations = new HashMap<>();

        // put all the entities into the array
        entityArray = new Entity[maxHeight][maxWidth];
        for (int i = 0; i < maxHeight; i++) { // set all to null first since the map may be non-rectangle
            for (int j = 0; j < maxWidth; j++) {
                entityArray[i][j] = null;
            }
        }

        for (int i = 0; i < maxHeight; i++) {
            for (int j = 0; j < mapTextSplited[i+1].length(); j++)  {
                char temp = mapTextSplited[i+1].charAt(j); // get the character
                if ((temp >= 'A') && (temp <= 'Z')) { // finding player entity
                    playerLocations.put(temp, new Position(j, i));
                    putEntity(new Position(j, i), new Player(temp-65));
                } else if ((temp >= 'a') && (temp <= 'z')) { // finding box entity
                    boxLocations.put(new Position(j, i), temp);
                    putEntity(new Position(j, i), new Box(temp-97));
                } else if (temp == '#') { // finding wall entity
                    walls.add(new Position(j, i));
                    putEntity(new Position(j, i), new Wall());
                } else if ((temp == '.') || (temp == '@')){ // finding empty entity
                    putEntity(new Position(j, i), new Empty());
                }
            }
        }
    }

    /**
     * Parses the map from a string representation.
     * The first line is undo limit.
     * Starting from the second line, the game map is represented as follows,
     * <li># represents a {@link Wall}</li>
     * <li>@ represents a box destination.</li>
     * <li>Any upper-case letter represents a {@link Player}.</li>
     * <li>
     * Any lower-case letter represents a {@link Box} that is only movable by the player with the corresponding upper-case letter.
     * For instance, box "a" can only be moved by player "A" and not movable by player "B".
     * </li>
     * <li>. represents an {@link Empty} position in the map, meaning there is no player or box currently at this position.</li>
     * <p>
     * Notes:
     * <li>
     * There can be at most 26 players.
     * All implementations of classes in the hk.ust.comp3021.game package should support up to 26 players.
     * </li>
     * <li>
     * For simplicity, we assume the given map is bounded with a closed boundary.
     * There is no need to check this point.
     * </li>
     * <li>
     * Example maps can be found in "src/main/resources".
     * </li>
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     * @throws IllegalArgumentException if undo limit is negative but not -1.
     * @throws IllegalArgumentException if there are multiple same upper-case letters, i.e., one player can only exist at one position.
     * @throws IllegalArgumentException if there are no players in the map.
     * @throws IllegalArgumentException if the number of boxes is not equal to the number of box destinations.
     * @throws IllegalArgumentException if there are boxes whose {@link Box#getPlayerId()} do not match any player on the game board,
     *                                  or if there are players that have no corresponding boxes.
     */
    public static GameMap parse(String mapText) {
        mapTextSplited = mapText.split("\n"); // split the text line by line

        int undoLimit = Integer.parseInt(mapTextSplited[0]); // first line contains undoLimit (str -> int)
        if (undoLimit < -1) { // invalid undoLimit
            throw new IllegalArgumentException();
        }

        // initialization
        int[] playerList = new int[26]; // for checking player's validity
        for (int i = 0; i < playerList.length; i++) { // initialize all elements
            playerList[i] = 0;
        }
        int[] boxList = new int[26]; // for checking box's validity
        for (int i = 0; i < boxList.length; i++) { // initialize all elements
            boxList[i] = 0;
        }
        int numOfdestinations = 0; // for checking destination's validity
        int maxWidth = 0; // for creating GameMap object used
        int maxHeight = 0; // for creating GameMap object used
        Set<Position> destinations = new HashSet<Position>();

        // handle each character and finding width & height
        for (int i = 1; i < mapTextSplited.length; i++) { // checking each line (except first line)
            maxHeight += 1;
            int tempWidth = 0; // for checking weight of each line
            for (int j = 0; j < mapTextSplited[i].length(); j++) { // checking each character
                char temp = mapTextSplited[i].charAt(j); // get the character
                tempWidth += 1;
                if ((temp >= 'A') && (temp <= 'Z')) { // finding players
                    int tempIndex = temp - 65;
                    playerList[tempIndex] += 1; // add 1 to the corresponding box
                    playerIDs.add(tempIndex); // add the playerID to the set
                } else if ((temp >= 'a') && (temp <= 'z')) { // finding boxes
                    int tempIndex = temp - 97;
                    boxList[tempIndex] += 1; // add 1 to the corresponding box
                } else if (temp == '@') { // finding destinations
                    numOfdestinations += 1; // add 1 to the variable
                    destinations.add(new Position(j, i-1));
                }
            }
            if (tempWidth > maxWidth) { // update maxWeight if needed
                maxWidth = tempWidth;
            }
        }

        // do checking
        // for players
        int sumChecking = 0;
        for (int i = 0; i < playerList.length; i++) {
            if (playerList[i] > 1) { // same player exist more than once
                throw new IllegalArgumentException();
            }
            sumChecking += playerList[i];
        }
        if (sumChecking == 0) { // no players in the map
            throw new IllegalArgumentException();
        }

        // for boxes and des.
        sumChecking = 0;
        for (int i = 0; i < boxList.length; i++) {
            sumChecking += boxList[i];
        }
        if (sumChecking != numOfdestinations) { // box and destination not equal
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < playerList.length; i++) {
            if ((playerList[i] == 0) && (boxList[i] > 0)) { // no players but have corresponding box
                throw new IllegalArgumentException();
            }
            if ((playerList[i] > 0) && (boxList[i] == 0)) { // have player but no corresponding box
                throw new IllegalArgumentException();
            }
        }

        // after checking, all ok
        return new GameMap(maxWidth, maxHeight, destinations, Optional.ofNullable(undoLimit));

    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    @Nullable
    public Entity getEntity(Position position) {
        return entityArray[position.y()][position.x()];
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        entityArray[position.y()][position.x()] = entity;
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return undo limit.
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit;
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        return playerIDs;
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return maximum width.
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMaxHeight() {
        return maxHeight;
    }
}
