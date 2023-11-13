package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.config.WrongDataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

public class ParkingSpotDAOIT {

    private DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private WrongDataBaseTestConfig wrongDataBaseTestConfig = new WrongDataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;
    private ParkingSpotDAO parkingSpotDAO;

    @BeforeAll
    public static void initDB(){
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    public void init(){
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void getNextAvailableSlotCarTest(){
        int parkingSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        assertEquals(1, parkingSpot);
    }

    @Test
    public void getNextAvailableSlotBikeTest(){
        int parkingSpot = parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

        assertEquals(4, parkingSpot);
    }

    @Test
    public void updateParkingCarTest(){

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        boolean updateParking = parkingSpotDAO.updateParking(parkingSpot);

        assertTrue(updateParking);
    }

    @Test
    public void updateParkingBikeTest(){
        ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.BIKE, false);

        boolean updateParking = parkingSpotDAO.updateParking(parkingSpot);

        assertTrue(updateParking);
    }

    @Test
    public void updateParkingUnknowSpotTest(){

        ParkingSpot parkingSpot = new ParkingSpot(10, ParkingType.CAR, false);

        boolean updateParking = parkingSpotDAO.updateParking(parkingSpot);

        assertFalse(updateParking);
    }

    @Test
    public void wrongConnectiongetNextAvailableSlotTest(){

        LogCaptor logCaptor = LogCaptor.forClass(ParkingSpotDAO.class);

        parkingSpotDAO.dataBaseConfig = wrongDataBaseTestConfig;

        parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE);

        assertTrue(logCaptor.getLogs().contains("Error fetching next available slot"));

    }

    @Test
    public void wrongConnectionUpdateParkingTest(){

        LogCaptor logCaptor = LogCaptor.forClass(ParkingSpotDAO.class);

        parkingSpotDAO.dataBaseConfig = wrongDataBaseTestConfig;

        ParkingSpot parkingSpot = new ParkingSpot(10, ParkingType.CAR, false);

        parkingSpotDAO.updateParking(parkingSpot);
        
        assertTrue(logCaptor.getLogs().contains("Error updating parking info"));

    }
}
