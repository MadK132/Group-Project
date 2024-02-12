package org.example.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.FlightSystems;
import org.example.database.interfaces.IDB;
import org.example.database.interfaces.PostgresDB;
import java.sql.SQLException;

@Getter @Setter
@NoArgsConstructor

public class BoardingPass {
    private static int id_gen = 1;
    private int reserve_id;
    private String phone_number;
    private String flight_number;
    IDB db = new PostgresDB();

    public BoardingPass(String phone_number, String flight_number){
        setReserve_id();
        setFlight_number(flight_number);
        setPhone_number(phone_number);
    }

    public void MakeReservation() {
        FlightSystems flightSystems = new FlightSystems(db);
        flightSystems.makeReservation(getFlight_number(), getPhone_number());
    }

    public void setReserve_id() {
        this.reserve_id = id_gen++;
    }
}
