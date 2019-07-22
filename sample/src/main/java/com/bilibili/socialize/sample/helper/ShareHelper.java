/*
 * Copyright (c) 2015. BiliBili Inc.
 */

package com.bilibili.socialize.sample.helper;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;

import com.bilibili.socialize.share.core.BiliShare;
import com.bilibili.socialize.share.core.BiliShareConfiguration;
import com.bilibili.socialize.share.core.SocializeListeners;
import com.bilibili.socialize.share.core.SocializeMedia;
import com.bilibili.socialize.share.core.shareparam.BaseShareParam;
import com.bilibili.socialize.share.utils.selector.BaseSharePlatformSelector;
import com.bilibili.socialize.share.utils.selector.BaseSharePlatformSelector.ShareTarget;
import com.bilibili.socialize.share.utils.selector.DialogSharePlatformSelector;
import com.bilibili.socialize.share.utils.selector.PopFullScreenSharePlatformSelector;
import com.bilibili.socialize.share.utils.selector.PopWrapSharePlatformSelector;

/**
 * Helper
 *
 * @author yrom & Jungly.
 */
public final class ShareHelper {
    public static final String QQ_APPID = "";
    public static final String WECHAT_APPID = "";
    public static final String SINA_APPKEY = "1040420100";

    static final String APP_URL = "http://app.bilibili.com";
    private FragmentActivity mContext;
    private Callback mCallback;
    private BaseSharePlatformSelector mPlatformSelector;

    public static ShareHelper instance(FragmentActivity context, Callback callback) {
        return new ShareHelper(context, callback);
    }

    private ShareHelper(FragmentActivity context, Callback callback) {
        mContext = context;
        mCallback = callback;
        if (context == null) {
            throw new NullPointerException();
        }
        BiliShareConfiguration configuration = new BiliShareConfiguration.Builder(context)
                .imageDownloader(new ShareFrescoImageDownloader())
                .qq(QQ_APPID)
                .weixin(WECHAT_APPID)
                .sina(SINA_APPKEY, null, null)
                .build();
        shareClient().config(configuration);
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public void showShareDialog() {
        mPlatformSelector = new DialogSharePlatformSelector(mContext, new BaseSharePlatformSelector.OnShareSelectorDismissListener() {
            @Override
            public void onDismiss() {
                onShareSelectorDismiss();
            }
        }, mShareItemClick);
        mPlatformSelector.show();
    }

    public void showShareWarpWindow(View anchor) {
        mPlatformSelector = new PopWrapSharePlatformSelector(mContext, anchor, new BaseSharePlatformSelector.OnShareSelectorDismissListener() {
            @Override
            public void onDismiss() {
                onShareSelectorDismiss();
            }
        }, mShareItemClick);
        mPlatformSelector.show();
    }

    public void showShareFullScreenWindow(View anchor) {
        mPlatformSelector = new PopFullScreenSharePlatformSelector(mContext, anchor, new BaseSharePlatformSelector.OnShareSelectorDismissListener() {
            @Override
            public void onDismiss() {
                onShareSelectorDismiss();
            }
        }, mShareItemClick);
        mPlatformSelector.show();
    }

    void onShareSelectorDismiss() {
        mCallback.onDismiss(this);
    }

    public void hideShareWindow() {
        if (mPlatformSelector != null)
            mPlatformSelector.dismiss();
    }

    private AdapterView.OnItemClickListener mShareItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ShareTarget item = (ShareTarget) parent.getItemAtPosition(position);
            shareTo(item);
            hideShareWindow();
        }
    };

    public void shareTo(ShareTarget item) {
        BaseShareParam content = mCallback.getShareContent(ShareHelper.this, item.media);
        if (content == null) {
            return;
        }
        shareClient().share(mContext, item.media, content, shareListener);
    }

    protected SocializeListeners.ShareListener shareListener = new SocializeListeners.ShareListenerAdapter() {

        @Override
        public void onStart(SocializeMedia type) {
            if (mCallback != null)
                mCallback.onShareStart(ShareHelper.this);
        }

        @Override
        protected void onComplete(SocializeMedia type, int code, Throwable error) {
            if (mCallback != null)
                mCallback.onShareComplete(ShareHelper.this, code);
        }
    };

    public Context getContext() {
        return mContext;
    }

    public void reset() {
        if (mPlatformSelector != null) {
            mPlatformSelector.release();
            mPlatformSelector = null;
        }
        mShareItemClick = null;
    }

    public static BiliShare shareClient()  {
        return BiliShare.global();
    }

    public interface Callback {
        BaseShareParam getShareContent(ShareHelper helper, SocializeMedia target);

        void onShareStart(ShareHelper helper);

        void onShareComplete(ShareHelper helper, int code);

        void onDismiss(ShareHelper helper);
    }

}
