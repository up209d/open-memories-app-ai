package com.sony.imaging.app.srctrl.util;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.sony.imaging.app.base.common.MediaNotificationManager;
import com.sony.imaging.app.srctrl.shooting.SRCtrlExecutorCreator;
import com.sony.imaging.app.srctrl.shooting.camera.SRCtrlPictureQualityController;
import com.sony.imaging.app.srctrl.util.MediaObserver;
import com.sony.imaging.app.srctrl.webapi.availability.ParamsGenerator;
import com.sony.imaging.app.srctrl.webapi.specific.ServerEventHandler;
import com.sony.imaging.app.srctrl.webapi.specific.ShootingHandler;
import com.sony.imaging.app.util.NotificationListener;
import com.sony.scalar.provider.AvindexStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class MediaObserverAggregator implements MediaObserver.MediaObserverListener, NotificationListener {
    private static final String tag = MediaObserverAggregator.class.getName();
    private static final String[] tags = {MediaNotificationManager.TAG_MEDIA_STATUS_CHANGE};
    private ContentResolver mContentResolver;
    private Handler mHandler;
    private boolean mRegistered = false;
    private List<MediaObserver> mObserverList = new ArrayList();
    private HashMap<String, Integer> mAccumulatedStoredContentsPerMedia = new HashMap<>();
    private boolean mIsMounted = false;

    protected synchronized void finalize() throws Throwable {
        super.finalize();
        stop();
    }

    public synchronized boolean start(Context context, Handler handler) {
        boolean z;
        if (this.mRegistered) {
            Log.v(tag, "Already registered.");
            z = false;
        } else {
            Context appContext = context.getApplicationContext();
            this.mContentResolver = appContext.getContentResolver();
            this.mHandler = handler;
            String[] internalMediaIds = AvindexStore.getInternalMediaIds();
            registerObserversToAllMedia(internalMediaIds, MediaObserver.MediaType.INTERNAL);
            String[] virtualMediaIds = AvindexStore.getVirtualMediaIds();
            registerObserversToAllMedia(virtualMediaIds, MediaObserver.MediaType.VIRTUAL);
            MediaNotificationManager.getInstance().setNotificationListener(this);
            this.mRegistered = true;
            z = this.mRegistered;
        }
        return z;
    }

    public synchronized void stop() {
        MediaNotificationManager.getInstance().removeNotificationListener(this);
        unregisterObserversFromAllMedia();
        this.mAccumulatedStoredContentsPerMedia.clear();
        this.mHandler = null;
        this.mContentResolver = null;
        notifyAll();
        this.mRegistered = false;
    }

    private synchronized boolean registerObserversToAllMedia(String[] mediaIds, MediaObserver.MediaType mediaType) {
        if (mediaIds != null) {
            if (mediaIds.length != 0) {
                for (String mediaId : mediaIds) {
                    registerObserver(mediaId, mediaType);
                }
            }
        }
        return true;
    }

    private synchronized MediaObserver registerObserver(String mediaId, MediaObserver.MediaType mediaType) {
        MediaObserver observer;
        observer = new MediaObserver(this, this.mContentResolver, this.mHandler, mediaId, mediaType);
        this.mContentResolver.registerContentObserver(AvindexStore.Images.Media.getContentUri(mediaId), true, observer);
        this.mObserverList.add(observer);
        MediaObserver.MediaInfo media_info = observer.getMediaInfo();
        Log.v(tag, "New observer added: \n  Media ID:             " + media_info.mMediaId + "\n  Media Type:           " + media_info.mMediaType.name() + "\n  CurrentContentsCount: " + media_info.mCurrentContentsCount + "\n  InitialContentsCount: " + media_info.mInitialContentsCount + "\n  InitialRecorderCount: " + media_info.mInitialRecorderCount + "\n  Expected Pics Count:  " + this.mAccumulatedStoredContentsPerMedia.get(mediaId));
        return observer;
    }

    private synchronized void unregisterObserversFromAllMedia() {
        MediaObserver[] observers = (MediaObserver[]) this.mObserverList.toArray(new MediaObserver[0]);
        for (MediaObserver observer : observers) {
            unregisterObserver(observer);
        }
    }

    private synchronized void unregisterObserver(MediaObserver observer) {
        this.mObserverList.remove(observer);
        this.mContentResolver.unregisterContentObserver(observer);
    }

    @Override // com.sony.imaging.app.srctrl.util.MediaObserver.MediaObserverListener
    public synchronized void notifyMediaContentsInfoChanged() {
        notifyAll();
    }

    public synchronized int getRecordedContentsCount() {
        int total;
        total = 0;
        Iterator i$ = this.mObserverList.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            MediaObserver o = i$.next();
            int count = o.getRecordedContentsCount();
            if (-1 == count) {
                total = -1;
                break;
            }
            total += count;
        }
        return total;
    }

    public synchronized int getRecordedContentsCount(String mediaId) {
        MediaObserver observer;
        observer = getMediaObserver(mediaId);
        return observer == null ? -1 : observer.getRecordedContentsCount();
    }

    public synchronized int getInitialContentsCount(String mediaId) {
        MediaObserver observer;
        observer = getMediaObserver(mediaId);
        return observer == null ? -1 : observer.getInitialContentsCount();
    }

    public synchronized int getCurrentContentsCount(String mediaId) {
        MediaObserver observer;
        observer = getMediaObserver(mediaId);
        return observer == null ? -1 : observer.getCurrentContentsCount();
    }

    public synchronized List<String> getImageFileList(String mediaId, int lastCount) {
        MediaObserver observer;
        observer = getMediaObserver(mediaId);
        return observer != null ? observer.getRecentImageFileList(lastCount) : null;
    }

    private synchronized MediaObserver getMediaObserver(String mediaId) {
        MediaObserver observer;
        observer = null;
        Iterator i$ = this.mObserverList.iterator();
        while (true) {
            if (!i$.hasNext()) {
                break;
            }
            MediaObserver o = i$.next();
            if (o.isMediaIdEqualsTo(mediaId)) {
                observer = o;
                break;
            }
        }
        return observer;
    }

    public void invokeFlushingMediaDatabase(String mediaId) {
        AvindexStore.loadMedia(mediaId, 1);
        AvindexStore.Images.waitAndUpdateDatabase(this.mContentResolver, mediaId);
        AvindexStore.waitLoadMediaComplete(mediaId);
    }

    public void increaseExpectedStoredPictures(String mediaId, int num) {
        Integer numOfAccumulatedTotalStoredContents;
        synchronized (this.mAccumulatedStoredContentsPerMedia) {
            Integer numOfAccumulatedTotalStoredContents2 = this.mAccumulatedStoredContentsPerMedia.remove(mediaId);
            if (numOfAccumulatedTotalStoredContents2 == null) {
                numOfAccumulatedTotalStoredContents = Integer.valueOf(num);
            } else {
                numOfAccumulatedTotalStoredContents = Integer.valueOf(numOfAccumulatedTotalStoredContents2.intValue() + num);
            }
            this.mAccumulatedStoredContentsPerMedia.put(mediaId, numOfAccumulatedTotalStoredContents);
        }
    }

    public void decreaseExpectedStoredPictures(String mediaId, int num) {
        int numOfAccumulatedTotalStoredContents;
        synchronized (this.mAccumulatedStoredContentsPerMedia) {
            Integer numOfAccumulatedTotalStoredContents2 = this.mAccumulatedStoredContentsPerMedia.remove(mediaId);
            if (numOfAccumulatedTotalStoredContents2 == null) {
                numOfAccumulatedTotalStoredContents = 0;
            } else if (numOfAccumulatedTotalStoredContents2.intValue() - num < 0) {
                numOfAccumulatedTotalStoredContents = 0;
            } else {
                numOfAccumulatedTotalStoredContents = Integer.valueOf(numOfAccumulatedTotalStoredContents2.intValue() - num);
            }
            this.mAccumulatedStoredContentsPerMedia.put(mediaId, numOfAccumulatedTotalStoredContents);
        }
    }

    public synchronized int getExpectedStoredPictures(String mediaId) {
        int intValue;
        synchronized (this.mAccumulatedStoredContentsPerMedia) {
            Integer numOfAccumulatedTotalStoredContents = this.mAccumulatedStoredContentsPerMedia.get(mediaId);
            intValue = numOfAccumulatedTotalStoredContents == null ? 0 : numOfAccumulatedTotalStoredContents.intValue();
        }
        return intValue;
    }

    public synchronized MediaObserver.MediaType getMediaType(String mediaId) {
        MediaObserver observer;
        observer = getMediaObserver(mediaId);
        return observer == null ? MediaObserver.MediaType.UNKNOWN : observer.getMediaInfo().mMediaType;
    }

    public static boolean isExternalMediaMounted() {
        MediaObserverAggregator mediaObservers;
        String mediaId = SRCtrlExecutorCreator.getRecordingMedia();
        if (mediaId == null || (mediaObservers = ShootingHandler.getInstance().getMediaObserverAggregator()) == null) {
            return false;
        }
        MediaObserver.MediaType mediaType = mediaObservers.getMediaType(mediaId);
        return MediaObserver.MediaType.EXTERNAL == mediaType;
    }

    private synchronized void resetMediaObserversForExternalMedia() {
        Log.v(tag, "Resetting MediaObservers for external media");
        String[] externalMediaIds = AvindexStore.getExternalMediaIds();
        for (String mediaId : externalMediaIds) {
            MediaObserver observer = getMediaObserver(mediaId);
            if (observer != null) {
                unregisterObserver(observer);
            }
            this.mAccumulatedStoredContentsPerMedia.remove(mediaId);
            registerObserver(mediaId, MediaObserver.MediaType.EXTERNAL);
        }
    }

    private synchronized void removeMediaObserversForExternalMedia() {
        Log.v(tag, "Removing MediaObservers for external media");
        String[] externalMediaIds = AvindexStore.getExternalMediaIds();
        for (String mediaId : externalMediaIds) {
            MediaObserver observer = getMediaObserver(mediaId);
            if (observer != null) {
                unregisterObserver(observer);
            }
        }
        notifyAll();
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag2) {
        int state = MediaNotificationManager.getInstance().getMediaState();
        if (2 == state) {
            Log.v(tag2, "External media was mounted, resetting information of all media");
            resetMediaObserversForExternalMedia();
            ((SRCtrlPictureQualityController) SRCtrlPictureQualityController.getInstance()).setMounted(true);
        } else if (state == 0) {
            Log.v(tag2, "External media was not mounted, removing the observer");
            removeMediaObserversForExternalMedia();
            ((SRCtrlPictureQualityController) SRCtrlPictureQualityController.getInstance()).setMounted(false);
        }
        boolean changed = ParamsGenerator.updatePostviewImageSize();
        if (changed) {
            ServerEventHandler.getInstance().onServerStatusChanged();
        }
    }
}
