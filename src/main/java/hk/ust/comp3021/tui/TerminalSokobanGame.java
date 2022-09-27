package hk.ust.comp3021.tui;


import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.game.*;
import hk.ust.comp3021.utils.NotImplementedException;
import hk.ust.comp3021.utils.StringResources;

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
                if (gameState.getEntity(new Position(j, i)) instanceof Player) {
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
        renderingEngine.message(StringResources.GAME_READY_MESSAGE);

        do { // game loop
            renderingEngine.render(state);
            renderingEngine.message("");
            if (state.getUndoQuota().isEmpty()) { // unlimited undo quota
                renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, StringResources.UNDO_QUOTA_UNLIMITED));
            }
            else { // limited undo quota
                renderingEngine.message(String.format(StringResources.UNDO_QUOTA_TEMPLATE, state.getUndoQuota().get()));
            }
            renderingEngine.message(">>>");
            Action action = inputEngine.fetchAction(); // get the action
            ActionResult result = processAction(action); // process the action

            // if fail to process, give message; if success, process
            if (result instanceof ActionResult.Failed) {
                renderingEngine.message(((ActionResult.Failed)result).getReason());
            }
            else if (action instanceof InvalidInput) {
                renderingEngine.message(((InvalidInput)action).getMessage());
            }
            else if (action instanceof Exit) {
                break; // leave the game loop
            }
            else if (action instanceof Move.Up) {
                state.checkpoint(); // record the checkpoint for undo use
                int id = action.getInitiator();
                Position position = state.getPlayerPositionById(id);
                // check there is a box to move
                if (state.getEntity(new Position(position.x(), position.y()-1)) instanceof Box) {
                    // move the box
                    state.move(new Position(position.x(), position.y()-1), new Position(position.x(), position.y()-2));
                }
                // move the player
                state.move(new Position(position.x(), position.y()), new Position(position.x(), position.y()-1));
            }
            else if (action instanceof Move.Down) {
                state.checkpoint(); // record the checkpoint for undo use
                int id = action.getInitiator();
                Position position = state.getPlayerPositionById(id);
                // check there is a box to move
                if (state.getEntity(new Position(position.x(), position.y()+1)) instanceof Box) {
                    // move the box
                    state.move(new Position(position.x(), position.y()+1), new Position(position.x(), position.y()+2));
                }
                // move the player
                state.move(new Position(position.x(), position.y()), new Position(position.x(), position.y()+1));
            }
            else if (action instanceof Move.Left) {
                state.checkpoint(); // record the checkpoint for undo use
                int id = action.getInitiator();
                Position position = state.getPlayerPositionById(id);
                // check there is a box to move
                if (state.getEntity(new Position(position.x()-1, position.y())) instanceof Box) {
                    // move the box
                    state.move(new Position(position.x()-1, position.y()), new Position(position.x()-2, position.y()));
                }
                // move the player
                state.move(new Position(position.x(), position.y()), new Position(position.x()-1, position.y()));
            }
            else if (action instanceof Move.Right) {
                state.checkpoint(); // record the checkpoint for undo use
                int id = action.getInitiator();
                Position position = state.getPlayerPositionById(id);
                // check there is a box to move
                if (state.getEntity(new Position(position.x()+1, position.y())) instanceof Box) {
                    // move the box
                    state.move(new Position(position.x()+1, position.y()), new Position(position.x()+2, position.y()));
                }
                // move the player
                state.move(new Position(position.x(), position.y()), new Position(position.x()+1, position.y()));
            }
            else { // Undo

            }


        } while (!shouldStop());


        // end of game
        if (state.isWin()) { // win the game
            renderingEngine.render(state);
            renderingEngine.message("");
            renderingEngine.message(StringResources.WIN_MESSAGE);
        }
        else  { // exit the game
            renderingEngine.render(state);
            renderingEngine.message("");
            renderingEngine.message(StringResources.GAME_EXIT_MESSAGE);
        }
    }
}
