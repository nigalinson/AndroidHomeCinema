package com.sloth.film;

import androidx.annotation.NonNull;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private static final int RETRY_TIMES = 3;

    private final AtomicInteger concurrency = new AtomicInteger(-1);
    private final FilmDataBaseConnection dbConnection;

    private Disposable autoDownloadLoop;
    private final Set<Long> autoDownloadQueue = new HashSet<>();

    public DownloadCenter(FilmDataBaseConnection conn) {
        dbConnection = conn;
    }

    public DownloadCenter setPolicy(int newConcurrency){
        concurrency.set(newConcurrency);
        return this;
    }

    public boolean isDownloading(String url){
        return getDownloadManager(url).isDownloading(url);
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

        downloadLink(filmLink, RETRY_TIMES);
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

        downloadLink(filmLink, RETRY_TIMES);
    }

    public void download(Long filmId, Long linkId, int retryTime){
        Film film = queryFilm(filmId);
        if(film == null){
            return;
        }
        FilmLink filmLink = filterLink(film, linkId);

        if(filmLink == null){
            return;
        }

        downloadLink(filmLink, retryTime);
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

    public void addAutoDownload(Long filmId){
        autoDownloadQueue.add(filmId);
        stopAutoDownload();
        Observable.interval(60 * 1000, 30 * 60 * 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        autoDownloadLoop = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Iterator<Long> iterator = autoDownloadQueue.iterator();
                        while(iterator.hasNext()){
                            Film film = queryFilm(iterator.next());
                            if(film == null){
                                continue;
                            }
                            FilmLink filmLink = filterLink(film, null);

                            if(filmLink == null){
                                continue;
                            }

                            downloadLink(filmLink, RETRY_TIMES);
                            iterator.remove();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LogUtils.e(TAG, e.getMessage() != null ? e.getMessage() : "auto download failed !");
                    }

                    @Override
                    public void onComplete() { }
                });

    }

    public void stopAutoDownload(){
        if(autoDownloadLoop != null && !autoDownloadLoop.isDisposed()){
            autoDownloadLoop.dispose();
            autoDownloadLoop = null;
        }
    }

    public void stopDownloadAll(){
        stopAutoDownload();
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

    private void downloadLink(FilmLink link, int retryTimes){
        getDownloadManager(link.getUrl()).download(
                link.getUrl(),
                DownloadConstants.downloadMovieFilePath(link.getFilmId()),
                new DownloadCallback(link.getFilmId(), link.getId(), retryTimes));
    }

    private void stopDownloadLink(FilmLink link){
        getDownloadManager(link.getUrl()).terminate(link.getUrl());
    }

    private class DownloadCallback implements DownloadListener{

        private final long filmId;
        private final long linkId;
        private final int retryTimes;

        private long lastProgressStamp = 0;

        public DownloadCallback(long filmId, long linkId, int retryTimes) {
            this.filmId = filmId;
            this.linkId = linkId;
            this.retryTimes = retryTimes;
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
            if(retryTimes > 0){
                LogUtils.d(TAG, "retry downloading !");
                download(filmId, linkId, retryTimes - 1);
            }else{
                onDownloadFinish(false);
            }
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
