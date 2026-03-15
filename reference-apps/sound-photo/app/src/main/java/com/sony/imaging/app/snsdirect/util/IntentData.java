package com.sony.imaging.app.snsdirect.util;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class IntentData implements Parcelable {
    public static final Parcelable.Creator<IntentData> CREATOR = new Parcelable.Creator<IntentData>() { // from class: com.sony.imaging.app.snsdirect.util.IntentData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentData createFromParcel(Parcel in) {
            return new IntentData(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntentData[] newArray(int size) {
            return new IntentData[size];
        }
    };
    private ImageDataInfo mImage;

    public IntentData() {
    }

    public void setImageDataInfo(ImageDataInfo i) {
        this.mImage = i;
    }

    public ImageDataInfo getImageInfo() {
        return this.mImage;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeSerializable(this.mImage);
    }

    private IntentData(Parcel in) {
        this.mImage = (ImageDataInfo) in.readSerializable();
    }
}
