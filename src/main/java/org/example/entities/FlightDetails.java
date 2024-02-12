package org.example.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.FlightSystems;
import org.example.database.interfaces.IDB;
import org.example.database.interfaces.PostgresDB;

import java.sql.Timestamp;
@NoArgsConstructor
@Setter @Getter
public class FlightDetails{
    private static int id_gen = 1;
    private int flight_id;
    private String flight_number;
    private String departure_location;
    private String destination_location;
    private Timestamp departure_time;
    private Timestamp arrival_time;
    private int available_seats;
    IDB db = new PostgresDB();
    public FlightDetails(String flight_number, String from, String to, Timestamp from_time, Timestamp to_time, int seats){
        setId();
        setFlight_number(flight_number);
        setDeparture_location(from);
        setDestination_location(to);
        setDeparture_time(from_time);
        setArrival_time(to_time);
        setAvailable_seats(seats);
    }

    public void setId(){
        this.flight_id = id_gen++;
    }

    public void addFlightDetails() {
        FlightSystems flightSystems = new FlightSystems(db);
        flightSystems.addFlightDetails(getFlight_number(), getDeparture_location(), getDestination_location(), getDeparture_time(), getArrival_time(), getAvailable_seats());
    }
}