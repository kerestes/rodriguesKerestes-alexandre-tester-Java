package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.clearAllCaches;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.TestInstance;

public class InputReaderUtilTest {

    LogCaptor log = LogCaptor.forClass(InputReaderUtil.class);

    InputReaderUtil inputReaderUtil;

    @AfterAll
    private static void realeseAll(){
        System.setIn(System.in);
        clearAllCaches();
    }

    @Test
    public void readVehicleRegistrationNumberTest(){

        System.setIn(new ByteArrayInputStream("ABCDEF".getBytes()));

        inputReaderUtil = new InputReaderUtil();

        try {
            String registration = inputReaderUtil.readVehicleRegistrationNumber();
            assertEquals("ABCDEF", registration);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        

    }

    @Test
    public void readVehicleRegistrationNumberEmptyTest(){

        System.setIn(new ByteArrayInputStream(" ".getBytes()));

        inputReaderUtil = new InputReaderUtil();

        assertThrows(IllegalArgumentException.class, inputReaderUtil::readVehicleRegistrationNumber);
    }

    @Test
    public void readVehicleRegistrationNumberNoArgumentTest(){

        System.setIn(new ByteArrayInputStream("".getBytes()));

        inputReaderUtil = new InputReaderUtil();

        assertThrows(Exception.class, inputReaderUtil::readVehicleRegistrationNumber);
        assertTrue(log.getLogs().contains("Error while reading user input from Shell"));
    }

    @Test
    public void readSelectionExceptionTest(){

        System.setIn(new ByteArrayInputStream("A".getBytes()));

        inputReaderUtil = new InputReaderUtil();
        inputReaderUtil.readSelection();

        assertTrue(log.getLogs().contains("Error while reading user input from Shell"));

    }
}
