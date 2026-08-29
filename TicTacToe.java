import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.Timer;

public class TicTacToe {

    int borderWidth = 600;
    int borderHeight = 720;

    JFrame frame = new JFrame("Tic Tac Toe");
    JLabel textLabel = new JLabel("Tic Tac Toe");
    JLabel playerInfoLabel = new JLabel(""); // New label to show player info
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel scorePanel = new JPanel();

    JButton[][] buttons = new JButton[3][3];
    JButton restartBtn = new JButton("Restart");

    String playerX = "X";
    String playerO = "O";
    String currentPlayer = playerX;

    boolean gameOver = false;
    int turnCount = 0;

    int xScore = 0;
    int oScore = 0;

    JLabel xScoreLabel = new JLabel("Player X: 0");
    JLabel oScoreLabel = new JLabel("Player O: 0");

    boolean computerMode = false;
    String humanSymbol = playerX; // Human symbol (X or O)
    String computerSymbol = playerO; // Computer symbol (opposite of human)

    public TicTacToe() {
        frame.setSize(borderWidth, borderHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        setupMenuBar();
        setupTitlePanel();
        setupScorePanel();
        setupBoardPanel();
        initBoard();

        choosePlayer();

        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                frame.requestFocusInWindow();
            }
        });

        frame.setVisible(true);
    }

    // ================= PLAYER CHOICE ======================
    void choosePlayer() {
        JDialog dialog = new JDialog(frame, "Choose Your Symbol", true);
        dialog.setSize(320, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(new Color(35, 35, 35));

        JLabel label = new JLabel("Choose your symbol and mode:");
        label.setForeground(Color.white);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(new EmptyBorder(15, 0, 10, 0));
        dialog.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(35, 35, 35));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 10));

        JButton xButton = new JButton("X");
        JButton oButton = new JButton("O");

        JCheckBox vsComputer = new JCheckBox("Play vs Computer");
        vsComputer.setForeground(Color.white);
        vsComputer.setBackground(new Color(35, 35, 35));
        vsComputer.setFont(new Font("Arial", Font.PLAIN, 14));
        vsComputer.setHorizontalAlignment(SwingConstants.CENTER);

        JButton[] buttonsArr = {xButton, oButton};

        for (JButton b : buttonsArr) {
            b.setFont(new Font("Arial", Font.BOLD, 30));
            b.setForeground(Color.white);
            b.setBackground(new Color(30, 144, 255));
            b.setFocusPainted(false);
            b.setOpaque(true);
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(new Color(70, 170, 255)); }
                public void mouseExited(MouseEvent e) { b.setBackground(new Color(30, 144, 255)); }
                public void mousePressed(MouseEvent e) { b.setBackground(new Color(15, 100, 200)); }
                public void mouseReleased(MouseEvent e) { b.setBackground(new Color(30, 144, 255)); }
            });
            buttonPanel.add(b);
        }

        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.add(vsComputer, BorderLayout.SOUTH);

        xButton.addActionListener(e -> {
            currentPlayer = playerX;
            computerMode = vsComputer.isSelected();
            humanSymbol = playerX;
            computerSymbol = playerO;
            dialog.dispose();
            updatePlayerInfo();
            textLabel.setText("Player " + currentPlayer + "'s Turn");
            
            // If computer plays first (when human is O, computer is X)
            if (computerMode && currentPlayer.equals(computerSymbol)) {
                computerMove();
            }
        });

        oButton.addActionListener(e -> {
            currentPlayer = playerO;
            computerMode = vsComputer.isSelected();
            humanSymbol = playerO;
            computerSymbol = playerX;
            dialog.dispose();
            updatePlayerInfo();
            textLabel.setText("Player " + currentPlayer + "'s Turn");
            
            // If computer plays first (when human is O, computer is X)
            if (computerMode && currentPlayer.equals(computerSymbol)) {
                computerMove();
            }
        });

        dialog.setVisible(true);
    }

    // Update player information display
    void updatePlayerInfo() {
        if (computerMode) {
            playerInfoLabel.setText("You: " + humanSymbol + " | Computer: " + computerSymbol);
        } else {
            playerInfoLabel.setText("Player X vs Player O");
        }
    }

    // ================= MENU BAR ======================
    void setupMenuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBorder(new EmptyBorder(6, 8, 6, 8));
        mb.setOpaque(true);
        mb.setBackground(new Color(25, 25, 25));

        JMenu menu = new JMenu("Game");
        styleMenuComponent(menu);

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem resetScoresItem = new JMenuItem("Reset Scores");
        JMenuItem exitItem = new JMenuItem("Exit");

        styleMenuComponent(newGameItem);
        styleMenuComponent(resetScoresItem);
        styleMenuComponent(exitItem);

        newGameItem.addActionListener(e -> resetGame());
        resetScoresItem.addActionListener(e -> {
            xScore = 0;
            oScore = 0;
            updateScore();
            resetGame();
        });
        exitItem.addActionListener(e -> System.exit(0));

        menu.add(newGameItem);
        menu.add(resetScoresItem);
        menu.addSeparator();
        menu.add(exitItem);

        mb.add(menu);

        JLabel tip = new JLabel("  Tip: Press 1–9 to play");
        tip.setForeground(new Color(180, 180, 180));
        tip.setFont(new Font("Arial", Font.PLAIN, 12));
        mb.add(Box.createHorizontalGlue());
        mb.add(tip);

        frame.setJMenuBar(mb);
    }

    void styleMenuComponent(JComponent c) {
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        c.setForeground(Color.WHITE);
        c.setBackground(new Color(35, 35, 35));
        c.setOpaque(true);
        if (c instanceof JMenuItem) {
            ((JMenuItem)c).setBorder(new EmptyBorder(6, 12, 6, 12));
            c.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) { c.setBackground(new Color(55, 100, 170)); }
                public void mouseExited(MouseEvent evt) { c.setBackground(new Color(35, 35, 35)); }
            });
        }
    }

    // ================= TITLE PANEL ======================
    void setupTitlePanel() {
        textLabel.setBackground(Color.darkGray);
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial", Font.BOLD, 40));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setOpaque(true);
        
        // Player info label
        playerInfoLabel.setBackground(Color.darkGray);
        playerInfoLabel.setForeground(Color.lightGray);
        playerInfoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        playerInfoLabel.setHorizontalAlignment(JLabel.CENTER);
        playerInfoLabel.setOpaque(true);
        playerInfoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        textPanel.add(playerInfoLabel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);
    }

    // ================= SCORE PANEL ======================
    void setupScorePanel() {
        scorePanel.setLayout(new GridLayout(1, 3, 10, 0));
        scorePanel.setBackground(Color.darkGray);
        scorePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        xScoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        oScoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        xScoreLabel.setForeground(Color.white);
        oScoreLabel.setForeground(Color.white);
        xScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        oScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        restartBtn.setFont(new Font("Arial", Font.BOLD, 20));
        restartBtn.setFocusable(false);
        restartBtn.setBackground(new Color(30, 144, 255));
        restartBtn.setForeground(Color.white);
        restartBtn.setOpaque(true);
        restartBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        restartBtn.addActionListener(e -> resetGame());

        scorePanel.add(xScoreLabel);
        scorePanel.add(restartBtn);
        scorePanel.add(oScoreLabel);

        frame.add(scorePanel, BorderLayout.SOUTH);
    }

    // ================= BOARD ======================
    void setupBoardPanel() {
        boardPanel.setLayout(new GridLayout(3, 3));
        boardPanel.setBackground(Color.darkGray);
        frame.add(boardPanel, BorderLayout.CENTER);
        addKeyControls();
    }

    void initBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton tile = new JButton("");
                buttons[i][j] = tile;

                tile.setFont(new Font("Arial", Font.BOLD, 120));
                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.white);
                tile.setFocusable(false);

                boardPanel.add(tile);

                tile.addActionListener(e -> {
                    if (gameOver) return;
                    if (!tile.getText().equals("")) return;

                    tile.setText(currentPlayer);
                    turnCount++;
                    animateTile(tile);
                    checkWin();

                    if (!gameOver) {
                        currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                        textLabel.setText("Player " + currentPlayer + "'s Turn");

                        if (computerMode && currentPlayer.equals(computerSymbol)) {
                            Timer t = new Timer(500, ev -> computerMove());
                            t.setRepeats(false);
                            t.start();
                        }
                    }
                });
            }
        }
    }

    // ================= CHECK WIN ======================
    void checkWin() {
        for (int i = 0; i < 3; i++) {
            if (match(buttons[i][0], buttons[i][1], buttons[i][2])) { win(buttons[i][0], buttons[i][1], buttons[i][2]); return; }
            if (match(buttons[0][i], buttons[1][i], buttons[2][i])) { win(buttons[0][i], buttons[1][i], buttons[2][i]); return; }
        }

        if (match(buttons[0][0], buttons[1][1], buttons[2][2])) { win(buttons[0][0], buttons[1][1], buttons[2][2]); return; }
        if (match(buttons[0][2], buttons[1][1], buttons[2][0])) { win(buttons[0][2], buttons[1][1], buttons[2][0]); return; }

        // DRAW
        if (turnCount == 9) {
            gameOver = true;
            textLabel.setText("Draw!");
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setForeground(new Color(255, 165, 0)); // orange
                    buttons[i][j].setBackground(new Color(80, 50, 0));
                }
            Timer autoRestart = new Timer(2000, e -> resetGame());
            autoRestart.setRepeats(false);
            autoRestart.start();
        }
    }

    boolean match(JButton b1, JButton b2, JButton b3) {
        return !b1.getText().equals("") &&
                b1.getText().equals(b2.getText()) &&
                b2.getText().equals(b3.getText());
    }

    void win(JButton b1, JButton b2, JButton b3) {
        b1.setForeground(Color.green);
        b2.setForeground(Color.green);
        b3.setForeground(Color.green);

        // Show who won based on the symbol
        String winnerSymbol = currentPlayer;
        if (computerMode) {
            if (winnerSymbol.equals(humanSymbol)) {
                textLabel.setText("You Win!");
            } else {
                textLabel.setText("Computer Wins!");
            }
        } else {
            textLabel.setText("Player " + currentPlayer + " Wins!");
        }
        
        gameOver = true;

        if (currentPlayer.equals("X")) xScore++;
        else oScore++;

        updateScore();
    }

    void updateScore() {
        xScoreLabel.setText("Player X: " + xScore);
        oScoreLabel.setText("Player O: " + oScore);
    }

    void resetGame() {
        turnCount = 0;
        gameOver = false;

        // Reset board
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setForeground(Color.white);
                buttons[i][j].setBackground(Color.darkGray);
            }

        // Ask player choice again
        choosePlayer();
        frame.requestFocusInWindow();
    }

    void addKeyControls() {
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (gameOver) return;
                if (computerMode && currentPlayer.equals(computerSymbol)) return; // Don't allow key press during computer's turn

                int key = e.getKeyChar() - '1';
                if (key < 0 || key > 8) return;

                int r = key / 3;
                int c = key % 3;

                JButton tile = buttons[r][c];
                if (tile.getText().equals("")) {
                    tile.setText(currentPlayer);
                    turnCount++;
                    animateTile(tile);
                    checkWin();

                    if (!gameOver) {
                        currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
                        textLabel.setText("Player " + currentPlayer + "'s Turn");

                        if (computerMode && currentPlayer.equals(computerSymbol)) {
                            Timer t = new Timer(500, ev -> computerMove());
                            t.setRepeats(false);
                            t.start();
                        }
                    }
                }
            }
        });
    }

    void animateTile(JButton tile) {
        Color original = tile.getBackground();
        tile.setBackground(new Color(100, 100, 140));
        new Timer(150, e -> tile.setBackground(original)).start();
    }

    // ================= SMARTER COMPUTER MOVE ======================
    void computerMove() {
        if (!computerMode || gameOver || !currentPlayer.equals(computerSymbol)) return;
        
        JButton move = null;

        // 1️⃣ Win if possible (computer wins)
        move = findWinningMove(computerSymbol);

        // 2️⃣ Block player (block human win)
        if (move == null) move = findWinningMove(humanSymbol);

        // 3️⃣ Take center if available
        if (move == null && buttons[1][1].getText().equals("")) {
            move = buttons[1][1];
        }

        // 4️⃣ Take corners if available
        if (move == null) {
            java.util.List<JButton> corners = new ArrayList<>();
            corners.add(buttons[0][0]);
            corners.add(buttons[0][2]);
            corners.add(buttons[2][0]);
            corners.add(buttons[2][2]);
            
            Collections.shuffle(corners); // Randomize corner selection
            for (JButton corner : corners) {
                if (corner.getText().equals("")) {
                    move = corner;
                    break;
                }
            }
        }

        // 5️⃣ Random move (sides)
        if (move == null) {
            java.util.List<JButton> empty = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (buttons[i][j].getText().equals("")) empty.add(buttons[i][j]);
                }
            }
            if (!empty.isEmpty()) {
                move = empty.get((int)(Math.random() * empty.size()));
            }
        }

        if (move != null) {
            move.setText(computerSymbol);
            turnCount++;
            animateTile(move);
            checkWin();

            if (!gameOver) {
                currentPlayer = humanSymbol; // Switch back to human player
                textLabel.setText("Player " + currentPlayer + "'s Turn");
            }
        }
    }

    JButton findWinningMove(String symbol) {
        for (int i = 0; i < 3; i++) {
            if (twoInLine(buttons[i][0], buttons[i][1], buttons[i][2], symbol)) 
                return getEmpty(buttons[i][0], buttons[i][1], buttons[i][2]);
            if (twoInLine(buttons[0][i], buttons[1][i], buttons[2][i], symbol)) 
                return getEmpty(buttons[0][i], buttons[1][i], buttons[2][i]);
        }
        if (twoInLine(buttons[0][0], buttons[1][1], buttons[2][2], symbol)) 
            return getEmpty(buttons[0][0], buttons[1][1], buttons[2][2]);
        if (twoInLine(buttons[0][2], buttons[1][1], buttons[2][0], symbol)) 
            return getEmpty(buttons[0][2], buttons[1][1], buttons[2][0]);
        return null;
    }

    boolean twoInLine(JButton b1, JButton b2, JButton b3, String symbol) {
        int count = 0;
        int emptyCount = 0;
        
        JButton[] line = {b1, b2, b3};
        for (JButton button : line) {
            if (button.getText().equals(symbol)) {
                count++;
            } else if (button.getText().equals("")) {
                emptyCount++;
            }
        }
        return count == 2 && emptyCount == 1;
    }

    JButton getEmpty(JButton b1, JButton b2, JButton b3) {
        if (b1.getText().equals("")) return b1;
        if (b2.getText().equals("")) return b2;
        if (b3.getText().equals("")) return b3;
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe());
    }
}