package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.*;
import hk.ust.comp3021.game.InputEngine;
import hk.ust.comp3021.utils.NotImplementedException;
import hk.ust.comp3021.utils.StringResources;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Scanner;

/**
 * An input engine that fetches actions from terminal input.
 */
public class TerminalInputEngine implements InputEngine {

    /**
     * The {@link Scanner} for reading input from the terminal.
     */
    private final Scanner terminalScanner;

    /**
     * @param terminalStream The stream to read terminal inputs.
     */
    public TerminalInputEngine(InputStream terminalStream) {
        this.terminalScanner = new Scanner(terminalStream);
    }

    /**
     * Fetch an action from user in terminal to process.
     *
     * @return the user action.
     */
    @Override
    public @NotNull Action fetchAction() {
        // This is an example showing how to read a line from the Scanner class.
        // Feel free to change it if you do not like it.
        final var inputLine = terminalScanner.nextLine();
        var inputLineUP = inputLine.toUpperCase();

        switch (inputLineUP) {
            case "W": // player A move up
                return new Move.Up(0);
            case "A": // player A move left
                return new Move.Left(0);
            case "S": // player A move down
                return new Move.Down(0);
            case "D": // player A move right
                return new Move.Right(0);
            case "K": // player B move up
                return new Move.Up(1);
            case "H": // player B move left
                return new Move.Left(1);
            case "J": // player B move down
                return new Move.Down(1);
            case "L": // player B move down
                return new Move.Right(1);
            case "U": // undo
                return new Undo(-1);
            case "EXIT": // exit
                return new Exit(-1);
            default: // invalid
                return new InvalidInput(-1, StringResources.INVALID_INPUT_MESSAGE);

        }
    }
}
