package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.database.LensCompensationParameter;
import com.sony.imaging.app.manuallenscompensation.database.LensParameterProviderDefinition;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;

/* loaded from: classes.dex */
public class LensSelectionAdapter extends BaseAdapter implements View.OnTouchListener {
    private static final String TAG = "LensSelectionAdapter";
    private Cursor mCursor;
    private LayoutInflater mInflater;

    public LensSelectionAdapter(Context context, Cursor c) {
        this.mInflater = null;
        this.mCursor = null;
        this.mCursor = c;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "INSIDE getView");
        if (!this.mCursor.moveToPosition(position)) {
            return null;
        }
        String lensName = this.mCursor.getString(this.mCursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.LENS_NAME));
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.lens_list_style, (ViewGroup) null);
        }
        TextView lensNameView = (TextView) convertView.findViewById(R.id.LensName);
        Log.d(TAG, "position is " + position + "in the getView");
        if (!lensName.isEmpty()) {
            lensNameView.setText(lensName);
        } else {
            lensNameView.setText("--");
        }
        convertView.setOnTouchListener(this);
        return convertView;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int count = this.mCursor.getCount();
        return count;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        if (this.mCursor == null || !this.mCursor.moveToPosition(position)) {
            return null;
        }
        LensCompensationParameter param = LensCompensationParameter.getCompensationParameter(this.mCursor);
        return param;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return 0L;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        AppLog.enter(TAG, "event " + event.getAction());
        BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
        return false;
    }

    public void deinitialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mInflater = null;
        this.mCursor = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
