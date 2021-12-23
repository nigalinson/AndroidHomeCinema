package com.sloth.film;

import androidx.annotation.NonNull;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.download.DownloadConstants;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmLinkDao;
import com.sloth.ifilm.FilmManager;
import com.sloth.ifilm.FilmQueryParam;
import com.sloth.ifilm.FilmState;
import com.sloth.ifilm.LinkState;
import com.sloth.ifilm.Strategy;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.SPUtils;
import com.sloth.tools.util.StringUtils;
import com.sloth.tools.util.Utils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    public Observable<Boolean> addFilm(String name) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            Film film = new Film();
            film.setName(name);
            film.setState(FilmState.WAIT);
            film.setLinks(new ArrayList<>());
            film.setCreateTime(System.currentTimeMillis());
            filmDataBaseConnection.getDaoSession().getFilmDao().insertOrReplace(film);
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> searchFilmResources(long filmId) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            crawlerBridge.crawler(filmId);
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFilm(long id) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            String ori = "delete from %s where %s == %d;";
            String sql = String.format(Locale.CHINA, ori,
                    FilmLinkDao.TABLENAME,
                    FilmLinkDao.Properties.FilmId.columnName,
                    id
            );
            filmDataBaseConnection.getDaoSession().getDatabase().rawQuery(sql, null);

            filmDataBaseConnection.getDaoSession().getFilmDao().deleteByKey(id);
            FileUtils.delete(DownloadConstants.downloadMovieFilePath(id));
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> downloadFilm(long id) {
        return Observable.just(id)
                .subscribeOn(Schedulers.io())
                .map(new DownloadFilmFunc())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> downloadFilmByLink(long filmId, long linkId) {
        return Observable.just(filmId)
                .subscribeOn(Schedulers.io())
                .map(new DownloadFilmByLinkFunc(linkId))
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> removeFilmCache(long id) {
        return Observable.just(id)
                .subscribeOn(Schedulers.io())
                .map(new DeleteFilmCacheFunc())
                .map(new UselessFilmFunc())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new StopDownloadFilmFunc())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> disableLink(long linkId) {
        return Observable.just(true)
                .subscribeOn(Schedulers.io())
                .map(new DisableFirstLinkFunc(linkId))
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

    private final class DisableFirstLinkFunc implements Function<Boolean, Boolean>{

        private final Long linkId;

        public DisableFirstLinkFunc(Long linkId) {
            this.linkId = linkId;
        }

        @Override
        public Boolean apply(@NonNull Boolean r) throws Exception {
            String ori = "update %s set %s = '%d' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmLinkDao.TABLENAME,
                    FilmLinkDao.Properties.State.columnName,
                    LinkState.USELESS,
                    FilmLinkDao.Properties.Id,
                    linkId);
            filmDataBaseConnection.getDaoSession().getDatabase().rawQuery(sql, null);
            return true;
        }
    }


    private final class DownloadFilmFunc implements Function<Long, Boolean>{

        @Override
        public Boolean apply(@NonNull Long filmId) throws Exception {
            downloadCenter.download(filmId);
            return true;
        }
    }

    private final class DownloadFilmByLinkFunc implements Function<Long, Boolean>{

        private final Long linkId;

        public DownloadFilmByLinkFunc(Long linkId) {
            this.linkId = linkId;
        }

        @Override
        public Boolean apply(@NonNull Long filmId) throws Exception {
            downloadCenter.download(filmId, linkId);
            return true;
        }
    }

    private static final class DeleteFilmCacheFunc implements Function<Long, Long>{

        @Override
        public Long apply(@NonNull Long filmId) throws Exception {
            FileUtils.delete(DownloadConstants.downloadMovieFilePath(filmId));
            return filmId;
        }
    }

    private final class UselessFilmFunc implements Function<Long, Long>{

        @Override
        public Long apply(@NonNull Long filmId) throws Exception {
            String ori = "update %s set %s = '%d' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.State.columnName,
                    FilmState.WAIT,
                    FilmDao.Properties.Id,
                    filmId );
            filmDataBaseConnection.getDaoSession().getDatabase().rawQuery(sql, null);
            return filmId;
        }
    }

    private final class StopDownloadFilmFunc implements Function<Long, Boolean>{

        @Override
        public Boolean apply(@NonNull Long filmId) throws Exception {
            downloadCenter.stopDownload(filmId);
            return true;
        }
    }
}
