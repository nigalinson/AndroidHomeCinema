package com.sloth.film;

import androidx.annotation.NonNull;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmCachePolicy;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmManager;
import com.sloth.ifilm.FilmQueryParam;
import com.sloth.ifilm.Strategy;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.SPUtils;
import com.sloth.tools.util.StringUtils;
import com.sloth.tools.util.Utils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
        int policy = SPUtils.getInstance().getInt(FilmConstants.SP.KEY_FILM_CACHE_POLICY, FilmConstants.DEF_FILM_CACHE_POLICY);
        int downloadConcurrency = SPUtils.getInstance().getInt(FilmConstants.SP.KEY_FILM_DOWNLOAD_CONCURRENCY, FilmConstants.DEF_FILM_DOWNLOAD_CONCURRENCY);
        downloadCenter = new DownloadCenter(filmDataBaseConnection).setPolicy(policy, downloadConcurrency);
        crawlerBridge = new CrawlerBridge(filmDataBaseConnection);
    }

    @Override
    public void openEngine(boolean open) {
        if(open){
            downloadCenter.reStartDownloading(true);
            crawlerBridge.start();
        }else{
            downloadCenter.stopDownloadAll();
            downloadCenter.destroy();
            crawlerBridge.stop();
        }
    }

    @Override
    public Observable<List<Film>> getFilms(FilmQueryParam param) {
        return Observable.create((ObservableOnSubscribe<List<Film>>) emitter -> {
            QueryBuilder<Film> queryBuilder = filmDataBaseConnection.getDaoSession().getFilmDao().queryBuilder();

            if(StringUtils.notEmpty(param.getName())){
                queryBuilder.where(FilmDao.Properties.Name.like("%"+param.getName()+"%"));
            }

            int start = param.getPageIndex() * param.getPageSize();
            int size = param.getPageSize();
            queryBuilder.offset(start).limit(size);

            emitter.onNext(queryBuilder.list());
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> addFilm(Film film) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            filmDataBaseConnection.getDaoSession().getFilmDao().insertOrReplace(film);
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> editFilm(Film film) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            filmDataBaseConnection.getDaoSession().getFilmDao().insertOrReplace(film);
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFilm(long id) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            filmDataBaseConnection.getDaoSession().getFilmDao().deleteByKey(id);
            FileUtils.delete(DownloadConstants.downloadMovieFilePath(id));
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFilm(Film film) {
        return removeFilm(film.getId());
    }

    @Override
    public Observable<Boolean> cacheFilm(long id) {
        return Observable.just(id)
                .map(new QueryFilmByIdFunc())
                .subscribeOn(Schedulers.io())
                .map(new ReDownloadFilmFunc())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> cacheFilm(Film film) {
        return Observable.just(film)
                .map(new ReDownloadFilmFunc())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFilmCache(long id) {
        return Observable.just(id)
                .map(new QueryFilmByIdFunc())
                .subscribeOn(Schedulers.io())
                .map(new CancelDownloadFilmFunc())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFilmCache(Film film) {
        return Observable.just(film)
                .map(new CancelDownloadFilmFunc())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> setCachePolicy(int policy, int concurrency) {
        return Observable.just(true)
                .map(new UpdatePolicyFunc(policy, concurrency))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private final class QueryFilmByIdFunc implements Function<Long, Film>{

        @Override
        public Film apply(@NonNull Long aId) throws Exception {
            List<Film> films = filmDataBaseConnection.getDaoSession().getFilmDao().queryBuilder()
                    .where(FilmDao.Properties.Id.eq(aId)).list();
            if(!films.isEmpty()){
                return films.get(0);
            }else{
                throw new RuntimeException("film not exists");
            }
        }
    }

    private final class ReDownloadFilmFunc implements Function<Film, Boolean>{

        @Override
        public Boolean apply(@NonNull Film film) throws Exception {
            downloadCenter.reDownload(film);
            return true;
        }
    }

    private final class CancelDownloadFilmFunc implements Function<Film, Boolean>{

        @Override
        public Boolean apply(@NonNull Film film) throws Exception {
            downloadCenter.stopDownload(film);
            return true;
        }
    }

    private final class UpdatePolicyFunc implements Function<Boolean, Boolean>{

        @FilmCachePolicy
        private final int policy;

        private final int concurrency;

        public UpdatePolicyFunc(@FilmCachePolicy int policy, int concurrency) {
            this.policy = policy;
            this.concurrency = concurrency;
        }

        @Override
        public Boolean apply(@NonNull Boolean b) throws Exception {
            SPUtils.getInstance().put(FilmConstants.SP.KEY_FILM_CACHE_POLICY, policy);
            SPUtils.getInstance().put(FilmConstants.SP.KEY_FILM_DOWNLOAD_CONCURRENCY, concurrency);
            downloadCenter.setPolicy(policy, concurrency);
            return true;
        }
    }


}
