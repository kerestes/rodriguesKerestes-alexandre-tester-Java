package com.parkit.parkingsystem.Unitaire;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;
    private ParkingSpot parkingSpot;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @Mock
    private Logger logger;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeAll
    private static void setUpAll(){

        mockStatic(InteractiveShell.class);
        
        try{
            when(InteractiveShell.getVehichleRegNumber()).thenReturn("ABCDEF");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @BeforeEach
    private void setUpPerTest() {

        parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

    }

    @Test
    public void processIncomingVehicleTest(){

        try(MockedStatic<ParkingSpot> mock = mockStatic(ParkingSpot.class)){

            when(ParkingSpot.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);
        
            when(ticketDAO.getNbTicket(anyString())).thenReturn(1);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

            parkingService.processIncomingVehicle();
            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
            verify(ticketDAO, Mockito.times(1)).getNbTicket(anyString());
            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));

        }
        
    }

    @Test
    public void testGetNextParkingNumberIfAvailable(){

        try(MockedStatic<ParkingSpot> mock = mockStatic(ParkingSpot.class)){
        
            when(ParkingSpot.getNextParkingNumberIfAvailable()).thenReturn(parkingSpot);
            
            when(ticketDAO.getNbTicket(anyString())).thenReturn(1);
            when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

            parkingService.processIncomingVehicle();
            assertEquals(1, ParkingSpot.getNextParkingNumberIfAvailable().getId());
        }
    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberNotFound(){

        try(MockedStatic<ParkingSpot> mock = mockStatic(ParkingSpot.class)){
            
            when(ParkingSpot.getNextParkingNumberIfAvailable()).thenThrow(new RuntimeException());
            parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

            LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);

            parkingService.processIncomingVehicle();    
            assertTrue(logCaptor.getLogs().contains("Unable to process incoming vehicle"));
        }

    }

    @Test
    public void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument(){



        when(InteractiveShell.getVehichleType()).thenThrow(new IllegalArgumentException("Entered input is invalid"));
        parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

        LogCaptor logCaptor = LogCaptor.forClass(ParkingSpot.class);

        parkingService.processIncomingVehicle();   
        
        assertTrue(logCaptor.getLogs().contains("Error parsing user input for type of vehicle"));

    }

    @Test
    public void processExitingVehicleTest(){

        Ticket ticket = createTicket();

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
        verify(ticketDAO, Mockito.times(1)).updateTicket((any(Ticket.class)));
    }

    @Test
    public void processExitingVehicleTestUnableUpdate(){

        System.setOut(new PrintStream(outputStreamCaptor));

        Ticket ticket = createTicket();

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

        parkingService.processExitingVehicle();
        
        assertEquals("Unable to update ticket information. Error occurred", outputStreamCaptor.toString().trim());

        System.setOut(System.out);
    }

    @Test
    public void processExitingVehicleFreeParkingTest(){

        System.setOut(new PrintStream(outputStreamCaptor));

        Ticket ticket = createTicket();
        ticket.setInTime(new Date(System.currentTimeMillis()));

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

        parkingService.processExitingVehicle();
        assertTrue(outputStreamCaptor.toString().contains("Free - less than 30 minutes"));

        System.setOut(System.out);
    }

    @Test
    public void processExitingVehicleUnknowVheicleTest(){

        when(ticketDAO.getTicket(anyString())).thenThrow(new RuntimeException());

        parkingService = new ParkingService(parkingSpotDAO, ticketDAO);

        LogCaptor logCaptor = LogCaptor.forClass(ParkingService.class);

        parkingService.processExitingVehicle();
        assertTrue(logCaptor.getLogs().contains("Unable to process exiting vehicle"));
       
    }

    

    private Ticket createTicket(){
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        return ticket;
    }

}
