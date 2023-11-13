package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.service.InteractiveShell;
import nl.altindag.log.LogCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class InteractiveShellTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

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
    public void loadAndExitingInterfaceTest() throws IOException{
        
        System.setIn(new ByteArrayInputStream("4\n3".getBytes()));
        System.setOut(new PrintStream(outputStreamCaptor));

        InteractiveShell.loadInterface();
        
        assertTrue(outputStreamCaptor.toString().contains("Unsupported option. Please enter a number corresponding to the provided menu"));
        assertTrue(outputStreamCaptor.toString().contains("Welcome to Parking System!"));
        assertTrue(outputStreamCaptor.toString().contains("Exiting from the system!"));
    }

    @Test
    public void loadInterfaceLogTest(){

        System.setIn(new ByteArrayInputStream("3".getBytes()));

        LogCaptor logCaptor = LogCaptor.forClass(InteractiveShell.class);
        InteractiveShell.loadInterface();

        assertTrue(logCaptor.getLogs().contains("App initialized!!!"));
    }

    @Test
    public void loadMenuTest(){

        System.setIn(new ByteArrayInputStream("3".getBytes()));

        System.setOut(new PrintStream(outputStreamCaptor));

        InteractiveShell.loadInterface();

        assertTrue(outputStreamCaptor.toString().contains("Please select an option. Simply enter the number to choose an action"));
        assertTrue(outputStreamCaptor.toString().contains("1 New Vehicle Entering - Allocate Parking Space"));
        assertTrue(outputStreamCaptor.toString().contains("2 Vehicle Exiting - Generate Ticket Price"));
        assertTrue(outputStreamCaptor.toString().contains("3 Shutdown System"));

    }

    @Test
    public void getVehichleRegNumberTest() throws Exception{
        
        System.setIn(new ByteArrayInputStream("ABCDEF".getBytes()));

        String test = InteractiveShell.getVehichleRegNumber();

        assertEquals("ABCDEF", test);
    }

    @Test
    public void getVehichleTypeCarTest(){

        System.setIn(new ByteArrayInputStream("1".getBytes()));

        ParkingType test = InteractiveShell.getVehichleType();

        assertEquals(test, ParkingType.CAR);

    }

    @Test
    public void getVehichleTypeBikeTest(){

        System.setIn(new ByteArrayInputStream("2".getBytes()));

        ParkingType test = InteractiveShell.getVehichleType();

        assertEquals(test, ParkingType.BIKE);

    }

    @Test
    public void getVehichleTypeUnknowTest(){

        System.setIn(new ByteArrayInputStream("3".getBytes()));

        assertThrows(IllegalArgumentException.class, () -> InteractiveShell.getVehichleType());

    }
    
}
