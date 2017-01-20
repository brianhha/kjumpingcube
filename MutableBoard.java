package jumpCube;


import static jumpCube.Color.*;
import java.util.ArrayList;
import static jumpCube.GameException.error;

/** A jumpCube board state.
 *  @author Brian Ha
 */
class MutableBoard extends Board {

    MutableBoard(int N) {
        _moves = 0;
        _N = N;
        _table = new String[_N][_N];
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                _table[i][j] = "white 0";
            }
        }
        history = new ArrayList<String[][]>();
    }

    MutableBoard(Board board0) {
        _moves = board0.numMoves();
        _N = board0.size();
        _table = new String[_N][_N];
        for (int i = 1; i <= _N; i++) {
            for (int j = 1; j <= _N; j++) {
                _table[i - 1][j - 1] = board0.color(i, j).toString()
                    + " " + board0.spots(i, j);
            }
        }
        history = new ArrayList<String[][]>();
    }

    @Override
    void clear(int N) {
        history.clear();
        _N = N;
        _table = new String[_N][_N];
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                _table[i][j] = "white 0";
            }
        }
        _moves = 0;
    }

    @Override
    void copy(Board board) {
        _table = ((MutableBoard) board).getTable();
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        String[] contents = _table[r - 1][c - 1].split(" ");
        return Integer.parseInt(contents[1]);
    }

    @Override
    int spots(int n) {
        int r = row(n);
        int c = col(n);
        return spots(r, c);
    }

    @Override
    Color color(int r, int c) {
        String[] contents = _table[r - 1][c - 1].split(" ");
        String clr = contents[0];
        if (clr.equals("white")) {
            return WHITE;
        } else if (clr.equals("red")) {
            return RED;
        } else {
            return BLUE;
        }
    }

    @Override
    Color color(int n) {
        int r = row(n);
        int c = col(n);
        return color(r, c);
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    int numOfColor(Color color) {
        int n = 0;
        for (int i = 1; i <= _N; i++) {
            for (int j = 1; j <= _N; j++) {
                if (color == color(i, j)) {
                    n++;
                }
            }
        }
        return n;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        String[][] temp = new String[_N][_N];
        for (int i = 0; i < _N; i++) {
            for (int j = 0; j < _N; j++) {
                temp[i][j] =  new String(_table[i][j]);
            }
        }
        history.add(temp);
        _table[r - 1][c - 1] = player.toString() + " " + (spots(r, c) + 1);
        jump(sqNum(r, c));
    }

    @Override
    void addSpot(Color player, int n) {
        int r = row(n);
        int c = col(n);
        addSpot(player, r, c);
    }

    /** addSpot without updating history at square at R, C of color PLAYER. */
    void addOne(Color player, int r, int c) {
        _table[r - 1][c - 1] = player.toString() + " " + (spots(r, c) + 1);
        jump(sqNum(r, c));
    }

    /** addSpot without updating history at square at N of color PLAYER. */
    void addOne(Color player, int n) {
        if (exists(n)) {
            int r = row(n);
            int c = col(n);
            addOne(player, r, c);
        }
    }

    @Override
    void set(int r, int c, int num, Color player) {
        int neigh = neighbors(r, c);
        if (exists(r, c)) {
            if (num == 0) {
                _table[r - 1][c - 1] = "white 0";
            } else if (num > -1 && num <= neigh) {
                _table[r - 1][c - 1] = player.toString() + " " + num;
            } else {
                throw error("The new number of dots must be less"
                            + "than %d and greater than 0.",
                            neigh);
            }
        } else {
            throw error("%d:%d is an invalid position.", r, c);
        }
    }

    @Override
    void set(int n, int num, Color player) {
        int r = row(n);
        int c = col(n);
        set(r, c, num, player);
    }

    @Override
    void setMoves(int num) {
        assert num >= 0;
        _moves = num;
    }

    @Override
    void undo() {
        String[][] temp = history.remove(history.size() - 1);
        _table = temp;
    }

    /** Returns representation of the board. */
    String[][] getTable() {
        return _table;
    }

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {
        int neigh = neighbors(S);
        if (exists(S) && spots(S) > neigh && getWinner() == null) {
            Color p = color(S);
            int n = size();
            set(S, spots(S) - neigh, p);
            if (col(S) != 1) {
                addOne(p, S - 1);
            }
            if (col(S) != size()) {
                addOne(p, S + 1);
            }
            addOne(p, S - n);
            addOne(p, S + n);
            if (col(S) != 1) {
                jump(S - 1);
            }
            if (col(S) != size()) {
                jump(S + 1);
            }
            jump(S - n);
            jump(S + n);
        }
    }

    /** Total combined number of moves by both sides. */
    protected int _moves;
    /** Convenience variable: size of board (squares along one edge). */
    private int _N;
    /** Collection of the past states of the board. */
    private ArrayList<String[][]> history;
    /** 2-D array representation of the board squares. */
    private String[][] _table;
}
