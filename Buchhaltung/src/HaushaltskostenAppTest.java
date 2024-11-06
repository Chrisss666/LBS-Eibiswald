import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class HaushaltskostenAppTest {

    private HaushaltskostenApp app;

    @BeforeEach
    public void setUp() {
        app = new HaushaltskostenApp();
        try {
            String url = "jdbc:mysql://localhost:3306/buchhaltung";
            String user = "java";
            String password = "java";
            // Using reflection to set the private connection field
            java.lang.reflect.Field connectionField = HaushaltskostenApp.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            connectionField.set(app, DriverManager.getConnection(url, user, password));
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() {
        app.dispose();
    }

    // Frontend Tests
    @Test
    public void testShowAddBookingForm() {
        assertDoesNotThrow(() -> app.showAddBookingForm(), "ShowAddBookingForm should not throw an exception.");
    }

    @Test
    public void testShowEditBookingForm() {
        JTable table = app.table;
        table.setRowSelectionInterval(0, 0); // Select the first row
        assertDoesNotThrow(() -> app.showEditBookingForm(), "ShowEditBookingForm should not throw an exception.");
    }

    @Test
    public void testDeleteSelectedBookingButtonWithoutSelection() {
        app.table.clearSelection();
        assertDoesNotThrow(() -> app.deleteSelectedBooking(), "DeleteSelectedBooking should not throw an exception even without selection.");
    }

    @Test
    public void testDeleteSelectedBookingButtonWithSelection() {
        JTable table = app.table;
        table.setRowSelectionInterval(0, 0); // Select the first row
        assertDoesNotThrow(() -> app.deleteSelectedBooking(), "DeleteSelectedBooking should not throw an exception with a selection.");
    }

    @Test
    public void testFilterDialogShowsWithoutError() {
        assertDoesNotThrow(() -> app.showFilterDialog(), "FilterDialog should open without throwing an exception.");
    }

    // Backend Tests
    @Test
    public void testConnectDatabase() {
        try {
            java.lang.reflect.Field connectionField = HaushaltskostenApp.class.getDeclaredField("connection");
            connectionField.setAccessible(true);
            Connection connection = (Connection) connectionField.get(app);
            assertNotNull(connection, "Connection should be established.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access connection field: " + e.getMessage());
        }
    }

    @Test
    public void testAddBookingToDatabase() {
        Timestamp timestamp = Timestamp.valueOf("2024-10-10 12:00:00");
        String info = "Test Buchung";
        double betrag = 50.0;
        int katID = 1;

        assertDoesNotThrow(() -> app.addBooking(timestamp, info, betrag, katID), "AddBooking should not throw an exception.");
    }

    @Test
    public void testEditBookingInDatabase() {
        int id = 1;
        Timestamp timestamp = Timestamp.valueOf("2024-10-15 12:00:00");
        String info = "Bearbeitete Buchung";
        double betrag = 75.0;
        String kategorieName = "Lebensmittel";

        assertDoesNotThrow(() -> app.editBooking(id, timestamp, info, betrag, kategorieName), "EditBooking should not throw an exception.");
    }

    @Test
    public void testDeleteBookingFromDatabase() {
        int id = 1;
        String query = "DELETE FROM buchungen WHERE ID = ?";

        try (PreparedStatement pstmt = app.connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            assertDoesNotThrow(() -> pstmt.executeUpdate(), "DeleteBooking should not throw an exception.");
        } catch (SQLException e) {
            fail("SQL Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testLoadTableData() {
        assertDoesNotThrow(() -> app.loadTableData(), "LoadTableData should not throw an exception.");
    }
}