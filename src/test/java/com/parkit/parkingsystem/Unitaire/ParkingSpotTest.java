package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

public class ParkingSpotTest {

    

    @BeforeAll
    public static void initStdIn(){
        System.setIn(new ByteArrayInputStream("0".getBytes()));
    }

    @AfterAll
    private static void clearBufferRead(){
        //System.setIn(System.in);
    }

    @Test
    public void getNextParkingNumberIfAvailable(){
        LogCaptor logCaptor = LogCaptor.forClass(ParkingSpot.class);

        ParkingSpot.getNextParkingNumberIfAvailable();
            
        assertTrue(logCaptor.getLogs().toString().contains("Error parsing user input for type of vehicle"));

    }

    @Test
    public void equalsTrueParkingSpotTest(){
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.BIKE, true);

        assertTrue(parkingSpot.equals(parkingSpot2));
    }

    @Test
    public void equalsFalseParkingSpotTest(){
        ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, false);
        ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.CAR, false);

        assertFalse(parkingSpot.equals(parkingSpot2));
    }
}
