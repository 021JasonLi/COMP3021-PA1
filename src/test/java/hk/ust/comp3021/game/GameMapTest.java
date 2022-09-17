package hk.ust.comp3021.game;

import hk.ust.comp3021.entities.Box;
import hk.ust.comp3021.entities.Empty;
import hk.ust.comp3021.entities.Player;
import hk.ust.comp3021.entities.Wall;
import hk.ust.comp3021.utils.TestHelper;
import hk.ust.comp3021.utils.TestKind;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    private static final String rectangularMap = """
        233
        ######
        #A..@#
        #...@#
        #....#
        #.a..#
        #..a.#
        ######
        """;

    @Tag(TestKind.PUBLIC)
    @Test
    void testWidthForRectangularMap() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        assertEquals(6, gameMap.getMaxWidth());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testHeightForRectangularMap() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        assertEquals(7, gameMap.getMaxHeight());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testGetDestinations() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        assertEquals(2, gameMap.getDestinations().size());
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(4, 1)));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testWallParsing() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        assertInstanceOf(Wall.class, gameMap.getEntity(Position.of(0, 0)));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testPlayerParsing() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        final var player = assertInstanceOf(Player.class, gameMap.getEntity(Position.of(1, 1)));
        assertNotNull(player);
        assertEquals(0, player.getId());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testBoxParsing() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        final var box = assertInstanceOf(Box.class, gameMap.getEntity(Position.of(2, 4)));
        assertNotNull(box);
        assertEquals(0, box.getPlayerId());
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testEmptyCellParsing() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        assertInstanceOf(Empty.class, gameMap.getEntity(Position.of(2, 1)));
    }

    @Tag(TestKind.PUBLIC)
    @Test
    void testGetEntity() {
        final var gameMap = TestHelper.parseGameMap(rectangularMap);
        final var entity = gameMap.getEntity(Position.of(0, 0));
        assertTrue(entity instanceof Wall);
    }

}
