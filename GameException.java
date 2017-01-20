package jumpCube;

/** A general exception case for the jumpCube game.
 *  @author Brian Ha
 */
class GameException extends RuntimeException {

    /** A GameException with no message. */
    GameException() {
    }

    /** A GameException with a message MSG. */
    GameException(String msg) {
        super(msg);
    }

    /** Returns an exception containing a  message formatted according
     *  to FORMAT and ARGS. */
    static GameException error(String format, Object... args) {
        return new GameException(String.format(format, args));
    }

}
