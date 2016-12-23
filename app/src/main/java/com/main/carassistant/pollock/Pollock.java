package com.main.carassistant.pollock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.main.carassistant.http.HttpClient;
import com.main.carassistant.threads.OnResultCallback;
import com.main.carassistant.threads.Operation;
import com.main.carassistant.threads.ProgressCallback;
import com.main.carassistant.threads.ThreadManager;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;

public class Pollock {

    private static Pollock instance = new Pollock();
    private static final int MAX_MEMORY_FOR_IMAGES = 64 * 1000 * 1000;
    private final LruCache<String, Bitmap> lruCache;
    private final Object lock = new Object();
    private ThreadManager threadManager = new ThreadManager(Executors.newFixedThreadPool(ThreadManager.COUNT_CORE));
    private BitmapOperation bitmapOperation = new BitmapOperation();

    private Pollock() {
        this.lruCache = new LruCache<String, Bitmap>(Math.min((int) (Runtime.getRuntime().maxMemory() / 4), MAX_MEMORY_FOR_IMAGES)) {

//            @Override
//            protected int sizeOf(final String key, final Bitmap value) {
//                return key.length() + value.getByteCount();
//            }
        };
    }

    public static Pollock getInstance() {
        if (instance == null) {
            instance = new Pollock();
        }
        return instance;
    }

    public void drawImage(final ImageView imageView, final String imgUrl) {

        synchronized (lock) {
            final Bitmap bitmap = lruCache.get(imgUrl);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        threadManager.execute(bitmapOperation, imgUrl, new BitmapResultCallback(imgUrl, imageView) {
            @Override
            public void onSuccess(Bitmap bitmap) {
                synchronized (lock) {
                    if (bitmap != null) {
                        lruCache.put(imgUrl, bitmap);
                    }
                }
                super.onSuccess(bitmap);
            }
        });
    }

    private static class BitmapOperation implements Operation<String, Void, Bitmap> {

        @Override
        public Bitmap doing(final String s, final ProgressCallback<Void> progressCallback) throws Exception {
            return new HttpClient().getResult(s, new HttpClient.ResultConverter<Bitmap>() {

                @Override
                public Bitmap convert(final InputStream inputStream) {
                    return BitmapFactory.decodeStream(inputStream);
                }
            });
        }
    }

    private static class BitmapResultCallback implements OnResultCallback<Bitmap, Void> {

        private String value;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapResultCallback(final String value, final ImageView imgView) {
            this.value = value;
            this.imageViewReference = new WeakReference<>(imgView);
            imgView.setTag(value);
        }

        @Override
        public void onSuccess(final Bitmap bitmap) {
            ImageView imageView = this.imageViewReference.get();
            if (imageView != null) {
                Object tag = imageView.getTag();
                if (tag != null && tag.equals(value)) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        public void onError(final Exception e) {
            e.printStackTrace();
        }

        @Override
        public void onProgressChanged(final Void aVoid) {

        }
    }
}

