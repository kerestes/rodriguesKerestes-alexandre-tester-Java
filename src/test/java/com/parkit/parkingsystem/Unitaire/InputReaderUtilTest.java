package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

public class InputReaderUtilTest {

    @AfterAll
    private static void clearBufferRead(){
        //System.setIn(System.in);
    }
    
    @Test
    public void readVehicleRegistrationNumberTest() throws Exception{

        LogCaptor logCaptor = LogCaptor.forClass(InputReaderUtil.class);
        
        System.setIn(new ByteArrayInputStream("".getBytes()));
        
        InputReaderUtil inputReaderUtil = new InputReaderUtil();
        
        assertThrows(Exception.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
        assertTrue(logCaptor.getLogs().toString().contains("Error while reading user input from Shell"));
    }
}
