package com.sloth.film;

import android.content.Context;
import android.os.Environment;

import com.sloth.ifilm.DaoMaster;
import com.sloth.ifilm.DaoSession;
import com.sloth.tools.util.FileUtils;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.query.QueryBuilder;
import java.io.File;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      2017/8/16 下午5:53
 * Description: 数据库工具类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/8/16      ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 */
public class FilmDataBaseConnection {

    private DaoSession mDaoSession;
    private FilmOrmUpgradeHelper filmOrmUpgradeHelper;

    public FilmDataBaseConnection(Context context) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        FileUtils.createOrExistsDir(dir);
        String filmDbPath = dir.getAbsolutePath() + "film-db";
        filmOrmUpgradeHelper = new FilmOrmUpgradeHelper(context, filmDbPath);
        Database db = filmOrmUpgradeHelper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession(IdentityScopeType.None);
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void debug(boolean open) {
        QueryBuilder.LOG_SQL = open;
        QueryBuilder.LOG_VALUES = open;
    }

    public void closeConnection() {
        if(filmOrmUpgradeHelper != null){
            filmOrmUpgradeHelper.close();
            filmOrmUpgradeHelper = null;
        }
        if (mDaoSession != null) {
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

}
