package com.sloth.film;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.icrawler.InfoFinder;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmLinkDao;
import com.sloth.ifilm.FilmManager;
import com.sloth.ifilm.FilmQueryParam;
import com.sloth.ifilm.FilmState;
import com.sloth.ifilm.LinkState;
import com.sloth.ifilm.Strategy;
import com.sloth.tools.util.EncodeUtils;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.SPUtils;
import com.sloth.tools.util.StringUtils;
import com.sloth.tools.util.Utils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 19:07
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = FilmManager.class, key = Strategy._DEFAULT, singleton = true, defaultImpl = true)
public class FilmManagerImpl implements FilmManager {

    private final FilmDataBaseConnection filmDataBaseConnection;
    private final DownloadCenter downloadCenter;
    private final CrawlerBridge crawlerBridge;

    public FilmManagerImpl() {
        filmDataBaseConnection = new FilmDataBaseConnection(Utils.getApp());
        int downloadConcurrency = SPUtils.getInstance().getInt(FilmConstants.SP.KEY_FILM_DOWNLOAD_CONCURRENCY, FilmConstants.DEF_FILM_DOWNLOAD_CONCURRENCY);
        downloadCenter = new DownloadCenter(filmDataBaseConnection).setPolicy(downloadConcurrency);
        crawlerBridge = new CrawlerBridge(filmDataBaseConnection);
    }

    @Override
    public void openEngine(boolean open) {
        if(open){
            crawlerBridge.start();
        }else{
            downloadCenter.stopDownloadAll();
            crawlerBridge.stop();
        }
    }

    @Override
    public List<Film> getFilms(FilmQueryParam param) {
        QueryBuilder<Film> queryBuilder = filmDataBaseConnection.getDaoSession().getFilmDao().queryBuilder();
        if(StringUtils.notEmpty(param.getName())){
            queryBuilder.where(FilmDao.Properties.Name.like("%"+param.getName()+"%"));
        }
        int start = param.getPageIndex() * param.getPageSize();
        int size = param.getPageSize();
        queryBuilder.offset(start).limit(size);
        return queryBuilder.list();
    }

    @Override
    public void addFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setState(FilmState.WAIT);
        film.setLinks(new ArrayList<>());
        film.setCreateTime(System.currentTimeMillis());
        long id = filmDataBaseConnection.getDaoSession().getFilmDao().insertOrReplace(film);

        //crawler info
        Router.getService(InfoFinder.class, com.sloth.icrawler.Strategy._DEFAULT_INFO_FINDER).find(id, name, (filmId, infoMap) -> {
            String ori = "update %s set %s = '%s', %s = '%s', %s = '%s' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.Name.columnName,
                    infoMap.get("name"),
                    FilmDao.Properties.Img.columnName,
                    infoMap.get("img"),
                    FilmDao.Properties.Description.columnName,
                    infoMap.get("intro"),
                    FilmDao.Properties.Id.columnName,
                    id);
            filmDataBaseConnection.getDaoSession().getDatabase().execSQL(sql);
        });

        //crawler link
        crawlerBridge.crawler(id);
    }

    private String encodeIfNull(String origin) {
        if(StringUtils.isEmpty(origin)){
            return "";
        }
        return new String(EncodeUtils.base64Encode(origin));
    }

    @Override
    public void searchFilmResources(long filmId) {
        crawlerBridge.crawler(filmId);
    }

    @Override
    public void removeFilm(long id) {
        String ori = "delete from %s where %s == %d;";
        String sql = String.format(Locale.CHINA, ori,
                FilmLinkDao.TABLENAME,
                FilmLinkDao.Properties.FilmId.columnName,
                id
        );
        filmDataBaseConnection.getDaoSession().getDatabase().execSQL(sql);

        filmDataBaseConnection.getDaoSession().getFilmDao().deleteByKey(id);
        FileUtils.delete(DownloadConstants.downloadMovieFilePath(id));
    }

    @Override
    public void downloadFilm(long id) {
        downloadCenter.download(id);
    }

    @Override
    public void downloadFilmByLink(long filmId, long linkId) {
        downloadCenter.download(filmId, linkId);
    }

    @Override
    public void removeFilmCache(long id) {

        FileUtils.delete(DownloadConstants.downloadMovieFilePath(id));
        FileUtils.delete(DownloadConstants.getDownloadMovieFolder() + id);

        String ori = "update %s set %s = '%d' where %s = '%d';";
        String sql = String.format(Locale.CHINA, ori,
                FilmDao.TABLENAME,
                FilmDao.Properties.State.columnName,
                FilmState.WAIT,
                FilmDao.Properties.Id.columnName,
                id );
        filmDataBaseConnection.getDaoSession().getDatabase().execSQL(sql);

        downloadCenter.stopDownload(id);

    }

    @Override
    public void disableLink(long linkId) {
        String ori = "update %s set %s = '%d' where %s = '%d';";
        String sql = String.format(Locale.CHINA, ori,
                FilmLinkDao.TABLENAME,
                FilmLinkDao.Properties.State.columnName,
                LinkState.USELESS,
                FilmLinkDao.Properties.Id.columnName,
                linkId);
        filmDataBaseConnection.getDaoSession().getDatabase().execSQL(sql);
    }

}
