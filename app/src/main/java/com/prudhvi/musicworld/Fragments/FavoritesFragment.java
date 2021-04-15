package com.prudhvi.musicworld.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prudhvi.musicworld.Activity.MasterActivity;
import com.prudhvi.musicworld.Adapters.ArtistAdapter;
import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.SongUtils;

import java.util.ArrayList;


public class FavoritesFragment extends Fragment implements ArtistAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    SongUtils songUtils;
    ArrayList<Song> artists = new ArrayList<>();
    ArtistAdapter artistAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView = v.findViewById(R.id.artistList);
        songUtils   = new SongUtils(getActivity());
        artists    =  songUtils.getArtists();
        artistAdapter = new ArtistAdapter(artists, getActivity(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(artistAdapter);
        return v;

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), MasterActivity.class);
        intent.putExtra("TYPE","ARTISTS");
        intent.putExtra("ARTIST",artists.get(position).getArtist());
        startActivity(intent);
    }
}