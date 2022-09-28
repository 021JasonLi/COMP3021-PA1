package hk.ust.comp3021.tui;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import hk.ust.comp3021.game.GameState;
import hk.ust.comp3021.game.Position;
import hk.ust.comp3021.game.RenderingEngine;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Set;

/**
 * A rendering engine that prints to the terminal.
 */
public class TerminalRenderingEngine implements RenderingEngine {

    private final PrintStream outputSteam;

    /**
     * @param outputSteam The {@link PrintStream} to write the output to.
     */
    public TerminalRenderingEngine(PrintStream outputSteam) {
        this.outputSteam = outputSteam;
    }

    @Override
    public void render(@NotNull GameState state) {
        final var builder = new StringBuilder();
        for (int y = 0; y < state.getMapMaxHeight(); y++) {
            for (int x = 0; x < state.getMapMaxWidth(); x++) {
                final var entity = state.getEntity(Position.of(x, y));
                final var charToPrint = switch (entity) {
                    case Wall ignored -> '#';
                    case Box b -> Character.toString((char)b.getPlayerId()+97);
                    case Player p -> Character.toString((char)p.getId()+65);
                    case Empty ignored -> emptyOrDes(new Position(x, y), state);
                    case null -> ' ';
                    default -> ' ';
                };
                builder.append(charToPrint);
            }
            builder.append('\n');
        }
        outputSteam.print(builder);
    }

    private char emptyOrDes(Position position, GameState state) {
        Set<Position> desPosition = state.getDestinations();
        if (desPosition.contains(position)) {
            return '@';
        }
        return '.';
    }

    @Override
    public void message(@NotNull String content) {
        // Hint: System.out is also a PrintStream.
        outputSteam.println(content);

    }
}
