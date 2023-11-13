package com.parkit.parkingsystem.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import static org.junit.jupiter.api.Assertions.*;


public class TicketDAOIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    
    private Ticket ticket = new Ticket();

    private Connection con = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    @BeforeAll
    private static void setUp() throws Exception{
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
        ticket = new Ticket();
        Date date = new Date(System.currentTimeMillis());
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setInTime(date);
        ticket.setVehicleRegNumber("ABCDEF");
        ticket.setPrice(0);
        ticket.setOutTime(null);
        ticketDAO.saveTicket(ticket);
    }

    @Test
    public void testSaveTicketIn() throws ClassNotFoundException, SQLException{

        startDB(DBConstants.GET_TICKET);
        ps.setString(1, "ABCDEF");
        rs = ps.executeQuery();

        if(rs.next()){
            Optional<Date> optionalDateIn = Optional.ofNullable(rs.getTimestamp(4));
            Optional<Date> optionalDateOut = Optional.ofNullable(rs.getTimestamp(5));
            assertTrue(rs.getInt(1) == 1);
            assertTrue(optionalDateIn.isPresent());
            assertFalse(optionalDateOut.isPresent());
        }

        closeDB();
    }

    @Test
    public void testUpdateTicket() throws ClassNotFoundException, SQLException{
    
        ticket.setId(1);
        ticket.setOutTime(new Date(System.currentTimeMillis()));
        ticketDAO.updateTicket(ticket);

        startDB(DBConstants.GET_TICKET);
        ps.setString(1, "ABCDEF");
        rs = ps.executeQuery();

        if(rs.next()){
            Optional<Date> optionalDateIn = Optional.ofNullable(rs.getTimestamp(4));
            Optional<Date> optionalDateOut = Optional.ofNullable(rs.getTimestamp(5));
            assertTrue(rs.getInt(1) == 1);
            assertTrue(optionalDateIn.isPresent());
            assertTrue(optionalDateOut.isPresent());
        }

        closeDB();
    }

    @Test
    public void testDoubleTicketSave() throws ClassNotFoundException, SQLException{
        ticket.setId(1);
        ticket.setOutTime(new Date(System.currentTimeMillis()));
        ticketDAO.updateTicket(ticket);

        Ticket ticket2 = new Ticket();
        ticket2.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket2.setInTime(new Date(System.currentTimeMillis()));
        ticket2.setVehicleRegNumber("ABCDEF");
        ticket2.setPrice(2);
        ticket2.setOutTime(null);
        ticketDAO.saveTicket(ticket2);

        startDB(DBConstants.GET_TICKET);
        ps.setString(1, "ABCDEF");
        rs = ps.executeQuery();

        if(rs.next()){
            assertEquals(rs.getLong(2), 2);
            assertTrue(rs.getInt(3) == 2);
        }
    }

    @Test
    public void testDoubleRegNum(){
        boolean testSameRegNum = ticketDAO.verifyRegNumber("ABCDEF");
        boolean testNewRegNum = ticketDAO.verifyRegNumber("GHIJKLM");
        assertFalse(testSameRegNum);
        assertTrue(testNewRegNum);
    }

    private void startDB(String dbConstants) throws ClassNotFoundException, SQLException{
        con = dataBaseTestConfig.getConnection();
        ps = con.prepareStatement(dbConstants);
    }

    private void closeDB(){
        dataBaseTestConfig.closeResultSet(rs);
        dataBaseTestConfig.closePreparedStatement(ps);
        dataBaseTestConfig.closeConnection(con);
    }
}
