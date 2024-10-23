    import javax.swing.*;
    import java.awt.*;

    public class MainFrame extends JFrame {
        private GamePanel gamePanel;
        private InputPanel inputPanel;
        private GameState gameState;
        private WordManager wordManager;

        public MainFrame(int maxAttempts) {
            setTitle("Hangman Game");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            wordManager = new WordManager();
            gameState = new GameState(wordManager.getCurrentWord().length());
            gameState.setMaxAttempts(maxAttempts);
            gamePanel = new GamePanel(gameState);
            inputPanel = new InputPanel(wordManager, gameState, gamePanel);

            setLayout(new BorderLayout());
            add(gamePanel, BorderLayout.CENTER);
            add(inputPanel, BorderLayout.SOUTH);

            createMenu();

            setVisible(true);
        }

        private void createMenu() {
            JMenuBar menuBar = new JMenuBar();
            JMenu gameMenu = new JMenu("Game");

            JMenuItem newGameItem = new JMenuItem("Neues Spiel");
            newGameItem.addActionListener(e -> startNewGame());

            JCheckBoxMenuItem showHistoryItem = new JCheckBoxMenuItem("Verlauf anzeigen", true);
            showHistoryItem.addActionListener(e -> inputPanel.setShowHistory(showHistoryItem.isSelected()));

            gameMenu.add(newGameItem);
            gameMenu.add(showHistoryItem);
            menuBar.add(gameMenu);

            setJMenuBar(menuBar);
        }

        public void startNewGame() {
            wordManager.loadNewWord();
            gameState.reset(wordManager.getCurrentWord().length());
            gamePanel.repaint();
            inputPanel.reset();
        }
    }