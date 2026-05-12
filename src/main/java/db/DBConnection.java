package db;

import java.sql.Connection;
import utils.MyDB;

public class DBConnection {

    public static Connection getConnection() {
        return MyDB.getInstance().getConnection();
    }
}
