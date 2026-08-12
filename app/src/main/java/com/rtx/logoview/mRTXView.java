package com.rtx.logoview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ouropro.player.R;
import com.rtx.DNS.InternetConnectivityChecker;
import com.rtx.DNS.mConfig;
import java.io.File;

/* JADX INFO: loaded from: classes2.dex */
public class mRTXView extends ImageView {
    private static final String DEFAULT_IMAGE_URL = mConfig.mAPI + "bg.php";
    private static final String ERROR_IMAGE_URL = "file:///android_asset/offline/logo.png";
    private static final long REFRESH_INTERVAL = 300000;
    public static Context mContext;
    private static ImageView mImageView;
    private Handler handler;
    private Runnable refreshRunnable;

    public mRTXView(Context context) {
        super(context);
        mContext = context;
        mImageView = this;
        init(context);
    }

    public mRTXView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mImageView = this;
        init(context);
    }

    public mRTXView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mImageView = this;
        init(context);
    }

    private void init(Context context) {
        loadImage(DEFAULT_IMAGE_URL);
    }

    public void loadImage(String imageUrl) {
        try {
            File cacheDir = mContext.getCacheDir();
            File oldFile = new File(cacheDir, "old_image1.jpg");
            Glide.with(mContext).load(oldFile).fitCenter().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).apply((BaseRequestOptions<?>) new RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Integer.MIN_VALUE)).error(R.drawable.background1).transition(DrawableTransitionOptions.withCrossFade()).into(mImageView);
        } catch (Exception e) {
            if (InternetConnectivityChecker.isConnectedToInternet(mContext)) {
                Glide.with(mContext).load(imageUrl).fitCenter().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).apply((BaseRequestOptions<?>) new RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Integer.MIN_VALUE)).error(R.drawable.background1).transition(DrawableTransitionOptions.withCrossFade()).into(mImageView);
            } else {
                Glide.with(mContext).load(ERROR_IMAGE_URL).fitCenter().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).apply((BaseRequestOptions<?>) new RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Integer.MIN_VALUE)).error(R.drawable.background1).transition(DrawableTransitionOptions.withCrossFade()).into(mImageView);
            }
        }
    }
}
