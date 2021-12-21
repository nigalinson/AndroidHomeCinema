package com.sloth.film;

import androidx.annotation.NonNull;
import com.sankuai.waimai.router.Router;
import com.sloth.icrawler.CrawlerManager;
import com.sloth.icrawler.Strategy;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmDao;
import com.sloth.ifilm.FilmState;
import com.sloth.tools.util.LogUtils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
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
        Observable.interval(2 * 1000, 10 * 60 * 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(new ClearCrawlerFunc())
                .map(new QueryFilmFunc())
                .filter(films -> !films.isEmpty())
                .flatMap(new FlatFilm())
                .map(new CrawlerFunc())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CrawlerObserver());
    }

    public void stop(){
        if(loop != null && !loop.isDisposed()){
            loop.dispose();
        }
    }

    private CrawlerManager getCrawlerManager(){
        if(crawlerManager == null){
            crawlerManager = Router.getService(CrawlerManager.class, Strategy._DEFAULT);
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
            queryBuilder.where(FilmDao.Properties.OnlineUrl.isNull());
            queryBuilder.where(FilmDao.Properties.State.eq(FilmState.WAITING));
            queryBuilder.limit(20);
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
    public void onCrawlerResult(long id, String name, List<String> urls) {
        if(urls.isEmpty()){
            String ori = "update %s set %s = '%d' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.State.columnName,
                    FilmState.DISABLE,
                    FilmDao.Properties.Id.columnName,
                    id);
            dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
        }else{
            String ori = "update %s set %s = '%s' where %s = '%d';";
            String sql = String.format(Locale.CHINA, ori,
                    FilmDao.TABLENAME,
                    FilmDao.Properties.OnlineUrl,
                    urls.get(0),
                    FilmDao.Properties.Id,
                    id);
            dbConnection.getDaoSession().getDatabase().rawQuery(sql, null);
        }
    }

    private final class CrawlerObserver implements Observer<Boolean>{

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
