package jumpCube;

import static jumpCube.Color.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.SwingWorker;
import javax.swing.SwingUtilities;

/** The GUI class for the jumpCube game.
 * @author Brian Ha
 */
public class JumpGUI {
    /** Title of the GUI. */
    private String _title = "JumpCube by Brian Ha";
    /** Information string. */
    private String desStr = "Please see the GitHub readme file for instructions.<br><br>"
        + "Valid operations through the GUI are clicking the start"
        + " button to start the game and clicking a square to move"
        + " as a human player.<br><br>"
        + "If AI is not moving, win message isn't appearing, or"
        + " an error seems to have occured, try pressing enter"
        + " in the terminal.";
    /** Local game object. */
    private Game _game;
    /** Local board object. */
    private Board _board;
    /** Representation of the grid. */
    private Square[][] _grid;
    /** JFrame object. */
    private JFrame _frame;
    /** JPanel holding the board.*/
    private JPanel _boardPanel;
    /** JPanel holding the settings and info. */
    private JPanel _ctrlPanel;
    /** JLabels holding the settings and info. */
    private JLabel _nextMove, _moves, _info, _settings;
    /** Start JButton. */
    private JButton _start;
    /** TitledBorders for settings. */
    private TitledBorder _nextBorder, _movesBorder;
    /** TitledBorders for info and settings. */
    private TitledBorder _infoBorder, _settingsBorder;
    /** Number of human players existing. */
    private int _numOfHumans;
    /** Move representation array. */
    private int[] _move;

    /** JumpGUI constuctor that takes in GAME. */
    public JumpGUI(Game game) {
        _game = game;
        _board = game.getBoard();
        if (_game.getFirstPlayer() instanceof HumanPlayer
            && _game.getSecondPlayer() instanceof HumanPlayer) {
            _numOfHumans = 2;
        } else if (_game.getFirstPlayer() instanceof HumanPlayer
                   || _game.getSecondPlayer() instanceof HumanPlayer) {
            _numOfHumans = 1;
        } else {
            _numOfHumans = 0;
        }
        game.setGUI(this);
    }

    /** Starts the GUI. */
    public void start() {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    createAndShowGUI();
                }
            });
        _game.play();
    }

    /** Draws the board for the first time. */
    public void drawBoard() {
        int n = _board.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int spots = _board.spots(i + 1, j + 1);
                _grid[i][j].setText(spots + "");
            }
        }
        if (_game.getFirstPlayer() instanceof HumanPlayer
            && _game.getSecondPlayer() instanceof HumanPlayer) {
            _numOfHumans = 2;
        } else if (_game.getFirstPlayer() instanceof HumanPlayer
                   || _game.getSecondPlayer() instanceof HumanPlayer) {
            _numOfHumans = 1;
        } else {
            _numOfHumans = 0;
        }
        jumpCube.Color wm = _board.whoseMove();
        _nextMove.setText("<html>"
                          + wm.toString().substring(0, 1).toUpperCase()
                          + wm.toString().substring(1) + "</html>");
        _moves.setText("<html>" + (_board.numMoves() + 1)
                       + "</html>");
        String s = "";
        s = "Number of Human Players: " + _numOfHumans;
        s += "<br>Number of AI Players: " + (2 - _numOfHumans);
        _settings.setText("<html>" + s + "</html>");
    }

    /** Updates the board. */
    public void updateBoard() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension boardSize = new Dimension(screenSize.height,
                                            screenSize.height);
        _boardPanel.removeAll();
        int n = _board.size();
        _boardPanel.setLayout(new GridLayout(n, n));
        _boardPanel.setPreferredSize(boardSize);
        _boardPanel.setBounds(0, 0, boardSize.width, boardSize.height);
        GridListener sgl = new GridListener();
        _grid = new Square[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                _grid[i][j] = new Square("", i, j);
                if (_board.color(i + 1, j + 1) == RED) {
                    _grid[i][j].setBackground(Color.RED);
                } else if (_board.color(i + 1, j + 1) == BLUE) {
                    _grid[i][j].setBackground(Color.BLUE);
                } else {
                    _grid[i][j].setBackground(Color.WHITE);
                }
                Border bor = BorderFactory.createLineBorder(Color.BLACK, 1);
                _grid[i][j].setBorder(bor);
                _grid[i][j].setOpaque(true);
                _grid[i][j].addActionListener(sgl);
                _boardPanel.add(_grid[i][j]);
            }
        }
        _boardPanel.validate();
        drawBoard();
    }

    /** Creates and shows the GUI. */
    public void createAndShowGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame.setDefaultLookAndFeelDecorated(true);
        _frame = new JFrame(_title);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension boardSize = new Dimension(screenSize.height,
                                            screenSize.height);
        _boardPanel = new JPanel(new GridLayout(6, 6));
        _boardPanel.setPreferredSize(boardSize);
        _boardPanel.setBounds(0, 0, boardSize.width, boardSize.height);

        GridListener sgl = new GridListener();
        _grid = new Square[6][6];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                _grid[i][j] = new Square("", i, j);
                _grid[i][j].setBackground(Color.WHITE);
                Border bor = BorderFactory.createLineBorder(Color.BLACK, 1);
                _grid[i][j].setBorder(bor);
                _grid[i][j].setOpaque(true);
                _grid[i][j].addActionListener(sgl);
                _boardPanel.add(_grid[i][j]);
            }
        }
        _ctrlPanel = new JPanel();
        _ctrlPanel.setLayout(new BoxLayout(_ctrlPanel, BoxLayout.PAGE_AXIS));
        _settingsBorder = BorderFactory.createTitledBorder("Game Settings");
        _nextBorder = BorderFactory.createTitledBorder("Current Player");
        _movesBorder = BorderFactory.createTitledBorder("Move Number");
        _infoBorder = BorderFactory.createTitledBorder("Information");
        _nextMove = new JLabel("Next Player: " + _board.whoseMove());
        _nextMove.setBorder(_nextBorder);
        _moves = new JLabel("Moves: 0");
        _moves.setBorder(_movesBorder);
        _start = new JButton("START");
        _start.setToolTipText("Click to play!");
        _start.setActionCommand("start");
        _start.addActionListener(new GameListener());
        _settings = new JLabel("");
        _settings.setBorder(_settingsBorder);
        _info = new JLabel("<html>" + desStr + "</html>");
        _info.setBorder(_infoBorder);
        _ctrlPanel.add(_nextMove);
        _ctrlPanel.add(_moves);
        _ctrlPanel.add(_settings);
        _ctrlPanel.add(_info);
        _ctrlPanel.add(Box.createVerticalStrut(screenSize.height / 10));
        _ctrlPanel.add(_start, BorderLayout.PAGE_END);
        _frame.add(_boardPanel, BorderLayout.WEST);
        _frame.add(_ctrlPanel, BorderLayout.CENTER);
        _frame.pack();
        _frame.setBounds(0, 0, screenSize.width, screenSize.height);
        _frame.setVisible(true);
        drawBoard();
    }

    /** Returns _move. */
    public int[] processMove() {
        return _move;
    }

    /** Announces the winner in the GUI. C is the winner's color.*/
    public void announceWinner(jumpCube.Color c) {
        JOptionPane.showMessageDialog(_frame,
                                      c.toString() + " wins.");
    }

    /** Square object that represents a grid square. Is also a JButton. */
    private class Square extends JButton {
        /** Row int. */
        private int _row;
        /** Col int. */
        private int _col;

        /** Square constructor using TEXT, ROW, and COL for JButton. */
        public Square(String text, int row, int col) {
            super(text);
            _row = row;
            _col = col;
        }

        /** Returns _row. */
        public int getRow() {
            return _row;
        }
        /** Returns _col. */
        public int getCol() {
            return _col;
        }
    }

    /** A Listener that handles the start button. */
    private class GameListener implements ActionListener {

        /** Takes E and handles the event. */
        public void actionPerformed(ActionEvent e) {
            if ("start".equals(e.getActionCommand())) {
                _start.setText("Game in progress.");
                _start.setEnabled(false);

                SwingWorker worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        _game.setGUIPlaying();
                        return null;
                    }
                };
                worker.execute();
            }
        }
    }

    /** A Listener that handles moving through clicking the grid. */
    private class GridListener implements ActionListener {

        /** Takes E and handles the event. */
        public void actionPerformed(ActionEvent e) {
            Square sq = (Square) e.getSource();
            if (_game.getPlaying()) {
                jumpCube.Color tempC = _board.whoseMove();
                if (tempC == RED) {
                    if (_game.getFirstPlayer() instanceof HumanPlayer) {
                        _game.makeMove(sq.getRow() + 1, sq.getCol() + 1);
                        if (_game.getSecondPlayer() instanceof AI) {
                            _game.getSecondPlayer().makeMove();
                        }
                    }
                } else {
                    if (_game.getSecondPlayer() instanceof HumanPlayer) {
                        _game.makeMove(sq.getRow() + 1, sq.getCol() + 1);
                        if (_game.getFirstPlayer() instanceof AI) {
                            _game.getFirstPlayer().makeMove();
                        }
                    }
                }
            }
        }
    }

    /** Re-enables the start button. */
    void enableStart() {
        _start.setEnabled(true);
        _start.setText("START");
    }
}
