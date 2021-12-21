package com.sloth.film;

import androidx.annotation.NonNull;
import com.sankuai.waimai.router.Router;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmCachePolicy;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmState;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.StringUtils;
import org.greenrobot.greendao.query.QueryBuilder;
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
import io.reactivex.functions.Predicate;
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

    public void reStartDownloading(boolean reInit){
        destroy();
        Observable.interval(0, 10 * 60000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(new ResetShuttingErrorFunc(reInit))
                .map(b -> concurrency.get())
                .map(new QueryFilmFunc())
                .filter(films -> !films.isEmpty())
                .flatMap(new FlatFilm())
                .filter(new ifValidFilm())
                .map(new UpdateDownloadingFunc())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver());
    }

    public void reDownload(Film film){
        if(getDownloadManager(film.getOnlineUrl()).isDownloading(film.getOnlineUrl())){
            return;
        }
        Observable.just(film)
                .map(new UpdateDownloadingFunc())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownloadObserver());
    }

    public void stopDownload(Film film){
        if(getDownloadManager(film.getOnlineUrl()).isDownloading(film.getOnlineUrl())){
            getDownloadManager(film.getOnlineUrl()).terminate(film.getOnlineUrl());
        }
    }

    public void stopDownloadAll(){
        getHttpDownloadManager().terminateAll();
        getTorrentDownloadManager().terminateAll();
    }

    public void destroy(){
        if(loop != null && !loop.isDisposed()){
            loop.dispose();
            loop = null;
        }
    }

    private final class ResetShuttingErrorFunc implements Function<Long, Boolean> {

        private final boolean reset;

        public ResetShuttingErrorFunc(boolean reset) {
            this.reset = reset;
        }

        @Override
        public Boolean apply(@NonNull Long aLong) throws Exception {
            if(reset){
                String ori = "update %s set %s = '%d' where %s = '%d';";
                String sql = String.format(Locale.CHINA, ori,
                        FilmDao.TABLENAME,
                        FilmDao.Properties.State.columnName,
                        FilmState.WAITING,
                        FilmDao.Properties.State.columnName,
                        FilmState.DOWNLOADING );
                dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
            }
            return true;
        }
    }

    private final class QueryFilmFunc implements Function<Integer, List<Film>> {

        @Override
        public List<Film> apply(@NonNull Integer free) throws Exception {
            if(policy.get() == FilmCachePolicy.NEVER_DOWNLOAD || free <= 0){
                return Collections.emptyList();
            }
            QueryBuilder<Film> queryBuilder = dbConnection.getDaoSession().getFilmDao().queryBuilder();
            queryBuilder.where(FilmDao.Properties.OnlineUrl.isNotNull());

            if(policy.get() == FilmCachePolicy.ALWAYS_DOWNLOAD){
                queryBuilder.where(FilmDao.Properties.State.in(FilmState.DISABLE, FilmState.WAITING));
            }else if(policy.get() == FilmCachePolicy.DOWNLOAD_ONCE){
                queryBuilder.where(FilmDao.Properties.State.eq(FilmState.WAITING));
            }else{
                return Collections.emptyList();
            }

            queryBuilder.limit(free);
            return queryBuilder.list();
        }
    }

    private static final class FlatFilm implements Function<List<Film>, ObservableSource<Film>> {

        @Override
        public ObservableSource<Film> apply(@NonNull List<Film> films) throws Exception {
            return Observable.fromIterable(films);
        }
    }

    private final class ifValidFilm implements Predicate<Film> {

        @Override
        public boolean test(@NonNull Film film) throws Exception {
            if(StringUtils.notEmpty(film.getName())){
                return getDownloadManager(film.getOnlineUrl()).runningTasks() < concurrency.get();
            }else{
                //clear invalid film
                dbConnection.getDaoSession().getFilmDao().deleteByKey(film.getId());
                return false;
            }
        }
    }

    private final class UpdateDownloadingFunc implements Function<Film, Film>{

        @Override
        public Film apply(@NonNull Film film) throws Exception {
            String ori = "update %s set %s = '%d' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.State.columnName,
                    FilmState.DOWNLOADING,
                    FilmDao.Properties.Id,
                    film.getId() );
            dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
            return film;
        }
    }

    private final class DownloadObserver implements Observer<Film> {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            loop = d;
        }

        @Override
        public void onNext(@NonNull Film film) {
            getDownloadManager(film.getOnlineUrl()).download(
                    film.getOnlineUrl(),
                    DownloadConstants.downloadMovieFilePath(film.getId()),
                    new DownloadCallback(film.getId()));
        }

        @Override
        public void onError(@NonNull Throwable e) {
            LogUtils.e(TAG, e.getMessage() != null ? e.getMessage() : "error" );
        }

        @Override
        public void onComplete() { }
    }

    private class DownloadCallback implements DownloadListener{

        private final long id;

        public DownloadCallback(long id) {
            this.id = id;
        }

        @Override
        public void onDownloadStart() { }

        @Override
        public void onDownloadProgress(long current, long total) { }

        @Override
        public void onDownloadComplete(String filePath) {
            onDownloadFinish(true, id);
        }


        @Override
        public void onDownloadFailed(String errCode) {
            onDownloadFinish(false, id);
        }

        protected void onDownloadFinish(boolean res, long id){
            Observable.just(res)
                    .map(new UpdateCompleteFunc(id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    private final class UpdateCompleteFunc implements Function<Boolean, Boolean>{

        private final long id;

        public UpdateCompleteFunc(long id) {
            this.id = id;
        }

        @Override
        public Boolean apply(@NonNull Boolean sus) throws Exception {
            String ori = "update %s set %s = '%d' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.State.columnName,
                    (sus ? FilmState.OK : FilmState.DISABLE),
                    FilmDao.Properties.Id,
                    id );
            dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
            return true;
        }
    }

    private DownloadManager getDownloadManager(String url){
        if(url.startsWith("magnet:") || url.startsWith("MAGNET:")){
            //磁力链接
            return getTorrentDownloadManager();
        }else{
            //http
            return getHttpDownloadManager();
        }
    }

    private DownloadManager getTorrentDownloadManager(){
        return Router.getService(DownloadManager.class, Strategies.DownloadEngine.TORRENT);
    }

    private DownloadManager getHttpDownloadManager(){
        return Router.getService(DownloadManager.class, Strategies.DownloadEngine.LIU_LI_SHO);
    }

}
