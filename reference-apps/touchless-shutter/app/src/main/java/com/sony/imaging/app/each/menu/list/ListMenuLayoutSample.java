package com.sony.imaging.app.each.menu.list;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.sony.imaging.app.base.BaseApp;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.caution.Info;
import com.sony.imaging.app.base.menu.layout.list.DefaultListMenuItem;
import com.sony.imaging.app.base.menu.layout.list.DefaultListMenuLayout;
import com.sony.imaging.app.base.menu.layout.list.ValueSelectorContent;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.touchlessshutter.R;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ListMenuLayoutSample extends DefaultListMenuLayout {
    private static final String TAG = ListMenuLayoutSample.class.getSimpleName();

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected ArrayList<DefaultListMenuItem> getMenuItems() {
        ArrayList<ValueSelectorContent> valueFor2 = new ArrayList<>();
        valueFor2.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        valueFor2.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        valueFor2.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        valueFor2.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        valueFor2.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        valueFor2.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        ArrayList<ValueSelectorContent> value = new ArrayList<>();
        value.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        value.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        value.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        value.add(new ValueSelectorContent(R.string.STRID_DLAPP_SAMPLE, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
        return getItemsToBeListed(new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, valueFor2, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, null, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, value, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, null, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, valueFor2, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, value, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, valueFor2, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, null, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, value, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)), new DefaultListMenuItem(R.string.STRID_DLAPP_SAMPLE, value, Integer.valueOf(R.string.STRID_DLAPP_SAMPLE), Integer.valueOf(R.string.app_name)));
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        getMenuItem(3).setEnabled(false);
        getMenuItem(5).setEnabled(false);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout, com.sony.imaging.app.base.menu.layout.list.AbstractListMenuAdapter.ListMenuSelectedListener
    public void onItemSelected(int index, boolean isEnabled) {
        super.onItemSelected(index, isEnabled);
        if (isEnabled) {
            getMenuItem(index);
            switch (item.getItemType()) {
                case SELECTOR:
                    openSelector();
                    return;
                default:
                    String str = "Opener Item " + index + " is selected!!";
                    Toast.makeText(getActivity().getApplicationContext(), str, 0).show();
                    return;
            }
        }
        CautionUtilityClass.getInstance().requestTrigger(Info.CAUTION_ID_DLAPP_INVALID_FUNCTION);
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getTitleBarStringId() {
        return R.string.app_name;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected boolean isInfiniteScrollEnabled() {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected boolean isInfiniteScrollByTouchEnabled() {
        return true;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int onKeyDownEx(int keyCode, int scanCode, KeyEvent event) {
        Log.d(TAG, "onKeyDownEx: " + scanCode);
        switch (scanCode) {
            case AppRoot.USER_KEYCODE.SK1 /* 229 */:
                BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_BACK_CANCEL);
                ((BaseApp) getActivity()).finish(false);
                return 1;
            default:
                return -1;
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int getInitialSelectedPosition() {
        return 0;
    }

    @Override // com.sony.imaging.app.base.menu.layout.list.AbstractListMenuLayout
    protected int onKeyUpEx(int keyCode, int scanCode, KeyEvent event) {
        return -1;
    }
}
