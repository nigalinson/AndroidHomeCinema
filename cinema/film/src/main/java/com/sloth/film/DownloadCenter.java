package com.sloth.film;

import com.sankuai.waimai.router.Router;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmLink;
import com.sloth.ifilm.FilmLinkDao;
import com.sloth.ifilm.FilmState;
import com.sloth.ifilm.LinkState;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.LogUtils;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/16 19:41
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/16         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class DownloadCenter {

    private static final String TAG = "DownloadCenter";

    private final AtomicInteger concurrency = new AtomicInteger(-1);
    private final FilmDataBaseConnection dbConnection;

    public DownloadCenter(FilmDataBaseConnection conn) {
        dbConnection = conn;
    }

    public DownloadCenter setPolicy(int newConcurrency){
        concurrency.set(newConcurrency);
        return this;
    }

    public void download(Long filmId){
        Film film = queryFilm(filmId);
        if(film == null){
            return;
        }
        FilmLink filmLink = filterLink(film, null);

        if(filmLink == null){
            return;
        }

        downloadLink(filmLink);
    }

    public void download(Long filmId, Long linkId){
        Film film = queryFilm(filmId);
        if(film == null){
            return;
        }
        FilmLink filmLink = filterLink(film, linkId);

        if(filmLink == null){
            return;
        }

        downloadLink(filmLink);
    }

    public void stopDownload(Long filmId){
        Film film = queryFilm(filmId);
        if(film == null){
            return;
        }

        List<FilmLink> filmLinks = film.getLinks();

        if(filmLinks == null){
            return;
        }

        for(FilmLink link: filmLinks){
            stopDownloadLink(link);
        }
    }

    public void stopDownloadAll(){
        getHttpDownloadManager().terminateAll();
        getFTPDownloadManager().terminateAll();
        getTorrentDownloadManager().terminateAll();
    }

    private Film queryFilm(Long filmId){
        List<Film> films = dbConnection.getDaoSession().getFilmDao().queryBuilder()
                .where(FilmDao.Properties.Id.eq(filmId)).list();
        if(!films.isEmpty()){
            return films.get(0);
        }
        return null;
    }

    private FilmLink filterLink(Film film, Long preLink){
        FilmLink link = null;
        if(preLink != null){
            link = FilmUtils.findLink(film, preLink);
        }else{
            link = FilmUtils.findFirstUsableLink(film);
        }

        if(link != null){
            return link;
        }
        return null;
    }

    private void downloadLink(FilmLink link){
        getDownloadManager(link.getUrl()).download(
                link.getUrl(),
                DownloadConstants.downloadMovieFilePath(link.getFilmId()),
                new DownloadCallback(link.getFilmId(), link.getId()));
    }

    private void stopDownloadLink(FilmLink link){
        getDownloadManager(link.getUrl()).terminate(link.getUrl());
    }

    private class DownloadCallback implements DownloadListener{

        private final long filmId;
        private final long linkId;

        private long lastProgressStamp = 0;

        public DownloadCallback(long filmId, long linkId) {
            this.filmId = filmId;
            this.linkId = linkId;
        }

        @Override
        public void onDownloadStart() {
            LogUtils.d(TAG, "onDownloadStart");
        }

        @Override
        public void onDownloadProgress(float current, float total) {
            //dump too much progress will block logcat
            long now = System.currentTimeMillis();
            if(now - lastProgressStamp > 1000){
                LogUtils.d(TAG, String.format(Locale.CHINA, "onDownloadProgress: %.4f / %.2f", current, total));
                lastProgressStamp = now;
            }
        }

        @Override
        public void onDownloadComplete(String filePath) {
            LogUtils.d(TAG, "onDownloadComplete: " + filePath);
            onDownloadFinish(true);
        }


        @Override
        public void onDownloadFailed(String errCode) {
            LogUtils.d(TAG, "onDownloadFailed: " + errCode);
            onDownloadFinish(false);
        }

        protected void onDownloadFinish(boolean res){
            LogUtils.d(TAG, "onDownloadFinish: " + res);
            editLinkState(res);
            editFilmState(res);
        }

        private void editLinkState(Boolean sus) {
            if(!sus){
                String ori = "update %s set %s = '%d' where %s = '%d';";
                String sql = String.format(Locale.CHINA, ori,
                        FilmLinkDao.TABLENAME,
                        FilmLinkDao.Properties.State.columnName,
                        LinkState.USELESS,
                        FilmLinkDao.Properties.Id.columnName,
                        linkId);
                dbConnection.getDaoSession().getDatabase().execSQL(sql);
            }
        }

        private void editFilmState(Boolean sus) {
            if(sus){
                String ori = "update %s set %s = '%d' where %s = '%d';";
                String sql = String.format(Locale.CHINA, ori,
                        FilmDao.TABLENAME,
                        FilmDao.Properties.State.columnName,
                        FilmState.OK,
                        FilmDao.Properties.Id.columnName,
                        filmId );
                dbConnection.getDaoSession().getDatabase().execSQL(sql);
            }
        }
    }

    private DownloadManager getDownloadManager(String url){
        if(url.startsWith("magnet:") || url.startsWith("MAGNET:") || url.endsWith(".torrent") || url.endsWith(".TORRENT")){
            //磁力链接
            return getTorrentDownloadManager();
        }else if(url.startsWith("ftp") || url.startsWith("FTP")){
            //ftp
            return getFTPDownloadManager();
        }else{
            //http
            return getHttpDownloadManager();
        }
    }

    private DownloadManager getTorrentDownloadManager(){
        return Router.getService(DownloadManager.class, Strategies.DownloadEngine.TORRENT);
    }

    private DownloadManager getFTPDownloadManager(){
        return Router.getService(DownloadManager.class, Strategies.DownloadEngine.FTP);
    }

    private DownloadManager getHttpDownloadManager(){
        return Router.getService(DownloadManager.class, Strategies.DownloadEngine.LIU_LI_SHO);
    }

}
