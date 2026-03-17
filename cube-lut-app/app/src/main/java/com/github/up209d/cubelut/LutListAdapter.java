package com.github.up209d.cubelut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LutListAdapter extends BaseAdapter {
    private final Context context;
    private final List<LutEntry> entries;
    private final LutFileManager fileManager;
    private int activePosition;

    // "None" entry is at position 0, LUT entries start at position 1
    private static final int NONE_POSITION = 0;

    public LutListAdapter(Context context, List<LutEntry> entries,
                          LutFileManager fileManager, int activePosition) {
        this.context = context;
        this.entries = entries;
        this.fileManager = fileManager;
        this.activePosition = activePosition;
    }

    public void setActivePosition(int position) {
        this.activePosition = position;
    }

    @Override
    public int getCount() {
        return entries.size() + 1; // +1 for "None" entry
    }

    @Override
    public Object getItem(int position) {
        if (position == NONE_POSITION) return null;
        return entries.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lut_list_item, parent, false);
        }

        ImageView preview = (ImageView) convertView.findViewById(R.id.preview);
        TextView titleText = (TextView) convertView.findViewById(R.id.title);
        TextView filenameText = (TextView) convertView.findViewById(R.id.filename);
        TextView descText = (TextView) convertView.findViewById(R.id.description);
        TextView activeMarker = (TextView) convertView.findViewById(R.id.active_marker);

        if (position == NONE_POSITION) {
            // "None" entry
            preview.setImageBitmap(createPlaceholder("---", "", 0));
            titleText.setText("None (No LUT)");
            filenameText.setText("Passthrough - no grading");
            descText.setText("");
            activeMarker.setVisibility(activePosition == -1 ? View.VISIBLE : View.GONE);
        } else {
            LutEntry entry = entries.get(position - 1);

            // Set preview image
            if (entry.previewValid && entry.jpgPath != null) {
                Bitmap thumb = loadThumbnail(entry);
                if (thumb != null) {
                    preview.setImageBitmap(thumb);
                } else {
                    preview.setImageBitmap(createPlaceholder("No Preview",
                            entry.getAbbreviation(), entry.filename.hashCode()));
                }
            } else if (entry.previewWrongSize) {
                preview.setImageBitmap(createPlaceholder("128x128!",
                        entry.getAbbreviation(), entry.filename.hashCode()));
            } else {
                preview.setImageBitmap(createPlaceholder("No Preview",
                        entry.getAbbreviation(), entry.filename.hashCode()));
            }

            // Set text fields
            titleText.setText(entry.getDisplayTitle());
            filenameText.setText(entry.filename);
            String desc = "";
            if (entry.description != null && !entry.description.isEmpty()) {
                desc = entry.description;
            }
            String source = entry.isBundled ? "Bundled" : "SD";
            if (!desc.isEmpty()) {
                desc = desc + "  " + source;
            } else {
                desc = source;
            }
            descText.setText(desc);

            // Active marker
            activeMarker.setVisibility(position - 1 == activePosition ? View.VISIBLE : View.GONE);
        }

        // Highlight active row
        boolean isActive = (position == NONE_POSITION && activePosition == -1)
                || (position > 0 && position - 1 == activePosition);
        convertView.setBackgroundColor(isActive ? 0xFF1A1A2E : 0xFF000000);

        return convertView;
    }

    private Bitmap loadThumbnail(LutEntry entry) {
        InputStream is = null;
        try {
            is = fileManager.openPreviewStream(entry);
            if (is == null) return null;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 2; // 128x128 -> 64x64, displayed at 80x80
            return BitmapFactory.decodeStream(is, null, opts);
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) { /* ignore */ }
            }
        }
    }

    private Bitmap createPlaceholder(String text, String abbreviation, int seed) {
        int size = 80;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        // Random dark background seeded from filename hash
        int hash = Math.abs(seed);
        int r = 20 + (hash % 30);
        int g = 20 + ((hash / 30) % 30);
        int b = 30 + ((hash / 900) % 30);
        canvas.drawColor(Color.rgb(r, g, b));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);

        // Main text
        paint.setTextSize(10);
        canvas.drawText(text, size / 2f, size / 2f - 4, paint);

        // Abbreviation below
        if (abbreviation != null && !abbreviation.isEmpty()) {
            paint.setTextSize(16);
            paint.setColor(0xAAFFFFFF);
            canvas.drawText(abbreviation, size / 2f, size / 2f + 16, paint);
        }

        return bmp;
    }
}
