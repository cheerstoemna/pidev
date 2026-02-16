package test;

import utils.MyDB;

import java.sql.Connection;

public class testDB {
    public static void main(String[] args) {

        // Get the connection
        Connection cnx = MyDB.getInstance().getConnection();

        if (cnx != null) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
        }
    }
}