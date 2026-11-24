import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:bus_station.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String busesTable = "CREATE TABLE IF NOT EXISTS buses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "number_plate TEXT NOT NULL," +
                "model TEXT NOT NULL)";

        String driversTable = "CREATE TABLE IF NOT EXISTS drivers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL)";

        String tripsTable = "CREATE TABLE IF NOT EXISTS trips (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bus_id INTEGER," +
                "driver_id INTEGER," +
                "destination TEXT NOT NULL," +
                "departure_time TEXT," +
                "FOREIGN KEY (bus_id) REFERENCES buses(id)," +
                "FOREIGN KEY (driver_id) REFERENCES drivers(id))";

        Statement stmt = connection.createStatement();
        stmt.execute(busesTable);
        stmt.execute(driversTable);
        stmt.execute(tripsTable);

        stmt.execute("INSERT OR IGNORE INTO buses (id, number_plate, model) VALUES (1, 'А123БВ', 'Volvo B7')");
        stmt.execute("INSERT OR IGNORE INTO buses (id, number_plate, model) VALUES (2, 'В456ГД', 'MAN Lion')");
        stmt.execute("INSERT OR IGNORE INTO drivers (id, first_name, last_name) VALUES (1, 'Иван', 'Петров')");
        stmt.execute("INSERT OR IGNORE INTO drivers (id, first_name, last_name) VALUES (2, 'Алексей', 'Сидоров')");
    }

    // Методы для автобусов
    public ResultSet getBuses() throws SQLException {
        return connection.createStatement().executeQuery("SELECT id, number_plate, model FROM buses");
    }

    public void addBus(String numberPlate, String model) throws SQLException {
        String sql = "INSERT INTO buses (number_plate, model) VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, numberPlate);
        pstmt.setString(2, model);
        pstmt.executeUpdate();
    }

    public void updateBus(int id, String numberPlate, String model) throws SQLException {
        String sql = "UPDATE buses SET number_plate = ?, model = ? WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, numberPlate);
        pstmt.setString(2, model);
        pstmt.setInt(3, id);
        pstmt.executeUpdate();
    }

    public void deleteBus(int id) throws SQLException {
        String sql = "DELETE FROM buses WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    // Методы для водителей
    public ResultSet getDrivers() throws SQLException {
        return connection.createStatement().executeQuery("SELECT id, first_name, last_name FROM drivers");
    }

    public void addDriver(String firstName, String lastName) throws SQLException {
        String sql = "INSERT INTO drivers (first_name, last_name) VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.executeUpdate();
    }

    public void updateDriver(int id, String firstName, String lastName) throws SQLException {
        String sql = "UPDATE drivers SET first_name = ?, last_name = ? WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, firstName);
        pstmt.setString(2, lastName);
        pstmt.setInt(3, id);
        pstmt.executeUpdate();
    }

    public void deleteDriver(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    // Методы для рейсов
    public ResultSet getTrips() throws SQLException {
        String query = "SELECT t.id, b.number_plate, b.model, d.first_name || ' ' || d.last_name as driver, " +
                "t.destination, t.departure_time " +
                "FROM trips t " +
                "LEFT JOIN buses b ON t.bus_id = b.id " +
                "LEFT JOIN drivers d ON t.driver_id = d.id";
        return connection.createStatement().executeQuery(query);
    }

    public void addTrip(int busId, int driverId, String destination, String departureTime) throws SQLException {
        String sql = "INSERT INTO trips (bus_id, driver_id, destination, departure_time) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, busId);
        pstmt.setInt(2, driverId);
        pstmt.setString(3, destination);
        pstmt.setString(4, departureTime);
        pstmt.executeUpdate();
    }

    public void updateTrip(int id, int busId, int driverId, String destination, String departureTime) throws SQLException {
        String sql = "UPDATE trips SET bus_id = ?, driver_id = ?, destination = ?, departure_time = ? WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, busId);
        pstmt.setInt(2, driverId);
        pstmt.setString(3, destination);
        pstmt.setString(4, departureTime);
        pstmt.setInt(5, id);
        pstmt.executeUpdate();
    }

    public void deleteTrip(int id) throws SQLException {
        String sql = "DELETE FROM trips WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }
}
