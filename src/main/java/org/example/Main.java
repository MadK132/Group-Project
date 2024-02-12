package org.example;
import org.example.database.interfaces.IDB;
import org.example.database.interfaces.PostgresDB;
import org.example.entities.BoardingPass;
import org.example.entities.FlightDetails;
import org.example.entities.Person;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        IDB db = new PostgresDB();
        Scanner sc = new Scanner(System.in);
        FlightSystems system = new FlightSystems(db);
        Person passenger = new Person();
        boolean registered = false;
        system.deleteExpiredDate();
        while (!registered) {
            System.out.println("Welcome to the Flight Reservation System!");
            System.out.print("Do you have an account already? (y/n): ");
            String hasAccount = sc.next().toLowerCase();

            if (hasAccount.equals("y")) {
                // Existing user login
                System.out.print("Enter your phone number: ");
                String phone_number = sc.next();
                System.out.print("Enter your password: ");
                String password = sc.next();

                // Check if user exists in the database, perform authentication.
                if (passenger.authenticate(password, phone_number)) {
                    System.out.println("Login successful. Welcome back!");
                    registered = true;
                } else {
                    System.out.println("Invalid phone number or password. Please try again.");
                }
            } else if (hasAccount.equals("n")) {
                // New user registration
                boolean validPhoneNumber = false;
                String phone_number = "";
                while (!validPhoneNumber) {
                    System.out.print("Enter your phone number, or type 'b' to go back: ");
                    String input = sc.next();
                    if (input.equalsIgnoreCase("b")) {
                        break; // Go back to the previous step
                    } else {
                        phone_number = input;
                        // Check if the phone number already exists in the database.
                        if (system.checkExistingUser(phone_number)) {
                            System.out.println("Phone number already exists. Please login or use a different number.");
                        } else {
                            validPhoneNumber = true;
                        }
                    }
                }

                // If validPhoneNumber is true, proceed with registration
                if (validPhoneNumber) {
                    // Registration process
                    System.out.print("Enter your first name: ");
                    String firstName = sc.next();
                    System.out.print("Enter your last name: ");
                    String lastName = sc.next();
                    System.out.print("Enter your age: ");
                    int age = sc.nextInt();
                    System.out.print("Enter your password: ");
                    String password = sc.next();

                    passenger = new Person(firstName, lastName, age, phone_number, password);
                    passenger.CreatePassenger();
                    System.out.println("Registration successful. Welcome, " + firstName + "!");
                    registered = true;
                }
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }


        boolean running = true;
        //options
        while (running) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Flight Details");
            System.out.println("2. Make Reservation");
            System.out.println("3. View Reservation History");
            System.out.println("4. Delete Flight Details");
            System.out.println("5. Change number of seats in Flight Details");
            System.out.println("6. Display all flight details");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter flight number: ");
                    String flightNumber = sc.next();
                    System.out.print("Enter departure location: ");
                    String departureLocation = sc.next();
                    System.out.print("Enter destination location: ");
                    String destinationLocation = sc.next();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setLenient(false);
                    System.out.print("Enter departure time (YYYY-MM-DD HH:mm:ss):");
                    sc.nextLine();
                    String departureTimeString = sc.nextLine();
                    Timestamp departureTime = null;
                    try {
                        departureTime = new Timestamp(dateFormat.parse(departureTimeString).getTime());
                    } catch (ParseException e) {
                        System.out.println("Invalid date format or out of range. Please use the format YYYY-MM-DD HH:mm:ss.");
                        break;
                    }
                    System.out.print("Enter arrival time (YYYY-MM-DD HH:mm:ss):");
                    String arrivalTimeString = sc.nextLine();
                    Timestamp arrivalTime = null;
                    try {
                        arrivalTime = new Timestamp(dateFormat.parse(arrivalTimeString).getTime());
                    } catch (ParseException e) {
                        System.out.println("Invalid date format or out of range. Please use the format YYYY-MM-DD HH:mm:ss.");
                        break;
                    }
                    System.out.print("Enter available seats: ");
                    int availableSeats = sc.nextInt();

                    FlightDetails flightDetails = new FlightDetails(flightNumber, departureLocation, destinationLocation, departureTime, arrivalTime, availableSeats);
                    flightDetails.addFlightDetails(); // inserting to database flight details.
                    break;
                case 2:
                    System.out.print("Enter your phone number: ");
                    String phone_number = sc.next();
                    System.out.print("Enter your Flight Number: ");
                    String flight_number = sc.next();
                    BoardingPass boardingPass = new BoardingPass(phone_number, flight_number);
                    boardingPass.MakeReservation(); //making reservation by user into available flights.
                    break;
                case 3:
                    System.out.println("Enter your Reservation ID: ");
                    int reserveID = sc.nextInt();
                    system.viewReservationHistory(reserveID); //to view reservation data.
                    break;
                case 4:
                    System.out.println("Enter Flight Number to delete: ");
                    String flightnum = sc.next();
                    system.deleteFlights(flightnum); //delete flight details from database.
                    break;
                case 5:
                    System.out.println("Enter Flight Number to change: ");
                    String flightnumber = sc.next();
                    System.out.println("Number of seats you want to change: ");
                    int seats = sc.nextInt();
                    system.changeSeats(flightnumber, seats); //to change seat number in flights.
                    break;
                case 6:
                    system.displayAvailableFlights(); //display all available flights.
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }

        sc.close();
    }
}
