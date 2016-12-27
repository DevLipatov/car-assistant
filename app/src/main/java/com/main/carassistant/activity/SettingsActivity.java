package com.main.carassistant.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.main.carassistant.App;
import com.main.carassistant.BuildConfig;
import com.main.carassistant.R;
import com.main.carassistant.constants.UrlConst;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.http.OwnHttpClient;
import com.main.carassistant.model.Cathegory;
import com.main.carassistant.model.Stats;

public class SettingsActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    Toolbar toolbar;
    private Handler handler;
    private NotificationManager notificationManager;
    private EditText editCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        editCategory = (EditText) findViewById(R.id.editCategory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dbHelper = ((App) getApplication()).getDbHelper();
        handler = new Handler();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResetStats(View view) {
        dbHelper.delete(Stats.class, null, null);
    }

    public void checkVersion(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OwnHttpClient ownHttpClient = new OwnHttpClient();
                final String version = ownHttpClient.getData(UrlConst.VERSION_URL);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Integer curVersion = BuildConfig.VERSION_CODE;
                        if (!version.isEmpty() && curVersion < Integer.valueOf(version)) {
                            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            Notification.Builder builder = new Notification.Builder(getApplicationContext());
//                            Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
//                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                            builder
//                                    setContentIntent(pendingIntent)
                                    .setSmallIcon(R.drawable.ic_action_android)
                                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_android))
                                    .setTicker("Your app is up to date")
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true)
                                    .setContentTitle("Your app is up to date.")
                                    .setContentText("Current version is "
                                            + String.valueOf(curVersion) + ". Actual version is " + version);

                            Notification notification = builder.build();
                            notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;
                            notificationManager.notify(127, notification);
                        }
                    }
                });
            }
        }).start();

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
//        Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder .setSmallIcon(R.drawable.ic_action_android)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_android))
                .setTicker("Your app is up to date")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Update your app")
//        setContentIntent(pendingIntent)
                .setContentText("Click for update your app to latest version");

        Notification notification = builder.build();
        notificationManager.notify(127, notification);
    }

    public void onAddNewCategory(View view) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(Cathegory.name, editCategory.getText().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper.insert(Cathegory.class, contentValues);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
