package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

@TestMethodOrder(OrderAnnotation.class)
public class InteractiveShellTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeAll
    private static void inputSet(){
        System.setIn(new ByteArrayInputStream("4\n3\n3\n3\nABCDEF\n1\n2\n3".getBytes()));
    }

    @AfterEach
    private void freeSystemOut(){
        System.setOut(System.out);
    }

    @AfterAll
    private static void clearBufferRead(){
        //System.setIn(System.in);
    }

    @Test
    @Order(1)
    public void loadAndExitingInterfaceTest() throws IOException{
        
        System.setOut(new PrintStream(outputStreamCaptor));

        InteractiveShell.loadInterface();
        
        assertTrue(outputStreamCaptor.toString().contains("Unsupported option. Please enter a number corresponding to the provided menu"));
        assertTrue(outputStreamCaptor.toString().contains("Welcome to Parking System!"));
        assertTrue(outputStreamCaptor.toString().contains("Exiting from the system!"));
    }

    @Test
    @Order(2)
    public void loadInterfaceLogTest(){
        LogCaptor logCaptor = LogCaptor.forClass(InteractiveShell.class);
        InteractiveShell.loadInterface();

        assertTrue(logCaptor.getLogs().contains("App initialized!!!"));
    }

    @Test
    @Order(3)
    public void loadMenuTest(){

        System.setOut(new PrintStream(outputStreamCaptor));

        InteractiveShell.loadInterface();

        assertTrue(outputStreamCaptor.toString().contains("Please select an option. Simply enter the number to choose an action"));
        assertTrue(outputStreamCaptor.toString().contains("1 New Vehicle Entering - Allocate Parking Space"));
        assertTrue(outputStreamCaptor.toString().contains("2 Vehicle Exiting - Generate Ticket Price"));
        assertTrue(outputStreamCaptor.toString().contains("3 Shutdown System"));

    }

    @Test
    @Order(4)
    public void getVehichleRegNumberTest() throws Exception{
        
        String test = InteractiveShell.getVehichleRegNumber();

        assertEquals("ABCDEF", test);
    }

    @Test
    @Order(5)
    public void getVehichleTypeCarTest(){

        ParkingType test = InteractiveShell.getVehichleType();

        assertEquals(test, ParkingType.CAR);

    }

    @Test
    @Order(6)
    public void getVehichleTypeBikeTest(){

        ParkingType test = InteractiveShell.getVehichleType();

        assertEquals(test, ParkingType.BIKE);

    }

    @Test
    @Order(7)
    public void getVehichleTypeUnknowTest(){

        assertThrows(IllegalArgumentException.class, () -> InteractiveShell.getVehichleType());

    }
    
}
