package com.sony.imaging.app.base.common;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import com.sony.imaging.app.fw.CustomizableFunction;

/* loaded from: classes.dex */
public class EventParcel implements Parcelable {
    public static final Parcelable.Creator<EventParcel> CREATOR = new Parcelable.Creator<EventParcel>() { // from class: com.sony.imaging.app.base.common.EventParcel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EventParcel createFromParcel(Parcel arg0) {
            return new EventParcel(arg0);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EventParcel[] newArray(int arg0) {
            return new EventParcel[arg0];
        }
    };
    public static final String FLICK = "flick";
    public static final String KEY_KEYCODE = "KEYCODE";
    public static final String KEY_KEYFUNCTION = "KEYFUNCTION";
    public static final String KEY_TOUCH = "TouchEvent";
    public static final String SCROLL = "scroll";
    public static final String TOUCH_DOWN = "touchdown";
    public static final String TOUCH_UP = "touchup";
    public String mAction;
    public float mDistanceX;
    public float mDistanceY;
    public MotionEvent mEvent1;
    public MotionEvent mEvent2;
    public boolean mIsReleasedInside;
    public int mKeyEvent;
    public CustomizableFunction mKeyFunction;
    public Rect mRect;
    public float mVelocityX;
    public float mVelocityY;

    public EventParcel(String action, MotionEvent e1, MotionEvent e2, boolean isReleasedInside, float velocityX, float velocityY, float distanceX, float distanceY, Rect videoRect) {
        this.mAction = action;
        this.mEvent1 = e1;
        this.mEvent2 = e2;
        this.mIsReleasedInside = isReleasedInside;
        this.mVelocityX = velocityX;
        this.mVelocityY = velocityY;
        this.mDistanceX = distanceX;
        this.mDistanceY = distanceY;
        this.mRect = videoRect;
        this.mKeyEvent = -1;
    }

    public EventParcel(int keyEvent) {
        this.mKeyEvent = keyEvent;
        this.mAction = null;
        this.mEvent1 = null;
        this.mEvent2 = null;
        this.mIsReleasedInside = false;
        this.mVelocityX = -1.0f;
        this.mVelocityY = -1.0f;
        this.mDistanceX = -1.0f;
        this.mDistanceY = -1.0f;
        this.mRect = null;
    }

    public EventParcel(int keyEvent, CustomizableFunction keyFunction) {
        this(keyEvent);
        this.mKeyFunction = keyFunction;
    }

    public EventParcel(CustomizableFunction keyFunction) {
        this.mKeyFunction = keyFunction;
        this.mKeyEvent = -1;
        this.mAction = null;
        this.mEvent1 = null;
        this.mEvent2 = null;
        this.mIsReleasedInside = false;
        this.mVelocityX = -1.0f;
        this.mVelocityY = -1.0f;
        this.mDistanceX = -1.0f;
        this.mDistanceY = -1.0f;
        this.mRect = null;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private EventParcel(Parcel in) {
        this.mAction = in.readString();
        ClassLoader l = MotionEvent.class.getClassLoader();
        this.mEvent1 = (MotionEvent) in.readParcelable(l);
        this.mEvent2 = (MotionEvent) in.readParcelable(l);
        boolean[] b = new boolean[1];
        in.readBooleanArray(b);
        this.mIsReleasedInside = b[0];
        this.mVelocityX = in.readFloat();
        this.mVelocityY = in.readFloat();
        this.mDistanceX = in.readFloat();
        this.mDistanceY = in.readFloat();
        this.mRect = (Rect) in.readParcelable(Rect.class.getClassLoader());
        this.mKeyEvent = in.readInt();
        this.mKeyFunction = (CustomizableFunction) in.readParcelable(CustomizableFunction.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mAction);
        parcel.writeParcelable(this.mEvent1, i);
        parcel.writeParcelable(this.mEvent2, i);
        parcel.writeBooleanArray(new boolean[]{this.mIsReleasedInside});
        parcel.writeFloat(this.mVelocityX);
        parcel.writeFloat(this.mVelocityY);
        parcel.writeFloat(this.mDistanceX);
        parcel.writeFloat(this.mDistanceY);
        parcel.writeParcelable(this.mRect, i);
        parcel.writeInt(this.mKeyEvent);
    }
}
