package com.main.carassistant.constants;

public class DatabaseConst {
    public static final String DATABASE_NAME = "CarAssistant.db";
    public static final int DATABASE_VERSION = 4;
    public static final String SQL_TABLE_CREATE_TEMPLATE = "CREATE TABLE IF NOT EXISTS %s (%s)";
    public static final String SQL_TABLE_CREATE_FIELD_TEMPLATE = "%s %s";
}
