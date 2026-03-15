package com.sony.imaging.app.startrails.playback.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.sony.imaging.app.base.common.EventParcel;
import com.sony.imaging.app.base.playback.contents.EditService;
import com.sony.imaging.app.base.playback.layout.BrowserIndexLayout;
import com.sony.imaging.app.base.shooting.camera.CameraNotificationManager;
import com.sony.imaging.app.fw.AppRoot;
import com.sony.imaging.app.startrails.R;
import com.sony.imaging.app.startrails.database.DataBaseAdapter;
import com.sony.imaging.app.startrails.database.DataBaseOperations;
import com.sony.imaging.app.startrails.database.StarTrailsBO;
import com.sony.imaging.app.startrails.playback.STPlayRootContainer;
import com.sony.imaging.app.startrails.playback.controller.PlayBackController;
import com.sony.imaging.app.startrails.util.STConstants;
import com.sony.imaging.app.util.ApoWrapper;
import com.sony.imaging.app.util.NotificationListener;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class ListViewLayout extends BrowserIndexLayout implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, NotificationListener {
    public static int listPosition = -1;
    public static int listlastSelected = 0;
    public static int mListSize = 0;
    public static int mSelectedDeleteImageSize = 1;
    ListViewAdapter adapter;
    private View mCurrentView;
    private ImageView mHeaderView;
    private TextView mListViewTitle;
    public boolean mTransitionToDeletion = false;
    private ArrayList<StarTrailsBO> mDetailList = null;
    private ListView mListView = null;
    EditService editService = null;
    private TextView mNoItemsToDisplayView = null;
    private final String[] tags = {STConstants.UPDATE_DELETED_FRAME};

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (this.mCurrentView == null) {
            this.mCurrentView = (ViewGroup) obtainViewFromPool(R.layout.pb_layout_list_view);
        }
        this.mNoItemsToDisplayView = (TextView) this.mCurrentView.findViewById(R.id.no_item_view);
        this.mListViewTitle = (TextView) this.mCurrentView.findViewById(R.id.list_view_title);
        this.mHeaderView = (ImageView) this.mCurrentView.findViewById(R.id.header);
        this.mListView = (ListView) this.mCurrentView.findViewById(R.id.list_view);
        this.mListView.setOnItemClickListener(this);
        return this.mCurrentView;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        listPosition = position;
        transitionToSinglePlayback();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedPlayBackKey() {
        PlayBackController.getInstance().setDefaultPBState();
        return super.pushedPlayBackKey();
    }

    private void transitionToSinglePlayback() {
        PlayBackController.getInstance().setPrevPBState((byte) 1);
        PlayBackController.getInstance().setCurrentPBState((byte) 0);
        transitionSinglePb();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, android.widget.AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        listPosition = position;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.IndexLayoutBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onResume() {
        Log.e("INSIDE", "onResume");
        if (validateDBStatus()) {
            PlayBackController.getInstance().setCurrentPBState((byte) 1);
            STPlayRootContainer.isSTListView = true;
            ApoWrapper.setApoType(ApoWrapper.APO_TYPE.SPECIAL);
            this.mListView.setOnTouchListener(new View.OnTouchListener() { // from class: com.sony.imaging.app.startrails.playback.layout.ListViewLayout.1
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    ListViewLayout.this.mListView.getParent().requestDisallowInterceptTouchEvent(true);
                    ListViewLayout.this.mListView.requestFocus();
                    return true;
                }
            });
            this.mListView.requestFocus();
            this.mListView.setOnItemSelectedListener(this);
            CameraNotificationManager.getInstance().setNotificationListener(this);
            setKeyBeepPattern(getResumeKeyBeepPattern());
            viewVisibility();
            listlastSelected = listPosition;
        }
    }

    private boolean validateDBStatus() {
        if (DataBaseAdapter.getInstance().isDBStatusCorrupt()) {
            transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
            return false;
        }
        this.mDetailList = DataBaseOperations.getInstance().getStartrailsBOList();
        mListSize = this.mDetailList.size();
        this.adapter = new ListViewAdapter(getActivity().getApplicationContext(), -1, this.mDetailList);
        this.mListView.setAdapter((ListAdapter) this.adapter);
        return true;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.FakeFragment
    public void onPause() {
        super.onPause();
        CameraNotificationManager.getInstance().removeNotificationListener(this);
        deInitialize();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.Layout, com.sony.imaging.app.fw.FakeFragment
    public void onDestroyView() {
        super.onDestroyView();
        deInitialize();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedSK2Key() {
        return -1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedS1Key() {
        PlayBackController.getInstance().setCurrentPBState((byte) 2);
        PlayBackController.getInstance().setPrevPBState((byte) 0);
        return super.pushedS1Key();
    }

    public void viewVisibility() {
        if (this.mListView.getAdapter().getCount() <= 0) {
            this.mNoItemsToDisplayView.setVisibility(0);
            this.mListViewTitle.setVisibility(4);
            this.mHeaderView.setVisibility(4);
        } else {
            this.mNoItemsToDisplayView.setVisibility(8);
            this.mListViewTitle.setVisibility(0);
            this.mHeaderView.setVisibility(0);
            this.mListView.setSelection(listPosition);
            this.mListView.setFocusable(true);
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase
    public int getLayoutResource() {
        return R.layout.pb_layout_list_view;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, android.widget.AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedUpKey() {
        if (this.mListView == null || this.mListView.getAdapter().getCount() == 0) {
            return -1;
        }
        if (listPosition < 1) {
            return 1;
        }
        listPosition--;
        this.mListView.setSelection(listPosition);
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedDownKey() {
        if (this.mListView == null || this.mListView.getAdapter().getCount() == 0) {
            return -1;
        }
        if (listPosition < this.mListView.getAdapter().getCount() - 1) {
            listPosition++;
            this.mListView.setSelection(listPosition);
        }
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToLeft() {
        if (this.mListView == null || this.mListView.getAdapter().getCount() == 0) {
            return -1;
        }
        this.mListView.dispatchKeyEvent(new KeyEvent(0, 19));
        return 1;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedShuttleToRight() {
        if (this.mListView == null || this.mListView.getAdapter().getCount() == 0) {
            return -1;
        }
        this.mListView.dispatchKeyEvent(new KeyEvent(0, 20));
        return 1;
    }

    @Override // com.sony.imaging.app.base.playback.layout.BrowserIndexLayout, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int pushedMenuKey() {
        if (this.mNoItemsToDisplayView.getVisibility() == 0) {
            PlayBackController.getInstance().setCurrentPBState((byte) 2);
            PlayBackController.getInstance().setPrevPBState((byte) 0);
            transitionShooting(new EventParcel(AppRoot.USER_KEYCODE.SK1));
            return 1;
        }
        STPlayRootContainer.isSTListView = false;
        listPosition = listlastSelected;
        transitionToSinglePlayback();
        return 1;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public String[] getTags() {
        return this.tags;
    }

    @Override // com.sony.imaging.app.util.NotificationListener
    public void onNotify(String tag) {
        if (tag.equals(STConstants.UPDATE_DELETED_FRAME)) {
            this.mDetailList = DataBaseOperations.getInstance().getStartrailsBOList();
            this.adapter.updateImageAdapter(this.mDetailList);
            this.mListView.requestFocus();
            if (listPosition == 0 && this.mListView.getAdapter().getCount() > 0) {
                Log.e("FIRST", "ITEM DELETED");
                this.mListView.setSelection(0);
            } else if (listPosition > 0 && this.mListView.getAdapter().getCount() > 0) {
                Log.e("" + listPosition, "ITEM DELETED");
                this.mListView.setSelection(listPosition - 1);
            }
            viewVisibility();
        }
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.fw.Layout
    public void onReopened() {
        updateLayout(2);
    }

    private void deInitialize() {
        this.mTransitionToDeletion = false;
        this.mDetailList = null;
        PlayBackController.getInstance().releaseAllAllocatedData();
        if (this.mListView != null) {
            this.mListView.setAdapter((ListAdapter) null);
            this.mListView = null;
        }
        if (this.adapter != null) {
            this.adapter.clearMemory();
            this.adapter = null;
        }
        this.editService = null;
        this.mNoItemsToDisplayView = null;
        this.mHeaderView = null;
        if (this.mCurrentView != null) {
            this.mCurrentView.destroyDrawingCache();
        }
        this.mCurrentView = null;
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToLeft(KeyEvent event) {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachKeyHandler
    public int onDial3TurnedToRight(KeyEvent event) {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedSubDialNext() {
        return pushedDownKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialPrev() {
        return pushedUpKey();
    }

    @Override // com.sony.imaging.app.base.playback.layout.IndexLayoutGridViewBase, com.sony.imaging.app.base.playback.layout.PlayLayoutBase, com.sony.imaging.app.fw.KeyReceiver, com.sony.imaging.app.fw.IEachFunctionEventHandler
    public int turnedMainDialNext() {
        return pushedDownKey();
    }
}
