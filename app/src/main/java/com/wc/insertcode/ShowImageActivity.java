package com.wc.insertcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.wc.dragphoto.widget.DragPhotoView;
import com.wc.insertcode.base.BaseActivity;
import com.wc.insertcode.utils.GifImageLoader;
import com.wc.utils.DisplayUtils;

import java.util.ArrayList;


public class ShowImageActivity extends BaseActivity {
    private FixMultiViewPager mViewPager;
    private String[] imageUrls;
    private ArrayList<ImageBean> imageBeans;
    private DragPhotoView[] mPhotoViews;
    private int currentPosition;
    private boolean isShowStatusBar;
    private ImageView imageDelete;
    private static Drawable sDrawable;

    public static void startImageActivity(Activity activity, View imageView, String imageUrl) {
        if (imageView != null) {
            startImageActivity(activity, new View[]{imageView}, new String[]{imageUrl}, 0, false);
        } else {
            startImageActivity(activity, null, new String[]{imageUrl}, 0, false);
        }
    }

    public static void startImageActivity(Activity activity, View[] imageViews, String[] imageUrls, int currentPosition, boolean showDelete) {
        startImageActivity(activity, imageViews, imageUrls, currentPosition, showDelete, null);
    }

    public static void startImageActivity(Activity activity, View[] imageViews, String[] imageUrls, int currentPosition, boolean showDelete, Drawable drawable) {
        Intent intent = new Intent(activity, ShowImageActivity.class);
//        ImageBean[] imageBeans = new ImageBean[imageViews.length];
        ArrayList<ImageBean> imageBeans = new ArrayList<>();
        if (imageViews != null) {
            for (View imageView : imageViews) {
                ImageBean imageBean = new ImageBean();
                int location[] = new int[2];
                if (imageView != null) {
                    imageView.getLocationOnScreen(location);
                    imageBean.left = location[0];
                    imageBean.top = location[1];
                    imageBean.width = imageView.getWidth();
                    imageBean.height = imageView.getHeight();
                    if (Math.abs(imageBean.width - imageBean.height) > 100) {
                        imageBean.width = 0;
                        imageBean.height = 0;
                    }
                }
//            imageBeans[i] = imageBean;
                imageBeans.add(imageBean);
            }
        } else {
            ImageBean imageBean = new ImageBean();
            imageBean.left = DisplayUtils.getScreenWidth() / 2;
            imageBean.top = DisplayUtils.getScreenHeight() / 2;
            imageBean.width = 0;
            imageBean.height = 0;
            imageBeans.add(imageBean);
        }
        //intent.putParcelableArrayListExtra("imageBeans", imageBeans);
        intent.putExtra("currentPosition", currentPosition);
        intent.putExtra("imageUrls", imageUrls);
        intent.putExtra("showDelete", showDelete);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("imageBeans", imageBeans);
        intent.putExtra("bundle", bundle);
        intent.setExtrasClassLoader(ImageBean.class.getClassLoader());
        sDrawable = drawable;
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("currentPosition", 0);
        imageUrls = intent.getStringArrayExtra("imageUrls");
        //imageBeans = intent.getParcelableArrayListExtra("imageBeans");
        Bundle bundle = intent.getBundleExtra("bundle");
        imageBeans = bundle.getParcelableArrayList("imageBeans");
        if (imageUrls == null || imageUrls.length == 0) {
            return;
        }
        mPhotoViews = new DragPhotoView[imageUrls.length];
        setPhotoViewAndViewPager();
        //设置入场动画参数
        mViewPager.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mViewPager.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                        int left = imageBeans.get(currentPosition).left;
                        int top = imageBeans.get(currentPosition).top;
                        int height = imageBeans.get(currentPosition).height;
                        int width = imageBeans.get(currentPosition).width;

                        final DragPhotoView photoView = mPhotoViews[currentPosition];
                        int[] locationPhoto = new int[2];
                        photoView.getLocationOnScreen(locationPhoto);
                        float targetHeight = (float) photoView.getHeight();
                        float targetWidth = (float) photoView.getWidth();
                        float scaleX = (float) width / targetWidth;
                        float scaleY = (float) height / targetHeight;

                        float targetCenterX = locationPhoto[0] + targetWidth / 2;
                        float targetCenterY = locationPhoto[1] + targetHeight / 2;

                        float translationX = left + width / 2 - targetCenterX;
                        float translationY = top + height / 2 - targetCenterY;
                        photoView.setTranslationX(translationX);
                        photoView.setTranslationY(translationY);

                        photoView.setScaleX(scaleX);
                        photoView.setScaleY(scaleY);
                        photoView.performEnterAnimation(scaleX, scaleY);
                        for (DragPhotoView mPhotoView : mPhotoViews) {
                            mPhotoView.setMinScale(scaleX);
                        }
                    }
                });
    }

    @Override
    protected View getContentView() {
        mViewPager = new FixMultiViewPager(this);
        if (getIntent().getBooleanExtra("showDelete", false)) {
            FrameLayout contentLayout = new FrameLayout(this);
            contentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            contentLayout.addView(mViewPager);
            imageDelete = new ImageView(this);
            //imageDelete.setImageResource();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            params.topMargin = DisplayUtils.dip2px(24);
            int padding = DisplayUtils.dip2px(12);
            imageDelete.setPadding(padding, padding, padding, padding);
            imageDelete.setLayoutParams(params);
            imageDelete.setImageResource(R.drawable.icon_delete_image);
//            imageDelete.setOnClickListener(v ->
//                    new DeleteDialog().setOnClickListener((dialog, pos) ->
//                            new IosDialog.Builder(ShowImageActivity.this).setTitle("提示").setMessage("是否确定删除照片？")
//                                    .setNegativeButton("取消", (dialog1, v1) -> dialog1.dismiss())
//                                    .setPositiveButton("删除", (dialog2, v2) -> {
//                                        dialog2.dismiss();
//                                        RxBus.getDefault().send(AllContants.RxBusCode.RXBUS_CODE_FRIEND_DETELE_IMAGE, mViewPager.getCurrentItem());
//                                        finish();
//                                    }).build().show()
//                    ).show(getSupportFragmentManager(), "DeleteDialog"));
            contentLayout.addView(imageDelete);
            return contentLayout;
        }
        return mViewPager;
    }

    private void setPhotoViewAndViewPager() {
        for (int i = 0; i < mPhotoViews.length; i++) {
            mPhotoViews[i] = new DragPhotoView(this);
            mPhotoViews[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            showImage(imageUrls[i], mPhotoViews[i], i);
            mPhotoViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishWithAnimation();
                }
            });
            final int index = i;
            mPhotoViews[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    Dialog dialog = new AlertDialog.Builder(ImageShowActivity.this)
//                            .setTitle("长按Dialog").setMessage("这是第" + index + "个位置的图片")
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).create();
//                    dialog.show();
                    return true;
                }
            });
            mPhotoViews[i].setOnDragListener(new DragPhotoView.OnDragListener() {
                @Override
                public void onDrag(DragPhotoView view, float moveX, float moveY) {
                    if (!isShowStatusBar) {
                        setIsShowStatusBar(true);
                    }
                }
            });
            mPhotoViews[i].setOnTapListener(new DragPhotoView.OnTapListener() {
                @Override
                public void onTap(DragPhotoView view) {
                    if (isShowStatusBar) {
                        setIsShowStatusBar(false);
                    }
                }
            });

            mPhotoViews[i].setOnExitListener(new DragPhotoView.OnExitListener() {
                @Override
                public void onExit(DragPhotoView view, float x, float y, float w, float h, int maxTranslateY) {
                    performExitAnimation(view, x, y, w, h);
//                    finish();
//                    overridePendingTransition(0, 0);
                }
            });
        }
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return imageUrls.length;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mPhotoViews[position]);
                return mPhotoViews[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mPhotoViews[position]);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mViewPager.setCurrentItem(currentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                ImageView imageView = currentPosition < mPhotoViews.length ? mPhotoViews[currentPosition] : null;
                if (imageView != null && TextUtils.equals(String.valueOf(imageView.getTag(R.id.photo_view_load_image)), "no loaded")) {
                    String url = currentPosition < imageUrls.length ? imageUrls[currentPosition] : "";
                    if (!TextUtils.isEmpty(url)) {
                        String path = url;
                        if (url.endsWith(".gif")) {
                            GifImageLoader.with(ShowImageActivity.this).load(url).into(imageView);
                        } else {
                            Glide.with(ShowImageActivity.this).load(url).thumbnail(Glide.with(ShowImageActivity.this).load(path)).into(imageView);
                        }
                        imageView.setTag(R.id.photo_view_load_image, "loaded");
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void performExitAnimation(final DragPhotoView view, float x, float y, float w, float h) {
        if (mPhotoViews[currentPosition].getDrawable() instanceof GifDrawable) {
            String url = currentPosition < imageUrls.length ? imageUrls[currentPosition] : "";
            if (!TextUtils.isEmpty(url)) {
                String path = url;
                Glide.with(ShowImageActivity.this).load(url).thumbnail(Glide.with(ShowImageActivity.this).load(path)).into(mPhotoViews[currentPosition]);
            }
        }
        mPhotoViews[currentPosition].performExitAnimation(this, imageBeans.get(currentPosition).left, imageBeans.get(currentPosition).top,
                imageBeans.get(currentPosition).width, imageBeans.get(currentPosition).height);
    }

    private void finishWithAnimation() {
        if (!isShowStatusBar) {
            setIsShowStatusBar(true);
        }
        if (mPhotoViews[currentPosition].getDrawable() instanceof GifDrawable) {
            String url = currentPosition < imageUrls.length ? imageUrls[currentPosition] : "";
            if (!TextUtils.isEmpty(url)) {
                String path = url;
                Glide.with(ShowImageActivity.this).load(url).thumbnail(Glide.with(ShowImageActivity.this).load(path)).into(mPhotoViews[currentPosition]);
            }
        }
        mPhotoViews[currentPosition].finishWithAnimation(this, imageBeans.get(currentPosition).left, imageBeans.get(currentPosition).top,
                imageBeans.get(currentPosition).width, imageBeans.get(currentPosition).height);
    }

    @Override
    public void onBackPressed() {
        finishWithAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sDrawable = null;
    }

    /**
     * 显示图片
     */
    public void showImage(String url, ImageView imageView, int i) {
        if (!TextUtils.isEmpty(url) && url.length() > 0) {
            url = url.replaceAll(" ", "");
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        String path = url;
        if (currentPosition == i) {
            if (sDrawable != null) {
                if (url.endsWith(".gif")) {
                    GifImageLoader.with(this).load(url).into(imageView);
                } else {
                    Glide.with(this).load(url).apply(new RequestOptions().placeholder(sDrawable)).thumbnail(Glide.with(this).load(path)).into(imageView);
                    imageView.setTag(R.id.photo_view_load_image, "loaded");
                }
            } else {
                if (url.endsWith(".gif")) {
                    GifImageLoader.with(this).load(url).into(imageView);
                } else {
                    Glide.with(this).load(url).thumbnail(Glide.with(this).load(path)).into(imageView);
                }
                imageView.setTag(R.id.photo_view_load_image, "loaded");
            }
        } else {
            if (url.endsWith(".gif")) {
                GifImageLoader.with(this).load(url).into(imageView);
            } else {
                Glide.with(this).load(path).into(imageView);
            }
            imageView.setTag(R.id.photo_view_load_image, "no loaded");
        }
    }

    /**
     * 不会导致Activity重新排版的全屏
     */
    /*
    关键是，在做了该Activity的全屏设置的前提下，还要在onCreate()方法中加入如下语句：
    //将window扩展至全屏，并且不会覆盖状态栏
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    //避免在状态栏的显示状态发生变化时重新布局
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
     */
    private void setIsShowStatusBar(boolean show) {
        isShowStatusBar = show;
        if (show) {
            if (imageDelete != null) {
                imageDelete.setVisibility(View.INVISIBLE);
            }
//            //显示
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            if (imageDelete != null) {
                imageDelete.setVisibility(View.VISIBLE);
            }
//            //隐藏
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }
    }

    private static class ImageBean implements Parcelable {
        int top;
        int left;
        int width;
        int height;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.top);
            dest.writeInt(this.left);
            dest.writeInt(this.width);
            dest.writeInt(this.height);
        }

        private ImageBean() {
        }

        private ImageBean(Parcel in) {
            this.top = in.readInt();
            this.left = in.readInt();
            this.width = in.readInt();
            this.height = in.readInt();
        }

        public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
            @Override
            public ImageBean createFromParcel(Parcel source) {
                return new ImageBean(source);
            }

            @Override
            public ImageBean[] newArray(int size) {
                return new ImageBean[size];
            }
        };
    }

    /**
     * Author :  suzeyu
     * Time   :  2016-12-26  上午1:41
     * ClassDescription : 对多点触控场景时, {@link ViewPager#onInterceptTouchEvent(MotionEvent)}中
     * pointerIndex = -1. 发生IllegalArgumentException: pointerIndex out of range 处理
     */
    private class FixMultiViewPager extends ViewPager {
        private final String TAG = FixMultiViewPager.class.getSimpleName();

        public FixMultiViewPager(Context context) {
            super(context);
        }

        public FixMultiViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            try {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "ACTION_UP");
                        break;
                }
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException ex) {
                Log.w(TAG, "onInterceptTouchEvent() ", ex);
                ex.printStackTrace();
            }
            return false;
        }

    }

    //设置不能在3秒内连续点击两次返回按钮
    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 3000) {
                mExitTime = System.currentTimeMillis();
            } else {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
