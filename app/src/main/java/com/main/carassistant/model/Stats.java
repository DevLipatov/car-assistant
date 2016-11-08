package com.main.carassistant.model;

import android.provider.BaseColumns;
import com.main.carassistant.db.annotations.Table;
import com.main.carassistant.db.annotations.type.dbInteger;
import com.main.carassistant.db.annotations.type.dbLong;
import com.main.carassistant.db.annotations.type.dbString;

@Table(name = "Stats")
public class Stats implements BaseColumns{

    @dbInteger
    public static final String MILEAGE = "mileage";

    @dbInteger
    public static final String FUELING = "fueling";

    @dbInteger
    public static final String CURRENT_FUEL = "current_fuel";

    @dbInteger
    public static final String OIL_FILLED = "oil_filled";

    @dbLong
    public static final String DATE = "date";

    @dbString
    public static final String COMMENT = "comment";

}
