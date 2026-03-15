package com.sony.imaging.app.fw;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.shooting.camera.PictureQualityController;
import com.sony.imaging.app.fw.IFunctionTable;
import com.sony.imaging.app.util.Environment;
import com.sony.imaging.app.util.StringBuilderThreadLocal;

/* loaded from: classes.dex */
public enum CustomizableFunction implements IKeyFunction, Parcelable {
    ExposureMode(IKeyFunction.TYPE_SETTING),
    SubExposureMode(IKeyFunction.TYPE_SETTING),
    FlashMode(IKeyFunction.TYPE_SETTING, 1037),
    FocusMode(IKeyFunction.TYPE_SETTING, 1038),
    AutoFocusMode(IKeyFunction.TYPE_SETTING),
    FaceDetection(IKeyFunction.TYPE_SETTING, 1040),
    IsoSensitivity(IKeyFunction.TYPE_SETTING, 1042),
    IsoSensitivityIncrement(IKeyFunction.TYPE_EXEC),
    IsoSensitivityDecrement(IKeyFunction.TYPE_EXEC),
    DriveMode(IKeyFunction.TYPE_SETTING, 1036),
    ExposureCompensation(IKeyFunction.TYPE_SETTING, 1035),
    ExposureCompensationIncrement(IKeyFunction.TYPE_EXEC, 1031),
    ExposureCompensationDecrement(IKeyFunction.TYPE_EXEC, 1182),
    FocusArea(IKeyFunction.TYPE_SETTING, 1039),
    WhiteBalance(IKeyFunction.TYPE_SETTING, 1045),
    DroHdr(IKeyFunction.TYPE_SETTING, 1046),
    CreativeStyle(IKeyFunction.TYPE_SETTING, 1047),
    PictureEffect(IKeyFunction.TYPE_SETTING, 1048),
    MeteringMode(IKeyFunction.TYPE_SETTING, 1043),
    FlashCompensation(IKeyFunction.TYPE_SETTING, 1044),
    ImageQuality(IKeyFunction.TYPE_SETTING, 1050),
    ImageSize(IKeyFunction.TYPE_SETTING, 1049),
    ImageAspect(IKeyFunction.TYPE_SETTING, 1106),
    AelHold(IKeyFunction.TYPE_EXEC, 1052),
    AelToggle(IKeyFunction.TYPE_EXEC, 1053),
    SpotAelHold(IKeyFunction.TYPE_EXEC, 1054),
    SpotAelToggle(IKeyFunction.TYPE_EXEC, 1055),
    AfMfHold(IKeyFunction.TYPE_EXEC, 1057),
    AfMfToggle(IKeyFunction.TYPE_EXEC, 1058),
    ShutterSpeedIncrement(IKeyFunction.TYPE_EXEC, 1154),
    ShutterSpeedDecrement(IKeyFunction.TYPE_EXEC, 1185),
    ApertureIncrement(IKeyFunction.TYPE_EXEC, 1032),
    ApertureDecrement(IKeyFunction.TYPE_EXEC, 1184),
    ScnSelection(IKeyFunction.TYPE_SETTING),
    ProgramShiftIncrement(IKeyFunction.TYPE_EXEC, 1152),
    ProgramShiftDecrement(IKeyFunction.TYPE_EXEC, 1183),
    TvAvChange(IKeyFunction.TYPE_EXEC, 1176),
    TvOrAvInc(IKeyFunction.TYPE_EXEC, 1186),
    TvOrAvDec(IKeyFunction.TYPE_EXEC, 1187),
    AfLock(IKeyFunction.TYPE_EXEC),
    MfAssist(IKeyFunction.TYPE_EXEC, 1063),
    DigitalZoom(IKeyFunction.TYPE_SETTING, 1064),
    Gain(IKeyFunction.TYPE_SETTING, 1213),
    CinemaTone(IKeyFunction.TYPE_SETTING),
    IRRecInhDirectRec(IKeyFunction.TYPE_EXEC, 1210),
    MovieStartStop(IKeyFunction.TYPE_EXEC),
    Enter5Way(IKeyFunction.TYPE_EXEC, 1156),
    EnterJoyStick(IKeyFunction.TYPE_EXEC, 1029),
    EeMainNext(IKeyFunction.TYPE_EXEC, 1034),
    EeMainPrev(IKeyFunction.TYPE_EXEC, 1155),
    EeSubNext(IKeyFunction.TYPE_EXEC, 1179),
    EeSubPrev(IKeyFunction.TYPE_EXEC, 1180),
    IRShutterNotCheckDrivemode(IKeyFunction.TYPE_EXEC, 1209),
    MovieLock(IKeyFunction.TYPE_EXEC, 1243),
    MovieEELock(IKeyFunction.TYPE_EXEC, 1244),
    FocusHold(IKeyFunction.TYPE_EXEC, new int[]{1189, 1196}),
    PlayIndex(IKeyFunction.TYPE_EXEC, 1212),
    PbZoomPlus(IKeyFunction.TYPE_EXEC, 1107),
    PbZoomMinus(IKeyFunction.TYPE_EXEC, 1108),
    Delete(IKeyFunction.TYPE_EXEC, 1103),
    Rotate(IKeyFunction.TYPE_EXEC, 1157),
    DispChange(IKeyFunction.TYPE_EXEC, 1081),
    Guide(IKeyFunction.TYPE_EXEC, 1071),
    Reset(IKeyFunction.TYPE_EXEC, 1242),
    MainNext(IKeyFunction.TYPE_EXEC, 1204),
    MainPrev(IKeyFunction.TYPE_EXEC, 1203),
    SubNext(IKeyFunction.TYPE_EXEC, 1208),
    SubPrev(IKeyFunction.TYPE_EXEC, 1207),
    ThirdNext(IKeyFunction.TYPE_EXEC, 1219),
    ThirdPrev(IKeyFunction.TYPE_EXEC, 1218),
    RingNext(IKeyFunction.TYPE_EXEC, 1109),
    RingPrev(IKeyFunction.TYPE_EXEC, 1206),
    Custom(IKeyFunction.TYPE_EXEC, new int[]{1026, 1027}),
    Decided_by_Exposure(IKeyFunction.TYPE_EXEC, 1028),
    NoAssign(IKeyFunction.TYPE_NONE),
    Invalid(IKeyFunction.TYPE_NONE),
    Unknown(IKeyFunction.TYPE_NONE),
    Unchanged(IKeyFunction.TYPE_NONE, new int[]{1024, 1214}),
    DoNothing(IKeyFunction.TYPE_NONE, 1025);

    private static final int LOGIC_KEY_CAPACITY = 128;
    private static final String MSG_UNSUPPORTED_CUSTOM_KEY = "Unsupported key function : ";
    private static final String MSG_UNSUPPORTED_FUNCTION = "Unsupported function : ";
    private static final String TAG = "CustomizableFunction";
    private static SparseArray<CustomizableFunction> sLogit2FuncTable;
    protected static IFunctionTable usableFuncTable;
    private int[] codes;
    private String type;

    private static void add(int code, CustomizableFunction func) {
        if (sLogit2FuncTable == null) {
            sLogit2FuncTable = new SparseArray<>(LOGIC_KEY_CAPACITY);
        }
        sLogit2FuncTable.put(code, func);
    }

    CustomizableFunction(String type, int code) {
        this.codes = new int[]{code};
        this.type = type;
        add(code, this);
    }

    CustomizableFunction(String type, int[] codes) {
        this.codes = codes;
        this.type = type;
        for (int i : codes) {
            add(i, this);
        }
    }

    CustomizableFunction(String type) {
        this.type = type;
        this.codes = new int[]{1024};
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override // com.sony.imaging.app.fw.IKeyFunction
    public String getType() {
        return this.type;
    }

    public static void setTable(IFunctionTable table) {
        usableFuncTable = table;
    }

    protected IFunctionTable.IFunctionInfo getFunctionInfo(IKeyFunction function) {
        if (usableFuncTable != null) {
            return usableFuncTable.getFunctionInfo(function);
        }
        return null;
    }

    @Override // com.sony.imaging.app.fw.IKeyFunction
    public String getItemIdForMenu() {
        IFunctionTable.IFunctionInfo info = getFunctionInfo(this);
        if (info == null) {
            return null;
        }
        String itemId = info.getItemId();
        return itemId;
    }

    @Override // com.sony.imaging.app.fw.IKeyFunction
    public int getImageId() {
        IFunctionTable.IFunctionInfo info = getFunctionInfo(this);
        if (info == null) {
            return -1;
        }
        int imageId = info.getImageId();
        return imageId;
    }

    @Override // com.sony.imaging.app.fw.IKeyFunction
    public boolean isValid() {
        IFunctionTable.IFunctionInfo info = getFunctionInfo(this);
        if (info == null) {
            return false;
        }
        boolean isValid = info.isValid();
        return isValid;
    }

    public static CustomizableFunction logicKeyCode2CustomizableFunction(int func) {
        CustomizableFunction f = sLogit2FuncTable.get(func);
        if (f == null) {
            return Unknown;
        }
        return f;
    }

    public static int[] toLogicKeyCodes(CustomizableFunction func) {
        return func.codes;
    }

    public static CustomizableFunction keyFunction2CustomizableFunction(int func) {
        CustomizableFunction customizableFunction = Unknown;
        switch (func) {
            case 0:
                CustomizableFunction f = Unknown;
                return f;
            case 1:
                CustomizableFunction f2 = DispChange;
                return f2;
            case 2:
                CustomizableFunction f3 = NoAssign;
                return f3;
            case 3:
                CustomizableFunction f4 = ExposureCompensation;
                return f4;
            case 4:
                CustomizableFunction f5 = DriveMode;
                return f5;
            case 5:
                CustomizableFunction f6 = FlashMode;
                return f6;
            case 6:
                CustomizableFunction f7 = AutoFocusMode;
                return f7;
            case 7:
                CustomizableFunction f8 = FocusArea;
                return f8;
            case 8:
                CustomizableFunction f9 = FaceDetection;
                return f9;
            case 9:
                if (1 >= Environment.getVersionPfAPI()) {
                    CustomizableFunction f10 = Unknown;
                    return f10;
                }
                CustomizableFunction f11 = FaceDetection;
                return f11;
            case 10:
                CustomizableFunction f12 = IsoSensitivity;
                return f12;
            case 11:
                CustomizableFunction f13 = MeteringMode;
                return f13;
            case 12:
                CustomizableFunction f14 = FlashCompensation;
                return f14;
            case 13:
                CustomizableFunction f15 = WhiteBalance;
                return f15;
            case 14:
                CustomizableFunction f16 = DroHdr;
                return f16;
            case 15:
                CustomizableFunction f17 = CreativeStyle;
                return f17;
            case 16:
                CustomizableFunction f18 = PictureEffect;
                return f18;
            case 17:
                CustomizableFunction f19 = ImageSize;
                return f19;
            case 18:
                CustomizableFunction f20 = ImageQuality;
                return f20;
            case 19:
            case 24:
            case 28:
            case 29:
            case 30:
            case 34:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 48:
            case 49:
            case PictureQualityController.QUALITY_FINE /* 50 */:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 61:
            default:
                StringBuilder sBuilder = StringBuilderThreadLocal.getScratchBuilder();
                sBuilder.replace(0, sBuilder.length(), MSG_UNSUPPORTED_CUSTOM_KEY).append(func);
                Log.d(TAG, sBuilder.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(sBuilder);
                CustomizableFunction f21 = Unknown;
                return f21;
            case 20:
                CustomizableFunction f22 = AelHold;
                return f22;
            case 21:
                CustomizableFunction f23 = AelToggle;
                return f23;
            case 22:
                CustomizableFunction f24 = AelHold;
                return f24;
            case 23:
                CustomizableFunction f25 = AelToggle;
                return f25;
            case 25:
                CustomizableFunction f26 = AfMfHold;
                return f26;
            case 26:
                CustomizableFunction f27 = AfMfToggle;
                return f27;
            case 27:
                CustomizableFunction f28 = Unknown;
                return f28;
            case 31:
                CustomizableFunction f29 = MfAssist;
                return f29;
            case 32:
                CustomizableFunction f30 = ExposureMode;
                return f30;
            case 33:
                CustomizableFunction f31 = FocusMode;
                return f31;
            case 35:
                CustomizableFunction f32 = DigitalZoom;
                return f32;
            case 47:
                CustomizableFunction f33 = ImageAspect;
                return f33;
            case 51:
                CustomizableFunction f34 = Guide;
                return f34;
            case 60:
                CustomizableFunction f35 = FocusHold;
                return f35;
            case 62:
                CustomizableFunction f36 = MovieStartStop;
                return f36;
        }
    }

    public static CustomizableFunction customLauncherFunction2CustomizableFunction(int func) {
        CustomizableFunction customizableFunction = Unknown;
        if (1 >= Environment.getVersionPfAPI()) {
            switch (func) {
                case 0:
                    CustomizableFunction f = Unknown;
                    return f;
                case 1:
                case 9:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                default:
                    StringBuilder sBuilder = StringBuilderThreadLocal.getScratchBuilder();
                    sBuilder.replace(0, sBuilder.length(), MSG_UNSUPPORTED_FUNCTION).append(func);
                    Log.d(TAG, sBuilder.toString());
                    StringBuilderThreadLocal.releaseScratchBuilder(sBuilder);
                    CustomizableFunction f2 = Unknown;
                    return f2;
                case 2:
                    CustomizableFunction f3 = NoAssign;
                    return f3;
                case 3:
                    CustomizableFunction f4 = ExposureCompensation;
                    return f4;
                case 4:
                    CustomizableFunction f5 = DriveMode;
                    return f5;
                case 5:
                    CustomizableFunction f6 = FlashMode;
                    return f6;
                case 6:
                    CustomizableFunction f7 = AutoFocusMode;
                    return f7;
                case 7:
                    CustomizableFunction f8 = FocusArea;
                    return f8;
                case 8:
                    CustomizableFunction f9 = FaceDetection;
                    return f9;
                case 10:
                    CustomizableFunction f10 = IsoSensitivity;
                    return f10;
                case 11:
                    CustomizableFunction f11 = MeteringMode;
                    return f11;
                case 12:
                    CustomizableFunction f12 = FlashCompensation;
                    return f12;
                case 13:
                    CustomizableFunction f13 = WhiteBalance;
                    return f13;
                case 14:
                    CustomizableFunction f14 = DroHdr;
                    return f14;
                case 15:
                    CustomizableFunction f15 = CreativeStyle;
                    return f15;
                case 16:
                    CustomizableFunction f16 = PictureEffect;
                    return f16;
                case 17:
                    CustomizableFunction f17 = ImageSize;
                    return f17;
                case 18:
                    CustomizableFunction f18 = ImageQuality;
                    return f18;
                case 32:
                    CustomizableFunction f19 = ExposureMode;
                    return f19;
                case 33:
                    CustomizableFunction f20 = FocusMode;
                    return f20;
            }
        }
        switch (func) {
            case 1024:
                CustomizableFunction f21 = Unknown;
                return f21;
            case 1025:
                CustomizableFunction f22 = NoAssign;
                return f22;
            case 1026:
                CustomizableFunction f23 = DriveMode;
                return f23;
            case 1027:
                CustomizableFunction f24 = FlashMode;
                return f24;
            case 1028:
                CustomizableFunction f25 = IsoSensitivity;
                return f25;
            case 1029:
                CustomizableFunction f26 = MeteringMode;
                return f26;
            case 1030:
                CustomizableFunction f27 = AutoFocusMode;
                return f27;
            case 1031:
                CustomizableFunction f28 = FocusArea;
                return f28;
            case 1032:
                CustomizableFunction f29 = WhiteBalance;
                return f29;
            case 1033:
                CustomizableFunction f30 = DroHdr;
                return f30;
            case 1034:
                CustomizableFunction f31 = CreativeStyle;
                return f31;
            case 1035:
                CustomizableFunction f32 = FlashCompensation;
                return f32;
            case 1036:
                CustomizableFunction f33 = PictureEffect;
                return f33;
            case 1037:
                CustomizableFunction f34 = ExposureCompensation;
                return f34;
            case 1038:
                CustomizableFunction f35 = FaceDetection;
                return f35;
            case 1039:
            case 1040:
            case 1041:
            case 1045:
            case 1048:
            case 1049:
            case 1050:
            default:
                StringBuilder sBuilder2 = StringBuilderThreadLocal.getScratchBuilder();
                sBuilder2.replace(0, sBuilder2.length(), MSG_UNSUPPORTED_FUNCTION).append(func);
                Log.d(TAG, sBuilder2.toString());
                StringBuilderThreadLocal.releaseScratchBuilder(sBuilder2);
                CustomizableFunction f36 = Unknown;
                return f36;
            case 1042:
                CustomizableFunction f37 = ExposureMode;
                return f37;
            case 1043:
                CustomizableFunction f38 = SubExposureMode;
                return f38;
            case 1044:
                CustomizableFunction f39 = ImageSize;
                return f39;
            case 1046:
                CustomizableFunction f40 = ImageQuality;
                return f40;
            case 1047:
                CustomizableFunction f41 = ImageAspect;
                return f41;
            case 1051:
                CustomizableFunction f42 = CinemaTone;
                return f42;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name());
    }
}
