package com.github.up209d.cubelut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class LutMenuActivity extends BaseActivity {
    public static final String EXTRA_SELECTED_INDEX = "selected_index";
    public static final String EXTRA_SELECTED_PATH = "selected_path";
    public static final int RESULT_LUT_SELECTED = 1;

    private ListView listView;
    private LutListAdapter adapter;
    private List<LutEntry> entries;
    private LutFileManager fileManager;
    private int currentActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        currentActive = getIntent().getIntExtra(EXTRA_SELECTED_INDEX, -1);

        fileManager = new LutFileManager(this);
        entries = fileManager.scanLuts();

        // Header info
        TextView headerTitle = (TextView) findViewById(R.id.header_title);
        TextView headerSource = (TextView) findViewById(R.id.header_source);
        TextView headerCount = (TextView) findViewById(R.id.header_count);

        headerTitle.setText("LUT Browser");
        headerSource.setText(fileManager.isUsingBundled()
                ? "Using bundled LUTs" : fileManager.getSourcePath());
        headerCount.setText(entries.size() + " LUTs found");

        // List
        listView = (ListView) findViewById(R.id.lut_list);
        adapter = new LutListAdapter(this, entries, fileManager, currentActive);
        listView.setAdapter(adapter);

        // Scroll to active item
        if (currentActive >= 0) {
            listView.setSelection(currentActive + 1); // +1 for "None" entry
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectLut(position);
            }
        });

        FileLogger.log("MENU", "Opened LUT browser (" + entries.size() + " LUTs)");
    }

    private void selectLut(int position) {
        Intent result = new Intent();
        if (position == 0) {
            // "None" selected
            result.putExtra(EXTRA_SELECTED_INDEX, -1);
            result.putExtra(EXTRA_SELECTED_PATH, "");
        } else {
            int entryIndex = position - 1;
            LutEntry entry = entries.get(entryIndex);
            result.putExtra(EXTRA_SELECTED_INDEX, entryIndex);
            result.putExtra(EXTRA_SELECTED_PATH, entry.cubePath);
            FileLogger.log("MENU", "Selected: " + entry.filename + " (index " + entryIndex + ")");
        }
        setResult(RESULT_LUT_SELECTED, result);
        finish();
    }

    @Override
    protected boolean onEnterKeyDown() {
        int pos = listView.getSelectedItemPosition();
        if (pos >= 0) {
            selectLut(pos);
        }
        return true;
    }

    @Override
    protected boolean onUpKeyDown() {
        int pos = listView.getSelectedItemPosition();
        if (pos > 0) {
            listView.setSelection(pos - 1);
        }
        return true;
    }

    @Override
    protected boolean onDownKeyDown() {
        int pos = listView.getSelectedItemPosition();
        if (pos < adapter.getCount() - 1) {
            listView.setSelection(pos + 1);
        }
        return true;
    }

    @Override
    protected boolean onDeleteKeyDown() {
        return true;
    }

    @Override
    protected boolean onDeleteKeyUp() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        return true;
    }
}
