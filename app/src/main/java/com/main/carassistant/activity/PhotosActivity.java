package com.main.carassistant.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.main.carassistant.App;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.BusinessCard;
import com.main.carassistant.pollock.Pollock;
import com.main.carassistant.threads.ResultCallback;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    ListView listView;
    private Handler handler;
    private final ArrayList<String> IMAGE_URLS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        listView = (ListView) findViewById(R.id.list);
        dbHelper = ((App) getApplication()).getDbHelper();
        handler = new Handler();

        final Pollock pollock = Pollock.getInstance();

        getImageUrls(new ResultCallback<Cursor>() {
            @Override
            public void onSuccess(Cursor result) {
                if (result != null) {
                    result.moveToFirst();
                    while (!result.isAfterLast()) {
                        IMAGE_URLS.add(result.getString(result.getColumnIndex(BusinessCard.PATH_NAME)));
                        result.moveToNext();
                    }
                }
            }
        });

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.adapter_photo, android.R.id.text1, IMAGE_URLS) {
            @NonNull
            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                View view;
                if (convertView == null) {
                    view = super.getView(position, convertView, parent);
                } else {
                    view = convertView;
                }
                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                pollock.drawImage(imageView, IMAGE_URLS.get(position));
                return view;
            }
        };
        listView.setAdapter(stringArrayAdapter);
    }

    private void getImageUrls (final ResultCallback<Cursor> resultCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = dbHelper.query("SELECT rowid _id, pathName FROM " + DbHelper.getTableName(BusinessCard.class));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultCallback.onSuccess(cursor);
                    }
                });
            }
        }).start();

    }
}
