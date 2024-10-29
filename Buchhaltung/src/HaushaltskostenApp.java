import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class HaushaltskostenApp extends JFrame {
    private Connection connection;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField dateFilterField;

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
        tableModel = new DefaultTableModel(new String[]{"ID", "Datum", "Info", "Betrag", "Kategorie"}, 0);
        table = new JTable(tableModel);
        loadTableData();

        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        // Interaktionsfeld rechts
        JPanel interactionPanel = new JPanel();
        interactionPanel.setLayout(new BoxLayout(interactionPanel, BoxLayout.Y_AXIS));
        JButton addButton = new JButton("Hinzufügen");
        JButton editButton = new JButton("Bearbeiten");
        JButton deleteButton = new JButton("Entfernen");

        addButton.addActionListener(e -> showAddBookingForm());
        editButton.addActionListener(e -> showEditBookingForm());
        deleteButton.addActionListener(e -> deleteSelectedBooking());

        interactionPanel.add(addButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(editButton);
        interactionPanel.add(Box.createVerticalStrut(10));
        interactionPanel.add(deleteButton);

        add(interactionPanel, BorderLayout.EAST);

        // Filteroptionen unten
        JPanel filterPanel = new JPanel(new FlowLayout());
        dateFilterField = new JTextField(10);
        JButton filterButton = new JButton("Filtern");

        filterButton.addActionListener(e -> filterTableData());
        filterPanel.add(new JLabel("Filter nach Datum (YYYY-MM-DD):"));
        filterPanel.add(dateFilterField);
        filterPanel.add(filterButton);

        add(filterPanel, BorderLayout.SOUTH);
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

    private void loadTableData() {
        tableModel.setRowCount(0);
        String query = "SELECT buchungen.ID, buchungen.`Datum/Zeit`, buchungen.Info, buchungen.Betrag, kategorie.Bezeichnung AS Kategorie " +
                "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getTimestamp("Datum/Zeit"));
                row.add(rs.getString("Info"));
                row.add(rs.getDouble("Betrag") + " €");
                row.add(rs.getString("Kategorie"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterTableData() {
        String dateFilter = dateFilterField.getText();
        tableModel.setRowCount(0);
        String query = "SELECT buchungen.ID, buchungen.`Datum/Zeit`, buchungen.Info, buchungen.Betrag, kategorie.Bezeichnung AS Kategorie " +
                "FROM buchungen INNER JOIN kategorie ON buchungen.KatID = kategorie.ID WHERE DATE(`Datum/Zeit`) = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, dateFilter);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("ID"));
                row.add(rs.getTimestamp("Datum/Zeit"));
                row.add(rs.getString("Info"));
                row.add(rs.getDouble("Betrag"));
                row.add(rs.getString("Kategorie"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAddBookingForm() {
        JFrame addFrame = new JFrame("Buchung hinzufügen");
        addFrame.setSize(300, 250);
        addFrame.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField datumField = new JTextField();
        JTextField infoField = new JTextField();
        JTextField betragField = new JTextField();
        JTextField katIDField = new JTextField();

        JButton addButton = new JButton("Hinzufügen");
        addButton.addActionListener(e -> {
            try {
                addBooking(
                        Timestamp.valueOf(datumField.getText()),
                        infoField.getText(),
                        Double.parseDouble(betragField.getText()),
                        Integer.parseInt(katIDField.getText())
                );
                addFrame.dispose();
                loadTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addFrame, "Fehler: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Datum (YYYY-MM-DD HH:MM:SS):"));
        panel.add(datumField);
        panel.add(new JLabel("Info:"));
        panel.add(infoField);
        panel.add(new JLabel("Betrag:"));
        panel.add(betragField);
        panel.add(new JLabel("Kategorie-ID:"));
        panel.add(katIDField);
        panel.add(new JLabel());
        panel.add(addButton);

        addFrame.add(panel);
        addFrame.setVisible(true);
    }

    private void addBooking(Timestamp datumZeit, String info, double betrag, int katID) {
        String query = "INSERT INTO buchungen (`Datum/Zeit`, Info, Betrag, KatID) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, datumZeit);
            pstmt.setString(2, info);
            pstmt.setDouble(3, betrag);
            pstmt.setInt(4, katID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showEditBookingForm() {
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
        JTextField betragField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField katIDField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());

        JButton saveButton = new JButton("Speichern");
        saveButton.addActionListener(e -> {
            try {
                editBooking(id, Timestamp.valueOf(datumField.getText()), infoField.getText(), Double.parseDouble(betragField.getText()), Integer.parseInt(katIDField.getText()));
                editFrame.dispose();
                loadTableData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editFrame, "Fehler: " + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("Datum (YYYY-MM-DD HH:MM:SS):"));
        panel.add(datumField);
        panel.add(new JLabel("Info:"));
        panel.add(infoField);
        panel.add(new JLabel("Betrag:"));
        panel.add(betragField);
        panel.add(new JLabel("Kategorie-ID:"));
        panel.add(katIDField);
        panel.add(new JLabel());
        panel.add(saveButton);

        editFrame.add(panel);
        editFrame.setVisible(true);
    }

    private void editBooking(int id, Timestamp datumZeit, String info, double betrag, int katID) {
        String query = "UPDATE buchungen SET `Datum/Zeit` = ?, Info = ?, Betrag = ?, KatID = ? WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setTimestamp(1, datumZeit);
            pstmt.setString(2, info);
            pstmt.setDouble(3, betrag);
            pstmt.setInt(4, katID);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedBooking() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Zeile zum Löschen aus.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
        String query = "DELETE FROM buchungen WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            loadTableData();
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