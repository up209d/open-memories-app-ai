package com.sony.imaging.app.base.caution;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class RsrcBase implements Parcelable {
    public String beep_id;
    public String blink_period;
    public int cautionKind;
    public int cautionType;
    public String cold_off;
    private DatabaseManager dbManager;
    public String image_ids;
    public String layout_type;
    public String led_ids;
    public String mute;
    public String off_factor;
    public String string_ids;
    public int time_out;
    private static final String TAG = RsrcBase.class.getSimpleName();
    public static final Parcelable.Creator<RsrcBase> CREATOR = new Parcelable.Creator<RsrcBase>() { // from class: com.sony.imaging.app.base.caution.RsrcBase.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RsrcBase createFromParcel(Parcel source) {
            return new RsrcBase(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RsrcBase[] newArray(int size) {
            return new RsrcBase[size];
        }
    };

    RsrcBase() {
        this.dbManager = DatabaseManager.getInstance();
        this.string_ids = null;
        this.image_ids = null;
        this.beep_id = null;
        this.led_ids = null;
        this.layout_type = null;
        this.mute = null;
        this.blink_period = null;
        this.time_out = 0;
        this.off_factor = null;
        this.cold_off = null;
        this.cautionType = 0;
        this.cautionKind = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RsrcBase(Cursor c, int type, int kind) {
        this.dbManager = DatabaseManager.getInstance();
        this.string_ids = getStrIds(c);
        this.image_ids = getImageIds(c);
        this.beep_id = getBeepId(c);
        this.led_ids = getLedIds(c);
        this.layout_type = getLayoutType(c);
        this.mute = getMute(c);
        this.blink_period = getBlinkPeriod(c);
        this.time_out = getTimeOut(c);
        this.off_factor = getOffFactor(c);
        this.cold_off = getColdOff(c);
        this.cautionType = type;
        this.cautionKind = kind;
    }

    private RsrcBase(Parcel in) {
        this.dbManager = DatabaseManager.getInstance();
        this.string_ids = in.readString();
        this.image_ids = in.readString();
        this.beep_id = in.readString();
        this.led_ids = in.readString();
        this.layout_type = in.readString();
        this.mute = in.readString();
        this.blink_period = in.readString();
        this.time_out = in.readInt();
        this.off_factor = in.readString();
        this.cold_off = in.readString();
        this.cautionType = in.readInt();
        this.cautionKind = in.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel arg0, int arg1) {
        arg0.writeString(this.string_ids);
        arg0.writeString(this.image_ids);
        arg0.writeString(this.beep_id);
        arg0.writeString(this.led_ids);
        arg0.writeString(this.layout_type);
        arg0.writeString(this.mute);
        arg0.writeString(this.blink_period);
        arg0.writeInt(this.time_out);
        arg0.writeString(this.off_factor);
        arg0.writeString(this.cold_off);
        arg0.writeInt(this.cautionType);
        arg0.writeInt(this.cautionKind);
    }

    private String getStrIds(Cursor c) {
        String strids = null;
        if (c != null) {
            int columNum = c.getColumnIndex("string_ids");
            strids = c.getString(columNum);
        }
        Log.i(TAG, "getStrIds strids:" + strids);
        return strids;
    }

    private String getImageIds(Cursor c) {
        String imageids = null;
        if (c != null) {
            int columNum = c.getColumnIndex("image_ids");
            imageids = c.getString(columNum);
        }
        Log.i(TAG, "getImageIds imageids:" + imageids);
        return imageids;
    }

    private String getBeepId(Cursor c) {
        String beepid = null;
        if (c != null) {
            int columNum = c.getColumnIndex("beep_id");
            beepid = c.getString(columNum);
        }
        Log.i(TAG, "getBeepId beepid:" + beepid);
        return beepid;
    }

    private String getLedIds(Cursor c) {
        String ledids = null;
        if (c != null) {
            int columNum = c.getColumnIndex("led_ids");
            ledids = c.getString(columNum);
        }
        Log.i(TAG, "getLedIds ledids:" + ledids);
        return ledids;
    }

    private String getLayoutType(Cursor c) {
        String layoutType = null;
        if (c != null) {
            int columNum = c.getColumnIndex("layout_type");
            layoutType = c.getString(columNum);
        }
        Log.i(TAG, "getLayoutType layoutType:" + layoutType);
        return layoutType;
    }

    private String getMute(Cursor c) {
        String mute = null;
        if (c != null) {
            int columNum = c.getColumnIndex("mute");
            mute = c.getString(columNum);
        }
        Log.i(TAG, "getMute mute:" + mute);
        return mute;
    }

    private String getBlinkPeriod(Cursor c) {
        String blinkPeriod = null;
        if (c != null) {
            int columNum = c.getColumnIndex("blink_period");
            blinkPeriod = c.getString(columNum);
        }
        Log.i(TAG, "getBlinkPeriod blinlPeriod:" + blinkPeriod);
        return blinkPeriod;
    }

    private int getTimeOut(Cursor c) {
        int timeout = 0;
        if (c != null) {
            int columNum = c.getColumnIndex("time_out");
            timeout = c.getInt(columNum);
        }
        Log.i(TAG, "getTimeOut timeout:" + timeout);
        return timeout;
    }

    private String getOffFactor(Cursor c) {
        String offFactor = null;
        if (c != null) {
            int columNum = c.getColumnIndex("off_factor");
            offFactor = c.getString(columNum);
        }
        Log.i(TAG, "getOffFactor offFactor:" + offFactor);
        return offFactor;
    }

    private String getColdOff(Cursor c) {
        String coldOff = null;
        if (c != null) {
            int columNum = c.getColumnIndex("cold_off");
            coldOff = c.getString(columNum);
        }
        Log.i(TAG, "getColdOff coldOff:" + coldOff);
        return coldOff;
    }

    public ArrayList<String> getStrId() {
        Cursor stringIdCursor;
        String str3;
        String str2;
        ArrayList<String> strid = new ArrayList<>();
        if (this.string_ids != null && (stringIdCursor = this.dbManager.searchData("string_ids", this.string_ids, "stringIds", this.cautionKind)) != null) {
            stringIdCursor.moveToFirst();
            strid.add(stringIdCursor.getString(stringIdCursor.getColumnIndex("strid")));
            if (this.cautionKind == 0 || this.cautionKind == 262144) {
                int columNum = stringIdCursor.getColumnIndex("strid2");
                if (columNum != -1 && (str2 = stringIdCursor.getString(columNum)) != null) {
                    strid.add(str2);
                }
                int columNum2 = stringIdCursor.getColumnIndex("strid3");
                if (columNum2 != -1 && (str3 = stringIdCursor.getString(columNum2)) != null) {
                    strid.add(str3);
                }
            }
            stringIdCursor.close();
        }
        Log.i(TAG, "getStrId strid:" + strid.toString());
        return strid;
    }

    public ArrayList<String> getImageId() {
        Cursor imageIdCursor;
        int columNum;
        String image2;
        ArrayList<String> imageid = new ArrayList<>();
        if (this.image_ids != null && (imageIdCursor = this.dbManager.searchData("image_ids", this.image_ids, "imageIds", this.cautionKind)) != null) {
            imageIdCursor.moveToFirst();
            imageid.add(imageIdCursor.getString(imageIdCursor.getColumnIndex("imageid")));
            if ((this.cautionKind == 0 || this.cautionKind == 262144) && (columNum = imageIdCursor.getColumnIndex("imageid2")) != -1 && (image2 = imageIdCursor.getString(columNum)) != null) {
                imageid.add(image2);
            }
            imageIdCursor.close();
        }
        Log.i(TAG, "getImageId imageid:" + imageid.toString());
        return imageid;
    }

    public ArrayList<String> getLedId() {
        Cursor ledIdCursor;
        ArrayList<String> ledid = new ArrayList<>();
        if (this.led_ids != null && (ledIdCursor = this.dbManager.searchData("led_ids", this.led_ids, "ledIds", this.cautionKind)) != null) {
            ledIdCursor.moveToFirst();
            int columNum = ledIdCursor.getColumnIndex("ledid");
            ledid.add(ledIdCursor.getString(columNum));
            ledIdCursor.close();
        }
        Log.i(TAG, ledid.toString());
        return ledid;
    }
}
