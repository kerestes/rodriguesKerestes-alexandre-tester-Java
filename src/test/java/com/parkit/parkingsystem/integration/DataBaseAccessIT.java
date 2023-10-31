package com.parkit.parkingsystem.integration;

import org.junit.jupiter.api.Test;

import com.mysql.cj.xdevapi.PreparableStatement;
import com.parkit.parkingsystem.config.DataBaseConfig;

import nl.altindag.log.LogCaptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.*;

public class DataBaseAccessIT {
    
    DataBaseConfig dataBaseConfig = new DataBaseConfig();
    Connection conn;

    @Test
    public void testGetConnection() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);

        conn = dataBaseConfig.getConnection();

        assertTrue(logCaptor.getLogs().toString().contains("Create DB connection"));

        dataBaseConfig.closeConnection(conn);
    }

    @Test
    public void testCloseConnection() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);

        conn = dataBaseConfig.getConnection();

        dataBaseConfig.closeConnection(conn);

        assertTrue(logCaptor.getLogs().toString().contains("Closing DB connection"));
    }

    @Test
    public void testErrorCloseConnection() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);

        Connection conn2 = mock(Connection.class);
        doThrow(new SQLException()).when(conn2).close();

        dataBaseConfig.closeConnection(conn2);

        assertTrue(logCaptor.getLogs().toString().contains("Error while closing connection"));
    }

    @Test
    public void testClosePreparedStatement() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);
        
        conn = dataBaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement("select * from ticket");

        dataBaseConfig.closePreparedStatement(ps);

        assertTrue(logCaptor.getLogs().toString().contains("Closing Prepared Statement"));

        dataBaseConfig.closeConnection(conn);
    }

    @Test
    public void testErrorClosePreparedStatement() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);
        
        PreparedStatement ps = mock(PreparedStatement.class);
        doThrow(new SQLException()).when(ps).close();

        dataBaseConfig.closePreparedStatement(ps);

        assertTrue(logCaptor.getLogs().toString().contains("Error while closing prepared statement"));
    }

    @Test
    public void testCloseResultSet() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);
        
        conn = dataBaseConfig.getConnection();
        PreparedStatement ps = conn.prepareStatement("select * from ticket");
        ResultSet rs = ps.executeQuery();

        dataBaseConfig.closeResultSet(rs);

        assertTrue(logCaptor.getLogs().toString().contains("Closing Result Set"));

        dataBaseConfig.closePreparedStatement(ps);
        dataBaseConfig.closeConnection(conn);
    }

    @Test
    public void testErrorCloseResultSet() throws ClassNotFoundException, SQLException{
        
        LogCaptor logCaptor = LogCaptor.forClass(DataBaseConfig.class);
        
        ResultSet rs = mock(ResultSet.class);
        doThrow(new SQLException()).when(rs).close();

        dataBaseConfig.closeResultSet(rs);

        assertTrue(logCaptor.getLogs().toString().contains("Error while closing result set"));
    }
}
