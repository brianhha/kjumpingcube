package jumpCube;

/** Team colors for jumpCube.
 *  @author Brian Ha
 */
enum Color {

    /** Possible team colors. */
    WHITE, RED, BLUE;

    /** Return the reverse of the input Color: 
     * BLUE for RED, RED for BLUE, WHITE for WHITE. 
     */
    Color opposite() {
        switch (this) {
        case BLUE:
            return RED;
        case RED:
            return BLUE;
        default:
            return WHITE;
        }
    }

    /** Return true iff THIS Color player can move onto square of input COLOR. */
    boolean playableSquare(Color color) {
        return color == WHITE || color == this;
    }

    /** Return the color COLORNAME in upperCase. */
    static Color parseColor(String colorName) {
        return valueOf(colorName.toUpperCase());
    }

    /** Return my color's lowercase name. */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /** Return my color's uppercase name. */
    public String toCapitalizedString() {
        return super.toString().charAt(0) + toString().substring(1);
    }
}
