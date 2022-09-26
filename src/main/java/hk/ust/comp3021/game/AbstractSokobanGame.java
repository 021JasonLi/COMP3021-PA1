package hk.ust.comp3021.game;

import com.sun.net.httpserver.Authenticator;
import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.utils.NotImplementedException;
import org.jetbrains.annotations.NotNull;

/**
 * A base implementation of Sokoban Game.
 */
public abstract class AbstractSokobanGame implements SokobanGame {
    @NotNull
    protected final GameState state;

    protected AbstractSokobanGame(@NotNull GameState gameState) {
        this.state = gameState;
    }

    /**
     * @return True is the game should stop running.
     * For example when the user specified to exit the game or the user won the game.
     */
    protected boolean shouldStop() {
        if (state.isWin()) { // exit condition handle outside
            return true;
        }
        return false;
    }

    /**
     * @param action The action received from the user.
     * @return The result of the action.
     */
    protected ActionResult processAction(@NotNull Action action) {
        // TODO
        if  (action instanceof Exit) {
            return new ActionResult.Success(action);
        }
        else if (action instanceof InvalidInput) {
            return new ActionResult.Success(action);
        }
        else if (action instanceof Move.Up) {

        }
        else if (action instanceof Move.Down) {

        }
        else if (action instanceof Move.Left)  {

        }
        else if (action instanceof Move.Right) {

        }
        else { // undo

        }

        return null;
    }
}
