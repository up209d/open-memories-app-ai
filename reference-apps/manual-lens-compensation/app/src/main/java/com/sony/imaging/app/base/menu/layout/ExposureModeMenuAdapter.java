package com.sony.imaging.app.base.menu.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.menu.BaseMenuService;
import com.sony.imaging.app.base.shooting.trigger.ModeDialDetector;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.util.StringBuilderThreadLocal;
import java.util.List;

/* loaded from: classes.dex */
public class ExposureModeMenuAdapter extends BaseMenuAdapter {
    private static final String LOG_MSG_NOT_FOUND_RES = "Not found resouce,  item :";
    private static final String MR1_ICON = "android:drawable/p_16_dd_parts_expmodeguiden_mr1_divi1";
    private static final String MR1_SELECTED_ICON = "android:drawable/p_16_dd_parts_expmodeguidef_mr1_divi1";
    private static final String TAG = "ExposureModeMenuAdapter";

    public ExposureModeMenuAdapter(Context context, int ResId, BaseMenuService service) {
        super(context, ResId, service);
    }

    public Drawable getBackgroundDrawable(int position) {
        String itemId = getItem(position);
        String name = this.mService.getMenuItemOptionStr(itemId);
        if (name == null) {
            return null;
        }
        Drawable background = getResources().getDrawable(getResources().getIdentifier(name, null, getContext().getPackageName()));
        return background;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ImageView iv = (ImageView) v.findViewById(R.id.submenu_list_icon);
        iv.setScaleType(ImageView.ScaleType.CENTER);
        return v;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
    public Drawable getMenuItemDrawable(int position) {
        String itemId = this.mItems.get(position);
        Drawable icon = this.mIconCache.getImage(itemId);
        if (icon == null) {
            icon = this.mService.getMenuItemDrawable(itemId);
            List<Integer> list = ModeDialDetector.getSupportedModeDials();
            if (list != null && itemId.equals("ExposureMode_custom1") && list.contains(Integer.valueOf(AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM2))) {
                icon = getSpecialDrawables(itemId, MR1_ICON);
            }
            this.mIconCache.setImage(itemId, icon);
        }
        if (icon == null) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_NOT_FOUND_RES);
            builder.append(this.mItems.get(position));
            Log.e(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
        return icon;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuAdapter
    protected Drawable getMenuItemSelectedDrawable(int position) {
        String itemId = this.mItems.get(position);
        Drawable icon = this.mSelectedIconCache.getImage(itemId);
        if (icon == null) {
            icon = this.mService.getMenuItemSelectedDrawable(itemId);
            List<Integer> list = ModeDialDetector.getSupportedModeDials();
            if (list != null && itemId.equals("ExposureMode_custom1") && list.contains(Integer.valueOf(AppRoot.USER_KEYCODE.MODE_DIAL_CUSTOM2))) {
                icon = getSpecialDrawables(itemId, MR1_SELECTED_ICON);
            }
            this.mSelectedIconCache.setImage(itemId, icon);
        }
        if (icon == null) {
            StringBuilder builder = StringBuilderThreadLocal.getScratchBuilder();
            builder.replace(0, builder.length(), LOG_MSG_NOT_FOUND_RES);
            builder.append(this.mItems.get(position));
            Log.e(TAG, builder.toString());
            StringBuilderThreadLocal.releaseScratchBuilder(builder);
        }
        return icon;
    }

    private Drawable getSpecialDrawables(String itemId, String iconName) {
        int resId = getResources().getIdentifier(iconName, null, getContext().getPackageName());
        Drawable icon = getResources().getDrawable(resId);
        return icon;
    }

    public int getItemPosition(String itemId) {
        int length = getCount();
        for (int i = 0; i < length; i++) {
            if (itemId.equals(getItem(i))) {
                int position = i;
                return position;
            }
        }
        return -1;
    }
}
