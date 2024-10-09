import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private GameState gameState;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int failedAttempts = gameState.getFailedAttempts();
        ImageIcon image = new ImageIcon("images/hangman_" + failedAttempts + ".png");
        g.drawImage(image.getImage(), 10, 10, this);
    }
}
