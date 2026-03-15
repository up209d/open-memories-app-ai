package com.sony.imaging.app.srctrl.util.cameraoperation;

import android.util.Log;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.base.shooting.camera.CameraSetting;
import com.sony.imaging.app.base.shooting.camera.DigitalZoomController;
import com.sony.imaging.app.base.shooting.camera.FocusMagnificationController;
import com.sony.imaging.app.base.shooting.camera.executor.ExecutorCreator;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.sysutil.ScalarProperties;
import com.sony.scalar.webapi.service.camera.v1_0.common.struct.TouchAFCurrentPositionParams;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public class CameraOperationZoom {
    private static final String ZOOM_DIRECTION_IN = "in";
    private static final String ZOOM_DIRECTION_OUT = "out";
    private static final String ZOOM_MOVEMENT_SHORT_PUSH = "1shot";
    private static final String ZOOM_MOVEMENT_STOP = "stop";
    private static final String ZOOM_MOVEMENT_STRAT = "start";
    private static final int ZOOM_NUMBER_BOX_DIGIT = 1;
    private static final int ZOOM_NUMBER_BOX_OPT = 1;
    private static final int ZOOM_NUMBER_BOX_OPT_DIGIT = 2;
    private static final String TAG = CameraOperationZoom.class.getSimpleName();
    private static WeakReference<NotificationListener> s_NotificationListenerRef = new WeakReference<>(null);

    public static NotificationListener getNotificationListener() {
        NotificationListener notificationListener = s_NotificationListenerRef.get();
        if (notificationListener == null) {
            NotificationListener notificationListener2 = new NotificationListener() { // from class: com.sony.imaging.app.srctrl.util.cameraoperation.CameraOperationZoom.1
                public static final int DISPLAY_PATTERN_DIGIT = 2;
                public static final int DISPLAY_PATTERN_NONE = 5;
                public static final int DISPLAY_PATTERN_OPT = 3;
                public static final int DISPLAY_PATTERN_OPT_DIGIT = 0;
                public static final int DISPLAY_PATTERN_UNDEFINED = 10;
                private static final int INIT_ZOOM_MAG = 100;

                @Override // com.sony.imaging.app.util.NotificationListener
                public String[] getTags() {
                    return new String[]{CameraNotificationManager.ZOOM_INFO_CHANGED, CameraNotificationManager.POWER_ZOOM_CHANGED, CameraNotificationManager.DEVICE_LENS_CHANGED, CameraNotificationManager.PICTURE_QUALITY, CameraNotificationManager.PICTURE_SIZE, CameraNotificationManager.DIGITAL_ZOOM_MODE_CHANGED};
                }

                private int getDispPattern() {
                    int dispPattern;
                    if (1 == ScalarProperties.getInt("model.category")) {
                        if (DigitalZoomController.getInstance().isZoomAvailable()) {
                            if (ExecutorCreator.getInstance().isSpinalZoom()) {
                                if (1 == CameraSetting.getInstance().getPowerZoomStatus()) {
                                    dispPattern = 0;
                                } else {
                                    dispPattern = 2;
                                }
                            } else {
                                dispPattern = 3;
                            }
                        } else if (1 == CameraSetting.getInstance().getPowerZoomStatus()) {
                            dispPattern = 3;
                        } else {
                            dispPattern = 5;
                        }
                    } else if (1 == ScalarProperties.getInt("device.zoom.lever")) {
                        if (100 == DigitalZoomController.getInstance().getMaxDigitalZoomMagnification(DigitalZoomController.DIGITAL_ZOOM_TYPE_PRECISION)) {
                            dispPattern = 3;
                        } else {
                            dispPattern = 0;
                        }
                    } else if (DigitalZoomController.getInstance().isZoomAvailable()) {
                        dispPattern = 2;
                    } else {
                        dispPattern = 5;
                    }
                    Log.v(CameraOperationZoom.TAG, "getDispPattern: dispPattern=" + dispPattern);
                    return dispPattern;
                }

                private int adjustDigitalZoomPosition(int getPosition) {
                    if (getPosition == 0) {
                        int mag = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification();
                        if (mag == 100) {
                            return getPosition;
                        }
                        int ajustPosition = getPosition + 1;
                        return ajustPosition;
                    }
                    if (getPosition != 100) {
                        return getPosition;
                    }
                    int mag2 = DigitalZoomController.getInstance().getCurrentDigitalZoomMagnification();
                    if (mag2 == DigitalZoomController.getInstance().getMaxDigitalZoomMagnification(null)) {
                        return getPosition;
                    }
                    int ajustPosition2 = getPosition - 1;
                    return ajustPosition2;
                }

                @Override // com.sony.imaging.app.util.NotificationListener
                public void onNotify(String tag) {
                    if (CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag) || CameraNotificationManager.POWER_ZOOM_CHANGED.equals(tag) || CameraNotificationManager.DEVICE_LENS_CHANGED.equals(tag) || CameraNotificationManager.PICTURE_QUALITY.equals(tag) || CameraNotificationManager.PICTURE_SIZE.equals(tag) || CameraNotificationManager.DIGITAL_ZOOM_MODE_CHANGED.equals(tag)) {
                        int position = -1;
                        int numberBox = -1;
                        int indexCurrentBox = -1;
                        int positionCurrentBox = -1;
                        if (!CameraNotificationManager.ZOOM_INFO_CHANGED.equals(tag) && !CameraNotificationManager.POWER_ZOOM_CHANGED.equals(tag)) {
                            CameraSetting.getInstance().updateParameters();
                        }
                        switch (getDispPattern()) {
                            case 0:
                                numberBox = 2;
                                if (DigitalZoomController.getInstance().isDigitalZoomStatus()) {
                                    indexCurrentBox = 1;
                                    int positionCurrentBox2 = DigitalZoomController.getInstance().getDigitalZoomPosition();
                                    positionCurrentBox = adjustDigitalZoomPosition(positionCurrentBox2);
                                    position = (positionCurrentBox + 100) / 2;
                                    break;
                                } else {
                                    indexCurrentBox = 0;
                                    positionCurrentBox = DigitalZoomController.getInstance().getOpticalZoomPosition();
                                    position = positionCurrentBox / 2;
                                    break;
                                }
                            case 2:
                                numberBox = 1;
                                indexCurrentBox = 0;
                                int position2 = DigitalZoomController.getInstance().getDigitalZoomPosition();
                                position = adjustDigitalZoomPosition(position2);
                                positionCurrentBox = position;
                                break;
                            case 3:
                                numberBox = 1;
                                indexCurrentBox = 0;
                                position = DigitalZoomController.getInstance().getOpticalZoomPosition();
                                positionCurrentBox = position;
                                break;
                        }
                        Log.v(CameraOperationZoom.TAG, "onNotify: position=" + position + "  numberBox:" + numberBox + "  indexCurrentBox:" + indexCurrentBox + "  positionCurrentBox:" + positionCurrentBox);
                        boolean toBeNotified = ParamsGenerator.updateZoomInformationParams(position, numberBox, indexCurrentBox, positionCurrentBox);
                        if (toBeNotified) {
                            TouchAFCurrentPositionParams param = CameraOperationTouchAFPosition.get();
                            if (param.set.booleanValue()) {
                                CameraOperationTouchAFPosition.leaveTouchAFMode(true);
                            } else {
                                FocusMagnificationController focusMagnificationController = FocusMagnificationController.getInstance();
                                if (focusMagnificationController.isMagnifying() || focusMagnificationController.isStarting()) {
                                    focusMagnificationController.stop();
                                }
                            }
                            ServerEventHandler.getInstance().onServerStatusChanged();
                        }
                    }
                }
            };
            s_NotificationListenerRef = new WeakReference<>(notificationListener2);
            return notificationListener2;
        }
        return notificationListener;
    }

    private static int getBaseIdStr(String param) {
        if (ZOOM_DIRECTION_IN.equals(param)) {
            return 0;
        }
        if (ZOOM_DIRECTION_OUT.equals(param)) {
            return 1;
        }
        Log.e(TAG, "invalid direction. param=" + param);
        return -1;
    }

    private static int getZoomSpeed() {
        int speed = DigitalZoomController.getInstance().getMaxZoomSpeed();
        return speed / 8;
    }

    private static int getOneShotZoomSpeed() {
        int speed = DigitalZoomController.getInstance().getMaxZoomSpeed();
        return speed / 4;
    }

    private static int getOneShotWaitingTime() {
        return 100;
    }

    public static boolean set(String direction, String movement) {
        if ("start".equals(movement)) {
            int baseId = getBaseIdStr(direction);
            if (-1 == baseId) {
                return false;
            }
            DigitalZoomController.getInstance().startZoom(baseId, getZoomSpeed());
        } else if (!"stop".equals(movement)) {
            if (ZOOM_MOVEMENT_SHORT_PUSH.equals(movement)) {
                int baseId2 = getBaseIdStr(direction);
                if (-1 == baseId2) {
                    return false;
                }
                DigitalZoomController.getInstance().startZoom(baseId2, getOneShotZoomSpeed());
                try {
                    Thread.sleep(getOneShotWaitingTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    DigitalZoomController.getInstance().stopZoom();
                }
            }
        }
        return true;
    }
}
