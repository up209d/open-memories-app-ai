package com.sony.imaging.app.lightgraffiti.shooting.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sony.imaging.app.fw.Layout;
import com.sony.imaging.app.lightgraffiti.R;
import com.sony.imaging.app.lightgraffiti.util.LGUtility;

/* loaded from: classes.dex */
public class LGProgressLayout extends Layout {
    private static final String TAG = LGProgressLayout.class.getSimpleName();

    @Override // com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = obtainViewFromPool(R.layout.shooting_main_progress);
        return view;
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "getVisibility : " + getView().getVisibility());
        getView().setVisibility(0);
        super.onResume();
    }

    @Override // com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        LGUtility.getInstance().setAfterProgress(true);
        super.onPause();
    }
}
