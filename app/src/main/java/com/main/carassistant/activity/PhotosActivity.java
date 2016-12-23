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
import com.main.carassistant.pollock.Pollock;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.BusinessCard;
import com.main.carassistant.threads.ResultCallback;

public class PhotosActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    ListView listView;
    private Handler handler;

    private String[] IMAGE_URLS;
//            =
//            {
//                    "http://makeitlast.se/wp-content/uploads/2015/10/loppis_12.jpg",
//                    "https://images-na.ssl-images-amazon.com/images/G/01/img15/pet-products/small-tiles/30423_pets-products_january-site-flip_3-cathealth_short-tile_592x304._CB286975940_.jpg",
//                    "https://s-media-cache-ak0.pinimg.com/236x/8a/1b/7c/8a1b7c35091025bf2417ce2d9a6b058d.jpg",
//                    "https://cnet4.cbsistatic.com/hub/i/2011/10/27/a66dfbb7-fdc7-11e2-8c7c-d4ae52e62bcc/android-wallpaper5_2560x1600_1.jpg",
//                    "https://www.android.com/static/img/home/more-from-2.png",
//                    "http://www.howtablet.ru/wp-content/uploads/2016/04/%D0%9E%D0%B1%D0%BD%D0%BE%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-Android-6.0.1-Marshmallow.jpg",
//                    "http://keddr.com/wp-content/uploads/2015/12/iOS-vs-Android.jpg",
//                    "https://www.android.com/static/img/history/features/feature_icecream_3.png",
//                    "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcRfZ5OiAt7GIz57jyvjK8ca82pIvgd7pvD-3JyPG73ppN8FbqpbUA",
//                    "http://androidwallpape.rs/content/02-wallpapers/131-night-sky/wallpaper-2707591.jpg"
//            };

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
                    IMAGE_URLS = new String[result.getCount()];
//                    for(int i = 0; i < result.getCount(); i ++){
//                        IMAGE_URLS[i] = result.getString(result.getColumnIndex(BusinessCard.PATH_NAME));
//                    }
                    int i = 1;
                    result.moveToFirst();
                    while(result.moveToNext()){
                        IMAGE_URLS[i] =result.getString(result.getColumnIndex(BusinessCard.PATH_NAME));
                        i++;
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
                pollock.drawImage(imageView, IMAGE_URLS[position]);
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
