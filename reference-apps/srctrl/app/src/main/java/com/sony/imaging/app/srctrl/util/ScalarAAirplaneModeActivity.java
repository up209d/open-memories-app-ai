package com.sony.imaging.app.srctrl.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.sony.imaging.app.srctrl.R;

/* loaded from: classes.dex */
public class ScalarAAirplaneModeActivity extends Activity implements View.OnClickListener {
    private static final String ACTION_NOTIFY_APP_INFO = "com.android.server.DAConnectionManagerService.AppInfoReceive";
    private static final String EXTRA_CLASS_NAME = "class_name";
    private static final String EXTRA_LARGE_CATEGORY = "large_category";
    private static final String EXTRA_PACKAGE_NAME = "package_name";
    private static final String EXTRA_PKEY = "pkey";
    private static final String EXTRA_SMALL_CATEGORY = "small_category";
    private static final String[] PKEY = {"KEY_LANC", "KEY_EVF", "KEY_MENU", "KEY_AFMF", "KEY_FN", "KEY_SK1", "KEY_SK2"};
    private SEPlayer mPlayer;
    private View mSkCenterButton;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        String custom_title;
        TextView titleView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv1_airplane_mode);
        View titleBar = findViewById(R.id.titlebar);
        titleBar.findViewById(R.id.sk1_icon).setVisibility(8);
        titleBar.findViewById(R.id.sk_1).setVisibility(8);
        findViewById(R.id.wifi_icon).setVisibility(8);
        this.mSkCenterButton = findViewById(R.id.sk_center);
        this.mSkCenterButton.setOnClickListener(this);
        this.mPlayer = new SEPlayer();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && (custom_title = extras.getString("CUSTOM_TITLE")) != null && (titleView = (TextView) findViewById(R.id.title_text)) != null) {
            titleView.setText(custom_title);
        }
    }

    public static void notifyAppInfo(Context context, String largeCategory, String smallCategory, String packageName, String className, String[] key) {
        Intent intent = new Intent(ACTION_NOTIFY_APP_INFO);
        intent.putExtra(EXTRA_LARGE_CATEGORY, largeCategory);
        intent.putExtra(EXTRA_SMALL_CATEGORY, smallCategory);
        intent.putExtra(EXTRA_CLASS_NAME, className);
        intent.putExtra(EXTRA_PACKAGE_NAME, packageName);
        intent.putExtra(EXTRA_PKEY, key);
        context.sendBroadcast(intent);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        notifyAppInfo(this, "", "", getComponentName().getPackageName(), getComponentName().getClassName(), PKEY);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.mPlayer.release();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case 23:
            case 66:
                this.mPlayer.play("BEEP_ID_SWITCH_MODE");
                finish();
                overridePendingTransition(0, 0);
                return true;
            default:
                return false;
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mSkCenterButton) {
            this.mPlayer.play("BEEP_ID_SELECT");
            finish();
            overridePendingTransition(0, 0);
        }
    }
}
