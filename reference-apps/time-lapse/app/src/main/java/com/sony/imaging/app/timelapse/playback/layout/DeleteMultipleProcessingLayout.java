package com.sony.imaging.app.timelapse.playback.layout;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.timelapse.AppLog;
import com.sony.imaging.app.timelapse.R;
import com.sony.imaging.app.timelapse.TimeLapseConstants;
import com.sony.imaging.app.timelapse.metadatamanager.TLException;
import com.sony.imaging.app.timelapse.playback.controller.PlayBackController;
import com.sony.imaging.app.util.NotificationListener;

/* loaded from: classes.dex */
public class DeleteMultipleProcessingLayout extends EditorProcessingLayoutBase implements NotificationListener {
    private static final int FINAL_STEP = 100;
    private static final int FIRST_STEP = 25;
    private static final int INITIAL_START = 0;
    private static final long WAIT_TIME = 400;
    private DeleteControlTask mDeleteControlTask = null;
    private ProgressBar mProgressBar = null;
    private static int count = 0;
    private static String[] TAGS = {TimeLapseConstants.DELETE_FRAME, TimeLapseConstants.UPDATE_DELETED_FRAME, TimeLapseConstants.UPDATE_DELETED_FRAME_AVI, TimeLapseConstants.PLAY_ROOT_CONTAINER_REMOVED};

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    protected int getLayoutResource() {
        return R.layout.pb_layout_cmn_deletor_processing;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase
    protected int getProgressBarResource() {
        return R.id.executing_progress_timelapse;
    }

    @Override // com.sony.imaging.app.base.playback.layout.EditorProcessingLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        super.onResume();
        this.mProgressBar = (ProgressBar) getView().findViewById(getProgressBarResource());
        AppLog.trace(this.TAG, "onResume");
        count = 0;
        CameraNotificationManager.getInstance().setNotificationListener(this);
        this.mDeleteControlTask = new DeleteControlTask();
        this.mDeleteControlTask.execute(Integer.valueOf(ListViewLayout.listPosition));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showProgressInMovieMode() {
        if (PlayBackController.getInstance().getmTimeLapseBO().getShootingMode().equalsIgnoreCase("MOVIE")) {
            for (int progress = 0; progress < 100; progress += 25) {
                try {
                    AppLog.trace(this.TAG, " publishProgress  " + progress);
                    if (this.mProgressBar != null) {
                        this.mProgressBar.setProgress(progress);
                    }
                    if (progress != 75) {
                        AppLog.trace(this.TAG, " publishProgress  WAIT_TIME400");
                        Thread.sleep(WAIT_TIME);
                    }
                } catch (InterruptedException e) {
                    AppLog.trace("DeleteControlTask", e.toString());
                }
            }
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        AppLog.trace(this.TAG, "onPause");
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        if (this.mDeleteControlTask != null && this.mDeleteControlTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mDeleteControlTask.cancel(true);
        }
        this.mDeleteControlTask = null;
        this.mProgressBar = null;
        System.gc();
    }

    /* loaded from: classes.dex */
    class DeleteControlTask extends AsyncTask<Integer, Integer, Boolean> {
        DeleteControlTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Integer... arg) {
            DeleteMultipleProcessingLayout.this.showProgressInMovieMode();
            PlayBackController pbCntl = PlayBackController.getInstance();
            pbCntl.setContentResolver(DeleteMultipleProcessingLayout.this.getActivity().getContentResolver());
            try {
                pbCntl.deletePlaybackImages(arg[0].intValue());
            } catch (TLException e) {
                e.printStackTrace();
            }
            return true;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... values) {
            super.onProgressUpdate((Object[]) values);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            super.onCancelled();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            super.onPostExecute((DeleteControlTask) result);
        }
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return TAGS;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(TimeLapseConstants.UPDATE_DELETED_FRAME_AVI)) {
            AppLog.trace(this.TAG, " publishProgress UPDATE_DELETED_FRAME_AVI ");
            if (this.mProgressBar != null) {
                AppLog.trace(this.TAG, " publishProgress not null of mProgressBar Final proceed ");
                this.mProgressBar.setProgress(100);
            }
            AppLog.trace(this.TAG, " publishProgress UPDATE_DELETED_FRAME_AVI complete");
            return;
        }
        if (tag.equals(TimeLapseConstants.DELETE_FRAME) && PlayBackController.getInstance().getmTimeLapseBO().getShootingMode().equalsIgnoreCase("STILL")) {
            count++;
            onProgress(count, ListViewLayout.mSelectedDeleteImageSize);
            return;
        }
        if (tag.equals(TimeLapseConstants.UPDATE_DELETED_FRAME)) {
            if (this.mDeleteControlTask != null && this.mDeleteControlTask.getStatus() != AsyncTask.Status.FINISHED) {
                AppLog.trace(this.TAG, " publishProgress mDeleteControlTask canceled ");
                this.mDeleteControlTask.cancel(true);
                this.mDeleteControlTask = null;
            }
            closeLayout();
            return;
        }
        if (tag.equals(TimeLapseConstants.PLAY_ROOT_CONTAINER_REMOVED)) {
            closeLayout();
        }
    }
}
