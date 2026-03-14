package com.github.up209d.openmemories.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    static {
        System.loadLibrary("native-lib");
        new File(Environment.getExternalStorageDirectory(), "UPOpenMemoriesApp");
    }

    public native String getGreetingFromJNI();
    public native int[] processData(int[] inputData, int[] lutData);

    protected class ActivityListItem extends ListAdapter.ListItem {
        private int nameResource;
        private Class<? extends Activity> clazz;

        public ActivityListItem(int nameResource, Class<? extends Activity> clazz) {
            this.nameResource = nameResource;
            this.clazz = clazz;
        }

        @Override
        public String getText1() {
            return getResources().getString(nameResource);
        }

        public Class<? extends Activity> getActivityClass() {
            return clazz;
        }
    }

    protected ActivityListItem activities[] = {
            new ActivityListItem(R.string.title_activity_playback, PlaybackActivity.class),
            new ActivityListItem(R.string.title_activity_property, PropertyActivity.class),
            new ActivityListItem(R.string.title_activity_camera, CameraActivity.class),
            new ActivityListItem(R.string.title_activity_key_event, KeyEventActivity.class),
            new ActivityListItem(R.string.title_activity_time, TimeActivity.class),
            new ActivityListItem(R.string.title_activity_wifi, WifiActivity.class),
            new ActivityListItem(R.string.title_activity_wifi_setting, WifiSettingActivity.class),
            new ActivityListItem(R.string.title_activity_wifi_direct, WifiDirectActivity.class),
            new ActivityListItem(R.string.title_activity_display, DisplayActivity.class),
            new ActivityListItem(R.string.title_activity_led, LedActivity.class),
            new ActivityListItem(R.string.title_activity_install, InstallActivity.class),
    };

    @SuppressLint("StaticFieldLeak")
    protected void renderImageView() {
        setContentView(R.layout.image);
        final ScalingBitmapView imageView = (ScalingBitmapView) findViewById(R.id.imageView);
        Resources resources = getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap input = BitmapFactory.decodeResource(resources, R.drawable.test_input);
        int[] inputPixels = new int[input.getWidth() * input.getHeight()];
        input.getPixels(inputPixels, 0, input.getWidth(), 0, 0, input.getWidth(), input.getHeight());
        Bitmap lut = BitmapFactory.decodeResource(resources, R.drawable.filter_classic_negative);
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
                Log.i("Message fron JNI: ", getGreetingFromJNI());
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
                int[] processedData = processData(inputData, lutData);
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

    protected void renderListView() {
        setContentView(R.layout.list);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                StringWriter sw = new StringWriter();
                sw.append(throwable.toString());
                sw.append("\n");
                throwable.printStackTrace(new PrintWriter(sw));
                Logger.error(sw.toString());

                System.exit(0);
            }
        });
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ListAdapter<ActivityListItem>(this, activities));
        listView.setOnItemClickListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.renderListView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ActivityListItem item = (ActivityListItem) adapterView.getItemAtPosition(position);
        startActivity(new Intent(this, item.getActivityClass()));
    }
}
