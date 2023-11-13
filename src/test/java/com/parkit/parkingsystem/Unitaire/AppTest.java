package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.App;

import nl.altindag.log.LogCaptor;

public class AppTest {

    @AfterAll
    private static void clearBufferRead(){
        System.setIn(System.in);
    }

    @Test
    public void logAppTest(){

        System.setIn(new ByteArrayInputStream("3".getBytes()));

        LogCaptor logCaptor = LogCaptor.forClass(App.class);

        App.main(null);

        assertTrue(logCaptor.getLogs().toString().contains("Initializing Parking System"));

    }
}
