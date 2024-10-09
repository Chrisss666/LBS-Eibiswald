import javax.swing.*;

public class GameLauncher {
    public static void main(String[] args) {
        JFrame parentFrame = new JFrame();
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentFrame.setVisible(false);

        SettingsDialog settingsDialog = new SettingsDialog(parentFrame);
        settingsDialog.setVisible(true);

        if (settingsDialog.isConfirmed()) {
            int maxAttempts = settingsDialog.getMaxAttempts();
            new MainFrame(maxAttempts);
        } else {
            System.exit(0);
        }
    }
}
