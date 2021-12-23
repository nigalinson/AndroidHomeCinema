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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
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
        Observable.just(filmId)
                .subscribeOn(Schedulers.io())
                .map(new QueryFilmFunc())
                .map(new FilterLinkFunc(null))
                .observeOn(Schedulers.io())
                .subscribe(new DownloadObserver());
    }

    public void download(Long filmId, Long linkId){
        Observable.just(filmId)
                .subscribeOn(Schedulers.io())
                .map(new QueryFilmFunc())
                .map(new FilterLinkFunc(linkId))
                .observeOn(Schedulers.io())
                .subscribe(new DownloadObserver());
    }

    public void stopDownload(Long filmId){
        Observable.just(filmId)
                .subscribeOn(Schedulers.io())
                .map(new QueryFilmFunc())
                .map(Film::getLinks)
                .flatMap(new FlatLinkFunc())
                .observeOn(Schedulers.io())
                .subscribe(new StopDownloadObserver());
    }

    public void stopDownloadAll(){
        getHttpDownloadManager().terminateAll();
        getFTPDownloadManager().terminateAll();
        getTorrentDownloadManager().terminateAll();
    }

    private final class QueryFilmFunc implements Function<Long, Film> {

        @Override
        public Film apply(@NonNull Long filmId) throws Exception {
            List<Film> films = dbConnection.getDaoSession().getFilmDao().queryBuilder()
                    .where(FilmDao.Properties.Id.eq(filmId)).list();
            if(!films.isEmpty()){
                return films.get(0);
            }
            throw new RuntimeException("find no film by id: " + filmId);
        }
    }

    private static final class FilterLinkFunc implements Function<Film, FilmLink> {

        private final Long preLink;

        public FilterLinkFunc(Long preLink) {
            this.preLink = preLink;
        }

        @Override
        public FilmLink apply(@NonNull Film film) throws Exception {
            FilmLink link = null;
            if(preLink != null){
                link = FilmUtils.findLink(film, preLink);
            }else{
                link = FilmUtils.findFirstUsableLink(film);
            }

            if(link != null){
                return link;
            }
            throw new RuntimeException("found none link !");
        }
    }

    private static final class FlatLinkFunc implements Function<List<FilmLink>, Observable<FilmLink>> {

        @Override
        public Observable<FilmLink> apply(@NonNull List<FilmLink> filmLinks) throws Exception {
            return Observable.fromIterable(filmLinks);
        }
    }

    private final class DownloadObserver implements Observer<FilmLink> {
        @Override
        public void onSubscribe(@NonNull Disposable d) { }

        @Override
        public void onNext(@NonNull FilmLink link) {
            getDownloadManager(link.getUrl()).download(
                    link.getUrl(),
                    DownloadConstants.downloadMovieFilePath(link.getFilmId()),
                    new DownloadCallback(link.getFilmId(), link.getId()));
        }

        @Override
        public void onError(@NonNull Throwable e) {
            LogUtils.e(TAG, e.getMessage() != null ? e.getMessage() : "error" );
        }

        @Override
        public void onComplete() { }
    }

    private final class StopDownloadObserver implements Observer<FilmLink> {
        @Override
        public void onSubscribe(@NonNull Disposable d) { }

        @Override
        public void onNext(@NonNull FilmLink link) {
            getDownloadManager(link.getUrl()).terminate(link.getUrl());
        }

        @Override
        public void onError(@NonNull Throwable e) {
            LogUtils.e(TAG, e.getMessage() != null ? e.getMessage() : "error" );
        }

        @Override
        public void onComplete() { }
    }

    private class DownloadCallback implements DownloadListener{

        private final long filmId;
        private final long linkId;

        public DownloadCallback(long filmId, long linkId) {
            this.filmId = filmId;
            this.linkId = linkId;
        }

        @Override
        public void onDownloadStart() { }

        @Override
        public void onDownloadProgress(long current, long total) { }

        @Override
        public void onDownloadComplete(String filePath) {
            onDownloadFinish(true);
        }


        @Override
        public void onDownloadFailed(String errCode) {
            onDownloadFinish(false);
        }

        protected void onDownloadFinish(boolean res){
            Observable.just(res)
                    .map(new UpdateCompleteFunc(filmId, linkId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe();
        }
    }

    private final class UpdateCompleteFunc implements Function<Boolean, Boolean>{

        private final long filmId;
        private final long linkId;

        public UpdateCompleteFunc(long filmId, long linkId) {
            this.filmId = filmId;
            this.linkId = linkId;
        }

        @Override
        public Boolean apply(@NonNull Boolean sus) throws Exception {
            editLinkState(sus);
            editFilmState(sus);
            return true;
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
                dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
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
                dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
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
