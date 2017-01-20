package jumpCube;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.IOException;

/** The jumpCube game.
 * @author Brian Ha
 */
public class Main {

    static final String USAGE = "jumpCube/Usage.txt";

    /** Play jumpCube.  ARGS0 may contain the String
     *  '--display' to activate the GUI option. Prints
     *  a usage message if the arguments are incorrect. */
    public static void main(String[] args0) {
        if (args0.length > 1 || (args0.length == 1
                                 && !args0[0].equals("--display"))) {
            usage();
            System.exit(0);
        }

        boolean displayGUI = false;

        if (args0.length == 1) {
            displayGUI = true;
        }

        Writer output = new OutputStreamWriter(System.out);
        Game game = new Game(new InputStreamReader(System.in),
                             output, output,
                             new OutputStreamWriter(System.err));
        if (displayGUI) {
            JumpGUI jg = new JumpGUI(game);
            jg.start();
        } else {
            System.exit(game.play());
        }
    }

    static void printHelpResource(String name, PrintWriter out) {
        try {
            InputStream resource =
                Main.class.getClassLoader().getResourceAsStream(name);
            BufferedReader str =
                new BufferedReader(new InputStreamReader(resource));
            for (String s = str.readLine(); s != null; s = str.readLine())  {
                out.println(s);
            }
            str.close();
            out.flush();
        } catch (IOException excp) {
            out.printf("No help found.");
            out.flush();
        }
    }

    /** Print usage message. */
    private static void usage() {
        printHelpResource(USAGE, new PrintWriter(System.err));
    }

}

