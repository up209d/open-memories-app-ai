package com.sony.imaging.app.digitalfilter.menu.layout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.sony.imaging.app.base.common.DisplayModeObserver;
import com.sony.imaging.app.base.menu.layout.CursorableGridView;
import com.sony.imaging.app.base.menu.layout.Fn15LayerAbsLayout;
import com.sony.imaging.app.base.menu.layout.FnMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.ISOSensitivityController;
import com.sony.imaging.app.base.shooting.camera.WhiteBalanceController;
import com.sony.imaging.app.digitalfilter.R;
import com.sony.imaging.app.digitalfilter.common.GFConstants;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFEEAreaController;
import com.sony.imaging.app.digitalfilter.shooting.camera.GFWhiteBalanceLimitController;
import com.sony.imaging.app.digitalfilter.shooting.widget.BorderView;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class GFFnMenuLayout extends FnMenuLayout implements NotificationListener {
    private static CursorableGridView mGridView;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListenerForGridView;
    private static BorderView mBorderView = null;
    private static final String[] tags = {DisplayModeObserver.TAG_DEVICE_CHANGE, DisplayModeObserver.TAG_YUVLAYOUT_CHANGE, DisplayModeObserver.TAG_VIEW_PATTERN_CHANGE, DisplayModeObserver.TAG_DISPMODE_CHANGE, "com.sony.imaging.app.base.common.DisplayModeObserver.OledScreenChange", GFConstants.TAG_GYROSCOPE};

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = super.onCreateView(inflater, container, savedInstanceState);
        mBorderView = (BorderView) currentView.findViewById(R.id.border_view);
        mGridView = (CursorableGridView) currentView.findViewById(R.id.grid_view);
        this.mOnItemSelectedListenerForGridView = new OnItemSelectedListenerForGridView();
        return currentView;
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        System.gc();
        super.onResume();
        mGridView.setOnItemSelectedListener(null);
        mGridView.setOnItemSelectedListener(this.mOnItemSelectedListenerForGridView);
        updateItemText();
        DisplayModeObserver.getInstance().setNotificationListener(this);
        CameraNotificationManager.getInstance().setNotificationListener(this);
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        mGridView.setOnItemSelectedListener(null);
        DisplayModeObserver.getInstance().removeNotificationListener(this);
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        mGridView = null;
        mBorderView = null;
        this.mOnItemSelectedListenerForGridView = null;
        super.onPause();
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        System.gc();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void setFunctionGuideResources(ArrayList<Object> guideResources) {
        super.setFunctionGuideResources(guideResources);
        if (GFEEAreaController.getInstance().isSky()) {
            String guideTitleID = (String) guideResources.get(0);
            String guideDefi = (String) guideResources.get(1);
            if (isExposureComp()) {
                guideTitleID = getString(R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY);
            } else if (isISO()) {
                guideTitleID = getString(R.string.STRID_FUNC_DF_ISO_SKY);
            } else if (isWhiteBalance()) {
                guideTitleID = getString(R.string.STRID_FUNC_DF_WB_SKY);
            }
            guideResources.clear();
            guideResources.add(guideTitleID);
            guideResources.add(guideDefi);
            guideResources.add(true);
            return;
        }
        if (GFEEAreaController.getInstance().isLayer3()) {
            String guideTitleID2 = (String) guideResources.get(0);
            String guideDefi2 = (String) guideResources.get(1);
            if (isExposureComp()) {
                guideTitleID2 = getString(R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3);
            } else if (isISO()) {
                guideTitleID2 = getString(R.string.STRID_FUNC_DF_ISO_3RD_AREA);
            } else if (isWhiteBalance()) {
                guideTitleID2 = getString(R.string.STRID_FUNC_DF_WB_3RD_AREA);
            }
            guideResources.clear();
            guideResources.add(guideTitleID2);
            guideResources.add(guideDefi2);
            guideResources.add(true);
        }
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        Bundle b = new Bundle();
        if (!isWhiteBalance()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        Bundle b = new Bundle();
        if (!isWhiteBalance()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 4);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialNext() {
        Bundle b = new Bundle();
        if (!isWhiteBalance()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 1);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 3);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    @Override // com.sony.imaging.app.base.menu.layout.FnMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedThirdDialPrev() {
        Bundle b = new Bundle();
        if (!isWhiteBalance()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 2);
        } else if (GFWhiteBalanceLimitController.getInstance().isLimitToCTemp()) {
            b.putInt(Fn15LayerAbsLayout.BUNDLE_KEY_ACTION, 4);
        }
        openFn15LayerLayout(b);
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateItemText() {
        if (GFEEAreaController.getInstance().isSky()) {
            if (isExposureComp()) {
                this.mItemTextView.setText(R.string.STRID_FUNC_DF_EXPOSURECOMP_SKY);
                return;
            } else if (isISO()) {
                this.mItemTextView.setText(R.string.STRID_FUNC_DF_ISO_SKY);
                return;
            } else {
                if (isWhiteBalance()) {
                    this.mItemTextView.setText(R.string.STRID_FUNC_DF_WB_SKY);
                    return;
                }
                return;
            }
        }
        if (GFEEAreaController.getInstance().isLayer3()) {
            if (isExposureComp()) {
                this.mItemTextView.setText(R.string.STRID_FUNC_DF_EXPOSURECOMP_LAYER3);
            } else if (isISO()) {
                this.mItemTextView.setText(R.string.STRID_FUNC_DF_ISO_3RD_AREA);
            } else if (isWhiteBalance()) {
                this.mItemTextView.setText(R.string.STRID_FUNC_DF_WB_3RD_AREA);
            }
        }
    }

    private boolean isExposureComp() {
        String itemId = getItemId();
        return itemId != null && "ExposureCompensation".equals(itemId);
    }

    private boolean isISO() {
        String itemId = getItemId();
        return itemId != null && ISOSensitivityController.MENU_ITEM_ID_ISO.equals(itemId);
    }

    private boolean isWhiteBalance() {
        String itemId = getItemId();
        return itemId != null && WhiteBalanceController.WHITEBALANCE.equals(itemId);
    }

    /* loaded from: classes.dex */
    private class OnItemSelectedListenerForGridView implements AdapterView.OnItemSelectedListener {
        private OnItemSelectedListenerForGridView() {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String itemId = GFFnMenuLayout.this.getItemId();
            if (itemId != null) {
                GFFnMenuLayout.this.mItemTextView.setText(GFFnMenuLayout.this.mService.getMenuItemText(itemId));
                GFFnMenuLayout.this.updateItemText();
            }
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        mBorderView.invalidate();
    }
}
