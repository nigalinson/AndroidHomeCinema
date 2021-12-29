package com.sloth.client.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sloth.client.R;
import com.sloth.player.NativeSurfacePlayer;
import com.sloth.tools.util.StringUtils;

public class PlayerActivity extends AppCompatActivity {

    private NativeSurfacePlayer player;

    public static void play(Context context, String path){
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("data", path);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        player = findViewById(R.id.player);

        String path = getIntent().getStringExtra("data");
        if(StringUtils.notEmpty(path)){
            player.play(path);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }
}