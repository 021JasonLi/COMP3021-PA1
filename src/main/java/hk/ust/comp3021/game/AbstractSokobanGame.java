package hk.ust.comp3021.game;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.entities.*;
import hk.ust.comp3021.utils.NotImplementedException;
import hk.ust.comp3021.utils.StringResources;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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
        if  (action instanceof Exit) {
            return new ActionResult.Success(action);
        }
        else if (action instanceof InvalidInput) {
            return new ActionResult.Success(action);
        }

        else if (action instanceof Move.Up) {
            // does player exist?
            Position position = state.getPlayerPositionById(action.getInitiator());
            if (position == null) {
                return new ActionResult.Failed(action, StringResources.PLAYER_NOT_FOUND);
            }

            // check upper location
            // case 1: Box
            if (state.getEntity(new Position(position.x(), position.y()-1)) instanceof Box) {
                int id = ((Box)state.getEntity(new Position(position.x(), position.y()-1))).getPlayerId();
                // does the box belongs to you?
                if (id == action.getInitiator()) { // further check the upper position
                    // moveable only when there is empty
                    if (state.getEntity(new Position(position.x(), position.y()-2)) instanceof Empty) {
                        state.move(new Position(position.x(), position.y()-1), new Position(position.x(), position.y()-2));
                        state.move(new Position(position.x(), position.y()), new Position(position.x(), position.y()-1));
                        state.checkpoint();
                        return new ActionResult.Success(action);
                    }
                    else {
                        return new ActionResult.Failed(action, "Failed to push the box.");
                    }
                }
                else {
                    return new ActionResult.Failed(action, "You cannot move other players' boxes.");
                }
            }
            // case 2: Wall
            else if (state.getEntity(new Position(position.x(), position.y()-1)) instanceof Wall) {
                return new ActionResult.Failed(action, "You hit a wall.");
            }
            // case 3: Player
            else if (state.getEntity(new Position(position.x(), position.y()-1)) instanceof Player) {
                return new ActionResult.Failed(action, "You hit another player.");
            }
            // case 4: Empty
            else  {
                state.move(new Position(position.x(), position.y()), new Position(position.x(), position.y()-1));
                state.checkpoint();
                return new ActionResult.Success(action);
            }
        }

        else if (action instanceof Move.Down) {
            // does player exist?
            Position position = state.getPlayerPositionById(action.getInitiator());
            if (position == null) {
                return new ActionResult.Failed(action, StringResources.PLAYER_NOT_FOUND);
            }

            // check upper location
            // case 1: Box
            if (state.getEntity(new Position(position.x(), position.y()+1)) instanceof Box) {
                int id = ((Box)state.getEntity(new Position(position.x(), position.y()+1))).getPlayerId();
                // does the box belongs to you?
                if (id == action.getInitiator()) { // further check the lower position
                    // moveable only when there is empty
                    if (state.getEntity(new Position(position.x(), position.y()+2)) instanceof Empty) {
                        state.move(new Position(position.x(), position.y()+1), new Position(position.x(), position.y()+2));
                        state.move(new Position(position.x(), position.y()), new Position(position.x(), position.y()+1));
                        state.checkpoint();
                        return new ActionResult.Success(action);
                    }
                    else {
                        return new ActionResult.Failed(action, "Failed to push the box.");
                    }
                }
                else {
                    return new ActionResult.Failed(action, "You cannot move other players' boxes.");
                }
            }
            // case 2: Wall
            else if (state.getEntity(new Position(position.x(), position.y()+1)) instanceof Wall) {
                return new ActionResult.Failed(action, "You hit a wall.");
            }
            // case 3: Player
            else if (state.getEntity(new Position(position.x(), position.y()+1)) instanceof Player) {
                return new ActionResult.Failed(action, "You hit another player.");
            }
            // case 4: Empty
            else  {
                state.move(new Position(position.x(), position.y()), new Position(position.x(), position.y()+1));
                state.checkpoint();
                return new ActionResult.Success(action);
            }
        }

        else if (action instanceof Move.Left)  {
            // does player exist?
            Position position = state.getPlayerPositionById(action.getInitiator());
            if (position == null) {
                return new ActionResult.Failed(action, StringResources.PLAYER_NOT_FOUND);
            }

            // check upper location
            // case 1: Box
            if (state.getEntity(new Position(position.x()-1, position.y())) instanceof Box) {
                int id = ((Box)state.getEntity(new Position(position.x()-1, position.y()))).getPlayerId();
                // does the box belongs to you?
                if (id == action.getInitiator()) { // further check the left position
                    // moveable only when there is empty
                    if (state.getEntity(new Position(position.x()-2, position.y())) instanceof Empty) {
                        state.move(new Position(position.x()-1, position.y()), new Position(position.x()-2, position.y()));
                        state.move(new Position(position.x(), position.y()), new Position(position.x()-1, position.y()));
                        state.checkpoint();
                        return new ActionResult.Success(action);
                    }
                    else {
                        return new ActionResult.Failed(action, "Failed to push the box.");
                    }
                }
                else {
                    return new ActionResult.Failed(action, "You cannot move other players' boxes.");
                }
            }
            // case 2: Wall
            else if (state.getEntity(new Position(position.x()-1, position.y())) instanceof Wall) {
                return new ActionResult.Failed(action, "You hit a wall.");
            }
            // case 3: Player
            else if (state.getEntity(new Position(position.x()-1, position.y())) instanceof Player) {
                return new ActionResult.Failed(action, "You hit another player.");
            }
            // case 4: Empty
            else  {
                state.move(new Position(position.x(), position.y()), new Position(position.x()-1, position.y()));
                state.checkpoint();
                return new ActionResult.Success(action);
            }
        }

        else if (action instanceof Move.Right) {
            // does player exist?
            Position position = state.getPlayerPositionById(action.getInitiator());
            if (position == null) {
                return new ActionResult.Failed(action, StringResources.PLAYER_NOT_FOUND);
            }

            // check upper location
            // case 1: Box
            if (state.getEntity(new Position(position.x()+1, position.y())) instanceof Box) {
                int id = ((Box)state.getEntity(new Position(position.x()+1, position.y()))).getPlayerId();
                // does the box belongs to you?
                if (id == action.getInitiator()) { // further check the right position
                    // moveable only when there is empty
                    if (state.getEntity(new Position(position.x()+2, position.y())) instanceof Empty) {
                        state.move(new Position(position.x()+1, position.y()), new Position(position.x()+2, position.y()));
                        state.move(new Position(position.x(), position.y()), new Position(position.x()+1, position.y()));
                        state.checkpoint();
                        return new ActionResult.Success(action);
                    }
                    else {
                        return new ActionResult.Failed(action, "Failed to push the box.");
                    }
                }
                else {
                    return new ActionResult.Failed(action, "You cannot move other players' boxes.");
                }
            }
            // case 2: Wall
            else if (state.getEntity(new Position(position.x()+1, position.y())) instanceof Wall) {
                return new ActionResult.Failed(action, "You hit a wall.");
            }
            // case 3: Player
            else if (state.getEntity(new Position(position.x()+1, position.y())) instanceof Player) {
                return new ActionResult.Failed(action, "You hit another player.");
            }
            // case 4: Empty
            else  {
                state.move(new Position(position.x(), position.y()), new Position(position.x()+1, position.y()));
                state.checkpoint();
                return new ActionResult.Success(action);
            }
        }

        else { // undo
            Optional<Integer> undoQuota = state.getUndoQuota();
            if (undoQuota.isPresent()) { // limited quota
                if (undoQuota.get() == 0) { // 0 quota left -> cannot undo anymore
                    return new ActionResult.Failed(action, StringResources.UNDO_QUOTA_RUN_OUT);
                }
                else  { // quota > 0 -> can undo
                    return new ActionResult.Success(action);
                }
            }
            else  { // unlimited quota
                return new ActionResult.Success(action);
            }
        }
    }

}
