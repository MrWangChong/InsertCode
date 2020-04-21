package com.wc.insertcode.utils;

import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.wc.insertcode.R;

import java.io.File;
import java.net.URLDecoder;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Gif加载器
 * Created by RushKing on 2019/5/7.
 */
public class GifImageLoader {
    private static final String DATA_DIRECTORY = Environment.getExternalStorageDirectory()
            + "/iicorp";//
    private static final String GIF_DIRECTORY = DATA_DIRECTORY + "/gif/";//
    private String mUrl;
    private String mThumb;
    private File mFile;
    private ImageView mImageView;
    private static int mTag = 0x1;
    private int lastTag = 0x1;

    public static GifImageLoader with(Context context) {
        return new GifImageLoader();
    }

    public static GifImageLoader with(View view) {
        return new GifImageLoader();
    }

    public static GifImageLoader with(Fragment fragment) {
        return new GifImageLoader();
    }

    public static GifImageLoader with() {
        return new GifImageLoader();
    }

    public GifImageLoader load(String url) {
        return this.load(url, null);
    }

    public GifImageLoader load(String url, String thumb) {
        mUrl = url;
        mThumb = thumb;
        if (!mUrl.startsWith("http")) {
            mFile = new File(url);
        }
        return this;
    }

    public GifImageLoader load(File file) {
        mFile = file;
        return this;
    }

    public void into(ImageView imageView) {
        mImageView = imageView;
        //防止错位加载
        lastTag = ++mTag;
        mImageView.setTag(R.id.gif_image_loader, lastTag);
        download();
    }

    private void download() {
        if (mFile != null && mFile.exists()) {
            try {
                if (Integer.parseInt(mImageView.getTag(R.id.gif_image_loader).toString()) == lastTag) {
                    GifDrawable gifDrawable = new GifDrawable(mFile);
                    mImageView.setImageDrawable(gifDrawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        File dir = new File(GIF_DIRECTORY);
        if (!dir.exists()) {
            boolean b = dir.mkdirs();
        }
        // 从路径中获取
        String name = null;
        try {
            String urlfilename = mUrl.substring(mUrl.lastIndexOf("/") + 1);
            name = URLDecoder.decode(urlfilename, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String fileName = GIF_DIRECTORY + name;
        final File file = new File(fileName);
        if (file.exists()) {
            try {
                if (Integer.parseInt(mImageView.getTag(R.id.gif_image_loader).toString()) == lastTag) {
                    GifDrawable gifDrawable = new GifDrawable(file);
                    mImageView.setImageDrawable(gifDrawable);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (!TextUtils.isEmpty(mThumb)) {
            GlideApp.with(mImageView).load(mThumb).into(mImageView);
        }
        Log.d("GifImageLoader", "url : " + mUrl);
        FileDownloader.getImpl().create(mUrl)
                .setPath(fileName)
//                .setTag(videoBean)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("GifImageLoader", "download pending");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        int percent = (int) ((float) soFarBytes / totalBytes * 100);
                        Log.d("GifImageLoader", "progress " + percent + "% ");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        long fileSize = task.getSmallFileTotalBytes();
                        Log.d("GifImageLoader", "fileSize : " + fileSize);
                        try {
                            if (Integer.parseInt(mImageView.getTag(R.id.gif_image_loader).toString()) == lastTag) {
                                GifDrawable gifDrawable = new GifDrawable(file);
                                mImageView.setImageDrawable(gifDrawable);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d("GifImageLoader", "download paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.e("GifImageLoader", "download error");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.w("GifImageLoader", "download warn");
//                            while (task.getSmallFileSoFarBytes() != task.getSmallFileTotalBytes()) {
//                                int percent = (int) ((float) task.getSmallFileSoFarBytes() / task.getSmallFileTotalBytes() * 100);
//                                Log.d("MainControl", "warn " + percent + "%");
//                            }
                    }
                })
                .start();
    }
}
