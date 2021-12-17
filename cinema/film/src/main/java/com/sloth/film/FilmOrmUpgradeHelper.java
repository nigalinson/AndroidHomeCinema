package com.sloth.film;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sloth.ifilm.DaoMaster;
import com.sloth.tools.util.LogUtils;

import org.greenrobot.greendao.database.Database;

/**
 * Author:    CaoKang
 * Version    V1.0
 * Date:      2017/12/19
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/12/19      CaoKang            1.0                    1.0
 * Why & What is modified:
 * 基础包数据库 rongyi-db
 */
public class FilmOrmUpgradeHelper extends DaoMaster.DevOpenHelper {

    private static final String TAG = FilmOrmUpgradeHelper.class.getSimpleName();

    public FilmOrmUpgradeHelper(Context context, String name) {
        super(context, name);
    }

    public FilmOrmUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) { }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    private void insertColumn(Database db, String table, String columnName) {
        try {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + columnName + " TEXT;");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "已经存在列:" + columnName);
        }
    }

}
