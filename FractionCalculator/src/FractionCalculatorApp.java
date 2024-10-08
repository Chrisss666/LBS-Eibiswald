import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FractionCalculatorApp extends JFrame {

    private JTextField numerator1Field, denominator1Field, numerator2Field, denominator2Field;
    private JComboBox<String> operationBox;
    private JLabel resultLabel;
    private Calculator calculator;

    public FractionCalculatorApp() {
        calculator = new Calculator();

        setTitle("Bruchrechner");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JLabel headerLabel = new JLabel("Bruchrechner", SwingConstants.CENTER);
        headerLabel.setOpaque(true);
        headerLabel.setBackground(new Color(63, 81, 181)); // Smoother Blauton
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // Platz für Text
        add(headerLabel, BorderLayout.NORTH);

        // Hauptbereich
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        // Eingabebereich für die Brüche und Operation
        JPanel inputArea = new JPanel(new GridLayout(1, 3, 10, 10));

        // Bruch 1
        JPanel fraction1Panel = new JPanel();
        fraction1Panel.setLayout(new BoxLayout(fraction1Panel, BoxLayout.Y_AXIS));
        numerator1Field = createTextField();
        denominator1Field = createTextField();
        fraction1Panel.add(numerator1Field);
        fraction1Panel.add(createFractionBar()); // Fraction Bar als Linie
        fraction1Panel.add(denominator1Field);

        // Operationen
        String[] operations = {"+", "-", "×", "÷"};
        operationBox = new JComboBox<>(operations);
        operationBox.setFont(new Font("Arial", Font.PLAIN, 24));
        operationBox.setBackground(new Color(224, 224, 224));

        // Bruch 2
        JPanel fraction2Panel = new JPanel();
        fraction2Panel.setLayout(new BoxLayout(fraction2Panel, BoxLayout.Y_AXIS));
        numerator2Field = createTextField();
        denominator2Field = createTextField();
        fraction2Panel.add(numerator2Field);
        fraction2Panel.add(createFractionBar()); // Fraction Bar als Linie
        fraction2Panel.add(denominator2Field);

        inputArea.add(fraction1Panel);
        inputArea.add(operationBox);
        inputArea.add(fraction2Panel);

        mainPanel.add(inputArea);

        // Ergebnisbereich
        JPanel resultArea = new JPanel();
        resultArea.setLayout(new BoxLayout(resultArea, BoxLayout.Y_AXIS));

        JButton calculateButton = new JButton("Berechnen");
        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.setBackground(new Color(76, 175, 80)); // Smoother Grünton
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 18));
        calculateButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Zeiger für Button

        resultLabel = new JLabel("Ergebnis: ");
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setForeground(new Color(48, 63, 159)); // Dunklerer Blauton

        resultArea.add(calculateButton);
        resultArea.add(Box.createRigidArea(new Dimension(0, 20))); // Abstand
        resultArea.add(resultLabel);

        mainPanel.add(resultArea);

        // ActionListener für den Berechnen-Button
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });

        // Footer
        JLabel footerLabel = new JLabel("© 2024 Bruchrechner App", SwingConstants.CENTER);
        footerLabel.setOpaque(true);
        footerLabel.setBackground(Color.DARK_GRAY);
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footerLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Methode zum Erstellen von Eingabefeldern
    private JTextField createTextField() {
        JTextField textField = new JTextField(5);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        textField.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 2));
        return textField;
    }

    // Methode zum Erstellen der Bruchstriche
    private JSeparator createFractionBar() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(80, 1));
        separator.setForeground(Color.BLACK);
        return separator;
    }

    // Methode zum Berechnen des Ergebnisses
    private void calculateResult() {
        try {
            int numerator1 = Integer.parseInt(numerator1Field.getText());
            int denominator1 = Integer.parseInt(denominator1Field.getText());
            int numerator2 = Integer.parseInt(numerator2Field.getText());
            int denominator2 = Integer.parseInt(denominator2Field.getText());

            if (denominator1 == 0 || denominator2 == 0) {
                resultLabel.setText("Ergebnis: Fehler - Nenner darf nicht 0 sein.");
                return;
            }

            Fraction f1 = new Fraction(numerator1, denominator1);
            Fraction f2 = new Fraction(numerator2, denominator2);

            String operation = (String) operationBox.getSelectedItem();
            Fraction result;

            switch (operation) {
                case "+":
                    result = calculator.add(f1, f2);
                    break;
                case "-":
                    result = calculator.subtract(f1, f2);
                    break;
                case "×":
                    result = calculator.multiply(f1, f2);
                    break;
                case "÷":
                    result = calculator.divide(f1, f2);
                    break;
                default:
                    throw new UnsupportedOperationException("Ungültige Operation.");
            }

            resultLabel.setText("Ergebnis: " + result.toString());
        } catch (NumberFormatException e) {
            resultLabel.setText("Ergebnis: Fehler - Ungültige Eingabe.");
        }
    }

    public static void main(String[] args) {
        new FractionCalculatorApp();
    }
}