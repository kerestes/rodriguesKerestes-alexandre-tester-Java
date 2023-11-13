package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearAllCaches;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.service.InteractiveShell;

import nl.altindag.log.LogCaptor;

public class ParkingSpotTest {

    @AfterAll
    private static void clearBufferRead(){
        clearAllCaches();
    }

    @Test
    public void getNextParkingNumberIfAvailable(){
        try(MockedStatic<InteractiveShell> mock = mockStatic(InteractiveShell.class)){

            when(InteractiveShell.getVehichleType()).thenThrow(new IllegalArgumentException("Entered input is invalid"));

            LogCaptor log = LogCaptor.forClass(ParkingSpot.class);

            ParkingSpot.getNextParkingNumberIfAvailable();

            assertTrue(log.getLogs().contains("Error parsing user input for type of vehicle"));

        }
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
