package com.main.carassistant.model;

import android.provider.BaseColumns;
import com.main.carassistant.db.annotations.Table;
import com.main.carassistant.db.annotations.type.dbString;

@Table(name = "BusinessCards")
public class BusinessCard implements BaseColumns {

    @dbString
    public static final String NAME = "name";

    @dbString
    public static final String COMMENT = "comment";

    @dbString
    public static final String PATH_NAME = "pathName";

//    @dbLong
//    public static final String LATITUDE = "latitude";
//
//    @dbLong
//    public static final String LONGITUDE = "longitude";
}
