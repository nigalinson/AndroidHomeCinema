package com.sloth.film;

import androidx.annotation.NonNull;
import com.sankuai.waimai.router.Router;
import com.sloth.icrawler.CrawlerManager;
import com.sloth.icrawler.Strategy;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmLink;
import com.sloth.ifilm.FilmLinkDao;
import com.sloth.ifilm.FilmState;
import com.sloth.ifilm.LinkState;
import com.sloth.tools.util.LogUtils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/12/20 10:47
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/12/20         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class CrawlerBridge implements CrawlerManager.CrawlerListener {
    private static final String TAG = "PrepareCrawler";

    private final FilmDataBaseConnection dbConnection;

    private CrawlerManager crawlerManager;

    private Disposable loop;

    public CrawlerBridge(FilmDataBaseConnection filmDataBaseConnection) {
        this.dbConnection = filmDataBaseConnection;
    }

    public void start(){
        stop();
        Observable.interval(10 * 1000, 60 * 60 * 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(new ClearCrawlerFunc())
                .map(new QueryFilmFunc())
                .filter(films -> !films.isEmpty())
                .flatMap(new FlatFilm())
                .map(new CrawlerFunc())
                .observeOn(Schedulers.io())
                .subscribe(new CrawlerObserver());
    }

    public void stop(){
        if(loop != null && !loop.isDisposed()){
            loop.dispose();
        }
    }

    public void crawler(long filmId){
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            QueryBuilder<Film> queryBuilder = dbConnection.getDaoSession().getFilmDao().queryBuilder();
            queryBuilder.where(FilmDao.Properties.Id.eq(filmId) ,FilmDao.Properties.State.eq(FilmState.WAIT));
            List<Film> films = queryBuilder.list();
            if(!films.isEmpty()){
                Film film = films.get(0);
                getCrawlerManager().crawler(film.getId(), film.getName(), CrawlerBridge.this);
            }
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe();
    }

    private CrawlerManager getCrawlerManager(){
        if(crawlerManager == null){
            crawlerManager = Router.getService(CrawlerManager.class, Strategy._DEFAULT_CRAWLER);
            assert crawlerManager != null;
        }
        return crawlerManager;
    }

    private final class ClearCrawlerFunc implements Function<Long, Boolean>{
        @Override
        public Boolean apply(@NonNull Long aLong) throws Exception {
            getCrawlerManager().clear();
            return true;
        }
    }

    private final class QueryFilmFunc implements Function<Boolean, List<Film>> {

        @Override
        public List<Film> apply(@NonNull Boolean arg) throws Exception {
            QueryBuilder<Film> queryBuilder = dbConnection.getDaoSession().getFilmDao().queryBuilder();
            queryBuilder.where(FilmDao.Properties.State.eq(FilmState.WAIT));
            queryBuilder.limit(10);
            return queryBuilder.list();
        }
    }

    private static final class FlatFilm implements Function<List<Film>, ObservableSource<Film>> {

        @Override
        public ObservableSource<Film> apply(@NonNull List<Film> films) throws Exception {
            return Observable.fromIterable(films);
        }
    }

    private final class CrawlerFunc implements Function<Film, Boolean> {

        @Override
        public Boolean apply(@NonNull Film film) throws Exception {
            getCrawlerManager().crawler(film.getId(), film.getName(), CrawlerBridge.this);
            return true;
        }
    }

    @Override
    public void onCrawlerResult(long filmId, String filmName, String linkName, String linkUrl) {
        boolean exist = dbConnection.getDaoSession().getFilmLinkDao().queryBuilder()
                .where(FilmLinkDao.Properties.FilmId.eq(filmId), FilmLinkDao.Properties.Url.eq(linkUrl))
                .list().size() > 0;
        if(!exist){
            dbConnection.getDaoSession().getFilmLinkDao().insert(new FilmLink(null, filmId, linkName, LinkState.WAIT, linkUrl));
        }
    }

    private final class CrawlerObserver implements Observer<Boolean> {

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            loop = d;
        }

        @Override
        public void onNext(@NonNull Boolean aBoolean) { }

        @Override
        public void onError(@NonNull Throwable e) {
            LogUtils.e(TAG, e.getMessage() != null ? e.getMessage() : "crawler failed !");
        }

        @Override
        public void onComplete() { }
    }

}
