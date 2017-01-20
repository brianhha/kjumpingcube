package jumpCube;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;

import static jumpCube.Color.*;

/** Main logic for playing (a) game(s) of JumpCube.
 *  @author Brian Ha
 */
class Game {

    /** Name of resource containing help message. */
    private static final String HELP = "jumpCube/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _readonlyBoard = new ConstantBoard(_board);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)$|^|\\p{Blank}+");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
        _playing = false;
        _gameOn = true;
        _player1 = new HumanPlayer(this, RED);
        _player2 = new AI(this, BLUE);
        _cline = new ArrayList<String>();
    }

    /** Returns a readonly view of the game board.  This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _readonlyBoard;
    }

    /** Returns the mutable game board. */
    Board getMBoard() {
        return _board;
    }

    /** Returns the first player object. */
    Player getFirstPlayer() {
        return _player1;
    }

    /** Returns the second player object. */
    Player getSecondPlayer() {
        return _player2;
    }

    /** Playing when GUI start button is activated. */
    void setGUIPlaying() {
        _playing = true;
    }

    /** Return _playing for GUI use. */
    boolean getPlaying() {
        return _playing;
    }

    /** Set GUI. */
    public void setGUI(JumpGUI gui) {
        _onGUI = true;
        _gui = gui;
    }

    /** Play a session of JumpCube.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        _out.flush();
        while (_gameOn) {
            if (_playing) {
                checkForWin();
                if (_board.getWinner() == null) {
                    if (_board.whoseMove() == RED) {
                        _player1.makeMove();
                    } else {
                        _player2.makeMove();
                    }
                }
            } else {
                if (promptForNext()) {
                    readExecuteCommand();
                }
            }
        }
        _inp.close();
        _prompter.close();
        _out.close();
        _err.close();
        return 0;
    }

    /** Get a move from my input and place its row and column in
     *  MOVE.  Returns true if this is successful, false if game stops
     *  or ends first. */
    boolean getMove(int[] move) {
        while (_playing && _move[0] == 0 && promptForNext()) {
            readExecuteCommand();
        }
        if (_move[0] > 0) {
            move[0] = _move[0];
            move[1] = _move[1];
            _move[0] = 0;
            return true;
        } else {
            return false;
        }
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        Color clr = _board.whoseMove();
        if (_board.exists(r, c) && _board.isLegal(clr)
            && _board.isLegal(clr, r, c)) {
            _board.addSpot(clr, r, c);
            _board.setMoves(_board.numMoves() + 1);
            if (clr == RED) {
                if (_player1 instanceof AI) {
                    String str = clr.toString();
                    _out.println(str.substring(0, 1).toUpperCase()
                                 + str.substring(1) + " moves "
                                 + r + " " + c + ".");
                    _out.flush();
                }
            } else {
                if (_player2 instanceof AI) {
                    String str = clr.toString();
                    _out.println(str.substring(0, 1).toUpperCase()
                                 + str.substring(1) + " moves "
                                 + r + " " + c + ".");
                    _out.flush();
                }
            }
            if (_onGUI) {
                _gui.updateBoard();
            }
        } else {
            reportError("You are unable to add a spot to %d:%d.",
                        r, c);
        }
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        int r = _board.row(n);
        int c = _board.row(n);
        makeMove(r, c);
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        if (_playing && _board.getWinner() != null) {
            _playing = false;
            announceWinner();
            if (_onGUI) {
                _gui.announceWinner((jumpCube.Color) _board.getWinner());
            }
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        String str = _board.getWinner().toString();
        _out.println(str.substring(0, 1).toUpperCase()
                     + str.substring(1) + " wins.");
        _out.flush();
    }

    /** Make PLAYER an AI for subsequent moves. */
    private void setAuto(Color player) {
        if (player == RED) {
            _player1 = new AI(this, player);
        } else {
            _player2 = new AI(this, player);
        }
    }

    /** Make PLAYER take manual input from the user for subsequent moves. */
    private void setManual(Color player) {
        if (player == RED) {
            _player1 = new HumanPlayer(this, player);
        } else {
            _player2 = new HumanPlayer(this, player);
        }
    }

    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        _playing = false;
        if (_onGUI) {
            _gui.enableStart();
        }
        int n = _board.size();
        _board.clear(n);
        if (_onGUI) {
            _gui.updateBoard();
        }
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board);
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        _playing = false;
        if (_onGUI) {
            _gui.enableStart();
        }
        _board.setMoves(n);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots, String color) {
        int neigh = _board.neighbors(r, c);
        if (spots > -1 && spots <= neigh) {
            _board.set(r, c, spots, Color.parseColor(color));
        } else {
            reportError("The new number of dots must be"
                        + "less than %d and greater than 0.", neigh);
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(int n) {
        _playing = false;
        if (_onGUI) {
            _gui.enableStart();
        }
        _board.clear(n);
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    private void restartGame() {
        _playing = true;
        checkForWin();
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        /*if (!_board.exists(r, c)) {
          reportError("move %d %d out of bounds", r, c);
          }*/
        _move[0] = r;
        _move[1] = c;
    }

    /** Returns a color (player) name from _inp: either RED or BLUE.
     *  Throws an exception if not present. */
    private Color readColor() {
        return Color.parseColor(_inp.next("[rR][eE][dD]|[Bb][Ll][Uu][Ee]"));
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        _cline.clear();
        String str = _inp.nextLine();
        str = str.trim();
        String[] arr = str.split("\\s+");
        for (int i = 0; i < arr.length; i++) {
            _cline.add(arr[i]);
        }
        String temp1 = _cline.get(0);
        if (_cline.size() == 2 && temp1.matches("-?\\d+")
            && _cline.get(1).matches("-?\\d+")) {
            if (_playing) {
                saveMove(Integer.parseInt(temp1),
                         Integer.parseInt(_cline.get(1)));
                makeMove(_move[0], _move[1]);
            } else {
                reportError("No game in progress.");
            }
        } else {
            executeCommand(temp1);
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. */
    private void executeCommand(String cmnd) {
        switch (cmnd.toLowerCase()) {
        case "\n": case "\r\n": case "":
            return;
        case "#":
            break;
        case "clear":
            clear();
            break;
        case "start":
            restartGame();
            break;
        case "quit":
            _gameOn = false;
            break;
        case "auto":
            autoHelper();
            break;
        case "manual":
            manualHelper();
            break;
        case "size":
            sizeHelper();
            break;
        case "move":
            if (_cline.size() >= 2) {
                moveHelper();
            } else {
                reportError("Invalid number of arguments given.");
            }
            break;
        case "set":
            setHelper();
            break;
        case "dump":
            dump();
            break;
        case "seed":
            if (_cline.size() >= 2) {
                seedHelper();
            } else {
                reportError("Invalid number of arguments given.");
            }
            break;
        case "help":
            help();
            break;
        default:
            reportError("bad command: '%s'", cmnd);
        }
    }

    /** Helper manual function. */
    public void manualHelper() {
        if (_cline.size() >= 2) {
            String str2 = _cline.get(1);
            if (str2.toLowerCase().equals("red")
                || str2.toLowerCase().equals("blue")) {
                setManual(parseColor(str2));
                if (_onGUI) {
                    _gui.drawBoard();
                }
            } else {
                reportError("Invalid color %s was given.", str2);
            }
        } else {
            reportError("Invalid number of arguments given.");
        }
    }

    /** Helper auto function. */
    public void autoHelper() {
        if (_cline.size() >= 2) {
            String str1 = _cline.get(1);
            if (str1.toLowerCase().equals("red")
                || str1.toLowerCase().equals("blue")) {
                setAuto(parseColor(str1));
                if (_onGUI) {
                    _gui.drawBoard();
                }
            } else {
                reportError("Invalid color %s was given.", str1);
            }
        } else {
            reportError("Invalid number of arguments given.");
        }
    }

    /** Helper move function. */
    public void moveHelper() {
        String str4 = _cline.get(1);
        if (str4.matches("-?\\d+") && Integer.parseInt(str4) > 0) {
            setMoveNumber(Integer.parseInt(str4) - 1);
            if (_onGUI) {
                _gui.drawBoard();
            }
        } else {
            reportError("Invalid size %s was given.", str4);
        }
    }

    /** Helper size function. */
    public void sizeHelper() {
        if (_cline.size() >= 2) {
            String str3 = _cline.get(1);
            if (str3.matches("-?\\d+") && Integer.parseInt(str3) > 0) {
                setSize(Integer.parseInt(str3));
                if (_onGUI) {
                    _gui.updateBoard();
                }
            } else {
                reportError("Invalid size %s was given.", str3);
            }
        } else {
            reportError("Invalid number of arguments given.");
        }
    }

    /** Helper set function. */
    public void setHelper() {
        if (_cline.size() >= 5) {
            String str5 = _cline.get(1);
            String str6 = _cline.get(2);
            String str7 = _cline.get(3);
            String str8 = _cline.get(4);
            if (_board.exists(Integer.parseInt(str5), Integer.parseInt(str6))
                && str5.matches("-?\\d+") && str6.matches("-?\\d+")
                && str7.matches("-?\\d+")
                && (str8.toLowerCase().equals("r")
                    || str8.toLowerCase().equals("b"))) {
                if (str8.toLowerCase().equals("r")) {
                    setSpots(Integer.parseInt(str5), Integer.parseInt(str6),
                             Integer.parseInt(str7), "red");
                } else {
                    setSpots(Integer.parseInt(str5), Integer.parseInt(str6),
                             Integer.parseInt(str7), "blue");
                }
                _playing = false;
                if (_onGUI) {
                    _gui.enableStart();
                }
            } else {
                reportError("Invalid arguments %s, %s, %s, %s"
                            + " were given.", str5, str6, str7, str8);
            }
        } else {
            reportError("Invalid number of arguments given.");
        }
        if (_onGUI) {
            _gui.updateBoard();
        }
    }

    /** Helper seed function. */
    public void seedHelper() {
        String str9 = _cline.get(1);
        if (str9.matches("-?\\d+")) {
            setSeed(Long.parseLong(str9));
        } else {
            reportError("Invalid seed %s was given.", str9);
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token. */
    private boolean promptForNext() {
        if (!_playing) {
            _prompter.print("> ");
            _prompter.flush();
        } else {
            _prompter.print(_board.whoseMove().toString() + "> ");
            _prompter.flush();
        }
        boolean b = _inp.hasNextLine();
        if (b) {
            return b;
        } else {
            System.exit(0);
            return b;
        }
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
    }

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;

    /** The board on which I record all moves. */
    private final Board _board;
    /** A readonly view of _board. */
    private final Board _readonlyBoard;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;

    /** True if game does not quit. */
    private boolean _gameOn;

    /** Player 1 object. */
    private Player _player1;

    /** Player 2 object. */
    private Player _player2;

    /** Command line object. */
    private ArrayList<String> _cline;

    /** Used to return a move entered from the console.  Allocated
     *  here to avoid allocations. */
    private final int[] _move = new int[2];

    /** If this game is using GUI. */
    private boolean _onGUI;

    /** GUI variable. */
    private JumpGUI _gui;
}
