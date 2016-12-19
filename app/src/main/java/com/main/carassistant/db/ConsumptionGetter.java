package com.main.carassistant.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import com.main.carassistant.Constants.SqlConst;
import com.main.carassistant.model.Stats;
import com.main.carassistant.threads.ResultCallback;

public class ConsumptionGetter {

    public static void statsQuery(final DbHelper dbHelper, final Handler handler, final ResultCallback<ContentValues> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ContentValues lastRowValues = new ContentValues();
                final ContentValues contentValues = new ContentValues();
                Cursor cursor = dbHelper.query(SqlConst.QUERY_FOR_CONSUMPTION + DbHelper.getTableName(Stats.class));
                if (cursor.getCount() > 1) {
                    cursor.moveToLast();
                    lastRowValues.put(Stats.MILEAGE, cursor.getInt(cursor.getColumnIndex(Stats.MILEAGE)));
                    lastRowValues.put(Stats.FUELING, cursor.getInt(cursor.getColumnIndex(Stats.FUELING)));
//                    lastRowValues.put(Stats.OIL_FILLED, cursor.getInt(cursor.getColumnIndex(Stats.OIL_FILLED)));
                    lastRowValues.put(Stats.TOTAL_FUELING, cursor.getInt(cursor.getColumnIndex(Stats.TOTAL_FUELING)));
                    lastRowValues.put(Stats.CURRENT_FUEL, cursor.getInt(cursor.getColumnIndex(Stats.CURRENT_FUEL)));
                    cursor.moveToPrevious();

                    //potracheno topliva posle posledney zapravki
                    //TODO rename
                    int fuel_stealed = cursor.getInt(cursor.getColumnIndex(Stats.CURRENT_FUEL)) - (lastRowValues.getAsInteger(Stats.CURRENT_FUEL) - lastRowValues.getAsInteger(Stats.FUELING));
                    //probeg posle posledney zapravki
                    int last_run = lastRowValues.getAsInteger(Stats.MILEAGE) - cursor.getInt(cursor.getColumnIndex(Stats.MILEAGE));
                    //rashod posle posledney zapravki
                    contentValues.put("Fuel_consumption_after_last", fuel_stealed * 100 / last_run);

                    //TODO add oil consumption
//                    int oil_stealed = cursor.getInt(cursor.getColumnIndex(Stats.OIL_FILLED)) - (lastRowValues.getAsInteger(Stats.OIL_FILLED)-lastRowValues.getAsInteger(Stats.OIL_FILLED));

                    cursor.moveToFirst();
                    int total_run = lastRowValues.getAsInteger(Stats.MILEAGE) - cursor.getInt(cursor.getColumnIndex(Stats.MILEAGE));
                    contentValues.put("Total_consumption", lastRowValues.getAsInteger(Stats.TOTAL_FUELING) * 100 / total_run);
                    cursor.close();
                    lastRowValues.clear();
                } else {
                    contentValues.put("Fuel_consumption_after_last", "No data to calculate");
                    contentValues.put("Total_consumption", "No data to calculate");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(contentValues);
                    }
                });
            }
        }).start();
    }
}
