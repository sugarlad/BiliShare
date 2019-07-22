/*
 * Copyright (c) 2015. BiliBili Inc.
 */

package com.bilibili.socialize.sample;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bilibili.socialize.sample.helper.ShareHelper;
import com.bilibili.socialize.share.core.error.BiliShareStatusCode;
import com.bilibili.socialize.share.utils.R;


/**
 * Share Helper Activity
 *
 * @author yrom
 */
public abstract class BaseShareableActivity extends AppCompatActivity implements ShareHelper.Callback {
    protected ShareHelper mShare;

    public void startShare(@Nullable View anchor) {
        startShare(anchor, false);
    }

    public void startShare(@Nullable View anchor, boolean isWindowFullScreen) {
        if (mShare == null) {
            mShare = ShareHelper.instance(this, this);
        }
        if (anchor == null) {
            mShare.showShareDialog();
        } else {
            if (isWindowFullScreen)
                mShare.showShareFullScreenWindow(anchor);
            else
                mShare.showShareWarpWindow(anchor);
        }
    }

    @Override
    protected void onDestroy() {
        if (mShare != null) {
            mShare.reset(); // reset held instance
            mShare = null;
        }
        super.onDestroy();
    }

    @Override
    public void onShareStart(ShareHelper helper) {

    }

    @Override
    public void onShareComplete(ShareHelper helper, int code) {
        if (code == BiliShareStatusCode.ST_CODE_SUCCESSED)
            Toast.makeText(this, R.string.bili_share_sdk_share_success, Toast.LENGTH_SHORT).show();
        else if (code == BiliShareStatusCode.ST_CODE_ERROR)
            Toast.makeText(this, R.string.bili_share_sdk_share_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(ShareHelper helper) {
    }

}
