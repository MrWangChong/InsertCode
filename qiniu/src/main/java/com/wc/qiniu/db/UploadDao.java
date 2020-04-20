package com.wc.qiniu.db;

import com.j256.ormlite.dao.Dao;

import java.util.List;

public class UploadDao {
    private Dao<UploadBean, Integer> mDao;

    public UploadDao() {
        try {
            mDao = OrmliteDbHelper.getInstance().getDao(UploadBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addFile(UploadBean file) {
        if (file == null) {
            return;
        }
        try {
            mDao.create(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized UploadBean getFile(String path) {
        try {
            List<UploadBean> files = mDao.queryBuilder()
                    .orderBy("time", false)
                    .where()
                    .eq("path", path)
                    .query();
            if (files != null && files.size() > 0) {
                return files.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void update(UploadBean searchBean) {
        if (searchBean == null) {
            return;
        }
        try {
            mDao.update(searchBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(UploadBean searchBean) {
        if (searchBean == null) {
            return;
        }
        try {
            mDao.delete(searchBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void clear() {
        OrmliteDbHelper.getInstance().clearAllTable();
    }
}
