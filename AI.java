package jumpCube;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;

import static jumpCube.Color.*;

/** An automated Player.
 *  @author Brian Ha
 */
class AI extends Player {

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Game game = getGame();
        BestMove b = minmax(game.getBoard().whoseMove(), Defaults.DEPTH);
        int[] temp  = b.getMyMove();
        game.makeMove(temp[0], temp[1]);
    }

    /** Return the BestMove that represents the best move THIS can
     * make. This has Color C and calculates the move based on
     * DEPTH. 
     */
    private BestMove minmax(Color c, int depth) {
        Game game = getGame();
        BestMove bestSoFar = new BestMove();
        Board start = game.getMBoard();
        if (start.getWinner() == c) {
            bestSoFar.setMyScore(Integer.MAX_VALUE);
            return bestSoFar;
        } else if (start.getWinner() == opposite(c)) {
            bestSoFar.setMyScore(Integer.MIN_VALUE);
            return bestSoFar;
        } else if (depth == 0) {
            return guessBestMove(c, start);
        }
        BestMove reply;
        if (c == start.whoseMove()) {
            bestSoFar.setMyScore(Integer.MIN_VALUE);
        } else {
            bestSoFar.setMyScore(Integer.MAX_VALUE);
        }
        int n = start.size();
        ArrayList<Integer> arr = new ArrayList<Integer>();
        for (int i = 0; i < n * n; i++) {
            if (start.isLegal(c, i)) {
                arr.add(i);
            }
        }
        boolean boo = true;
        Iterator legalMoves = arr.iterator();
        while (legalMoves.hasNext()) {
            int mo = (Integer) legalMoves.next();
            int[] move = {start.row(mo), start.col(mo)};
            if (boo) {
                boo = false;
                bestSoFar.setMyMove(move);
            }
            start.addSpot(c, move[0], move[1]);
            reply = minmax(opposite(c), depth - 1);
            start.undo();
            if ((c == start.whoseMove() && reply.getMyScore()
                 > bestSoFar.getMyScore())
                || (c == opposite(start.whoseMove())
                    && reply.getMyScore()
                    < bestSoFar.getMyScore())) {
                bestSoFar.setMyMove(move);
                bestSoFar.setMyScore(reply.getMyScore());
            }
        }
        return bestSoFar;
    }

    /** Return the opposite color of the input Color C. */
    private Color opposite(Color c) {
        if (c == RED) {
            return BLUE;
        } else {
            return RED;
        }
    }

    /** Returns the guess of the BestMove from C and START. */
    private BestMove guessBestMove(Color c, Board start) {
        BestMove bm = new BestMove();
        int n = start.size();
        HashMap hm = new HashMap();
        for (int i = 0; i < n * n; i++) {
            if (start.isLegal(c, i)) {
                start.addSpot(c, i);
                int temp = start.numOfColor(c) - start.numOfColor(opposite(c));
                hm.put(i, temp);
                start.undo();
            }
        }
        Iterator ite = hm.entrySet().iterator();
        int tempSq = 0;
        int tempF = Integer.MIN_VALUE;
        while (ite.hasNext()) {
            Map.Entry pair = (Map.Entry) ite.next();
            int sqN = (Integer) pair.getKey();
            int factor = (Integer) pair.getValue();
            if (factor >= tempF) {
                tempSq = sqN;
                tempF = factor;
            }
        }
        int[] mo = {start.row(tempSq), start.col(tempSq)};
        bm.setMyMove(mo);
        bm.setMyScore(tempF);
        return bm;
    }

    /** Object representation of a BestMove that the current player
     * can make. 
     */
    private class BestMove {

        /** The potential move the player could make. */
        private int[] _myMove;

        /** The score of the potential move. */
        private int _myScore;

        /** RETURNS _MYMOVE. */
        private int[] getMyMove() {
            return _myMove;
        }

        /** RETURNS _MYSCORE. */
        private int getMyScore() {
            return _myScore;
        }

        /** Sets _myMove to input MYMOVE. */
        private void setMyMove(int[] myMove) {
            _myMove = myMove;
        }

        /** Sets _myScore to input MYSCORE. */
        private void setMyScore(int myScore) {
            _myScore = myScore;
        }
    }

}


