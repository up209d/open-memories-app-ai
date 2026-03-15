package com.sony.imaging.app.manuallenscompensation.menu.layout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.sony.imaging.app.base.caution.CautionUtilityClass;
import com.sony.imaging.app.base.menu.layout.BaseMenuLayout;
import com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.manuallenscompensation.R;
import com.sony.imaging.app.manuallenscompensation.caution.layout.OCInfo;
import com.sony.imaging.app.manuallenscompensation.commonUtil.AppLog;
import com.sony.imaging.app.manuallenscompensation.commonUtil.MenuHistory;
import com.sony.imaging.app.manuallenscompensation.commonUtil.OCConstants;

/* loaded from: classes.dex */
public class DeleteProfileProcessingMenuLayout extends DisplayMenuItemsMenuLayout {
    private static final String TAG = "ID_DELETEPROFILEPROCESSINGMENULAYOUT";
    private DeleteControlTask mDeleteControlTask = null;
    private ProgressBar mProgressBar = null;
    private int mProfileAction = OCConstants.profileAction;
    private int mProfileProcessError = -1;

    protected int getLayoutResource() {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        return R.layout.lc_delete_processing;
    }

    protected int getProgressBarResource() {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        return R.id.delete_executing_progress;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.data != null) {
            this.mProfileAction = this.data.getInt(OCConstants.PROFILE_ACTION);
            this.mProfileProcessError = this.data.getInt(OCConstants.PROFILE_PROCESS_ERROR, -1);
        } else {
            this.mProfileProcessError = -1;
        }
        View view = obtainViewFromPool(getLayoutResource());
        this.mProgressBar = (ProgressBar) view.findViewById(getProgressBarResource());
        if (this.mProgressBar != null) {
            this.mProgressBar.setProgress(0);
        }
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        return view;
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout
    public void closeMenuLayout(Bundle bundle) {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        BaseMenuLayout layout = (BaseMenuLayout) getLayout("ID_LENSLISTDELETEMENULAYOUT");
        if (layout != null) {
            layout.closeLayout();
        }
        if (this.mDeleteControlTask != null && this.mDeleteControlTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mDeleteControlTask.cancel(true);
        }
        this.mDeleteControlTask = null;
        super.closeMenuLayout(bundle);
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
    }

    public void onProgress(int current, int max) {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        if (this.mProgressBar != null) {
            int i = this.mProgressBar.getProgress();
            this.mProgressBar.incrementProgressBy(i + current);
        }
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        super.onResume();
        updateProcessingTitle();
        this.mDeleteControlTask = new DeleteControlTask();
        this.mDeleteControlTask.execute(5);
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
    }

    private void updateProcessingTitle() {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        TextView label = (TextView) getView().findViewById(R.id.deleteprocess_lc_id);
        if (label != null) {
            switch (this.mProfileAction) {
                case 0:
                    String text = getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DELETING_PROFILE);
                    label.setText(text);
                    break;
                case 1:
                    String text2 = getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_EXPORTING);
                    label.setText(text2);
                    break;
                case 2:
                    String text3 = getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_IMPORTING);
                    label.setText(text3);
                    break;
                case 3:
                    String text4 = getResources().getString(R.string.STRID_FUNC_OPTICAL_COMPENSATION_DELETING_PROFILE);
                    label.setText(text4);
                    break;
                default:
                    AppLog.info("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName() + "default case");
                    break;
            }
        }
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.DisplayMenuItemsMenuLayout, com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        super.onPause();
        if (this.mDeleteControlTask != null && this.mDeleteControlTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.mDeleteControlTask.cancel(true);
        }
        this.mDeleteControlTask = null;
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
    }

    /* loaded from: classes.dex */
    class DeleteControlTask extends AsyncTask<Integer, Integer, Boolean> {
        private final int HUNDRED = 100;
        private final int TWENTY_FIVE = 25;
        private final int ONE_TWENTY_FIVE = 125;

        DeleteControlTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Integer... arg) {
            AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            for (int i = 25; i <= 125 && !isCancelled(); i += 25) {
                try {
                    if (DeleteProfileProcessingMenuLayout.this.mProfileAction == 3) {
                        Thread.sleep(200L);
                    } else {
                        Thread.sleep(80L);
                    }
                } catch (InterruptedException e) {
                    Log.d("DeleteControlTask", e.toString());
                }
                if (i == 100 && DeleteProfileProcessingMenuLayout.this.mProfileProcessError != -1) {
                    displayErrorCautions();
                    break;
                }
                publishProgress(25);
            }
            AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            return true;
        }

        private void displayErrorCautions() {
            AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            switch (DeleteProfileProcessingMenuLayout.this.mProfileProcessError) {
                case 0:
                    CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_CANNOT_DELETE);
                    break;
                case 1:
                    CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_EXPORTING_FAILED);
                    break;
                case 2:
                    CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_IMPORTING_FAILED);
                    break;
            }
            AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onProgressUpdate(Integer... values) {
            AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            if (values != null && values[0].intValue() <= 100) {
                DeleteProfileProcessingMenuLayout.this.onProgress(values[0].intValue(), 100);
                super.onProgressUpdate((Object[]) values);
                AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            super.onCancelled();
            AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            if (DeleteProfileProcessingMenuLayout.this.data == null) {
                DeleteProfileProcessingMenuLayout.this.data = new Bundle();
            }
            closePreviouslayouts();
            if (DeleteProfileProcessingMenuLayout.this.mDeleteControlTask != null && DeleteProfileProcessingMenuLayout.this.mDeleteControlTask.getStatus() == AsyncTask.Status.RUNNING) {
                switch (DeleteProfileProcessingMenuLayout.this.mProfileAction) {
                    case 2:
                        AppLog.trace("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
                        if (DeleteProfileProcessingMenuLayout.this.mProfileProcessError != -1) {
                            DeleteProfileProcessingMenuLayout.this.data.remove(OCConstants.TAG_IMPORT_SUCCESSFUL);
                        } else {
                            DeleteProfileProcessingMenuLayout.this.data.putBoolean(OCConstants.TAG_IMPORT_SUCCESSFUL, true);
                        }
                        DeleteProfileProcessingMenuLayout.this.openPreviousMenu();
                        break;
                    case 3:
                        MenuHistory.getInstance().clearMenuHistory();
                        CameraNotificationManager.getInstance().requestNotify(OCConstants.TAG_DELETE_PROCESS);
                        DeleteProfileProcessingMenuLayout.this.openMenuLayout(ExternalProfileMenuLayout.TAG, DeleteProfileProcessingMenuLayout.this.data);
                        break;
                    default:
                        DeleteProfileProcessingMenuLayout.this.closeLayout();
                        break;
                }
            }
            super.onPostExecute((DeleteControlTask) result);
            AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        }

        private void closePreviouslayouts() {
            AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
            switch (DeleteProfileProcessingMenuLayout.this.mProfileAction) {
                case 1:
                    String previousMenuID = MenuHistory.getInstance().popMenuItem();
                    DeleteProfileProcessingMenuLayout.this.closeStackedLayout(previousMenuID);
                    if (DeleteProfileProcessingMenuLayout.this.mProfileProcessError == -1) {
                        CautionUtilityClass.getInstance().requestTrigger(OCInfo.CAUTION_ID_DLAPP_EXPORTING_COMPLETE);
                        break;
                    }
                    break;
                case 2:
                    String previousMenuID2 = MenuHistory.getInstance().popMenuItem();
                    DeleteProfileProcessingMenuLayout.this.closeStackedLayout(previousMenuID2);
                    String previousMenuID3 = MenuHistory.getInstance().popMenuItem();
                    DeleteProfileProcessingMenuLayout.this.closeStackedLayout(previousMenuID3);
                    break;
                case 3:
                    String previousMenuID4 = MenuHistory.getInstance().popMenuItem();
                    DeleteProfileProcessingMenuLayout.this.closeStackedLayout(previousMenuID4);
                    break;
                default:
                    AppLog.trace("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
                    break;
            }
            AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeStackedLayout(String previousID) {
        BaseMenuLayout layout;
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        if (previousID != null && (layout = (BaseMenuLayout) getLayout(previousID)) != null) {
            layout.closeLayout();
        }
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
    }

    @Override // com.sony.imaging.app.base.menu.layout.BaseMenuLayout, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyDown(int keyCode, KeyEvent event) {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IKeyHandler
    public int onKeyUp(int keyCode, KeyEvent event) {
        AppLog.enter("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        AppLog.exit("ID_DELETEPROFILEPROCESSINGMENULAYOUT", AppLog.getMethodName());
        return -1;
    }
}
