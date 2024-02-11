package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.FlightSystems;
import org.example.database.interfaces.IDB;
import org.example.database.interfaces.PostgresDB;
import org.mindrot.jbcrypt.BCrypt;

@Getter
@NoArgsConstructor
@Setter


public class Person {
    private static int id_gen = 1;
    private int passport_id;
    private String name;
    private String surname;
    private int age;
    private String phone_number;
    private String password;
    IDB db = new PostgresDB();
    public Person(String name, String surname, int age, String phone_number, String password) {
       setName(name);
       setSurname(surname);
       setAge(age);
       setPhone_number(phone_number);
       setId();
       setPassword(hashPassword(password));
    }
    public void CreatePassenger(){
        FlightSystems flightSystems = new FlightSystems(db);
        flightSystems.createPassenger(getName(), getSurname(), getAge(), getPhone_number(), getPassword());
    }
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    public boolean authenticate(String password, String phone_number){
        FlightSystems flightSystems = new FlightSystems(db);
        String hashedPassword = flightSystems.getPassword(phone_number);
        if (hashedPassword != null) {
            return verifyPassword(password, hashedPassword);
        }
        else{
            return false;
        }
    }
    public void setId() {
        this.passport_id = id_gen++;
    }

}
