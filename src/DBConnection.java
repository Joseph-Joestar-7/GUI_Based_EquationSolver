import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/EquationSolverDB?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "982005"; 

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useSSL=false", USER, PASSWORD);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS EquationSolverDB");
            stmt.close();
            conn.close();

            Connection dbConn = getConnection();
            Statement st = dbConn.createStatement();

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(255) NOT NULL, "
                    + "email VARCHAR(255) UNIQUE NOT NULL, "
                    + "password VARCHAR(255) NOT NULL)";
            st.executeUpdate(createUsersTable);

            String createHistoryTable = "CREATE TABLE IF NOT EXISTS history ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "equation TEXT, "
                    + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)";
            st.executeUpdate(createHistoryTable);
            st.close();
            dbConn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
