package com.main.carassistant.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.main.carassistant.App;
import com.main.carassistant.BuildConfig;
import com.main.carassistant.Constants.WeatherConst;
import com.main.carassistant.R;
import com.main.carassistant.adapters.SamplePagerAdapter;
import com.main.carassistant.db.ConsumptionGetter;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.design.ZoomOutPageTransformer;
import com.main.carassistant.http.OwnHttpClient;
import com.main.carassistant.model.Weather;
import com.main.carassistant.parsing.weather.WeatherClient;
import com.main.carassistant.threads.ResultCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView txtTemperature;
    private ImageView imgWeather;
    private DbHelper dbHelper;
    private ViewPager viewPager;
    Toolbar toolbar;
    ProgressBar pb;
    NavigationView navigationView;
    private SamplePagerAdapter samplePagerAdapter;
    private View page;
    private DrawerLayout drawerLayout;
    private android.os.Handler handler;
    private String city;
    private NotificationManager notificationManager;
    private static final String VERSION_URL = "https://carassistant-153318.appspot.com/version";
    private final int NOTIFICATION_ID = 127;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        imgWeather = (ImageView) findViewById(R.id.imgWeather);
        pb = (ProgressBar) findViewById(R.id.pbWeather);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = ((App) getApplication()).getDbHelper();
        handler = new Handler();

        pb.setVisibility(View.VISIBLE);
        setNavigationView();

        //TODO add city selection
        city = "Hrodna";

        setStats();
        setWeather();

        checkVersion();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_add_stats) {
            intent = new Intent(getApplicationContext(), StatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_photos) {
            intent = new Intent(getApplicationContext(), PhotosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_stats) {
            intent = new Intent(getApplicationContext(), AllStatsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search_costs) {
            intent = new Intent(getApplicationContext(), AllCostsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_business_card) {
            intent = new Intent(getApplicationContext(), BusinessCardActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_cost) {
            intent = new Intent(getApplicationContext(), CostsActivity.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setWeather() {

        String weatherUrl = WeatherConst.BASE_URL + city + WeatherConst.PARAMS + WeatherConst.APIKEY;
        WeatherClient.getWeather(weatherUrl, handler, new ResultCallback<Weather>() {
            @Override
            public void onSuccess(Weather result) {
                if (result.getTemp() != null) {
                    String temp = Integer.toString(result.getTemp()) + "°C";
                    txtTemperature.setText(temp);
                    //set image if exists
                    if (result.iconData != null && result.iconData.length > 0) {
                        Bitmap image = BitmapFactory.decodeByteArray(result.iconData, 0, result.iconData.length);
                        pb.setVisibility(View.GONE);
                        imgWeather.setImageBitmap(image);
                    }
                } else {
                    txtTemperature.setTextSize(16);
                    txtTemperature.setText(R.string.connection_error);
                }
            }
        });
    }

    public void onMapClick(View view) {


//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("geo:55.754283,37.62002"));
//        startActivity(intent);
    }

    private void setStats() {
        ConsumptionGetter.statsQuery(dbHelper, handler, new ResultCallback<ContentValues>() {
            @Override
            public void onSuccess(ContentValues result) {
                final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                //pages for ViewPager
                final List<View> pages = new ArrayList<>();

                page = inflater.inflate(R.layout.pager_adapter_page, null);
                TextView textConsumption = (TextView) page.findViewById(R.id.tvConsumption);
                TextView textLabel = (TextView) page.findViewById(R.id.tvLabel);
                textConsumption.setText(result.getAsString("Fuel_consumption_after_last"));
                textLabel.setText("Fuel consumption after last fueling");
                pages.add(page);

                page = inflater.inflate(R.layout.pager_adapter_page, null);
                textConsumption = (TextView) page.findViewById(R.id.tvConsumption);
                textLabel = (TextView) page.findViewById(R.id.tvLabel);
                textConsumption.setText(result.getAsString("Total_consumption"));
                textLabel.setText("Total fuel consumption");
                pages.add(page);

                samplePagerAdapter = new SamplePagerAdapter(pages);
                viewPager.setAdapter(samplePagerAdapter);
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                viewPager.setCurrentItem(0);
            }
        });
    }

    private void setNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    public void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OwnHttpClient ownHttpClient = new OwnHttpClient();
                final String version = ownHttpClient.getData(VERSION_URL);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Integer curVersion = BuildConfig.VERSION_CODE;
                        if (!version.isEmpty()&& curVersion < Integer.valueOf(version)) {
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
                            notificationManager.notify(NOTIFICATION_ID, notification);
                        }
                    }
                });
            }
        }).start();


        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_action_android)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_action_android))
                .setTicker("Your app is up to date")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Update your app")
                .setContentText("Click for update your app to latest version");

        Notification notification = builder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


}
