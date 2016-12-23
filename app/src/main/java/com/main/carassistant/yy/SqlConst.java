package com.main.carassistant.yy;

public class SqlConst {
    public static final String QUERY_FOR_CONSUMPTION = "SELECT mileage, fueling, current_fuel, oil_filled, total_fueling  FROM ";
    public static final String QUERY_FOR_STATS_LIST = "SELECT rowid _id, fueling, oil_filled, date FROM ";
    public static final String QUERY_FOR_COSTS_LIST = "SELECT rowid _id, cost, comment, date FROM ";
    public static final String QUERY_FOR_TOTAL_FUELING = "SELECT total_fueling FROM ";
}
