package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDB {

    private static MyDB instance;
    private Connection connection;

    private final String URL = "jdbc:mysql://localhost:3306/mindnest";
    private final String USER = "root"; // change if needed
    private final String PASSWORD = ""; // change if needed

    private MyDB() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("DB Connected ✔");
        } catch (SQLException e) {
            System.out.println("DB Connection failed ❌");
            e.printStackTrace();
        }
    }

    public static MyDB getInstance() {
        if (instance == null) {
            instance = new MyDB();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
