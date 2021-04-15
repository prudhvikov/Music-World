package com.prudhvi.musicworld.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prudhvi.musicworld.Adapters.FavoritesAdapter;
import com.prudhvi.musicworld.Database.Favorites;
import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.SongUtils;

import java.util.ArrayList;
import java.util.Collections;

public class PlaylistFragment extends Fragment implements FavoritesAdapter.OnItemClickListener{

    Favorites favorites;
    ArrayList<Song> fSongs = new ArrayList<>();
    TextView textView;
    RecyclerView recyclerView;
    SongUtils songUtils;
    FavoritesAdapter favoritesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_playlist, container, false);
        textView = v.findViewById(R.id.textView);
        recyclerView = v.findViewById(R.id.recyclerView);

        favorites = new Favorites(getActivity());
        favorites.open();
        songUtils = new SongUtils(getActivity());

        fSongs = favorites.getFavorites();
        Collections.reverse(fSongs);

        if (fSongs.size() == 0){
           textView.setVisibility(View.VISIBLE);
           textView.setText("There Are No Favorite Songs Yet !!!");
        }else{
            favoritesAdapter = new FavoritesAdapter(fSongs, getActivity(),this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(favoritesAdapter);
        }

        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position) {
           songUtils.playSong(position, fSongs);
    }
}
