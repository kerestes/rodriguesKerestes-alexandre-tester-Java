package com.parkit.parkingsystem.constants;

public class DBConstants {

    public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    public static final String UPDATE_PARKING_SPOT = "update parking set available = ? where PARKING_NUMBER = ?";

    public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, DISCOUNT) values(?,?,?,?,?,?)";
    public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, t.DISCOUNT, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";

    public static final String NUMBER_OF_TICKET = "SELECT count(VEHICLE_REG_NUMBER) FROM ticket WHERE VEHICLE_REG_NUMBER = ?";

    public static final String VERIFY_INCOME_VEHICLE_TEST = "SELECT t.vehicle_reg_number, p.available FROM ticket t join parking p on t.parking_number = p.parking_number WHERE t.vehicle_reg_number = \"ABCDEF\" AND out_time = null";
}
