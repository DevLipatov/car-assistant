package com.main.carassistant.model;

import android.provider.BaseColumns;
import com.main.carassistant.db.annotations.Table;
import com.main.carassistant.db.annotations.type.dbString;

@Table(name = "Cathegory")
public class Cathegory implements BaseColumns {

    @dbString
    public static final String name = "cathegory";
}
