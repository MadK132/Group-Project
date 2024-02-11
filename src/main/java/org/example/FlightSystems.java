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
        }
        catch (SQLException e) {
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
        }
        catch (SQLException e){
            System.out.println("Something went wrong: " + e.getMessage());
        }
        return null;
    }
    public void makeReservation(String flightNumber, String phone_number) throws SQLException {
        connection = db.getConnection();
        int reservationId = -1;
        int flightId = getFlightIdByFlightNumber(flightNumber);
        int passengerId = getPassengerIdByPassengerName(phone_number);
        if (flightId != -1 && passengerId != -1) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO reserve (flight_id, passenger_id, reserve_date) VALUES (?, ?, CURRENT_TIMESTAMP) RETURNING reserve_id");
                statement.setInt(1, flightId);
                statement.setInt(2, passengerId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    reservationId = resultSet.getInt("reserve_id");
                }
                PreparedStatement statementUpdate = connection.prepareStatement("UPDATE flightdetails SET available_seats = flightdetails.available_seats - 1 WHERE flight_id = ?");
                statementUpdate.setInt(1, flightId);
                statementUpdate.executeUpdate();
                System.out.println("Reservation made successfully. Your reservation ID is: " + reservationId);
            }
            catch (SQLException e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
        else {
            System.out.println("Flight or passenger not found.");
        }
    }
// Method to get the flight ID by flight numberpublic
    int getFlightIdByFlightNumber(String flightNumber) throws SQLException {
    connection = db.getConnection();
    int flightId = -1;
    try {
        PreparedStatement statement = connection.prepareStatement("SELECT flight_id FROM FlightDetails WHERE flight_number = ?");
        statement.setString(1, flightNumber);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            flightId = resultSet.getInt("flight_id");
        }
    }
    catch (SQLException e) {
        System.out.println("Something went wrong: " + e.getMessage());
    }
    return flightId;
    }
// Method to get the passenger ID by passenger phone number
    public int getPassengerIdByPassengerName(String phone_number) throws SQLException {
        connection = db.getConnection();
    int passengerId = -1;
    try {
        PreparedStatement statement = connection.prepareStatement("SELECT passenger_id FROM Passenger WHERE phone_number = ?");
        statement.setString(1, phone_number);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            passengerId = resultSet.getInt("passenger_id");        }
    }
    catch (SQLException e) {
        System.out.println("Something went wrong: " + e.getMessage());
    }
    return passengerId;
}
    public void addFlightDetails(String flightnumber, String departure_location, String destination_location, Timestamp departure_time, Timestamp arrival_time, int available_seats) {
        try {        connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO flightdetails (flight_number, departure_location, destination_location, departure_time, arrival_time, available_seats) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, flightnumber);
            statement.setString(2, departure_location);        statement.setString(3, destination_location);
            statement.setTimestamp(4, departure_time);        statement.setTimestamp(5, arrival_time);
            statement.setInt(6, available_seats);        statement.executeUpdate();
        }
        catch (SQLException e){
            System.out.println("Something went Wrong" + e.getMessage());
        }
    }
    //delete flights
    public void deleteFlights(String flightNumber) {
        try {
        connection = db.getConnection();
        // First, delete reservations related to the flight
        PreparedStatement deleteReservationsStatement = connection.prepareStatement("DELETE FROM Reserve WHERE flight_id IN (SELECT flight_id FROM FlightDetails WHERE flight_number = ?)");        deleteReservationsStatement.setString(1, flightNumber);
        int reservationsDeleted = deleteReservationsStatement.executeUpdate();
        // Then, delete the flight from the FlightDetails table
        PreparedStatement deleteFlightStatement = connection.prepareStatement("DELETE FROM FlightDetails WHERE flight_number = ?");
        deleteFlightStatement.setString(1, flightNumber);
        int flightsDeleted = deleteFlightStatement.executeUpdate();
        if (flightsDeleted > 0) {
            System.out.println("Deleted flight " + flightNumber + ".");
        }
        else {
            System.out.println("Flight " + flightNumber + " not found.");
        }
        }
        catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
    public void displayAvailableFlights() {
        try {
            connection = db.getConnection();
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM flightdetails");
                while (resultSet.next()) {
                    System.out.println("Flight: " + resultSet.getString("flight_number") +
                            ", Departure: " + resultSet.getString("departure_location") +
                            ", Destination: " + resultSet.getString("destination_location") +
                            ", Departure Time: " + resultSet.getTimestamp("departure_time") +
                            ", Arrival Time: " + resultSet.getTimestamp("arrival_time") +
                            ", Available Seats: " + resultSet.getInt("available_seats"));
                }
            } else {
                System.out.println("Connection to database is null.");
            }
        } catch (SQLException e) {
            System.out.println("Something went Wrong" + e.getMessage());
        }
    }
    public void changeSeats(String flightNumber, int availableSeats) {
        try {
            connection = db.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE FlightDetails SET available_seats = ? WHERE flight_number = ?");
            statement.setInt(1, availableSeats);
            statement.setString(2, flightNumber);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Flight '" + flightNumber + "' not found in the database.");
            }
            else {
                System.out.println("Available seats updated successfully.");
            }
        }
        catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
}
