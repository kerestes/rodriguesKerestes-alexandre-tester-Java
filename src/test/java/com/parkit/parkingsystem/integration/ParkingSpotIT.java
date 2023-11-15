package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.integration.constants.DBConstantsTest;
import org.junit.jupiter.api.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParkingSpotIT {

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();
    @AfterEach
    private void freeSystemOut(){
        System.setIn(System.in);
        System.setOut(System.out);
    }
    @AfterAll
    private static void clearBufferRead(){
        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    public void fullParkingSpot(){

        System.setIn(new ByteArrayInputStream("1".getBytes()));

        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstantsTest.TURN_OFF_PARKING_PLACE_TEST);
            ps.execute();
            dataBaseConfig.closeConnection(con);

            LogCaptor logCaptor = LogCaptor.forClass(ParkingSpot.class);

            ParkingSpot.getNextParkingNumberIfAvailable();

            assertTrue(logCaptor.getLogs().toString().contains("Error fetching next available parking slot"));

            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstantsTest.TURN_ON_PARKING_PLACE_TEST);
            ps.execute();
            dataBaseConfig.closeConnection(con);

        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void getNextParkingAvailableIT(){

        System.setIn(new ByteArrayInputStream("1".getBytes()));

        ParkingSpot parkingSpot = ParkingSpot.getNextParkingNumberIfAvailable();

        assertEquals(1, parkingSpot.getId());
    }
}
