package com.wc.qiniu;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

import java.util.ArrayList;
import java.util.List;

public class FileListUtil {
    public static void getFileList(OnFileFindCallback callback) {
        String accessKey = QiniuConfig.ACCESSKEY;
        String secretKey = QiniuConfig.SECRETKEY;
        String bucket = QiniuConfig.BUCKET;
        Auth auth = Auth.create(accessKey, secretKey);
        Configuration configuration = new Configuration();
        BucketManager bucketManager = new BucketManager(auth, configuration);
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(bucket, prefix, limit, delimiter);
        List<FileBean> files = new ArrayList<>();
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                FileBean file = new FileBean();
                file.key = item.key;
                file.fsize = item.fsize;
                file.mimeType = item.mimeType;
                file.putTime = item.putTime;
                files.add(file);
            }
        }
        if (callback != null) {
            callback.onFinish(files);
        }
    }

    public interface OnFileFindCallback {
        void onFinish(List<FileBean> files);
    }

    public static class FileBean {
        public String key;
        public long fsize;
        public long putTime;
        public String mimeType;
    }
}
