import java.sql.*;

public class EmployeeData {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/javatest";
        String user = "java";
        String password = "java";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Verbindung zur Datenbank erfolgreich!");

            stmt = conn.createStatement();
            String query = "SELECT * FROM EMP";
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                int empNo = rs.getInt("EMPNO");
                String eName = rs.getString("ENAME");
                String job = rs.getString("JOB");
                int mgr = rs.getInt("MGR");
                Date hireDate = rs.getDate("HIREDATE");
                double sal = rs.getDouble("SAL");
                double comm = rs.getDouble("COMM");
                int deptNo = rs.getInt("DEPTNO");

                System.out.println("Mitarbeiter-Nr: " + empNo + ", Name: " + eName + ", Job: " + job +
                        ", Manager: " + mgr + ", Einstellungsdatum: " + hireDate +
                        ", Gehalt: " + sal + ", Provision: " + comm + ", Abteilungs-Nr: " + deptNo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
