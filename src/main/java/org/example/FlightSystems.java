package org.example;

import org.example.database.interfaces.IDB;
import org.example.entities.Person;

import java.sql.*;
import java.util.Scanner;


public class FlightSystems {
    private Connection connection = null;
    private final IDB db;
    public FlightSystems(IDB db){
       this.db = db;
    }

    //create account for passenger
    public void createPassenger(String firstName, String lastName, int age, String phoneNumber, String hashed_password) {
        try {
            connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO passenger (first_name, last_name, age, phone_number, password) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setInt(3, age);
            statement.setString(4, phoneNumber);
            statement.setString(5, hashed_password);
            statement.executeUpdate();
            System.out.println("Passenger created successfully.");
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
    //getting hashed password from passengers account
    public String getPassword(String phone_number){
        try{
            connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT password FROM passenger WHERE phone_number = ?");
            statement.setString(1, phone_number);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("password");
            }
        }catch (SQLException e){
            System.out.println("Something went wrong: " + e.getMessage());
        }
        return null;
    }

}
