package com.main.carassistant.Pollock;

import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Pollock {

    private byte[] imgByte;

    public void drawImage (final ImageView imageView, final String imgUrl) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null ;

                try {
                    URL url = new URL(imgUrl);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    byte[] buffer = new byte[inputStream.available()];
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    while ( inputStream.read(buffer) != -1) {
                        byteArrayOutputStream.write(buffer);
                    }

                    imgByte = byteArrayOutputStream.toByteArray();
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(imgByte, 0 ,imgByte.length));
                    inputStream.close();
                }
                catch(Throwable t) {
                    t.printStackTrace();
                }
                finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }
        });

        thread.start();
    }
}

