package com.main.carassistant.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.main.carassistant.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BusinessCardActivity extends AppCompatActivity {

    private Button btnBusinessCard;
    private ImageView imgBusinessCard;
    private Toolbar toolbar;
    private Bitmap bitMapImg;
    private Boolean success;
    private File filename;
    private String nn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_card_layout);

        btnBusinessCard = (Button) findViewById(R.id.btnTakeBusinessCard);
        imgBusinessCard = (ImageView) findViewById(R.id.imgBusinessCard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnBusinessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nn = getFileName().toString();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + nn));
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO save full size photo

//        if (data != null) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            bitMapImg = bitmap;
//            btnBusinessCard.setVisibility(View.GONE);
//            imgBusinessCard.setImageBitmap(bitmap);
//            imgBusinessCard.setScaleType(ImageView.ScaleType.FIT_XY);
//        }
        Bitmap myBitmap = BitmapFactory.decodeFile("file:/" + nn);
        imgBusinessCard.setImageBitmap(myBitmap);
    }

    public void onSaveClick(View view) throws IOException {
        if (isExternalStorageWritable()) {
//            File dir = getFilePath();
//            //Creating file name with date
//            Date date = Calendar.getInstance().getTime();
//            DateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.US);
//            String today = formatter.format(date);
//
//            filename = new File(dir, "/" + today + ".jpg");

            FileOutputStream out = new FileOutputStream(filename);
            bitMapImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            success = true;
            MediaStore.Images.Media.insertImage(getContentResolver(), filename.getAbsolutePath(), filename.getName(), filename.getName());
        }
        if (success) {
            Toast.makeText(getApplicationContext(), "File is Saved as  " + filename, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "File is NOT Saved as  " + filename, Toast.LENGTH_SHORT).show();

        }
    }

    //Checks if external storage is available for read and write
    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    private File getFileName() {
        File dir = getFilePath();
        //Creating file name with date
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm", Locale.US);
        String today = formatter.format(date);
        filename = new File(dir, "/" + today + ".jpg");
        return filename;
    }

    private File getFilePath() {
        File path = new File(Environment.getExternalStorageDirectory() + "/CarAssistant/Cards");
        //Create directory if not exists
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        return path;
    }
}
