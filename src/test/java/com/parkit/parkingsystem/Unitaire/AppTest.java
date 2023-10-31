package com.parkit.parkingsystem.Unitaire;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.App;

import nl.altindag.log.LogCaptor;

public class AppTest {

    @AfterAll
    private static void clearBufferRead(){
        //System.setIn(System.in);
    }

    @Test
    @Disabled
    public void logAppTest(){
        LogCaptor logCaptor = LogCaptor.forClass(App.class);

        System.setIn(new ByteArrayInputStream("3".getBytes()));
        App.main(null);

        assertTrue(logCaptor.getLogs().toString().contains("Initializing Parking System"));

        System.setIn(System.in);

    }
}
