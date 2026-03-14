package com.github.up209d.openmemories.app;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.github.ma1co.openmemories.framework.ImageInfo;
import com.github.ma1co.openmemories.framework.MediaManager;
import com.github.up209d.openmemories.easylut.EasyLUT;
import com.github.up209d.openmemories.easylut.filter.Filter;
import com.github.up209d.openmemories.easylut.filter.LutFilterFromResource;
import com.github.up209d.openmemories.easylut.lutimage.CoordinateToColor;
import com.github.up209d.openmemories.easylut.lutimage.LutAlignment;

public class ImageActivityEasyLut extends BaseActivity {
    private Bitmap image;

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
        image = BitmapUtil.fixOrientation(BitmapFactory.decodeStream(info.getThumbnail()), info.getOrientation());
        LutFilterFromResource.Builder haldRgb =
                EasyLUT.fromResourceId().withColorAxes(CoordinateToColor.Type.RGB_TO_XYZ).withResources(resources)
                        .withAlignmentMode(LutAlignment.Mode.HALD);
        final Filter filter = haldRgb.withLutBitmapId(R.drawable.filter_classic_negative).createFilter();
        new AsyncTask<Void, Void, Bitmap>() {
            long start;

            @Override
            protected void onPreExecute() {
                start = System.nanoTime();
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                filter.apply(image);
                Bitmap fullImage = BitmapFactory.decodeStream(info.getFullImage());
                if (fullImage != null) {
                    filter.apply(fullImage);
                }
                return filter.apply(image);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        image.recycle();
        image = null;
    }
}
