package com.sloth.client.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sloth.client.R;
import com.sloth.client.adapter.FilmAdapter;
import com.sloth.client.data.DataListener;
import com.sloth.client.data.Repository;
import com.sloth.ifilm.Film;
import com.sloth.tools.util.ScreenUtils;
import com.sloth.tools.util.ToastUtils;

import org.evilbinary.tv.widget.TvGridLayoutManagerScrolling;

import java.util.List;

public class ListActivity extends AppCompatActivity implements DataListener {

    private RecyclerView rvList;

    private Repository repository;

    private int pageIndex = 0;
    private final int pageSize = 120;

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
            rvList.setAdapter(new FilmAdapter(this));
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
}