package com.wc.qiniu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


/**
 * ContactDbHelper
 * Created by RushKing on 2017/11/6.
 */

public class OrmliteDbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "com.wc.qiniu.db";
    private static final int DB_VERSION = 1; // 更新时候只需要修改这里就可以了

    private static volatile OrmliteDbHelper mInstance;

    /**
     * 初始化，放在Application中
     */
    public static void init(Context context) {
        mInstance = new OrmliteDbHelper(context);
    }

    static OrmliteDbHelper getInstance() {
        return mInstance;
    }

    private OrmliteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTableIfNotExists(connectionSource, UploadBean.class);//记录表
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这个方法在你的应用升级以及它有一个更高的版本号时调用。所以需要你调整各种数据来适应新的版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVersion,
                          int newVersion) {
        try {
            //删掉旧版本的数据
            TableUtils.dropTable(connectionSource, UploadBean.class, true);
            //创建一个新的版本
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 清空所有表
     */
    public void clearAllTable() {
        try {
            TableUtils.clearTable(connectionSource, UploadBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //释放资源
    @Override
    public void close() {
        super.close();
    }
}
