package hk.ust.comp3021.tui;


import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.game.*;
import hk.ust.comp3021.utils.NotImplementedException;

/**
 * A Sokoban game running in the terminal.
 */
public class TerminalSokobanGame extends AbstractSokobanGame {

    private final InputEngine inputEngine;

    private final RenderingEngine renderingEngine;

    /**
     * Create a new instance of TerminalSokobanGame.
     * Terminal-based game only support at most two players, although the hk.ust.comp3021.game package supports up to 26 players.
     * This is only because it is hard to control too many players in a terminal-based game.
     *
     * @param gameState       The game state.
     * @param inputEngine     the terminal input engin.
     * @param renderingEngine the terminal rendering engine.
     * @throws IllegalArgumentException when there are more than two players in the map.
     */
    public TerminalSokobanGame(GameState gameState, TerminalInputEngine inputEngine, TerminalRenderingEngine renderingEngine) {
        super(gameState);
        this.inputEngine = inputEngine;
        this.renderingEngine = renderingEngine;
        // Check the number of players
        int numOfPlayers = 0;
        for (int i = 0; i < gameState.getMapMaxHeight(); i++) {
            for (int j = 0; j < gameState.getMapMaxWidth(); j++) {
                if (gameState.getEntity(new Position(j ,i)) instanceof Player) {
                    numOfPlayers++;
                }
            }
        }
        if (numOfPlayers > 2) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void run() {
        // TODO

    }
}
