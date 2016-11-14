//package com.main.carassistant.db;
//
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.widget.SimpleCursorAdapter;
//
//import com.main.carassistant.http.HttpClient;
//import com.main.carassistant.threads.Operation;
//import com.main.carassistant.threads.ProgressCallback;
//import com.main.carassistant.threads.ThreadManager;
//
//import java.io.InputStream;
//import java.util.Map;
//
//public class CursorDataLoader {
//
//    ThreadManager threadManager;
//
//    public Map<String, String> getData(final Cursor cursor, final String[] fields) {
//        Map<String, String> res = null;
//        for (String field: fields) {
//            res.put(field, cursor.getString(cursor.getColumnIndex(field)));
//            res.put(field, cursor.getString(cursor.getColumnIndex(field)));
//            res.put(field, cursor.getString(cursor.getColumnIndex(field)));
//            res.put(field, cursor.getString(cursor.getColumnIndex(field)));
//            )
//        }
//    }
//
//    SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, );
//
//
//}
