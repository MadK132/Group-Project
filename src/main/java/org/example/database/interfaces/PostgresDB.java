package org.example.database.interfaces;

import java.sql.*;

public class PostgresDB implements IDB {
    @Override
    public Connection getConnection() {
        Connection con = null;
        String URL = "jdbc:postgresql://localhost:5432/airport_management";
        String USERNAME = "postgres";
        String PASSWORD = "1234";
        try {
            // Here we load the driver’s class file into memory at the runtime
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            return con;
        } catch (Exception e) {
            System.out.println("failed to connect to postgres: " + e.getMessage());
            return null;
        }
    }
}
