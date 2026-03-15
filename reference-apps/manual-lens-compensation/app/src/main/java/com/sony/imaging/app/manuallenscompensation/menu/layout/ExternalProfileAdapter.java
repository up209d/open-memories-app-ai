package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppContext;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataAnalyser;
import com.sony.imaging.app.manuallenscompensation.commonUtil.FileInfoDetails;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class ExternalProfileAdapter extends BaseAdapter implements View.OnTouchListener {
    private static final String TAG = "ExternalProfileAdapter";
    FileInputStream fin = null;
    private LayoutInflater mInflater;

    public ExternalProfileAdapter(Context context) {
        this.mInflater = null;
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mInflater = LayoutInflater.from(context);
        AppLog.exit(TAG, AppLog.getMethodName());
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.trace(TAG, "INSIDE getView");
        if (OCUtil.getInstance().getFileArray().size() == 0) {
            return null;
        }
        FileInfoDetails fileInfo = OCUtil.getInstance().getFileArray().get(position);
        byte[] data_bytes = new byte[OCConstants.FOUR_NINETY_SIX];
        ByteDataAnalyser byteDataAnalyser = new ByteDataAnalyser(data_bytes);
        if (fileInfo.getByteDataAnalyser() == null) {
            try {
                this.fin = new FileInputStream(fileInfo.getFile());
                this.fin.read(data_bytes);
                this.fin.close();
            } catch (FileNotFoundException e) {
                AppLog.info(TAG, AppLog.getMethodName() + e.getMessage());
            } catch (IOException e2) {
                AppLog.info(TAG, AppLog.getMethodName() + e2.getMessage());
            }
            byteDataAnalyser.setDataBytes(data_bytes);
            if (fileInfo.getFile().length() != 496) {
                byteDataAnalyser.setProfileValidation(false);
            }
            fileInfo.setByteDataAnalyser(byteDataAnalyser);
        } else {
            fileInfo.getByteDataAnalyser();
        }
        String fName = fileInfo.getFile().getName();
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.external_profile_list_style, (ViewGroup) null);
        }
        TextView lensNameView = (TextView) convertView.findViewById(R.id.LensName);
        TextView fileName = (TextView) convertView.findViewById(R.id.FileName);
        AppLog.info("LensSelectionAdapter", "position is " + position + "in the getView");
        if (fileInfo.getByteDataAnalyser().isValid() == 0) {
            String lensName = fileInfo.getByteDataAnalyser().getLensName();
            if (!lensName.isEmpty()) {
                lensNameView.setText(lensName);
            } else {
                lensNameView.setText("--");
            }
            lensNameView.setTextColor(AppContext.getAppContext().getResources().getColor(R.color.RESID_FONTSTYLE_CMN_WHITE_NO_SKIN));
        } else {
            lensNameView.setText(AppContext.getAppContext().getResources().getString(R.string.STRID_CAU_OPTICAL_COMPENSATION_ERROR_PROFILE));
            lensNameView.setTextColor(AppContext.getAppContext().getResources().getColor(R.color.RESID_FONTSTYLE_STD_FOCUSED));
        }
        fileName.setText(fName);
        convertView.setOnTouchListener(this);
        AppLog.exit(TAG, AppLog.getMethodName());
        return convertView;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        AppLog.enter(TAG, AppLog.getMethodName());
        int count = OCUtil.getInstance().getFileArray().size();
        AppLog.exit(TAG, AppLog.getMethodName());
        return count;
    }

    @Override // android.widget.Adapter
    public ByteDataAnalyser getItem(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        FileInfoDetails fileInfo = OCUtil.getInstance().getFileArray().get(position);
        ByteDataAnalyser analyser = null;
        if (fileInfo != null) {
            analyser = fileInfo.getByteDataAnalyser();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return analyser;
    }

    public int getItemValidity(int position) {
        int itemValidityRank = 0;
        FileInfoDetails fileInfo = OCUtil.getInstance().getFileArray().get(position);
        if (fileInfo != null) {
            itemValidityRank = fileInfo.getByteDataAnalyser().isValid();
        }
        return itemValidityRank;
    }

    public String getItemFileName(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        String fName = null;
        FileInfoDetails fileInfo = OCUtil.getInstance().getFileArray().get(position);
        if (fileInfo != null) {
            fName = fileInfo.getFile().getName();
        }
        AppLog.exit(TAG, AppLog.getMethodName());
        return fName;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        AppLog.enter(TAG, AppLog.getMethodName());
        AppLog.exit(TAG, AppLog.getMethodName());
        return 0L;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        AppLog.enter(TAG, "event " + event.getAction());
        AppLog.exit(TAG, AppLog.getMethodName());
        return false;
    }

    public void deinitialize() {
        AppLog.enter(TAG, AppLog.getMethodName());
        this.mInflater = null;
        AppLog.exit(TAG, AppLog.getMethodName());
    }
}
