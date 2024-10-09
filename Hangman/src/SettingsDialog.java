import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private int maxAttempts = 9;
    private boolean confirmed = false;

    public SettingsDialog(JFrame parent) {
        super(parent, "Spieleinstellungen", true);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Wählen der Anzahl der zulässigen Versuche aus:");
        JTextField attemptsField = new JTextField("9", 5);

        JPanel inputPanel = new JPanel();
        inputPanel.add(label);
        inputPanel.add(attemptsField);
        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Spiel starten");
        JButton cancelButton = new JButton("Abbrechen");

        confirmButton.addActionListener(e -> {
            try {
                maxAttempts = Integer.parseInt(attemptsField.getText());
                confirmed = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Bitte einen gültigen Wert eingeben.");
            }
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
