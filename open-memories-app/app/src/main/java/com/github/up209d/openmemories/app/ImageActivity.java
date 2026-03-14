package com.github.up209d.openmemories.app;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.github.ma1co.openmemories.framework.ImageInfo;
import com.github.ma1co.openmemories.framework.MediaManager;

public class ImageActivity extends BaseActivity {
    private Bitmap input;
    private Bitmap lut;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        final ScalingBitmapView imageView = (ScalingBitmapView) findViewById(R.id.imageView);
        long id = getIntent().getLongExtra("id", 0);

        MediaManager mediaManager = MediaManager.create(this);
        ImageInfo info = mediaManager.getImageInfo(id);
        Resources resources = getResources();
        input = BitmapUtil.fixOrientation(BitmapFactory.decodeStream(info.getThumbnail()), info.getOrientation());
        int[] inputPixels = new int[input.getWidth() * input.getHeight()];
        input.getPixels(inputPixels, 0, input.getWidth(), 0, 0, input.getWidth(), input.getHeight());
        lut = BitmapFactory.decodeResource(resources, R.drawable.filter_develop_room);
        int[] lutPixels = new int[lut.getWidth() * lut.getHeight()];
        lut.getPixels(lutPixels, 0, lut.getWidth(), 0, 0, lut.getWidth(), lut.getHeight());
        new AsyncTask<Void, Void, Bitmap>() {
            long start;

            @Override
            protected void onPreExecute() {
                start = System.nanoTime();
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                MainActivity mainActivity = new MainActivity();
                int[] inputData = new int[inputPixels.length * 4];
                for (int i = 0; i < inputPixels.length; i++) {
                    inputData[i * 4] = (int) Color.red(inputPixels[i]);
                    inputData[i * 4 + 1] = (int) Color.green(inputPixels[i]);
                    inputData[i * 4 + 2] = (int) Color.blue(inputPixels[i]);
                    inputData[i * 4 + 3] = (int) Color.alpha(inputPixels[i]);
                }
                input.recycle();
                int[] lutData = new int[lutPixels.length * 4];
                for (int i = 0; i < lutPixels.length; i++) {
                    lutData[i * 4] = (int) Color.red(lutPixels[i]);
                    lutData[i * 4 + 1] = (int) Color.green(lutPixels[i]);
                    lutData[i * 4 + 2] = (int) Color.blue(lutPixels[i]);
                    lutData[i * 4 + 3] = (int) Color.alpha(lutPixels[i]);
                }
                lut.recycle();
                int[] processedData = mainActivity.processData(inputData, lutData);
                int[] processedPixels = new int[processedData.length / 4];
                for (int i = 0; i < processedData.length; i += 4) {
                    processedPixels[i / 4] =
                            Color.argb(
                                    processedData[i + 3],
                                    processedData[i],
                                    processedData[i + 1],
                                    processedData[i + 2]
                            );
                }
                Bitmap bitmap = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
                bitmap.setPixels(processedPixels, 0, input.getWidth(), 0, 0, input.getWidth(), input.getHeight());
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Log.v("Performance: ", String.valueOf(System.nanoTime() - start));
                imageView.setImageBitmap(bitmap);
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        input.recycle();
        input = null;
        lut.recycle();
        lut = null;
    }
}
