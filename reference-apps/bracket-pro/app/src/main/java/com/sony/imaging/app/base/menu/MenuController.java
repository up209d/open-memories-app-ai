package com.sony.imaging.app.base.menu;

import android.util.Log;
import android.util.SparseArray;
import com.sony.imaging.app.base.menu.IController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MenuController implements IController {
    public static final String SEGMENT_1ST_LAYER = "SEGMENT_1ST_LAYER";
    public static final String SEGMENT_1ST_LAYER_INTERVAL = "interval";
    public static final String SEGMENT_1ST_LAYER_LIVE = "live";
    public static final String SEGMENT_1ST_LAYER_LOOPREC = "loop";
    public static final String SEGMENT_1ST_LAYER_MOVIE = "movie";
    public static final String SEGMENT_1ST_LAYER_PHOTO = "photo";
    public static final String SEGMENT_1ST_LAYER_PLAY = "play";
    public static final String SEGMENT_1ST_LAYER_POWER_OFF = "power_off";
    public static final String SEGMENT_1ST_LAYER_SETUP = "setup";
    public static final String SEGMENT_CONFIG = "config_transition";
    protected static final String TAG = "MenuController";
    protected static MenuController sInstance;
    protected static SparseArray<String> sMapRecModeToValue;
    protected static SparseArray<MenuSelection> sMapScalarPropToValue;
    protected static List<String> sSupported1stLayer = new ArrayList();
    protected String m1stLayerSelection = "movie";

    /* loaded from: classes.dex */
    public static class MenuSelection {
        public String containerId;
        public String itemId;

        public MenuSelection(String container, String item) {
            this.containerId = container;
            this.itemId = item;
        }
    }

    static {
        sSupported1stLayer.add("movie");
        sSupported1stLayer.add("photo");
        sSupported1stLayer.add("interval");
        sSupported1stLayer.add("loop");
        sSupported1stLayer.add("live");
        sSupported1stLayer.add(SEGMENT_1ST_LAYER_PLAY);
        sSupported1stLayer.add(SEGMENT_1ST_LAYER_SETUP);
        sSupported1stLayer.add(SEGMENT_1ST_LAYER_POWER_OFF);
        sMapRecModeToValue = new SparseArray<>();
        sMapRecModeToValue.put(2, "movie");
        sMapRecModeToValue.put(1, "photo");
        sMapRecModeToValue.put(4, "interval");
        sMapRecModeToValue.put(8, "loop");
        sMapScalarPropToValue = new SparseArray<>();
        sMapScalarPropToValue.put(7, new MenuSelection(SEGMENT_1ST_LAYER_SETUP, SEGMENT_CONFIG));
        sMapScalarPropToValue.put(5, new MenuSelection(SEGMENT_1ST_LAYER, "live"));
        sMapScalarPropToValue.put(6, new MenuSelection(SEGMENT_1ST_LAYER, SEGMENT_1ST_LAYER_PLAY));
        sMapScalarPropToValue.put(8, new MenuSelection(SEGMENT_1ST_LAYER, SEGMENT_1ST_LAYER_SETUP));
    }

    protected static void setInstance(MenuController instance) {
        if (sInstance == null) {
            sInstance = instance;
        }
    }

    protected MenuController() {
        setInstance(this);
    }

    public static MenuController getInstance() {
        if (sInstance == null) {
            new MenuController();
        }
        return sInstance;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setValue(String tag, String value) throws IllegalArgumentException {
        if (SEGMENT_1ST_LAYER.equals(tag)) {
            Log.d(TAG, "setValue : " + value);
            this.m1stLayerSelection = value;
        }
    }

    public void storePosition(String tag, String value) {
        if (SEGMENT_1ST_LAYER.equals(tag)) {
            Log.d(TAG, "storePosition : " + value);
            this.m1stLayerSelection = value;
        }
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public String getValue(String tag) throws IController.NotSupportedException {
        if (!SEGMENT_1ST_LAYER.equals(tag)) {
            return null;
        }
        Log.d(TAG, "getValue : " + this.m1stLayerSelection);
        return this.m1stLayerSelection;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public Object getDetailValue() {
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public void setDetailValue(Object obj) {
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getSupportedValue(String tag) {
        if (SEGMENT_1ST_LAYER.equals(tag)) {
            return sSupported1stLayer;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public List<String> getAvailableValue(String tag) {
        if (SEGMENT_1ST_LAYER.equals(tag)) {
            return sSupported1stLayer;
        }
        return null;
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public boolean isAvailable(String tag) {
        return SEGMENT_1ST_LAYER.equals(tag);
    }

    @Override // com.sony.imaging.app.base.menu.IController
    public int getCautionIndex(String itemId) {
        return 0;
    }

    public String convertFromRecMode(int recMode) {
        return sMapRecModeToValue.get(recMode);
    }

    public MenuSelection convertFromScalarProperty(int property) {
        return sMapScalarPropToValue.get(property);
    }
}
