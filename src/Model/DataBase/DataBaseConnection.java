package Model.DataBase;

import java.sql.*;
import java.util.Hashtable;

public class DataBaseConnection {

    private final Connection connection;

    public static final String MYSQL = "MySql";

    private static final Hashtable<String, String> drivers;

    static {
        drivers = new Hashtable<>();
        drivers.put(MYSQL, "com.mysql.cj.jdbc.Driver");
    }

    public DataBaseConnection(String type, String server, String dbName, String user, String password) throws Exception {
        Class.forName(drivers.get(type));

        String url;
        if (type.equals(MYSQL))
        {
            url = "jdbc:mysql://" + server + "/" + dbName;
        }
        else
        {
            throw new Exception("Type missing");
        }

        connection = DriverManager.getConnection(url, user, password);
    }

    public synchronized ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public synchronized int executeUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeUpdate(sql);
    }

    public Connection getConnection() {
        return connection;
    }

    public synchronized void close() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}