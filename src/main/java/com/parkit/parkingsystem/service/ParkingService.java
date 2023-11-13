package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger(ParkingService.class);

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;

    public ParkingService(ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = ParkingSpot.getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0){
                String vehicleRegNumber = InteractiveShell.getVehichleRegNumber();
                if(ticketDAO.verifyRegNumber(vehicleRegNumber)) {
                    parkingSpot.setAvailable(false);
                    parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                    Date inTime = new Date();
                    Ticket ticket = new Ticket();

                    int visitedTimes = ticketDAO.getNbTicket(vehicleRegNumber);
                    if (visitedTimes > 0 && visitedTimes%2 == 1)
                        ticket.setDiscount(true);

                    //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                    //ticket.setId(ticketID);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setVehicleRegNumber(vehicleRegNumber);
                    ticket.setPrice(0);
                    ticket.setInTime(inTime);
                    ticket.setOutTime(null);
                    ticketDAO.saveTicket(ticket);

                    System.out.println("Generated Ticket and saved in DB");
                    if (visitedTimes%2  == 1)
                        System.out.println("Good to see you again! As a regular user of our parking lot, you will get a 5% discount");
                    System.out.println("Please park your vehicle in spot number:"+parkingSpot.getId());
                    System.out.println("Recorded in-time for vehicle number:"+vehicleRegNumber+" is:"+inTime);
                } else {
                    logger.error("Duplacation registration number");
                    System.out.println("There is a current parked vehicle with the same registration number");
                }
            } 
        }catch(Exception e){
            logger.error("Unable to process incoming vehicle");
        }
    }

    public void processExitingVehicle() {   
        try{
            String vehicleRegNumber = InteractiveShell.getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            Date outTime = new Date();
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            if(ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                if (ticket.getPrice() == 0 )
                    System.out.println("Free - less than 30 minutes");
                else
                    System.out.println("Please pay the parking fare: " + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number: " + ticket.getVehicleRegNumber() + " is:" + outTime);
            }else{
                System.out.println("Unable to update ticket information. Error occurred");
            }
        }catch(Exception e){
            logger.error("Unable to process exiting vehicle",e);
        }
    }
}
