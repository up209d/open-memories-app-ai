package com.sony.imaging.app.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.hardware.Camera;
import com.sony.scalar.hardware.CameraEx;
import com.sony.scalar.media.AudioManager;
import com.sony.scalar.media.MediaRecorder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: classes.dex */
public abstract class AvailableInfo {
    public static boolean DEBUG = false;
    private static AvailableInfoInhMgr defaultInfo = null;
    private static List<AvailableInfoImpl> additionalInfos = null;
    private static Context mContext = null;
    private static final String[] MEDIAOFFSET = {"_00", "_01", "_02", "_03", "_04", "_05", "_06", "_07"};
    private static HashMap<String, Integer> mediaIDs = new HashMap<>();

    public static void initialize() {
        initialize(null);
    }

    public static void initialize(Context context) {
        if (defaultInfo == null) {
            defaultInfo = new AvailableInfoInhMgr();
            defaultInfo.initialize();
        }
        mContext = context;
    }

    public static void addDatabase(String DataBasePath) {
        if (additionalInfos == null) {
            additionalInfos = new ArrayList();
        } else {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                additionalInfo.terminate();
            }
            additionalInfos.clear();
        }
        AvailableInfoImpl info = new AvailableInfoChangeable(mContext, DataBasePath);
        try {
            info.initialize();
            additionalInfos.add(info);
        } catch (SQLiteException e) {
            info.terminate();
        }
    }

    public static void terminate() {
        if (defaultInfo != null) {
            defaultInfo.terminate();
            defaultInfo = null;
        }
        if (additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                additionalInfo.terminate();
            }
            additionalInfos.clear();
            additionalInfos = null;
        }
    }

    public static void update() {
        if (defaultInfo != null) {
            defaultInfo.update();
        }
    }

    public static boolean isFactor(String factorID, String mediaID) {
        int offset = getMediaOffset(mediaID);
        return isFactor(factorID + MEDIAOFFSET[offset]);
    }

    public static boolean isFactor(String factorID) {
        boolean ret = false;
        if (defaultInfo != null) {
            ret = defaultInfo.isFactor(factorID);
        }
        if (!ret && additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                ret = additionalInfo.isFactor(factorID);
                if (ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isFactor(String factorID, Object... api) {
        boolean ret = false;
        if (defaultInfo != null) {
            Cursor featuresCursor = defaultInfo.getFeaturesCursor(api);
            Cursor factorsCursor = defaultInfo.getFactorsCursor(featuresCursor);
            ret = defaultInfo.isFactorWithCursor(factorID, factorsCursor);
            if (!ret && additionalInfos != null) {
                List<String> features = defaultInfo.getFeatures(featuresCursor);
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = additionalInfo.isInhibitionWithFactor(factorID, features);
                    if (ret) {
                        break;
                    }
                }
            }
            if (featuresCursor != null) {
                featuresCursor.close();
            }
            if (factorsCursor != null) {
                factorsCursor.close();
            }
        }
        return ret;
    }

    public static boolean isFactor(String factorID, Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        List<Object[]> list;
        boolean ret = false;
        if (defaultInfo != null && !(ret = defaultInfo.isFactor(factorID, (list = defaultInfo.getApiList(p, m, mp, ma, api)))) && additionalInfos != null) {
            for (Object[] objs : list) {
                Cursor featuresCursor = defaultInfo.getFeaturesCursor(objs);
                List<String> features = defaultInfo.getFeatures(featuresCursor);
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = !additionalInfo.isInhibitionWithFactor(factorID, features);
                    if (!ret) {
                        break;
                    }
                }
                featuresCursor.close();
                if (!ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isFactor(String factorID, Camera.Parameters p, CameraEx.ParametersModifier m, Object... api) {
        return isFactor(factorID, p, m, null, null, api);
    }

    public static boolean isInhibition(String inhFeatureID, String mediaID) {
        int offset = getMediaOffset(mediaID);
        return isInhibition(inhFeatureID + MEDIAOFFSET[offset]);
    }

    public static boolean isInhibition(String inhFeatureID) {
        boolean ret = false;
        if (defaultInfo != null) {
            ret = defaultInfo.isInhibition(inhFeatureID);
        }
        if (!ret && additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                ret = additionalInfo.isInhibition(inhFeatureID);
                if (ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isAvailable(Object... api) {
        boolean ret = true;
        if (defaultInfo != null) {
            Cursor featuresCursor = defaultInfo.getFeaturesCursor(api);
            Cursor factorsCursor = defaultInfo.getFactorsCursor(featuresCursor);
            ret = defaultInfo.isAvailable(factorsCursor);
            if (ret && additionalInfos != null) {
                List<String> features = defaultInfo.getFeatures(featuresCursor);
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = !additionalInfo.isInhibition(features);
                    if (!ret) {
                        break;
                    }
                }
            }
            if (featuresCursor != null) {
                featuresCursor.close();
            }
            if (factorsCursor != null) {
                factorsCursor.close();
            }
        }
        return ret;
    }

    public static boolean isAvailable(Camera.Parameters p, CameraEx.ParametersModifier m, MediaRecorder.Parameters mp, AudioManager.Parameters ma, Object... api) {
        List<Object[]> list;
        boolean ret = true;
        if (defaultInfo != null && (ret = defaultInfo.isAvailabel((list = defaultInfo.getApiList(p, m, mp, ma, api)))) && additionalInfos != null) {
            for (Object[] objs : list) {
                Cursor featuresCursor = defaultInfo.getFeaturesCursor(objs);
                List<String> features = defaultInfo.getFeatures(featuresCursor);
                for (AvailableInfoImpl additionalInfo : additionalInfos) {
                    ret = !additionalInfo.isInhibition(features);
                    if (!ret) {
                        break;
                    }
                }
                featuresCursor.close();
                if (!ret) {
                    break;
                }
            }
        }
        return ret;
    }

    public static boolean isAvailable(Camera.Parameters p, CameraEx.ParametersModifier m, Object... api) {
        return isAvailable(p, m, null, null, api);
    }

    private static int getMediaOffset(String mediaID) {
        Integer offset = mediaIDs.get(mediaID);
        if (offset == null) {
            String[] _mID = {mediaID};
            int[] _ret = defaultInfo.getMediaOffsets(_mID);
            if (-1 == _ret[0]) {
                throw new InvalidParameterException("MediaID: " + mediaID + " doesn't exist!");
            }
            mediaIDs.put(mediaID, Integer.valueOf(_ret[0]));
            int ret = _ret[0];
            return ret;
        }
        int ret2 = offset.intValue();
        return ret2;
    }

    public static void setFactor(String factorID, boolean value) {
        if (additionalInfos != null) {
            for (AvailableInfoImpl additionalInfo : additionalInfos) {
                additionalInfo.setFactor(factorID, value);
            }
        }
    }
}
