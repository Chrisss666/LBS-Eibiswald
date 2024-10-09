import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputPanel extends JPanel {
    private WordManager wordManager;
    private GameState gameState;
    private GamePanel gamePanel;
    private JTextField inputField;
    private JLabel wordLabel;
    private JTextArea historyArea;
    private boolean showHistory = true;

    public InputPanel(WordManager wordManager, GameState gameState, GamePanel gamePanel) {
        this.wordManager = wordManager;
        this.gameState = gameState;
        this.gamePanel = gamePanel;

        setLayout(new BorderLayout());

        wordLabel = new JLabel(wordManager.getMaskedWord());
        inputField = new JTextField(1);
        historyArea = new JTextArea(5, 20);
        historyArea.setEditable(false);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput(inputField.getText());
                inputField.setText("");
            }
        });

        add(wordLabel, BorderLayout.NORTH);
        add(inputField, BorderLayout.CENTER);
        add(new JScrollPane(historyArea), BorderLayout.SOUTH);
    }

    private void processInput(String input) {
        if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
            boolean correctGuess = wordManager.processGuess(input.charAt(0));
            gameState.addLetterToHistory(input.charAt(0));
            wordLabel.setText(wordManager.getMaskedWord());
            gamePanel.repaint();

            if (showHistory) {
                historyArea.setText(gameState.getLetterHistory());
            }

            if (!correctGuess) {
                gameState.incrementFailedAttempts();
            }

            if (gameState.isGameOver()) {
                int response = JOptionPane.showConfirmDialog(this, "Verloren! Das Wort war: " + wordManager.getCurrentWord() + ". Möchtest du ein neues Spiel starten?", "Spiel beendet", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).startNewGame();
                } else {
                    System.exit(0);
                }
            }
            else if (wordManager.isWordGuessed()) {
                int response = JOptionPane.showConfirmDialog(this, "Glückwunsch! Du hast das Wort erraten! Möchtest du ein neues Spiel starten?", "Spiel gewonnen", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    ((MainFrame) SwingUtilities.getWindowAncestor(this)).startNewGame();
                } else {
                    System.exit(0);
                }
            }
        }
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
        historyArea.setVisible(showHistory);
    }

    public void reset() {
        wordLabel.setText(wordManager.getMaskedWord());
        historyArea.setText("");
        inputField.setText("");
    }
}
