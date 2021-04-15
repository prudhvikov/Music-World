package com.prudhvi.musicworld.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.prudhvi.musicworld.Fragments.AlbumsFragment;
import com.prudhvi.musicworld.Fragments.FavoritesFragment;
import com.prudhvi.musicworld.Fragments.PlaylistFragment;
import com.prudhvi.musicworld.Fragments.SongsFragment;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Services.MusicPlayerServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AllSongs extends AppCompatActivity {


    MusicPlayerServices musicPlayerServices;

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toolbar.getOverflowIcon().setColorFilter(Color.WHITE , PorterDuff.Mode.SRC_ATOP);
        initialFragmentSetup();
        initView();
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    } // oncreate method ends here



    private void initialFragmentSetup() {
        Fragment fragment = new SongsFragment();
        loadFragment(fragment);
    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

    }

    private void setWindowFullScreenMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.songs:
                    fragment = new SongsFragment();
                    getSupportActionBar().setTitle("All Songs");
                    loadFragment(fragment);
                    return true;
                case R.id.albums:
                    fragment = new AlbumsFragment();
                    getSupportActionBar().setTitle("Albums");
                    loadFragment(fragment);
                    return true;
                case R.id.playlists:
                    fragment = new PlaylistFragment();
                    getSupportActionBar().setTitle("Favourites");
                    loadFragment(fragment);
                    return true;
                case R.id.artists:
                    fragment = new FavoritesFragment();
                    getSupportActionBar().setTitle("Artists");
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }



} // Allsongs Activity ends here
