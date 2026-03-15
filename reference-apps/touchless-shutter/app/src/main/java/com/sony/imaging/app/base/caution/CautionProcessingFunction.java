package com.sony.imaging.app.base.caution;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sony.imaging.app.base.R;
import com.sony.imaging.app.base.common.widget.FooterGuide;
import com.sony.imaging.app.base.common.widget.FooterGuideDataResId;
import com.sony.imaging.app.base.common.widget.IFooterGuideData;
import com.sony.imaging.app.fw.ICustomKey;
import com.sony.imaging.app.fw.IKeyFunction;
import com.sony.imaging.app.util.BeepUtility;
import com.sony.imaging.app.util.BeepUtilityRsrcTable;
import com.sony.scalar.hardware.indicator.Light;
import java.util.Arrays;

/* loaded from: classes.dex */
public class CautionProcessingFunction {
    private static final String BLINK_PERIOD_08 = "BLINK_PERIOD_08";
    private static final String BLINK_PERIOD_32 = "BLINK_PERIOD_32";
    private static final int BOOTFAILED_FOOTER_STR_EMT = 17040283;
    private static final int BOOTFAILED_FOOTER_STR_PJONE = 17040282;
    public static final int DIALOGTYPE_02 = 2;
    public static final int DIALOGTYPE_03 = 3;
    public static final int DIALOGTYPE_06 = 6;
    public static final int DIALOGTYPE_BOOTFAIL = -1;
    public static final int DIALOGTYPE_PJONE_NORMAL = 10;
    public static final int LAYOUT_TYPE_A_TEXT = 1;
    public static final int LAYOUT_TYPE_A_TEXTICON = 5;
    public static final int LAYOUT_TYPE_A_TEXTICON_CLOSEKEY = 10;
    public static final int LAYOUT_TYPE_A_TEXT_CENTERKEY = 3;
    public static final int LAYOUT_TYPE_A_TEXT_CLOSEKEY = 8;
    public static final int LAYOUT_TYPE_B_TEXT = 2;
    public static final int LAYOUT_TYPE_B_TEXTICON = 6;
    public static final int LAYOUT_TYPE_B_TEXTICON_CLOSEKEY = 11;
    public static final int LAYOUT_TYPE_B_TEXTICON_NOKEY = 7;
    public static final int LAYOUT_TYPE_B_TEXT_CENTERKEY = 4;
    public static final int LAYOUT_TYPE_B_TEXT_CLOSEKEY = 9;
    public static final int LAYOUT_TYPE_EXCLUSION = 17;
    public static final int LAYOUT_TYPE_EXCLUSION_APO_NORMAL = 18;
    public static final int LAYOUT_TYPE_EXCLUSION_APO_NORMAL_BATT = 19;
    public static final int LAYOUT_TYPE_EXCLUSION_APO_NORMAL_ERR = 20;
    public static final int LAYOUT_TYPE_STAY = 16;
    public static final int LAYOUT_TYPE_TRIGGER = 12;
    public static final int LAYOUT_TYPE_TRIGGER_APO_SPECIAL = 23;
    public static final int LAYOUT_TYPE_TRIGGER_APO_SPECIAL_DIALOG = 22;
    public static final int LAYOUT_TYPE_TRIGGER_BTN = 15;
    public static final int LAYOUT_TYPE_TRIGGER_DIALOG = 21;
    public static final int LAYOUT_TYPE_TRIGGER_MSG = 14;
    public static final int LAYOUT_TYPE_TRIGGER_MSG_APO_SPECIAL = 26;
    public static final int LAYOUT_TYPE_TRIGGER_MSG_APO_SPECIAL_DIALOG = 28;
    public static final int LAYOUT_TYPE_TRIGGER_MSG_CARD = 25;
    public static final int LAYOUT_TYPE_TRIGGER_MSG_DIALOG = 27;
    public static final int LAYOUT_TYPE_TRIGGER_MSG_FULL = 29;
    public static final int LAYOUT_TYPE_TRIGGER_MSG_LENS = 24;
    public static final int LAYOUT_TYPE_TRIGGER_REASON = 13;
    public static final int LAYOUT_TYPE_TRIGGER_REASON_TRIPLE_STRING = 30;
    private static final int TIMEOUT_EXTENSION_HDMI_CONNECTION = 5000;
    private static KeyDispatcherFactory mKeyDispatcherFactory;
    private static LayoutFactory mLayoutFactory;
    private static terminateCaution mTerminateCaution;
    private static int[] CautionID = null;
    private static Activity mActivity = null;
    private static RsrcBase cautionRsrc = null;
    private static IKeyDispatch mKeyDispatcher = null;
    private static final String TAG = CautionProcessingFunction.class.getSimpleName();
    private static String mLedId = null;
    private static SparseArray<IkeyDispatchEach> eachKeyHandler = new SparseArray<>();
    CautionUtilityClass cautionUtil = CautionUtilityClass.getInstance();
    private ImageView okButton = null;
    private ImageView cancelButton = null;
    private priorityData mPriData = null;
    private int mDialogType = -10;
    private Handler timeoutHandler = new Handler();
    private Runnable timeoutTerminater = new Runnable() { // from class: com.sony.imaging.app.base.caution.CautionProcessingFunction.1
        @Override // java.lang.Runnable
        public void run() {
            CautionProcessingFunction.this.timeoutHandler.removeCallbacks(CautionProcessingFunction.this.timeoutClose);
        }
    };
    private Runnable timeoutClose = new Runnable() { // from class: com.sony.imaging.app.base.caution.CautionProcessingFunction.2
        @Override // java.lang.Runnable
        public void run() {
            Log.i(CautionProcessingFunction.TAG, "timeoutClose run");
            CautionProcessingFunction.executeTerminate();
        }
    };

    /* loaded from: classes.dex */
    public interface KeyDispatcherFactory {
        IKeyDispatch getKeyDispatcher(int i, int i2, CautionProcessingFunction cautionProcessingFunction);
    }

    /* loaded from: classes.dex */
    public interface LayoutFactory {
        int getLayout(int i, int i2, CautionProcessingFunction cautionProcessingFunction);
    }

    /* loaded from: classes.dex */
    public interface terminateCaution {
        void onTerminate();
    }

    public CautionProcessingFunction(int[] cautionId, Activity activity) {
        setCautionId(cautionId);
        setActivity(activity);
        Log.i(TAG, "Constructor cautionId:" + Arrays.toString(cautionId) + "activity:" + activity);
    }

    public int getDialogType() {
        return this.mDialogType;
    }

    public static void setLayoutFactory(LayoutFactory l) {
        mLayoutFactory = l;
    }

    public static void setKeyDispatcherFactory(KeyDispatcherFactory k) {
        mKeyDispatcherFactory = k;
    }

    private int xml(int layoutType, int cautionId, CautionProcessingFunction ref) {
        int ret;
        int custom;
        switch (layoutType) {
            case 1:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 2:
                ret = R.layout.layout_type_01_wo_background;
                break;
            case 3:
                ret = R.layout.layout_type_06_w_background;
                break;
            case 4:
                ret = R.layout.layout_type_06_wo_background;
                break;
            case 5:
                ret = R.layout.layout_type_04_w_background;
                break;
            case 6:
                ret = R.layout.layout_type_04_wo_background;
                break;
            case 7:
                ret = R.layout.layout_type_04_wo_background;
                break;
            case 8:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 9:
                ret = R.layout.layout_type_01_wo_background;
                break;
            case 10:
                ret = R.layout.layout_type_04_w_background;
                break;
            case 11:
                ret = R.layout.layout_type_04_wo_background;
                break;
            case 12:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 13:
                ret = R.layout.layout_type_04_wo_background;
                break;
            case 14:
                ret = R.layout.layout_type_06_w_background;
                break;
            case 15:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 16:
                ret = R.layout.layout_type_01_wo_background;
                break;
            case 17:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 18:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 19:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 20:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 21:
                ret = R.layout.layout_type_01_wo_background;
                break;
            case 22:
                ret = R.layout.layout_type_01_wo_background;
                break;
            case 23:
                ret = R.layout.layout_type_01_w_background;
                break;
            case 24:
                ret = R.layout.layout_type_06_w_background;
                break;
            case 25:
                ret = R.layout.layout_type_06_w_background;
                break;
            case 26:
                ret = R.layout.layout_type_06_w_background;
                break;
            case 27:
                ret = R.layout.layout_type_06_wo_background;
                break;
            case 28:
                ret = R.layout.layout_type_06_wo_background;
                break;
            case 29:
                ret = R.layout.layout_type_06_w_background;
                break;
            case 30:
                ret = R.layout.layout_type_triplestr_wo_background;
                break;
            default:
                ret = R.layout.layout_type_04_wo_background;
                Log.i(TAG, "Not defined layout type");
                break;
        }
        if (mLayoutFactory != null && -1 != (custom = mLayoutFactory.getLayout(layoutType, cautionId, ref))) {
            return custom;
        }
        return ret;
    }

    private IKeyDispatch key(int layoutType, int cautionId, CautionProcessingFunction ref) {
        IKeyDispatch ret;
        IKeyDispatch custom;
        switch (layoutType) {
            case 1:
                ret = new KeyDispatchForDLT02and03(ref);
                break;
            case 2:
                if (cautionId == 102) {
                    ret = new KeyDispatchAllKeysIgnoreWithoutS2Off(ref);
                    break;
                } else {
                    ret = new KeyDispatchForDLT02and03(ref);
                    break;
                }
            case 3:
                ret = new KeyDispatchForDLT06(ref);
                break;
            case 4:
                ret = new KeyDispatchForDLT06(ref);
                break;
            case 5:
                ret = new KeyDispatchForDLT06(ref);
                break;
            case 6:
                ret = new KeyDispatchForDLT06(ref);
                break;
            case 7:
                if (cautionId == 1399) {
                    ret = new KeyDispatchAllKeysIgnoreWithoutS2Off(ref);
                    this.mDialogType = 3;
                    break;
                } else {
                    ret = new KeyDispatchForDLT06(ref);
                    this.mDialogType = 6;
                    break;
                }
            case 8:
                ret = new KeyDispatchForCloseKey(ref);
                break;
            case 9:
                ret = new KeyDispatchForCloseKey(ref);
                break;
            case 10:
                ret = new KeyDispatchForCloseKey(ref);
                break;
            case 11:
                ret = new KeyDispatchForCloseKey(ref);
                break;
            case 12:
                ret = new KeyDispatchForTriggerTable_Trigger(ref);
                break;
            case 13:
                ret = new KeyDispatchForTriggerTable_Reason(ref);
                break;
            case 14:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 15:
                ret = new KeyDispatchForTriggerTable_TriggerBtn(ref);
                break;
            case 16:
                ret = new KeyDispatchForTriggerTable_Stay(ref);
                break;
            case 17:
                ret = new KeyDispatchForTriggerTable_Exclusion(ref);
                break;
            case 18:
                ret = new KeyDispatchForTriggerTable_Exclusion(ref);
                break;
            case 19:
                ret = new KeyDispatchForTriggerTable_Exclusion(ref);
                break;
            case 20:
                ret = new KeyDispatchForTriggerTable_Exclusion(ref);
                break;
            case 21:
                ret = new KeyDispatchForTriggerTable_Trigger(ref);
                break;
            case 22:
                ret = new KeyDispatchForTriggerTable_Trigger(ref);
                break;
            case 23:
                ret = new KeyDispatchForTriggerTable_Trigger(ref);
                break;
            case 24:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 25:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 26:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 27:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 28:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 29:
                ret = new KeyDispatchForTriggerTable_Msg(ref);
                break;
            case 30:
                ret = new KeyDispatchForTriggerTable_Reason(ref);
                break;
            default:
                ret = new KeyDispatchForDLT06(ref);
                break;
        }
        IKeyDispatch tmp = ref.SpecilalKeyDispatcher(cautionId);
        if (tmp != null) {
            ret = tmp;
        }
        if (mKeyDispatcherFactory != null && (custom = mKeyDispatcherFactory.getKeyDispatcher(layoutType, cautionId, ref)) != null) {
            return custom;
        }
        return ret;
    }

    public int getCurrentLayoutType() {
        String str = cautionRsrc.layout_type;
        if (str.equals("LAYOUT_TYPE_A_TEXT")) {
            return 1;
        }
        if (str.equals("LAYOUT_TYPE_B_TEXT")) {
            return 2;
        }
        if (str.equals("LAYOUT_TYPE_A_TEXT_CENTERKEY")) {
            return 3;
        }
        if (str.equals("LAYOUT_TYPE_B_TEXT_CENTERKEY")) {
            return 4;
        }
        if (str.equals("LAYOUT_TYPE_A_TEXTICON")) {
            return 5;
        }
        if (str.equals("LAYOUT_TYPE_B_TEXTICON")) {
            return 6;
        }
        if (str.equals("LAYOUT_TYPE_B_TEXTICON_NOKEY")) {
            return 7;
        }
        if (str.equals("LAYOUT_TYPE_A_TEXT_CLOSEKEY")) {
            return 8;
        }
        if (str.equals("LAYOUT_TYPE_B_TEXT_CLOSEKEY")) {
            return 9;
        }
        if (str.equals("LAYOUT_TYPE_A_TEXTICON_CLOSEKEY")) {
            return 10;
        }
        if (str.equals("LAYOUT_TYPE_B_TEXTICON_CLOSEKEY")) {
            return 11;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER")) {
            return 12;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_REASON")) {
            return 13;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG")) {
            return 14;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_BTN")) {
            return 15;
        }
        if (str.equals("LAYOUT_TYPE_STAY")) {
            return 16;
        }
        if (str.equals("LAYOUT_TYPE_EXCLUSION")) {
            return 17;
        }
        if (str.equals("LAYOUT_TYPE_EXCLUSION_APO_NORMAL")) {
            return 18;
        }
        if (str.equals("LAYOUT_TYPE_EXCLUSION_APO_NORMAL_BATT")) {
            return 19;
        }
        if (str.equals("LAYOUT_TYPE_EXCLUSION_APO_NORMAL_ERR")) {
            return 20;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_DIALOG")) {
            return 21;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_APO_SPECIAL_DIALOG")) {
            return 22;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_APO_SPECIAL")) {
            return 23;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG_LENS")) {
            return 24;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG_CARD")) {
            return 25;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG_APO_SPECIAL")) {
            return 26;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG_DIALOG")) {
            return 27;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG_APO_SPECIAL_DIALOG")) {
            return 28;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_MSG_FULL")) {
            return 29;
        }
        if (str.equals("LAYOUT_TYPE_TRIGGER_REASON_TRIPLE_STRING")) {
            return 30;
        }
        return 6;
    }

    public int getCurrentCautionId() {
        return this.mPriData.maxPriorityId;
    }

    private View makeView(int[] cautionId) {
        Log.i(TAG, "[START] makeView cautionId:" + Arrays.toString(cautionId));
        View view = null;
        Activity activity = getActivity();
        LayoutInflater inflater = activity.getLayoutInflater();
        this.mPriData = getMaxPriorityId(cautionId);
        cautionRsrc = this.cautionUtil.getResourceInfo(this.mPriData.maxPriorityId, this.mPriData.type)[0];
        if (cautionRsrc != null) {
            Log.i(TAG, "makeView layout_type:" + cautionRsrc.layout_type);
            int layoutType = getCurrentLayoutType();
            switch (layoutType) {
                case 1:
                    this.mDialogType = 2;
                    break;
                case 2:
                    this.mDialogType = 2;
                    break;
                case 3:
                    this.mDialogType = 6;
                    break;
                case 4:
                    this.mDialogType = 6;
                    break;
                case 5:
                    this.mDialogType = 6;
                    break;
                case 6:
                    this.mDialogType = 6;
                    break;
                case 7:
                    this.mDialogType = 6;
                    break;
                case 8:
                    this.mDialogType = -1;
                    break;
                case 9:
                    this.mDialogType = -1;
                    break;
                case 10:
                    this.mDialogType = -1;
                    break;
                case 11:
                    this.mDialogType = -1;
                    break;
                case 12:
                    this.mDialogType = 10;
                    break;
                case 13:
                    this.mDialogType = 10;
                    break;
                case 14:
                    this.mDialogType = 10;
                    break;
                case 15:
                    this.mDialogType = 10;
                    break;
                case 16:
                    this.mDialogType = 10;
                    break;
                case 17:
                    this.mDialogType = 10;
                    break;
                case 18:
                    this.mDialogType = 10;
                    break;
                case 19:
                    this.mDialogType = 10;
                    break;
                case 20:
                    this.mDialogType = 10;
                    break;
                case 21:
                    this.mDialogType = 10;
                    break;
                case 22:
                    this.mDialogType = 10;
                    break;
                case 23:
                    this.mDialogType = 10;
                    break;
                case 24:
                    this.mDialogType = 10;
                    break;
                case 25:
                    this.mDialogType = 10;
                    break;
                case 26:
                    this.mDialogType = 10;
                    break;
                case 27:
                    this.mDialogType = 10;
                    break;
                case 28:
                    this.mDialogType = 10;
                    break;
                case 29:
                    this.mDialogType = 10;
                    break;
                case 30:
                    this.mDialogType = 10;
                    break;
                default:
                    this.mDialogType = 6;
                    break;
            }
            if (-1 != layoutType) {
                int xmlId = xml(layoutType, this.mPriData.maxPriorityId, this);
                if (-1 != xmlId) {
                    view = inflater.inflate(xmlId, (ViewGroup) null);
                } else {
                    view = null;
                }
                mKeyDispatcher = key(layoutType, this.mPriData.maxPriorityId, this);
            }
            Log.i(TAG, "makeView mKeyDiapatcher:" + mKeyDispatcher);
            Runnable timeoutTerminater = null;
            if (view != null) {
                Log.i(TAG, "makeView string_ids:" + cautionRsrc.string_ids);
                if (cautionRsrc.getStrId().size() > 1) {
                    TextView mTextView = (TextView) view.findViewById(R.id.caution_text);
                    TextView mTextViewReason = (TextView) view.findViewById(R.id.reason_title);
                    TextView mTextViewValue = (TextView) view.findViewById(R.id.reason_value);
                    mTextView.setText(getStr(0));
                    mTextViewReason.setText(getStr(1));
                    mTextViewValue.setText(getStr(2));
                } else {
                    TextView mTextView2 = (TextView) view.findViewById(R.id.caution_text);
                    mTextView2.setText(getStr());
                }
                FooterGuide footerGuide = (FooterGuide) view.findViewById(R.id.footer_guide);
                if (footerGuide != null && this.mDialogType == -1) {
                    IFooterGuideData data = new FooterGuideDataResId(getActivity().getApplicationContext(), 17040282, 17040283);
                    footerGuide.setData(data);
                }
                Log.i(TAG, "makeView image_ids:" + cautionRsrc.image_ids);
                ImageView mImageView = (ImageView) view.findViewById(R.id.exclametion_image);
                if (mImageView != null) {
                    mImageView.setImageResource(getImg());
                }
                Log.i(TAG, "makeView beep_id:" + cautionRsrc.beep_id);
                if (cautionRsrc.beep_id != null) {
                    BeepUtility.getInstance().playBeep(cautionRsrc.beep_id);
                }
                Log.i(TAG, "makeView time_out:" + cautionRsrc.time_out);
                if (cautionRsrc.time_out != 0) {
                    timeoutProcessing(cautionRsrc.time_out * 1000);
                    timeoutTerminater = this.timeoutTerminater;
                }
                Log.i(TAG, "makeView blink LED:" + cautionRsrc.led_ids);
                if (cautionRsrc.led_ids != null) {
                    Light.setState(getLedID(), true, getLedBlinkPeriod());
                    setLedIdBlinking(getLedID());
                } else {
                    setLedIdBlinking(null);
                }
            }
            this.cautionUtil.setCurrentCautionData(this.mPriData.type, cautionRsrc.cautionKind, this.mPriData.maxPriorityId, timeoutTerminater);
        }
        Log.i(TAG, "[END] makeView view:" + view);
        return view;
    }

    public IKeyDispatch SpecilalKeyDispatcher(int cautionId) {
        switch (cautionId) {
            case 86:
            case 128:
                IKeyDispatch keyDispatcher = new KeyDispatchForSpeclalPBButtonValid(this);
                return keyDispatcher;
            default:
                return null;
        }
    }

    /* loaded from: classes.dex */
    public class priorityData {
        int maxPriorityId;
        int type;

        public priorityData() {
        }
    }

    public priorityData getMaxPriorityId(int[] cautionId) {
        priorityData priData = new priorityData();
        priData.maxPriorityId = Info.INVALID_CAUTION_ID;
        for (int i = cautionId.length - 1; i >= 0; i--) {
            if (cautionId[i] != 65535) {
                priData.maxPriorityId = cautionId[i];
                priData.type = i;
            }
        }
        Log.i(TAG, "getMaxPriorityId id:" + priData.maxPriorityId);
        return priData;
    }

    public static int getCautionId(int[] cautionId) {
        int maxPriorityId = Info.INVALID_CAUTION_ID;
        for (int i = cautionId.length - 1; i >= 0; i--) {
            if (cautionId[i] != 65535) {
                maxPriorityId = cautionId[i];
            }
        }
        return maxPriorityId;
    }

    private String getStr() {
        String s = getStr(cautionRsrc.getStrId().get(0));
        Log.i(TAG, "getStr string:" + s);
        return s;
    }

    private String getStr(int column) {
        String s = getStr(cautionRsrc.getStrId().get(column));
        return s;
    }

    private String getStr(String strid) {
        Resources res = getActivity().getApplicationContext().getResources();
        String packageName = getActivity().getPackageName();
        int id = res.getIdentifier(strid, "string", "android");
        if (id == 0) {
            id = res.getIdentifier(strid, "string", packageName);
        }
        try {
            String str = getActivity().getApplicationContext().getString(id);
            return str;
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    private int getImg() {
        int img;
        try {
            Resources res = getActivity().getApplicationContext().getResources();
            String packageName = getActivity().getPackageName();
            if (cautionRsrc.getImageId().size() == 0) {
                img = res.getIdentifier("p_invalidfactor_011", "drawable", "android");
            } else {
                img = res.getIdentifier(cautionRsrc.getImageId().get(0), "drawable", "android");
                if (img == 0) {
                    img = res.getIdentifier(cautionRsrc.getImageId().get(0), "drawable", packageName);
                }
            }
            Log.i(TAG, "getImg img:" + img);
            return img;
        } catch (Resources.NotFoundException e) {
            return 0;
        }
    }

    private String getLedID() {
        if (cautionRsrc.getLedId().size() == 0) {
            return null;
        }
        String ledId = cautionRsrc.getLedId().get(0);
        return ledId;
    }

    private static void setLedIdBlinking(String ledId) {
        mLedId = ledId;
    }

    private static String getLedIdBlinking() {
        return mLedId;
    }

    private String getLedBlinkPeriod() {
        if (cautionRsrc.blink_period.equals(BLINK_PERIOD_08) || !cautionRsrc.blink_period.equals(BLINK_PERIOD_32)) {
            return "PTN_FRONT_08";
        }
        return "PTN_FRONT_32";
    }

    private void timeoutProcessing(int time) {
        if (this.mPriData.maxPriorityId == 2913) {
            time = TIMEOUT_EXTENSION_HDMI_CONNECTION;
        }
        this.timeoutHandler.postDelayed(this.timeoutClose, time);
        Log.i(TAG, "timeoutProcessing time:" + time);
    }

    private void setCautionId(int[] cautionId) {
        CautionID = cautionId;
        Log.i(TAG, "setCautionId cautionId:" + Arrays.toString(cautionId));
    }

    private int[] getCautionId() {
        Log.i(TAG, "getCautionId CautionID:" + Arrays.toString(CautionID));
        return CautionID;
    }

    private void setActivity(Activity activity) {
        mActivity = activity;
        Log.i(TAG, "setActivity activity:" + activity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Activity getActivity() {
        Log.i(TAG, "getActivity activity:" + mActivity);
        return mActivity;
    }

    public View getView() {
        Log.i(TAG, "getView");
        return makeView(getCautionId());
    }

    protected IKeyDispatch getCurrentKeyDispatcher() {
        IkeyDispatchEach mEachKeyDispatcher;
        IKeyDispatch keyDispatch = mKeyDispatcher;
        if (eachKeyHandler.indexOfKey(this.mPriData.maxPriorityId) >= 0 && (mEachKeyDispatcher = eachKeyHandler.get(this.mPriData.maxPriorityId)) != null) {
            return mEachKeyDispatcher;
        }
        return keyDispatch;
    }

    public int onKeyDown(int keyCode, KeyEvent event) {
        IKeyDispatch keyDispatch = getCurrentKeyDispatcher();
        if (keyDispatch == null) {
            return 0;
        }
        int ret = keyDispatch.onKeyDown(keyCode, event);
        return ret;
    }

    public int onKeyUp(int keyCode, KeyEvent event) {
        IKeyDispatch keyDispatch = getCurrentKeyDispatcher();
        if (keyDispatch == null) {
            return 0;
        }
        int ret = keyDispatch.onKeyUp(keyCode, event);
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final void setDispatchKeyEvent(int cautionId, IkeyDispatchEach mkey) {
        if (mkey == null) {
            eachKeyHandler.remove(cautionId);
        } else {
            eachKeyHandler.put(cautionId, mkey);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setTerminateCaution(terminateCaution terminate) {
        mTerminateCaution = terminate;
        Log.i(TAG, "setTerminateCaution terminateCaution:" + terminate);
    }

    public static final void executeTerminate() {
        if (mTerminateCaution != null) {
            mTerminateCaution.onTerminate();
            mKeyDispatcher = null;
            if (getLedIdBlinking() != null) {
                Light.setState(getLedIdBlinking(), false);
            }
        }
        Log.i(TAG, "executeTerminate");
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForSpeclalPBButtonValid extends IKeyDispatch {
        public KeyDispatchForSpeclalPBButtonValid(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForDLT02and03 extends IKeyDispatch {
        public KeyDispatchForDLT02and03(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchAllKeysIgnoreWithoutS2Off extends IKeyDispatch {
        public KeyDispatchAllKeysIgnoreWithoutS2Off(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForDLT06 extends IKeyDispatch {
        public KeyDispatchForDLT06(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForCloseKey extends IKeyDispatch {
        public KeyDispatchForCloseKey(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            this.cautionfunc.getActivity().finish();
            BeepUtility.getInstance().playBeep(BeepUtilityRsrcTable.BEEP_ID_SELECT);
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_Cmn extends IKeyDispatch {
        public KeyDispatchForTriggerTable_TrgTbl_Cmn(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial1ToRight() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial1ToLeft() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial2ToRight() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial2ToLeft() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDeleteKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAfMfSlideKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAFMFKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAFMFKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEVKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIsoKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedIsoKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedWBKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDriveModeKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSmartTeleconKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPreviewKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPreviewKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDigitalZoomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDigitalZoomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedRingClockwise() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromTele() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedRingCounterClockwise() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToTele() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromWide() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToWide() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFELKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFELKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfRangeKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedShootingModeKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedExpandFocusKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFocusKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieLockKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieEELockKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedIrisDial() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedResetFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedWaterHousing() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPeakingKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedWaterHousing() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedZebraKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLensFocusHold() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLensFocusHold() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int changedLensPartsState() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int operatedLensParts() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial3ToLeft() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial3ToRight() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnterJoyStickFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELToggleCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELToggleCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfToggleCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncPlus() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncMinus() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingPrev() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedTvAvChangeCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecInhDirectRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedIRShutterNotCheckDrivemodeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterNotCheckDrivemodeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayIndexKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialPrev() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialNext() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedLensApertureRingToRight() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedLensApertureRingToLeft() {
            return 0;
        }

        @Override // com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection extends KeyDispatchForTriggerTable_TrgTbl_Cmn {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAfMfSlideKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromTele() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToTele() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromWide() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToWide() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedShootingModeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFocusKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieLockKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieEELockKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int changedLensPartsState() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int operatedLensParts() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnterJoyStickFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDigitalZoomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedTvAvChangeCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayIndexKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture extends KeyDispatchForTriggerTable_TrgTbl_Cmn {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedIrisDial() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPeakingKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedZebraKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedIRShutterNotCheckDrivemodeKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterNotCheckDrivemodeKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Menu extends KeyDispatchForTriggerTable_TrgTbl_Cmn {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Menu(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial1ToRight() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial1ToLeft() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial2ToRight() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial2ToLeft() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAfMfSlideKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAFMFKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAFMFKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEVKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIsoKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedIsoKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedWBKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDriveModeKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSmartTeleconKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPreviewKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPreviewKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDigitalZoomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDigitalZoomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedExpandFocusKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLensFocusHold() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLensFocusHold() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int changedLensPartsState() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int operatedLensParts() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnterJoyStickFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayIndexKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_PB_Base extends KeyDispatchForTriggerTable_TrgTbl_Cmn {
        public KeyDispatchForTriggerTable_TrgTbl_TST_PB_Base(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromTele() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToTele() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromWide() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToWide() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnterJoyStickFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Index_Base extends KeyDispatchForTriggerTable_TrgTbl_Cmn {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Index_Base(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromTele() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToTele() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedZoomLeverFromWide() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pressedZoomLeverToWide() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnterJoyStickFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Root extends KeyDispatchForTriggerTable_TrgTbl_Cmn {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Root(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial1ToRight() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial1ToLeft() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial2ToRight() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedDial2ToLeft() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAfMfSlideKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAFMFKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAFMFKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEVKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIsoKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedIsoKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedWBKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDriveModeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSmartTeleconKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPreviewKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPreviewKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDigitalZoomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDigitalZoomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedShootingModeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedExpandFocusKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFocusKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieLockKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieEELockKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedResetFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLensFocusHold() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLensFocusHold() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int changedLensPartsState() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int operatedLensParts() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnterJoyStickFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedTvAvChangeCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayIndexKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Index_Func extends KeyDispatchForTriggerTable_TrgTbl_TST_Index_Base {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Index_Func(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Index_Base, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_PB_Func extends KeyDispatchForTriggerTable_TrgTbl_TST_PB_Base {
        public KeyDispatchForTriggerTable_TrgTbl_TST_PB_Func(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_PB_Base, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Caution extends KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Caution(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Base extends KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Base(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func extends KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture {
        public KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_Exclusion extends KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection {
        public KeyDispatchForTriggerTable_Exclusion(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedResetFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_Access extends KeyDispatchForTriggerTable_TrgTbl_TST_Caution {
        public KeyDispatchForTriggerTable_Access(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftUpKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftDownKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAfMfSlideKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFocusModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS1Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedUmRemoteS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUmRemoteRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedShootingModeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModePKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeAKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFocusKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeSKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedModeMKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedEVDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedWaterHousing() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedWaterHousing() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int detachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int changedLensPartsState() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int operatedLensParts() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfToggleCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDispFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDigitalZoomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedApertureCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedShutterSpeedCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedEnter5WayFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedTvOrAvCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedTvAvChangeCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int incrementedProgramShiftCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int decrementedExposureCompensationCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecInhDirectRecKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedIRShutterNotCheckDrivemodeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterNotCheckDrivemodeKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayIndexKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_Reason extends KeyDispatchForTriggerTable_TrgTbl_TST_Caution {
        public KeyDispatchForTriggerTable_Reason(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedUpKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDownKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedRightDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftUpKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedLeftDownKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedResetFuncKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedProjectorKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSettingFuncCustomKey(IKeyFunction func) {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELHoldCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAELToggleCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELHoldCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedSpotAELToggleCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfHoldCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedAfMfToggleCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMfAssistCustomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedGuideFuncKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDeleteFuncKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncPlus() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncPlus() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedPbZoomFuncMinus() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPbZoomFuncMinus() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedFuncRingPrev() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedDigitalZoomKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedMainDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialPrev() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedSubDialNext() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialPrev() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedThirdDialNext() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_Stay extends KeyDispatchForTriggerTable_Exclusion {
        public KeyDispatchForTriggerTable_Stay(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Exclusion, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Exclusion, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Exclusion, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Exe_Connection, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_Msg extends KeyDispatchForTriggerTable_Access {
        public KeyDispatchForTriggerTable_Msg(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS1Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedS2Key() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedMovieRecKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightUpKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedRightDownKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftUpKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedLeftDownKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieLockKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieEELockKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int attachedLens() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRShutterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIR2SecKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedIRRecKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedSpotAELHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedAfMfHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedInvalidCustomKey(IKeyFunction func) {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedFocusHoldCustomKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_Trigger extends KeyDispatchForTriggerTable_Access {
        public KeyDispatchForTriggerTable_Trigger(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedCenterKey() {
            CautionProcessingFunction.executeTerminate();
            return 1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int releasedCenterKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedPlayBackKey() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int turnedModeDial() {
            CautionProcessingFunction.executeTerminate();
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }

    /* loaded from: classes.dex */
    public static class KeyDispatchForTriggerTable_TriggerBtn extends KeyDispatchForTriggerTable_Access {
        public KeyDispatchForTriggerTable_TriggerBtn(CautionProcessingFunction p) {
            super(p);
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS1Key() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedS2Key() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMovieRecKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedMenuKey() {
            CautionProcessingFunction.executeTerminate();
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int pushedFnKey() {
            return -1;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseInvalidKeyHandler, com.sony.imaging.app.fw.BaseKeyHandler, com.sony.imaging.app.fw.IEachFunctionEventHandler
        public int movedAelFocusSlideKey() {
            return 0;
        }

        @Override // com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_Access, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Caution, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_TST_Shoot_Func_Capture, com.sony.imaging.app.base.caution.CautionProcessingFunction.KeyDispatchForTriggerTable_TrgTbl_Cmn, com.sony.imaging.app.fw.BaseKeyHandler
        protected String getKeyConvCategory() {
            return ICustomKey.CATEGORY_MENU;
        }
    }
}
