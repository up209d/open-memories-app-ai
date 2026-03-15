package com.sony.imaging.app.manuallenscompensation.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.manuallenscompensation.commonUtil.ByteDataAnalyser;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCUtil;
import com.sony.imaging.app.manuallenscompensation.database.LensParameterProviderDefinition;
import com.sony.imaging.app.manuallenscompensation.shooting.controller.OCController;
import java.util.List;

/* loaded from: classes.dex */
public class LensCompensationParameter implements Parcelable {
    public static final String ID_PARAMETER = "LENS_COMPENSATION_PARAMTER";
    private static final int PORTABLE_FLG = 2;
    private static final int WRITABLE_FLG = 1;
    public String mLensName = "";
    public String mFocalLength = "";
    public String mFValue = OCConstants.BLANK_FVALUE_STR;
    protected int mID = 0;
    private int mLightVignetting = 0;
    private int mRedColorVignetting = 0;
    private int mBlueColorVignetting = 0;
    private int mRedChromaticAberration = 0;
    private int mBlueChromaticAberration = 0;
    private int mDistortion = 0;
    public boolean mWritable = true;
    public boolean mPortable = true;

    public static LensCompensationParameter createLensCompensationParameter() {
        LensCompensationParameter param = new LensCompensationParameter();
        return param;
    }

    public static LensCompensationParameter createCopiedObject(LensCompensationParameter srcParam) {
        LensCompensationParameter destParam = new LensCompensationParameter();
        destParam.mLensName = srcParam.mLensName;
        destParam.mFocalLength = srcParam.mFocalLength;
        destParam.mFValue = srcParam.mFValue;
        destParam.mLightVignetting = srcParam.mLightVignetting;
        destParam.mRedColorVignetting = srcParam.mRedColorVignetting;
        destParam.mBlueColorVignetting = srcParam.mBlueColorVignetting;
        destParam.mRedChromaticAberration = srcParam.mRedChromaticAberration;
        destParam.mBlueChromaticAberration = srcParam.mBlueChromaticAberration;
        destParam.mDistortion = srcParam.mDistortion;
        destParam.mID = srcParam.mID;
        return destParam;
    }

    public static LensCompensationParameter createCurrentCompensationParamter(OCController controller) {
        LensCompensationParameter param = createLensCompensationParameter();
        param.mLightVignetting = controller.getLensCorrectionLevel(OCController.LIGHT_VIGNETTING);
        param.mRedColorVignetting = controller.getLensCorrectionLevel(OCController.RED_COLOR_VIGNETTING);
        param.mBlueColorVignetting = controller.getLensCorrectionLevel(OCController.BLUE_COLOR_VIGNETTING);
        param.mRedChromaticAberration = controller.getLensCorrectionLevel(OCController.RED_CHROMATIC_ABERRATION);
        param.mBlueChromaticAberration = controller.getLensCorrectionLevel(OCController.BLUE_CHROMATIC_ABERRATION);
        param.mDistortion = controller.getLensCorrectionLevel(OCController.DISTORTION);
        return param;
    }

    public static LensCompensationParameter getCompensationParamterFromByteAnalyzer(ByteDataAnalyser byteAnalyzer) {
        LensCompensationParameter param = createLensCompensationParameter();
        if (byteAnalyzer.isValid() == 0) {
            param.mLensName = byteAnalyzer.getLensName();
            param.mFocalLength = String.valueOf(byteAnalyzer.getFocalLength());
            param.mFValue = byteAnalyzer.getMinFNumber();
            param.mLightVignetting = byteAnalyzer.getFMinLightVignettingCorrection();
            param.mRedColorVignetting = byteAnalyzer.getFMinRedColorVignettingCorrection();
            param.mBlueColorVignetting = byteAnalyzer.getFMinBlueColorVignettingCorrection();
            param.mRedChromaticAberration = byteAnalyzer.getFMinRedChromaticAberationCorrection();
            param.mBlueChromaticAberration = byteAnalyzer.getFMinBlueChromaticAberationCorrection();
            param.mDistortion = byteAnalyzer.getFMinDistortionCorrection();
            param.mID = -1;
        }
        return param;
    }

    public static LensCompensationParameter queryPreviousProfileParam(Context context, int paramID) {
        LensCompensationParameter param = null;
        ContentResolver resolver = context.getContentResolver();
        Cursor mCursor = resolver.query(LensParameterProviderDefinition.LensColumns.CONTENT_URI, null, "_id = " + paramID, null, null);
        if (mCursor != null && mCursor.moveToLast()) {
            param = getCompensationParameter(mCursor);
        }
        if (mCursor != null) {
            mCursor.close();
        }
        return param;
    }

    public static Cursor queryAllProfilesFromDatabase(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(LensParameterProviderDefinition.LensColumns.CONTENT_URI, null, null, null, OCConstants.SORT_ORDER);
        return cursor;
    }

    public static LensCompensationParameter[] getCompensationParameters(Context context, String sortOrder) {
        return null;
    }

    public static LensCompensationParameter[] getCompensationParameters(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(LensParameterProviderDefinition.LensColumns.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        LensCompensationParameter[] ret = getCompensationParameters(cursor);
        cursor.close();
        return ret;
    }

    public static LensCompensationParameter[] getCompensationParameters(Cursor cursor) {
        int size;
        LensCompensationParameter[] params = null;
        if (cursor.moveToFirst() && (size = cursor.getCount()) > 0) {
            params = new LensCompensationParameter[size];
            int[] indexes = getIndexes(cursor);
            int i = 0;
            while (i < size) {
                LensCompensationParameter param = getCompensationParameter(cursor, indexes);
                params[i] = param;
                i++;
                cursor.moveToNext();
            }
        }
        return params;
    }

    public static LensCompensationParameter getCompensationParameter(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        int[] indexes = getIndexes(cursor);
        LensCompensationParameter ret = getCompensationParameter(cursor, indexes);
        return ret;
    }

    private static LensCompensationParameter getCompensationParameter(Cursor cursor, int[] indexes) {
        LensCompensationParameter param = new LensCompensationParameter();
        param.mID = cursor.getInt(indexes[0]);
        param.mLensName = cursor.getString(indexes[1]);
        param.mFocalLength = cursor.getString(indexes[2]);
        param.mFValue = cursor.getString(indexes[3]);
        param.mLightVignetting = cursor.getInt(indexes[4]);
        param.mRedColorVignetting = cursor.getInt(indexes[5]);
        param.mBlueColorVignetting = cursor.getInt(indexes[6]);
        param.mRedChromaticAberration = cursor.getInt(indexes[7]);
        param.mBlueChromaticAberration = cursor.getInt(indexes[8]);
        param.mDistortion = cursor.getInt(indexes[9]);
        int optionFlg = cursor.getInt(indexes[10]);
        param.mWritable = (optionFlg & 1) > 0;
        param.mPortable = (optionFlg & 2) > 0;
        return param;
    }

    private static int[] getIndexes(Cursor cursor) {
        int[] indexes = {cursor.getColumnIndex("_id"), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.LENS_NAME), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.FOCAL_LENGTH), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.F_VALUE), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.LIGHT_VIGNETTING), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.RED_COLOR_VIGNETTING), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.BLUE_COLOR_VIGNETTING), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.RED_CHROMATIC_ABERRATION), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.BLUE_CHROMATIC_ABERRATION), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.DISTORTION), cursor.getColumnIndex(LensParameterProviderDefinition.LensColumns.OPTION_FLG)};
        return indexes;
    }

    public static int addCompensationParameter(Context context, LensCompensationParameter param) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = convertToValues(param);
        try {
            Uri uri = resolver.insert(LensParameterProviderDefinition.LensColumns.CONTENT_URI, values);
            List<String> segments = uri.getPathSegments();
            int id = Integer.parseInt(segments.get(segments.size() - 1));
            param.mID = id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int addImportedCompensationParameter(Context context, ByteDataAnalyser param) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = convertToValuesFromAnalyzer(param);
        try {
            Uri uri = resolver.insert(LensParameterProviderDefinition.LensColumns.CONTENT_URI, values);
            List<String> segments = uri.getPathSegments();
            int id = Integer.parseInt(segments.get(segments.size() - 1));
            Log.d(ID_PARAMETER, "addImportedCompensationParameter id= " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static ContentValues convertToValuesFromAnalyzer(ByteDataAnalyser param) {
        ContentValues values = new ContentValues();
        values.put(LensParameterProviderDefinition.LensColumns.LENS_NAME, param.getLensName());
        values.put(LensParameterProviderDefinition.LensColumns.FOCAL_LENGTH, param.getFocalLength());
        values.put(LensParameterProviderDefinition.LensColumns.F_VALUE, param.getMinFNumber());
        values.put(LensParameterProviderDefinition.LensColumns.LIGHT_VIGNETTING, Integer.valueOf(param.getFMinLightVignettingCorrection()));
        values.put(LensParameterProviderDefinition.LensColumns.RED_COLOR_VIGNETTING, Integer.valueOf(param.getFMinRedColorVignettingCorrection()));
        values.put(LensParameterProviderDefinition.LensColumns.BLUE_COLOR_VIGNETTING, Integer.valueOf(param.getFMinBlueColorVignettingCorrection()));
        values.put(LensParameterProviderDefinition.LensColumns.RED_CHROMATIC_ABERRATION, Integer.valueOf(param.getFMinRedChromaticAberationCorrection()));
        values.put(LensParameterProviderDefinition.LensColumns.BLUE_CHROMATIC_ABERRATION, Integer.valueOf(param.getFMinBlueChromaticAberationCorrection()));
        values.put(LensParameterProviderDefinition.LensColumns.DISTORTION, Integer.valueOf(param.getFMinDistortionCorrection()));
        values.put(LensParameterProviderDefinition.LensColumns.OPTION_FLG, (Integer) 1);
        return values;
    }

    public static boolean saveCompensationParameter(Context context, LensCompensationParameter param) {
        try {
            ContentValues values = convertToValues(param);
            ContentResolver resolver = context.getContentResolver();
            int id = param.mID;
            if (id > 0) {
                Uri uri = param.getUri();
                resolver.update(uri, values, null, null);
            } else {
                Uri uri2 = resolver.insert(LensParameterProviderDefinition.LensColumns.CONTENT_URI, values);
                List<String> segments = uri2.getPathSegments();
                int id2 = Integer.parseInt(segments.get(segments.size() - 1));
                param.mID = id2;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCompensationParameter(Context context, LensCompensationParameter sparseArray) {
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = sparseArray.getUri();
            int count = resolver.delete(uri, null, null);
            if (count == -1) {
                return true;
            }
            CameraNotificationManager.getInstance().requestNotify(OCConstants.TAG_DELETE_PROCESS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void emptyInternalDBLensTable(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri.Builder builder = LensParameterProviderDefinition.LensColumns.CONTENT_URI.buildUpon();
        Uri uri = builder.build();
        resolver.delete(uri, null, null);
    }

    private static ContentValues convertToValues(LensCompensationParameter param) {
        ContentValues values = new ContentValues();
        values.put(LensParameterProviderDefinition.LensColumns.LENS_NAME, param.mLensName);
        values.put(LensParameterProviderDefinition.LensColumns.FOCAL_LENGTH, param.mFocalLength);
        if (param.mFValue != null) {
            values.put(LensParameterProviderDefinition.LensColumns.F_VALUE, param.mFValue);
        } else {
            values.put(LensParameterProviderDefinition.LensColumns.F_VALUE, OCConstants.BLANK_FVALUE_STR);
        }
        values.put(LensParameterProviderDefinition.LensColumns.LIGHT_VIGNETTING, Integer.valueOf(param.mLightVignetting));
        values.put(LensParameterProviderDefinition.LensColumns.RED_COLOR_VIGNETTING, Integer.valueOf(param.mRedColorVignetting));
        values.put(LensParameterProviderDefinition.LensColumns.BLUE_COLOR_VIGNETTING, Integer.valueOf(param.mBlueColorVignetting));
        values.put(LensParameterProviderDefinition.LensColumns.RED_CHROMATIC_ABERRATION, Integer.valueOf(param.mRedChromaticAberration));
        values.put(LensParameterProviderDefinition.LensColumns.BLUE_CHROMATIC_ABERRATION, Integer.valueOf(param.mBlueChromaticAberration));
        values.put(LensParameterProviderDefinition.LensColumns.DISTORTION, Integer.valueOf(param.mDistortion));
        int optionFlg = (param.mWritable ? 1 : 0) + (param.mPortable ? 2 : 0);
        values.put(LensParameterProviderDefinition.LensColumns.OPTION_FLG, Integer.valueOf(optionFlg));
        return values;
    }

    private LensCompensationParameter() {
    }

    public int getId() {
        return this.mID;
    }

    public void setId(int id) {
        this.mID = id;
    }

    public Uri getUri() {
        Uri.Builder builder = LensParameterProviderDefinition.LensColumns.CONTENT_URI.buildUpon();
        builder.appendPath(String.valueOf(this.mID));
        return builder.build();
    }

    public void applyCompensationParameter() {
        OCController controller = OCController.getInstance();
        if (!OCController.getInstance().isSupportPictureEffect()) {
            OCUtil.getInstance().setShadingEffectOff();
        } else {
            controller.setLensCorrectionLevel(OCController.LIGHT_VIGNETTING, this.mLightVignetting);
            controller.setLensCorrectionLevel(OCController.RED_COLOR_VIGNETTING, this.mRedColorVignetting);
            controller.setLensCorrectionLevel(OCController.BLUE_COLOR_VIGNETTING, this.mBlueColorVignetting);
        }
        controller.setLensCorrectionLevel(OCController.RED_CHROMATIC_ABERRATION, this.mRedChromaticAberration);
        controller.setLensCorrectionLevel(OCController.BLUE_CHROMATIC_ABERRATION, this.mBlueChromaticAberration);
        controller.setLensCorrectionLevel(OCController.DISTORTION, this.mDistortion);
    }

    public void setLevel(String tag, int value) {
        if (OCController.LIGHT_VIGNETTING.equals(tag)) {
            this.mLightVignetting = value;
            return;
        }
        if (OCController.RED_COLOR_VIGNETTING.equals(tag)) {
            this.mRedColorVignetting = value;
            return;
        }
        if (OCController.BLUE_COLOR_VIGNETTING.equals(tag)) {
            this.mBlueColorVignetting = value;
            return;
        }
        if (OCController.RED_CHROMATIC_ABERRATION.equals(tag)) {
            this.mRedChromaticAberration = value;
        } else if (OCController.BLUE_CHROMATIC_ABERRATION.equals(tag)) {
            this.mBlueChromaticAberration = value;
        } else if (OCController.DISTORTION.equals(tag)) {
            this.mDistortion = value;
        }
    }

    public int getLevel(String tag) {
        if (OCController.LIGHT_VIGNETTING.equals(tag)) {
            int value = this.mLightVignetting;
            return value;
        }
        if (OCController.RED_COLOR_VIGNETTING.equals(tag)) {
            int value2 = this.mRedColorVignetting;
            return value2;
        }
        if (OCController.BLUE_COLOR_VIGNETTING.equals(tag)) {
            int value3 = this.mBlueColorVignetting;
            return value3;
        }
        if (OCController.RED_CHROMATIC_ABERRATION.equals(tag)) {
            int value4 = this.mRedChromaticAberration;
            return value4;
        }
        if (OCController.BLUE_CHROMATIC_ABERRATION.equals(tag)) {
            int value5 = this.mBlueChromaticAberration;
            return value5;
        }
        if (!OCController.DISTORTION.equals(tag)) {
            return 0;
        }
        int value6 = this.mDistortion;
        return value6;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mID);
        dest.writeString(this.mLensName);
        dest.writeString(this.mFocalLength);
        dest.writeString(this.mFValue);
        dest.writeInt(this.mLightVignetting);
        dest.writeInt(this.mRedColorVignetting);
        dest.writeInt(this.mBlueColorVignetting);
        dest.writeInt(this.mRedChromaticAberration);
        dest.writeInt(this.mBlueChromaticAberration);
        dest.writeInt(this.mDistortion);
        boolean[] flgs = {this.mWritable, this.mPortable};
        dest.writeBooleanArray(flgs);
    }
}
