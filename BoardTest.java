package jumpCube;

import static jumpCube.Color.*;

import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests of Boards.
 *  @author Brian Ha
 */
public class BoardTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testSize() {
        Board B = new MutableBoard(5);
        assertEquals("bad length", 5, B.size());
        ConstantBoard C = new ConstantBoard(B);
        assertEquals("bad length", 5, C.size());
        Board D = new MutableBoard(C);
        assertEquals("bad length", 5, C.size());
    }

    @Test
    public void testSet() {
        Board B = new MutableBoard(5);
        B.set(2, 2, 1, RED);
        B.setMoves(1);
        assertEquals("wrong number of spots", 1, B.spots(2, 2));
        assertEquals("wrong color", RED, B.color(2, 2));
        assertEquals("wrong count", 1, B.numOfColor(RED));
        assertEquals("wrong count", 0, B.numOfColor(BLUE));
        assertEquals("wrong count", 24, B.numOfColor(WHITE));
    }

    @Test
    public void testClear() {
        Board b = new MutableBoard(2);
        b.addSpot(Color.BLUE, 0);
        b.addSpot(Color.RED, 1);
        b.addSpot(Color.BLUE, 2);
        b.addSpot(Color.RED, 3);
        b.clear(3);
        int n = 0;
        for (int i = 0; i < b.size() * b.size(); i++) {
            if (b.spots(i) == 0) {
                n++;
            }
        }
        assertEquals("There should be 9 white squares with 0 spots.", 9, n);
    }

    @Test
    public void testNeighbor() {
        Board b = new MutableBoard(3);
        assertEquals("There should be 2 neighbors.", 2, b.neighbors(1, 1));
        assertEquals("There should be 3 neighbors.", 3, b.neighbors(1));
        assertEquals("There should be 4 neighbors.", 4, b.neighbors(4));
    }

    @Test
    public void testJump() {
        Board B = new MutableBoard(3);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 3, 3);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 2, 2);
        B.addSpot(RED, 3, 2);
        B.addSpot(BLUE, 2, 2);
        B.addSpot(RED, 2, 1);
        B.addSpot(BLUE, 3, 1);
        B.addSpot(RED, 3, 2);
        B.addSpot(BLUE, 3, 1);
        B.addSpot(RED, 1, 2);
        B.addSpot(BLUE, 1, 3);
        B.addSpot(RED, 3, 2);
        B.addSpot(BLUE, 3, 1);
        B.addSpot(RED, 2, 3);
        B.addSpot(BLUE, 3, 2);
        B.addSpot(RED, 1, 2);
        B.addSpot(BLUE, 1, 3);
        B.addSpot(RED, 1, 1);
        B.addSpot(BLUE, 1, 3);
        int n = B.numOfColor(Color.BLUE);
        assertEquals("Number should be 8", 8, n);
    }

    @Test
    public void testisLegal() {
        Board b = new MutableBoard(5);
        assertTrue(b.isLegal(Color.RED, 24));
    }

    @Test
    public void testsqNum() {
        Board b = new MutableBoard(5);
        assertEquals("Number should be 3.", 3, b.sqNum(1, 4));
        assertEquals("Number should be 21.", 21, b.sqNum(5, 2));
    }

    @Test
    public void testCol() {
        Board b = new MutableBoard(4);
        assertEquals("Number should be 3.", 3, b.col(6));
        assertEquals("Number should be 4.", 4, b.col(15));
    }

    @Test
    public void testRow() {
        Board b = new MutableBoard(4);
        assertEquals("Number should be 1.", 1, b.row(1));
        assertEquals("Number should be 3.", 3, b.row(11));
    }

    @Test
    public void testSizeFunction() {
        Board B = new MutableBoard(3);
        assertEquals("The board dimension should be 3.", 3, B.size());
    }

    @Test
    public void testToString() {
        Board b = new MutableBoard(2);
        b.addSpot(Color.RED, 1, 1);
        b.addSpot(Color.BLUE, 1, 2);
        b.addSpot(Color.RED, 2, 1);
        b.addSpot(Color.BLUE, 2, 2);
        Board c = new MutableBoard(2);
        c.addSpot(Color.BLUE, 2, 2);
        c.addSpot(Color.RED, 2, 1);
        c.addSpot(Color.BLUE, 1, 2);
        c.addSpot(Color.RED, 1, 1);
        String result1 = b.toString();
        String result2 = c.toString();
        assertTrue(result1.equals(result2));
    }

    @Test
    public void testMove() {
        Board B = new MutableBoard(6);
        B.addSpot(RED, 1, 1);
        checkBoard("#1", B, 1, 1, 1, RED);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#2", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#3", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.addSpot(BLUE, 2, 1);
        checkBoard("#4", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.addSpot(RED, 1, 1);
        checkBoard("#5", B, 1, 1, 1, RED, 2, 1, 3, RED, 1, 2, 1, RED);
        B.undo();
        checkBoard("#4U", B, 1, 1, 2, RED, 2, 1, 2, BLUE);
        B.undo();
        checkBoard("#3U", B, 1, 1, 2, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#2U", B, 1, 1, 1, RED, 2, 1, 1, BLUE);
        B.undo();
        checkBoard("#1U", B, 1, 1, 1, RED);
    }

    private void checkBoard(String msg, Board B, Object... contents) {
        for (int k = 0; k < contents.length; k += 4) {
            String M = String.format("%s at %d %d", msg, contents[k],
                                     contents[k + 1]);
            assertEquals(M, (int) contents[k + 2],
                         B.spots((int) contents[k], (int) contents[k + 1]));
            assertEquals(M, contents[k + 3],
                         B.color((int) contents[k], (int) contents[k + 1]));
        }
        int c;
        c = 0;
        for (int i = B.size() * B.size() - 1; i >= 0; i -= 1) {
            assertTrue("bad white square #" + i,
                       (B.color(i) == WHITE) == (B.spots(i) == 0));
            if (B.color(i) != WHITE) {
                c += 1;
            }
        }
        assertEquals("extra squares filled", contents.length / 4, c);
    }

}
