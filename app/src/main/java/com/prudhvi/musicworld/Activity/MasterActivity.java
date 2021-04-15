package com.prudhvi.musicworld.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


import com.prudhvi.musicworld.Adapters.MasterAdapter;
import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.SongUtils;
import com.prudhvi.musicworld.Utils.Utilities;

import java.util.ArrayList;

public class MasterActivity extends AppCompatActivity implements MasterAdapter.OnItemClickListener {

    Intent intent;
    RecyclerView recyclerView;
    String type,name;
    ArrayList<Song> list;
    SongUtils songUtils;
    MasterAdapter masterAdapter;
    ImageView imageView;
    Toolbar toolbar;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.imageView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MasterActivity.super.onBackPressed();
//            }
//        });


        list = new ArrayList<>();
        songUtils = new SongUtils(MasterActivity.this);
        initView();
        intent = getIntent();
        type = intent.getStringExtra("TYPE");

        if (type.equals("ALBUMS")){
            name = intent.getStringExtra("ALBUMNAME");
            list = songUtils.getAlbumSongs(name);
            getSupportActionBar().setTitle(list.get(0).getAlbum());
        }else{
            name = intent.getStringExtra("ARTIST");
            list = songUtils.getArtistSongs(name);
            getSupportActionBar().setTitle(list.get(0).getArtist());
        }

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        bm = new Utilities(this).getBitmap(this,Long.parseLong(list.get(0).getAlbumID()));

        if (bm != null){
            imageView.setImageBitmap(bm);
        }else{
            imageView.setImageResource(R.drawable.musicart);
        }


        masterAdapter = new MasterAdapter(list, MasterActivity.this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MasterActivity.this));
        recyclerView.setAdapter(masterAdapter);



    }

    private void initView() {
            recyclerView = findViewById(R.id.recyclerView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position) {
        songUtils.playSong(position,list);
    }

}