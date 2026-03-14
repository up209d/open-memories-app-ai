package com.github.up209d.openmemories.app;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyActivity extends BaseActivity {
    protected class PropertyListItem extends ListAdapter.ListItem {
        private final int nameResource;
        private String value;

        public PropertyListItem(int nameResource, String value) {
            this.nameResource = nameResource;
            this.value = value;
        }
        @Override
        public String getText1() {
            return getResources().getString(nameResource);
        }
        @Override
        public String getText2() {
            return value;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PropertyListItem properties[] = {
                new PropertyListItem(R.string.internal_free_space,
                        Math.ceil(Environment.getDataDirectory().getFreeSpace() / 1024.0 / 1024.0) + " MBs"
                ),
                new PropertyListItem(R.string.external_free_space,
                        Math.ceil(Environment.getExternalStorageDirectory().getFreeSpace() / 1024.0 / 1024.0) + " MBs"
                ),
                new PropertyListItem(R.string.free_ram,
                        Math.ceil(this.getFreeRAM() / 1024 / 1024) + " MBs"
                ),
                new PropertyListItem(R.string.total_ram,
                        Math.ceil(this.getTotalRAM() / 1024.0 / 1024.0) + " MBs"
                ),
                new PropertyListItem(R.string.type, getDeviceInfo().isCamera() ? "Camera" : "Other"),
                new PropertyListItem(R.string.brand, getDeviceInfo().getBrand()),
                new PropertyListItem(R.string.model, getDeviceInfo().getModel()),
                new PropertyListItem(R.string.category, getDeviceInfo().getCategory().toString()),
                new PropertyListItem(R.string.productCode, getDeviceInfo().getProductCode()),
                new PropertyListItem(R.string.serial, getDeviceInfo().getSerialNumber()),
                new PropertyListItem(R.string.firmwareVersion, getDeviceInfo().getFirmwareVersion()), new PropertyListItem(R.string.hardwareVersion, Integer.toString(getDeviceInfo().getHardwareVersion())),
                new PropertyListItem(R.string.apiVersion, Integer.toString(getDeviceInfo().getApiVersion())),
                new PropertyListItem(R.string.androidVersion, getDeviceInfo().getAndroidVersion()),
                new PropertyListItem(R.string.androidSdk, Integer.toString(getDeviceInfo().getAndroidSdkVersion())),
                new PropertyListItem(R.string.androidIncremental, getDeviceInfo().getAndroidIncrementalVersion()),
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ListAdapter<PropertyListItem>(this, properties));
    }

    public double getFreeRAM() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return 0.0;
        }
        activityManager.getMemoryInfo(mi);
        return Double.parseDouble(String.valueOf(mi.availMem));
    }

    public double getTotalRAM() {
        RandomAccessFile reader = null;
        String load = null;
        double totRam = 0;
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
            }
            reader.close();
            totRam = Double.parseDouble(value);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return totRam * 1024;
    }
}
