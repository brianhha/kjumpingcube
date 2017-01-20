package jumpCube;

/** A ConstantBoard is an instance of an existance board that doesn't
 *  modifications. Each modification should result in the initialization
 *  of another ConstantBoard.
 *  @author Brian Ha
 */
class ConstantBoard extends Board {

    /** A new ConstantBoard - only allowing a read-only view of BOARD. */
    ConstantBoard(Board board) {
        _board = board;
    }

    @Override
    int size() {
        return _board.size();
    }

    @Override
    int spots(int r, int c) {
        return _board.spots(r, c);
    }

    @Override
    int spots(int n) {
        return _board.spots(n);
    }

    @Override
    Color color(int r, int c) {
        return _board.color(r, c);
    }

    @Override
    Color color(int n) {
        return _board.color(n);
    }

    @Override
    int numMoves() {
        return _board.numMoves();
    }

    @Override
    Color whoseMove() {
        return _board.whoseMove();
    }

    @Override
    boolean isLegal(Color player, int r, int c) {
        return _board.isLegal(player, r, c);
    }

    @Override
    boolean isLegal(Color player) {
        return _board.isLegal(player);
    }

    @Override
    int numOfColor(Color color) {
        return _board.numOfColor(color);
    }

    @Override
    public boolean equals(Object obj) {
        return _board.equals(obj);
    }

    @Override
    public int hashCode() {
        return _board.hashCode();
    }

    private Board _board;

}
