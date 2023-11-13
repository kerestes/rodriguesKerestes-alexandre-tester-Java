package com.parkit.parkingsystem.Unitaire;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class TicketDaoTest {

    public TicketDAO ticketDAO;
    @Mock
    public Ticket ticket;

    @BeforeEach
    public void setTicketDAO(){
        ticketDAO = new TicketDAO();
        DataBaseConfig dataBaseConfig = Mockito.mock(DataBaseConfig.class);
        ticketDAO.dataBaseConfig = dataBaseConfig;
        try {
            when(dataBaseConfig.getConnection()).thenThrow(new SQLException("Access denied for user ''@'localhost'"));
        } catch (ClassNotFoundException e) {
            System.out.println(e.getClass());
        } catch (SQLException e) {
            System.out.println(e.getClass());
        }
    }

    @Test
    public void saveTicketFailTest() {

        LogCaptor log = LogCaptor.forClass(TicketDAO.class);
        ticketDAO.saveTicket(ticket);
        assertTrue(log.getLogs().contains("Error fetching next available slot"));
    }

    @Test
    public void getTicketFailTest() {

        LogCaptor log = LogCaptor.forClass(TicketDAO.class);
        ticketDAO.getTicket("ABCDEF");
        assertTrue(log.getLogs().contains("Error fetching next available slot"));
    }

    @Test
    public void updateTicketFailTest() {

        LogCaptor log = LogCaptor.forClass(TicketDAO.class);
        ticketDAO.updateTicket(ticket);
        assertTrue(log.getLogs().contains("Error saving ticket info"));
    }

    @Test
    public void getNbTicketFailTest() {

        LogCaptor log = LogCaptor.forClass(TicketDAO.class);
        ticketDAO.getNbTicket("ABCDEF");
        assertTrue(log.getLogs().contains("Error fetching number of tickets"));
    }

    @Test
    public void verifyRegNumberFailTest() {

        LogCaptor log = LogCaptor.forClass(TicketDAO.class);
        ticketDAO.verifyRegNumber("ABCDEF");
        assertTrue(log.getLogs().contains("Error fetching registered vehicle"));
    }
}
