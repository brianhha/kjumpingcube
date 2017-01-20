package jumpCube;

/** A Player that gets its moves from manual input.
 *  @author Brian Ha
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR. */
    HumanPlayer(Game game, Color color) {
        super(game, color);
    }

    @Override
    void makeMove() {
        Game game = getGame();
        int[] store = new int[2];
        game.getMove(store);
    }

}
