package com.sloth.client.ui;

import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sankuai.waimai.router.Router;
import com.sloth.client.R;
import com.sloth.client.adapter.FilmAdapter;
import com.sloth.client.app.AppConstants;
import com.sloth.client.data.DataListener;
import com.sloth.client.data.Repository;
import com.sloth.ifilm.Film;
import com.sloth.ifilm.FilmLink;
import com.sloth.ifilm.FilmState;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.download.DownloadListener;
import com.sloth.pinsplatform.download.DownloadManager;
import com.sloth.tools.util.ByteDanceDpiUtils;
import com.sloth.tools.util.ExecutorUtils;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.SPUtils;
import com.sloth.tools.util.ScreenUtils;
import com.sloth.tools.util.ToastUtils;

import org.evilbinary.tv.widget.TvGridLayoutManagerScrolling;

import java.util.List;
import java.util.Locale;

public class ListActivity extends AppCompatActivity implements DataListener {

    private RecyclerView rvList;

    private Repository repository;

    private int pageIndex = 0;
    private final int pageSize = 20;

    private AlertDialog menuDialog;
    private AlertDialog loadingDialog;
    private AlertDialog resourceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        intiView();
        repository = new Repository(this, this);
        repository.getFilmList(pageIndex, pageSize, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(repository != null){
            repository.destroy();
            repository = null;
        }
    }

    private void intiView() {
        rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new TvGridLayoutManagerScrolling(this, ScreenUtils.isLandscape() ? 5 : 2));
    }

    @Override
    public void getFilmListSuccess(List<Film> data) {
        if(rvList.getAdapter() == null){
            rvList.setAdapter(new FilmAdapter(this).setOnItemClickListener(position -> popOutMenu()));
        }
        FilmAdapter adapter = (FilmAdapter) rvList.getAdapter();
        adapter.resetItems(data);
    }

    @Override
    public void getFilmListFailed(String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void loadMoreFilmListSuccess(List<Film> data) {
        FilmAdapter adapter = (FilmAdapter) rvList.getAdapter();
        if(adapter != null){
            adapter.addItems(data);
        }
    }

    @Override
    public void loadMoreFilmListFailed(String message) {
        ToastUtils.showShort(message);
    }

    @Override
    public void toast(String message) {
        ToastUtils.showShort(message);
    }

    private void popOutMenu() {
        if(menuDialog == null){
            menuDialog = new AlertDialog.Builder(this).setItems(new String[]{"播放", "寻找资源", "可用资源", "删除"}, (dialog, which) -> {
                dialog.dismiss();
                switch (which){
                    case 0: play(); break;
                    case 1: crawler(); break;
                    case 2: popOutResources(); break;
                    case 3: deleteMovie(); break;
                    default:break;
                }
            }).create();
        }
        if(!menuDialog.isShowing()){
            menuDialog.show();
        }
        ByteDanceDpiUtils.adjustDialogSize(this, menuDialog);
    }

    private void popOutLoading(float cur, float total) {
        if(loadingDialog == null){
            loadingDialog = new AlertDialog.Builder(this).setTitle("downloading").setMessage("0%").create();
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }
        if(total == -1){
            loadingDialog.setMessage(String.format(Locale.CHINA, "downloading:%.2fMB", cur / 1024 / 1024));
        }else{
            loadingDialog.setMessage(String.format(Locale.CHINA, "%.1f", cur / total * 100) + "%");
        }
        ByteDanceDpiUtils.adjustDialogSize(this, loadingDialog);
    }

    private void closeLoading() {
        if(loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    private void popOutResource(List<FilmLink> links) {
        if(resourceDialog != null && resourceDialog.isShowing()){
            resourceDialog.dismiss();
            resourceDialog = null;
        }

        String[] list = new String[links.size()];
        for(int i = 0; i < links.size(); i++){
            list[i] = links.get(i).getName();
        }

        resourceDialog = new AlertDialog.Builder(this)
                .setItems(list, (dialog, which) -> {
                    dialog.dismiss();
                    repository.downloadFilm(links.get(which).getFilmId(), links.get(which).getId());
                }).create();
        resourceDialog.show();
        ByteDanceDpiUtils.adjustDialogSize(this, resourceDialog);
    }


    private void play() {
        Film film = checkingFilm();
        if(film == null){
            return;
        }

        if(!film.getState().equals(FilmState.OK)){
            ToastUtils.showShort("请先选择可用资源");
            return;
        }

        ExecutorUtils.getNormal().execute(new ExecutorUtils.WorkRunnable() {
            @Override
            public void run() {
                String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + film.getId() + ".mp4";
                boolean exist = FileUtils.isFileExists(path);
                if(exist){
                    toPlay(path);
                }else{
                    Router.getService(DownloadManager.class, Strategies.DownloadEngine.LIU_LI_SHO)
                            .download(SPUtils.getInstance().getString(AppConstants.SP_IP_HOST) + film.getId() + ".mp4", path, new DownloadListener() {
                                @Override
                                public void onDownloadStart() {

                                }

                                @Override
                                public void onDownloadProgress(float current, float total) {
                                    runOnUiThread(()-> popOutLoading(current, total));
                                }

                                @Override
                                public void onDownloadComplete(String filePath) {
                                    closeLoading();
                                    toPlay(filePath);
                                }

                                @Override
                                public void onDownloadFailed(String errCode) {
                                    closeLoading();
                                    ToastUtils.showShort("error: " + errCode);
                                }
                            });
                }
            }
        });
    }

    private void crawler() {
        Film film = checkingFilm();
        if(film == null){
            return;
        }

        repository.crawlerFilm(film.getId());

    }

    private void popOutResources() {
        Film film = checkingFilm();
        if(film == null){
            return;
        }

        if(film.getLinks() == null || film.getLinks().size() <= 0){
            ToastUtils.showShort("暂无资源");
            return;
        }

        popOutResource(film.getLinks());
    }

    private void deleteMovie() {
        Film film = checkingFilm();
        if(film == null){
            return;
        }

        String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/" + film.getId() + ".mp4";
        FileUtils.delete(path);
        repository.deleteFilm(film.getId());

    }

    private Film checkingFilm(){
        int index = -1;
        FilmAdapter adapter = (FilmAdapter) rvList.getAdapter();
        if(adapter != null){
            index = adapter.getCheckedIndex();
        }

        if(index == -1){
            return null;
        }

        return adapter.getItemData(index);
    }

    private void toPlay(String path) {
        PlayerActivity.play(this, path);
    }

}