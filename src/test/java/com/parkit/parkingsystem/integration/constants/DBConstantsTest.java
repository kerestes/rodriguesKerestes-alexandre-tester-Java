package com.parkit.parkingsystem.integration.constants;

public class DBConstantsTest {
    public static final String VERIFY_INCOME_VEHICLE_TEST = "SELECT t.vehicle_reg_number, p.available FROM ticket t join parking p on t.parking_number = p.parking_number WHERE t.vehicle_reg_number = \"ABCDEF\" AND out_time = null";
    public static final String VERIFY_EXIT_VEHICLE_TEST = "SELECT t.out_time, p.available FROM ticket t join parking p on t.parking_number = p.parking_number WHERE t.vehicle_reg_number = \"ABCDEF\"";
    public static final String TURN_OFF_PARKING_PLACE_TEST = "UPDATE parking SET available = false";
    public static final String TURN_ON_PARKING_PLACE_TEST = "UPDATE parking SET available = TRUE";
    public static final String CHANGE_IN_DATE_TEST = "UPDATE ticket SET in_time = date_sub(curdate(), interval 1 day) WHERE id = ?";
    public static final String GET_ARRAY_TICKETS_TEST = "SELECT price, in_time, out_time, discount from ticket";
}
