package com.main.carassistant;

import android.app.Application;
import com.main.carassistant.Constants.DatabaseConst;
import com.main.carassistant.db.DbHelper;

public class App extends Application{
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DbHelper(getApplicationContext(), DatabaseConst.DATABASE_NAME, DatabaseConst.DATABASE_VERSION);
    }

//    @Override
//    public Object getSystemService(String name) {
//        if(name.equals("my_db")) {
//            return dbHelper;
//        }
//        return super.getSystemService(name);
//    }

    public DbHelper getDbHelper() {
        if (dbHelper == null) {
            dbHelper = new DbHelper(getApplicationContext(), DatabaseConst.DATABASE_NAME, DatabaseConst.DATABASE_VERSION);
        }
        return dbHelper;
    }
}
