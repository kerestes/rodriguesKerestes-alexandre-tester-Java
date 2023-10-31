package com.parkit.parkingsystem.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ParkingSpotIT {

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @AfterAll
    private static void clearBufferRead(){
        //System.setIn(System.in);
    }

    @Test
    @Disabled
    public void fullParkingSpot(){

        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.TURN_OFF_PARKING_PLACE_TEST);
            ps.execute();
            dataBaseConfig.closeConnection(con);

            LogCaptor logCaptor = LogCaptor.forClass(ParkingSpot.class);

            System.setIn(new ByteArrayInputStream("1".getBytes()));

            ParkingSpot.getNextParkingNumberIfAvailable();

            assertTrue(logCaptor.getLogs().toString().contains("Error fetching next available parking slot"));

            con = dataBaseConfig.getConnection();
            ps = con.prepareStatement(DBConstants.TURN_ON_PARKING_PLACE_TEST);
            ps.execute();
            dataBaseConfig.closeConnection(con);

        } catch (ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
