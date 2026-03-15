package com.sony.imaging.app.synctosmartphone.webapi.util;

import android.content.Context;
import android.util.Log;
import com.sony.imaging.app.synctosmartphone.webapi.AutoSyncConstants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/* loaded from: classes.dex */
public class DeviceDescription {
    private static final String TAG = DeviceDescription.class.getSimpleName();
    protected String description;

    public DeviceDescription(Context context, String baseFile) {
        this.description = null;
        this.description = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream fileInputStream = context.getAssets().open(baseFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, AutoSyncConstants.TEXT_ENCODING_UTF8));
            while (true) {
                try {
                    String readStr = bufferedReader.readLine();
                    if (readStr == null) {
                        break;
                    } else {
                        stringBuilder.append(readStr + "\n");
                    }
                } catch (Exception e) {
                    e = e;
                    Log.e(TAG, "error" + e.toString());
                    return;
                }
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            this.description = stringBuilder.toString();
        } catch (Exception e2) {
            e = e2;
        }
    }

    public String getDescription() {
        return this.description;
    }
}
