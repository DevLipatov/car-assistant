package com.main.carassistant.model;

import android.provider.BaseColumns;
import com.main.carassistant.db.annotations.Table;
import com.main.carassistant.db.annotations.type.dbLong;
import com.main.carassistant.db.annotations.type.dbString;

@Table(name = "Costs")
public class Costs implements BaseColumns{

    @dbString
    public static final String DATE = "date";

    @dbString
    public static final String CATEGORY = "category";

    @dbLong
    public static final String COST = "cost";

    @dbString
    public static final String COMMENT = "comment";

}
