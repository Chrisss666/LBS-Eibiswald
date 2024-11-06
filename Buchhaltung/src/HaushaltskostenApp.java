import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Vector;

public class HaushaltskostenApp extends JFrame {
    Connection connection;
    JTable table;
    private DefaultTableModel tableModel;

    protected JLabel gewinnLabel;
    protected JTextField startDateField;
    protected JTextField endDateField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HaushaltskostenApp app = new HaushaltskostenApp();
            app.setVisible(true);
        });
    }

    public HaushaltskostenApp() {
        setTitle("Haushaltskosten App");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        connectDatabase();

        // Tabelle und Model
        tableModel = new DefaultTableModel(new String[]{"ID", "Datum", "Info", "Betrag", "Kategorie", "Art"}, 0);
        table = new JTable(tableModel);

        gewinnLabel = new JLabel("Gewinnsumme: 0 €");

        loadTableData(null, null);

        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Interaktionsfeld rechts
        JPanel interactionPanel = new JPanel();
        interactionPanel.setLayout(new BoxLayout(interactionPanel, BoxLayout.Y_AXIS));

        JButton addButton = new JButton("Hinzufügen");
        JButton editButton = new JButton("Bearbeiten");
        JButton deleteButton = new JButton("Entfernen");
        JButton filterButton = new JButton("Filter");
        JButton resetFilterButton = new JButton("Filter zurücksetzen");
        JButton dateFilterButton = new JButton("Zeitraum filtern");

        addButton.addActionListener(e -> showAddBookingForm());
        editButton.addActionListener(e -> showEditBookingForm());
        deleteButton.addActionListener(e -> deleteSelectedBooking());
        filterButton.addActionListener(e -> showFilterDialog());
        resetFilterButton.addActionListener(e -> loadTableData(null, null));
        dateFilterButton.addActionListener(e -> showDateRangeFilterDialog());

        interactionPanel.add(addButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(editButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(deleteButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(filterButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(resetFilterButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(dateFilterButton);

        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(gewinnLabel);

        add(interactionPanel, BorderLayout.EAST);
    }

    private void connectDatabase() {
        String url = "jdbc:mysql://localhost:3306/buchhaltung";
        String user = "java";
        String password = "java";

        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void loadTableData(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT MIN(DATE(`Datum/Zeit`)), MAX(DATE(`Datum/Zeit`)) FROM buchungen");
                if (rs.next()) {
                    if (startDate == null) {
                        startDate = rs.getString(1);
                    }
                    if (endDate == null) {
                        endDate = rs.getString(2);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        }

        tableModel.setRowCount(0);
        String query = "SELECT buchungen.ID, DATE(buchungen.`Datum/Zeit`) AS Datum, buchungen.Info, buchungen.Betrag, " +
                "kategorie.Bezeichnung AS Kategorie, kategorie.`Ein/Aus` " +
                "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID";

        if (startDate != null && endDate != null) {
            query += " WHERE DATE(`Datum/Zeit`) BETWEEN ? AND ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (startDate != null && endDate != null) {
                pstmt.setString(1, startDate);
                pstmt.setString(2, endDate);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getDate("Datum"));
                row.add(rs.getString("Info"));
                row.add(rs.getDouble("Betrag") + " €");
                row.add(rs.getString("Kategorie"));
                row.add(rs.getInt("Ein/Aus") == 1 ? "Einnahme" : "Ausgabe"); // Art basierend auf Ein/Aus festlegen
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateGewinnSumme(startDate, endDate);
    }

    void showFilterDialog() {
        JFrame filterFrame = new JFrame("Filter");
        filterFrame.setSize(300, 150);
        filterFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        String[] columns = {"ID", "Info", "Betrag", "Kategorie", "Art"};
        JComboBox<String> columnSelector = new JComboBox<>(columns);
        JTextField filterValueField = new JTextField();
        JButton applyFilterButton = new JButton("Anwenden");

        applyFilterButton.addActionListener(e -> {
            String selectedColumn = columnSelector.getSelectedItem().toString();
            String filterValue = filterValueField.getText();
            filterTableData(selectedColumn, filterValue);
            filterFrame.dispose();
        });

        panel.add(new JLabel("Spalte:"));
        panel.add(columnSelector);
        panel.add(new JLabel("Filterwert:"));
        panel.add(filterValueField);
        panel.add(new JLabel());
        panel.add(applyFilterButton);

        filterFrame.add(panel);
        filterFrame.setVisible(true);
    }

    private void filterTableData(String column, String filterValue) {
        tableModel.setRowCount(0);
        String query;

        if ("ID".equals(column) || "Betrag".equals(column)) {
            query = "SELECT buchungen.ID, DATE(buchungen.`Datum/Zeit`) AS Datum, buchungen.Info, buchungen.Betrag, " +
                    "kategorie.Bezeichnung AS Kategorie, kategorie.`Ein/Aus` " +
                    "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID WHERE " + column + " = ?";
        } else if ("Art".equals(column)) {
            int type = "Einnahme".equalsIgnoreCase(filterValue) ? 1 : 0;
            query = "SELECT buchungen.ID, DATE(buchungen.`Datum/Zeit`) AS Datum, buchungen.Info, buchungen.Betrag, " +
                    "kategorie.Bezeichnung AS Kategorie, kategorie.`Ein/Aus` " +
                    "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID WHERE kategorie.`Ein/Aus` = ?";
            filterValue = String.valueOf(type);
        } else if ("Kategorie".equals(column)) {
            query = "SELECT buchungen.ID, DATE(buchungen.`Datum/Zeit`) AS Datum, buchungen.Info, buchungen.Betrag, " +
                    "kategorie.Bezeichnung AS Kategorie, kategorie.`Ein/Aus` " +
                    "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID WHERE kategorie.Bezeichnung = ?";
        } else {
            query = "SELECT buchungen.ID, DATE(buchungen.`Datum/Zeit`) AS Datum, buchungen.Info, buchungen.Betrag, " +
                    "kategorie.Bezeichnung AS Kategorie, kategorie.`Ein/Aus` " +
                    "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID WHERE " + column + " LIKE ?";
            filterValue = "%" + filterValue + "%"; // Wildcard für LIKE-Filter
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, filterValue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getDate("Datum"));
                row.add(rs.getString("Info"));
                row.add(rs.getDouble("Betrag") + " €");
                row.add(rs.getString("Kategorie"));
                row.add(rs.getInt("Ein/Aus") == 1 ? "Einnahme" : "Ausgabe"); // Art basierend auf Ein/Aus festlegen
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDateRangeFilterDialog() {
        JFrame filterFrame = new JFrame("Zeitraum für Gewinnsumme");
        filterFrame.setSize(300, 150);
        filterFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        startDateField = new JTextField("YYYY-MM-DD");
        endDateField = new JTextField("YYYY-MM-DD");

        JButton applyButton = new JButton("Anwenden");
        applyButton.addActionListener(e -> {
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            loadTableData(startDate, endDate);
            filterFrame.dispose();
        });

        panel.add(new JLabel("Startdatum:"));
        panel.add(startDateField);
        panel.add(new JLabel("Enddatum:"));
        panel.add(endDateField);
        panel.add(new JLabel());
        panel.add(applyButton);

        filterFrame.add(panel);
        filterFrame.setVisible(true);
    }

    private void updateGewinnSumme(String startDate, String endDate) {
        double sum = calculateTotalGewinn(startDate, endDate);
        gewinnLabel.setText("Gewinnsumme: " + sum + " €");
    }

    private double calculateTotalGewinn(String startDate, String endDate) {
        double einnahmen = 0;
        double ausgaben = 0;
        String einnahmenQuery = "SELECT SUM(Betrag) FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID " +
                "WHERE kategorie.`Ein/Aus` = 1";
        String ausgabenQuery = "SELECT SUM(Betrag) FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID " +
                "WHERE kategorie.`Ein/Aus` = 0";

        if (startDate != null && endDate != null) {
            einnahmenQuery += " AND DATE(`Datum/Zeit`) BETWEEN ? AND ?";
            ausgabenQuery += " AND DATE(`Datum/Zeit`) BETWEEN ? AND ?";
        }

        try (PreparedStatement pstmtEinnahmen = connection.prepareStatement(einnahmenQuery);
             PreparedStatement pstmtAusgaben = connection.prepareStatement(ausgabenQuery)) {
            if (startDate != null && endDate != null) {
                pstmtEinnahmen.setString(1, startDate);
                pstmtEinnahmen.setString(2, endDate);
                pstmtAusgaben.setString(1, startDate);
                pstmtAusgaben.setString(2, endDate);
            }
            ResultSet rsEinnahmen = pstmtEinnahmen.executeQuery();
            if (rsEinnahmen.next()) {
                einnahmen = rsEinnahmen.getDouble(1);
            }

            ResultSet rsAusgaben = pstmtAusgaben.executeQuery();
            if (rsAusgaben.next()) {
                ausgaben = rsAusgaben.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return einnahmen - ausgaben;
    }

    void showAddBookingForm() {
        JFrame addFrame = new JFrame("Buchung hinzufügen");
        addFrame.setSize(300, 250);
        addFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField datumField = new JTextField("YYYY-MM-DD");
        JTextField infoField = new JTextField();
        JTextField betragField = new JTextField();

        JComboBox<String> categoryComboBox = new JComboBox<>(loadCategoryNames());

        JButton addButton = new JButton("Hinzufügen");
        addButton.addActionListener(e -> {
            try {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                int katID = getCategoryIdByName(selectedCategory);
                addBooking(
                        Date.valueOf(datumField.getText()),
                        infoField.getText(),
                        Double.parseDouble(betragField.getText()),
                        katID
                );
                addFrame.dispose();
                loadTableData(null, null); // Fix: Added null arguments to match method signature
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addFrame, "Fehler: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Datum (YYYY-MM-DD):"));
        panel.add(datumField);
        panel.add(new JLabel("Info:"));
        panel.add(infoField);
        panel.add(new JLabel("Betrag:"));
        panel.add(betragField);
        panel.add(new JLabel("Kategorie:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel());
        panel.add(addButton);

        addFrame.add(panel);
        addFrame.setVisible(true);
    }

    void addBooking(Date datum, String info, double betrag, int katID) {
        String query = "INSERT INTO buchungen (`Datum/Zeit`, Info, Betrag, KatID) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, datum);
            pstmt.setString(2, info);
            pstmt.setDouble(3, betrag);
            pstmt.setInt(4, katID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void showEditBookingForm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Zeile zum Bearbeiten aus.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        JFrame editFrame = new JFrame("Buchung bearbeiten");
        editFrame.setSize(300, 250);
        editFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField datumField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField infoField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        String betragString = tableModel.getValueAt(selectedRow, 3).toString().replace(" €", "");
        JTextField betragField = new JTextField(betragString);

        JComboBox<String> categoryComboBox = new JComboBox<>(loadCategoryNames());
        categoryComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 4));

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> {
            try {
                editBooking(id, Date.valueOf(datumField.getText()), infoField.getText(), Double.parseDouble(betragField.getText()), categoryComboBox.getSelectedItem().toString());
                editFrame.dispose();
                loadTableData(null, null); // Fix: Added null arguments to match method signature
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editFrame, "Fehler: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Datum (YYYY-MM-DD):"));
        panel.add(datumField);
        panel.add(new JLabel("Info:"));
        panel.add(infoField);
        panel.add(new JLabel("Betrag:"));
        panel.add(betragField);
        panel.add(new JLabel("Kategorie:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel());
        panel.add(saveButton);

        editFrame.add(panel);
        editFrame.setVisible(true);
    }

    String[] loadCategoryNames() {
        String query = "SELECT Bezeichnung FROM kategorie";
        Vector<String> categories = new Vector<>();

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(rs.getString("Bezeichnung"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories.toArray(new String[0]);
    }

    private int getCategoryIdByName(String categoryName) {
        String query = "SELECT ID FROM kategorie WHERE Bezeichnung = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    void editBooking(int id, Date datum, String info, double betrag, String kategorieName) {
        // Kategorie-ID anhand des Namens ermitteln
        int katID = getCategoryIdByName(kategorieName);

        if (katID == -1) {
            JOptionPane.showMessageDialog(this, "Kategorie nicht gefunden.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "UPDATE buchungen SET `Datum/Zeit` = ?, Info = ?, Betrag = ?, KatID = ? WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDate(1, datum);
            pstmt.setString(2, info);
            pstmt.setDouble(3, betrag);
            pstmt.setInt(4, katID);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void deleteSelectedBooking() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Zeile zum Löschen aus.");
            return;
        }

        Date datum = (Date) tableModel.getValueAt(selectedRow, 1);
        LocalDate entryDate = datum.toLocalDate();
        LocalDate currentDate = LocalDate.now();

        if (!entryDate.equals(currentDate)) {
            JOptionPane.showMessageDialog(this, "Sie können nur Einträge vom heutigen Datum löschen.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM buchungen WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadTableData(null, null); // Fix: Added null arguments to match method signature
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
