package com.sloth.film;

import android.database.Cursor;
import androidx.annotation.NonNull;
import com.sankuai.waimai.router.Router;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmCachePolicy;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmLink;
import com.sloth.ifilm.FilmLinkDao;
import com.sloth.ifilm.FilmState;
import com.sloth.ifilm.LinkState;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;

import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    private final AtomicInteger policy = new AtomicInteger(-1);
    private final AtomicInteger concurrency = new AtomicInteger(-1);
    private final FilmDataBaseConnection dbConnection;
    private Disposable loop;

    public DownloadCenter(FilmDataBaseConnection conn) {
        dbConnection = conn;
    }

    public DownloadCenter setPolicy(@FilmCachePolicy int newPolicy, int newConcurrency){
        policy.set(newPolicy);
        concurrency.set(newConcurrency);
        return this;
    }

    public void reStartDownloading(){
        destroy();
        Observable.interval(1000, 30 * 60 * 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(b -> concurrency.get())
                .map(new QueryFilmFunc())
                .filter(films -> !films.isEmpty())
                .flatMap(new FlatFilm())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver());
    }

    public void reDownload(Film film){
        FilmLink link = findFirstUsableLink(film);

        if(link == null || StringUtils.isEmpty(link.getUrl())){
            LogUtils.e(TAG, "find none usable download link !");
            return;
        }

        Observable.just(film)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver());
    }

    public void stopDownload(Film film){
        FilmLink link = findFirstUsableLink(film);
        if(link != null && StringUtils.notEmpty(link.getUrl())
                && getDownloadManager(link.getUrl()).isDownloading(link.getUrl())){
            getDownloadManager(link.getUrl()).terminate(link.getUrl());
        }
    }

    public void stopDownloadAll(){
        getHttpDownloadManager().terminateAll();
        getFTPDownloadManager().terminateAll();
        getTorrentDownloadManager().terminateAll();
    }

    public void destroy(){
        if(loop != null && !loop.isDisposed()){
            loop.dispose();
            loop = null;
        }
    }

    private final class QueryFilmFunc implements Function<Integer, List<Film>> {

        @Override
        public List<Film> apply(@NonNull Integer free) throws Exception {
            if(policy.get() == FilmCachePolicy.NEVER_DOWNLOAD || free <= 0){
                return Collections.emptyList();
            }
            List<Film> result = new ArrayList<>();
            Cursor filmCursor = buildFilmCursor();

            while(filmCursor.moveToNext()){
                Long id = filmCursor.getLong(0);
                String name = filmCursor.getString(1);
                Integer state = filmCursor.getInt(2);
                Long time = filmCursor.getLong(3);
                QueryBuilder<FilmLink> queryBuilder = dbConnection.getDaoSession().getFilmLinkDao().queryBuilder();
                queryBuilder.where(FilmLinkDao.Properties.FilmId.eq(id), FilmLinkDao.Properties.State.eq(LinkState.WAIT));
                List<FilmLink> links = queryBuilder.list();
                if(links.size() > 0){
                    Film film = new Film(id, name, state, time);
                    film.setLinks(links);
                    result.add(film);
                }
                if(result.size() >= free){
                    break;
                }
            }
            filmCursor.close();
            return result;
        }

        private Cursor buildFilmCursor() {
            String ori = "select * from %s where %s == %d;";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.State.columnName,
                    FilmState.WAIT
            );
            return dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
        }
    }

    private static final class FlatFilm implements Function<List<Film>, ObservableSource<Film>> {

        @Override
        public ObservableSource<Film> apply(@NonNull List<Film> films) throws Exception {
            return Observable.fromIterable(films);
        }
    }

    private final class DownloadObserver implements Observer<Film> {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            loop = d;
        }

        @Override
        public void onNext(@NonNull Film film) {
            FilmLink link = findFirstUsableLink(film);
            if(link == null){
                throw new RuntimeException("found none link !");
            }
            getDownloadManager(link.getUrl()).download(
                    link.getUrl(),
                    DownloadConstants.downloadMovieFilePath(film.getId()),
                    new DownloadCallback(film.getId(), link.getId()));
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
                    .observeOn(AndroidSchedulers.mainThread())
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
                        FilmLinkDao.Properties.Id,
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
                        FilmDao.Properties.Id,
                        filmId );
                dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
            }
        }
    }

    private DownloadManager getDownloadManager(String url){
        if(url.startsWith("magnet:") || url.startsWith("MAGNET:")){
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

    private static FilmLink findFirstUsableLink(Film film){
        if(film.getLinks() != null && film.getLinks().size() > 0){
            for(FilmLink link: film.getLinks()){
                if(link.getState() == LinkState.WAIT){
                    return link;
                }
            }
        }
        return null;
    }
}
