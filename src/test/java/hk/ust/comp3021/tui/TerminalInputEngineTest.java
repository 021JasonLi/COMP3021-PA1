package hk.ust.comp3021.tui;

import hk.ust.comp3021.actions.Exit;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalInputEngineTest {


    @Tag(TestKind.PUBLIC)
    @Test
    void testExit() {
        final var inputStream = fixValueStream("exit");

        final var inputEngine = new TerminalInputEngine(inputStream);
        final var action = inputEngine.fetchAction();

        assertTrue(action instanceof Exit);
    }

    private InputStream fixValueStream(String content) {
        final var bytes = content.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }
}
