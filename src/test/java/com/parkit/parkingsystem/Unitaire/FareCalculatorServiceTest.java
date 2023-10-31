package com.parkit.parkingsystem.Unitaire;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;
    private Date inTime;
    private Date outTime;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
        inTime = new Date();
        outTime = new Date();
    }

    @Test
    public void calculateFareCar(){
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.CAR_RATE_PER_HOUR);

        assertEquals(price, ticket.getPrice());
    }

    @Test
    public void calculateFareBike(){    
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.BIKE_RATE_PER_HOUR);

        assertEquals(price, ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType(){
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThan30MinutesParkingTime(){
        inTime.setTime( System.currentTimeMillis() - (  15 * 60 * 1000) );//15 minutes parking  
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThan30MinutesParkingTime(){
        inTime.setTime( System.currentTimeMillis() - (  15 * 60 * 1000) ); //15 minutes parking
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        
        assertEquals(0, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourAndMoreThan30MinutesParkingTime(){
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.BIKE_RATE_PER_HOUR);

        assertEquals(price, ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourAndMoreThan30MinutesParkingTime(){
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) ); //45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.CAR_RATE_PER_HOUR);

        assertEquals(price , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.CAR_RATE_PER_HOUR);

        assertEquals(price , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithDiscount(){
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscount(true);

        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.CAR_RATE_PER_HOUR);

        assertEquals(price * 0.95, ticket.getPrice());
    }

    @Test
    public void calculateFareBikeWithDiscount(){
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscount(true);
        
        fareCalculatorService.calculateFare(ticket);

        double price = getPrice(Fare.BIKE_RATE_PER_HOUR);

        assertEquals(price * 0.95, ticket.getPrice());
    }

    @Test
    public void calculateFareWithUnkonwVehicleType(){
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.UNKOWN,false);


        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    private double getPrice(double rate_per_hour){
        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        double duration = (outHour - inHour);
        duration /= 3600000;
        return duration * rate_per_hour;

    }
}
