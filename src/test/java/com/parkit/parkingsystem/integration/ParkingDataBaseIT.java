package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearAllCaches;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){
        clearAllCaches();
    }

    @Test
    public void testParkingACar() throws ClassNotFoundException, SQLException{
        incomingVehicle();

        startDB(DBConstants.VERIFY_INCOME_VEHICLE_TEST);
        if(rs.next()){
            assertTrue(rs.getString(1).equals("ABCDEF"));
            assertEquals(rs.getInt(2), 0);
        }
        closeDB();
    }

    @Test
    public void testParkingLotExit() throws ClassNotFoundException, SQLException, InterruptedException{
        incomingVehicle();
        Thread.sleep(1000);
        parkingLotExit();
        startDB(DBConstants.VERIFY_EXIT_VEHICLE_TEST);
        if(rs.next()){
            assertTrue(!rs.getDate(1).equals(null));
        }
        closeDB();
        
    }

    @Test
    public void testDoubleParkingLotExit() throws ClassNotFoundException, SQLException, InterruptedException{
        incomingVehicle();
        Thread.sleep(1000);
        parkingLotExit();
        incomingVehicle();
        Thread.sleep(1000);
        parkingLotExit();

        startDB(DBConstants.VERIFY_EXIT_VEHICLE_TEST);
        int count = 0;

        while(rs.next()){
            Optional<Date> optionalDate = Optional.ofNullable(rs.getTimestamp(1));
            assertTrue(optionalDate.isPresent());
            count ++;
        }

        assertTrue(count == 2);
        
        closeDB();
    }

    @Test
    public void testParkingLotExitRecurringUserCar() throws ClassNotFoundException, SQLException{
        incomingVehicle();
        rewindInTime(1);
        parkingLotExit();

        incomingVehicle();
        rewindInTime(2);
        parkingLotExit();

        startDB(DBConstants.GET_ARRAY_TICKETS_TEST);
        while(rs.next()){
            double price = rs.getDouble(1);
            Long inTime = rs.getTimestamp(2).getTime();
            Long outTime = rs.getTimestamp(3).getTime();
            boolean discount = rs.getBoolean(4);

            double discountTaxe = 1;

            if(discount)
                discountTaxe *= 0.95;

            double duration = outTime - inTime;
            duration /= 3600000;

            assertEquals(duration * discountTaxe * Fare.CAR_RATE_PER_HOUR, price);
        }

    }

    private void startDB(String dbConstants) throws ClassNotFoundException, SQLException{
        con = dataBaseTestConfig.getConnection();
        ps = con.prepareStatement(dbConstants);
        rs = ps.executeQuery();
    }

    private void closeDB(){
        dataBaseTestConfig.closeResultSet(rs);
        dataBaseTestConfig.closePreparedStatement(ps);
        dataBaseTestConfig.closeConnection(con);
    }

    private void parkingLotExit(){
        ParkingService parkingService = new ParkingService(parkingSpotDAO, ticketDAO);
        try(MockedStatic<InteractiveShell> mock2 = mockStatic(InteractiveShell.class)){
            when(InteractiveShell.getVehichleRegNumber()).thenReturn("ABCDEF");
            parkingService.processExitingVehicle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void incomingVehicle(){
        try(MockedStatic<ParkingSpot> mock = mockStatic(ParkingSpot.class)){
            ParkingService parkingService = new ParkingService(parkingSpotDAO, ticketDAO);
            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            when(ParkingSpot.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);
            try{
                try(MockedStatic<InteractiveShell> mock2 = mockStatic(InteractiveShell.class)){
                    when(InteractiveShell.getVehichleRegNumber()).thenReturn("ABCDEF");
                    parkingService.processIncomingVehicle();
                }
            } catch(Exception e){
                System.out.println("error to get the vehicle Register Number");
            }           
        } 
    }

    private void rewindInTime(int id){
        try {
            con = dataBaseTestConfig.getConnection();
            ps = con.prepareStatement(DBConstants.CHANGE_IN_DATE_TEST);
            ps.setInt(1, id);
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        closeDB();
    }

}
