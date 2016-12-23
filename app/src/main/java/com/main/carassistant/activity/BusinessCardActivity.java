package com.main.carassistant.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.main.carassistant.App;
import com.main.carassistant.constants.FormatsConst;
import com.main.carassistant.R;
import com.main.carassistant.db.DbHelper;
import com.main.carassistant.model.BusinessCard;
import com.main.carassistant.threads.ResultCallback;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BusinessCardActivity extends AppCompatActivity {

    private Button btnBusinessCard;
    private ImageView imgBusinessCard;
    private EditText editCardName;
    private EditText editCardComment;
    private Toolbar toolbar;
    private String pathName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_card);

        btnBusinessCard = (Button) findViewById(R.id.btnTakeBusinessCard);
        imgBusinessCard = (ImageView) findViewById(R.id.imgBusinessCard);
        editCardName = (EditText) findViewById(R.id.editPhotoName);
        editCardComment = (EditText) findViewById(R.id.editPhotoComment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imgBusinessCard.setVisibility(View.GONE);
        editCardComment.setVisibility(View.GONE);
        btnBusinessCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathName = getFileName().toString();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + pathName));
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

        btnBusinessCard.setVisibility(View.GONE);
        editCardComment.setVisibility(View.VISIBLE);
        imgBusinessCard.setVisibility(View.VISIBLE);
        Bitmap myBitmap = BitmapFactory.decodeFile(pathName);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 120, 120, false);
        imgBusinessCard.setImageBitmap(scaledBitmap);

//        MediaStore.Images.Media.insertImage(getContentResolver(), filename.getAbsolutePath(), filename.getName(), filename.getName());
    }

    //Checks if external storage is available for read and write
    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    //photo file name part
    private File getFileName() {
        File dir = getFilePath();
        //Creating file name with date
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat(FormatsConst.PHOTO_NAME_FORMAT, Locale.US);
        String today = formatter.format(date);
        File filename = new File(dir, "/" + today + FormatsConst.PHOTO_FILE_FORMAT);
        return filename;
    }

    //photo file directory part
    private File getFilePath() {
        File path = new File(Environment.getExternalStorageDirectory() + FormatsConst.PHOTO_DIRECTORY_FORMAT);
        //Create directory if not exists
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        return path;
    }

    //save photo data to database
    public void onAddNewCard(View view) {
        final ContentValues contentValues = new ContentValues();
        final DbHelper dbHelper = ((App) getApplication()).getDbHelper();
        contentValues.put(BusinessCard.PATH_NAME, pathName);
        contentValues.put(BusinessCard.NAME, editCardName.toString());
        contentValues.put(BusinessCard.COMMENT, editCardComment.toString());

        if (isExternalStorageWritable()) {
            saveCard(MainActivity.handler, contentValues, dbHelper, new ResultCallback<Long>() {
                @Override
                public void onSuccess(final Long result) {
                    if (result > 0) {
                        MainActivity.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        } else {
            MainActivity.handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Unable to save card", Toast.LENGTH_SHORT).show();
                }
            });
        }
        finish();
    }

    private void saveCard(final Handler handler, final ContentValues contentValues,
                          final DbHelper dbHelper, final ResultCallback<Long> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final long id = dbHelper.insert(BusinessCard.class, contentValues);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(id);
                    }
                });
            }
        }).start();
    }
}
