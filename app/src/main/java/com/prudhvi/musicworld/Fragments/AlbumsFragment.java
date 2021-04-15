package com.prudhvi.musicworld.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.example.musicworld.Activity.AlbumSongs;
import com.prudhvi.musicworld.Activity.MasterActivity;
import com.prudhvi.musicworld.Adapters.AlbumsAdapter;
import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.SongUtils;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment implements AlbumsAdapter.OnItemClickListener{

    SongUtils songUtils;
    ArrayList<Song> albums = new ArrayList<>();
    RecyclerView albumList;
    AlbumsAdapter albumsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
           View v = inflater.inflate(R.layout.fragment_albums, container, false);

           songUtils = new SongUtils(getActivity());
           albums    = songUtils.getAlbums();
           albumList = v.findViewById(R.id.albumList);
           albumList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
           albumsAdapter = new AlbumsAdapter(albums, getActivity(), this);
           albumList.setAdapter(albumsAdapter);

           return v;
    } // oncreate function ends here


    @Override
    public void onItemClick(int position) {
           Intent intent = new Intent(getActivity(),MasterActivity.class);
           intent.putExtra("TYPE","ALBUMS");
           intent.putExtra("ALBUMNAME",albums.get(position).getAlbum());
           startActivity(intent);

    }
} // fragment ends here
