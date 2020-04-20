package com.wc.qiniu.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 上传记录
 * Created by RushKing on 2017/11/2.
 */
@DatabaseTable(tableName = "com_wc_qiniu_upload")
public class UploadBean {
    @DatabaseField(unique = true, generatedId = true)
    private int id;
    @DatabaseField
    private String fileid;//文件ID
    @DatabaseField
    private long time;//时间
    @DatabaseField
    private long size;//文件大小
    @DatabaseField
    private String path;//本地地址

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
