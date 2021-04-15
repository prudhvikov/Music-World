package com.prudhvi.musicworld.Fragments;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.prudhvi.musicworld.Adapters.AllsongsAdapter;
import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.SongUtils;


import java.util.ArrayList;


public class SongsFragment extends Fragment  implements AllsongsAdapter.OnItemClickListener {


    RecyclerView recyclerView;
    SongUtils songUtils;
    private MediaBrowserCompat mMediaBrowser;
    ArrayList<Song> songs = new ArrayList<>();
    AllsongsAdapter allsongsAdapter;
    TextView textView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        initView(view);
        songUtils = new SongUtils(getActivity());
        songs     = songUtils.allSongs();


        if (songs.size() == 0){
            textView.setText("There Are No Offline Songs");
        }else{
            allsongsAdapter = new AllsongsAdapter(songs, getActivity(),this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(allsongsAdapter);
        }



//        mMediaBrowser = new MediaBrowserCompat(getActivity(), new ComponentName(getActivity(), MusicPlayerServices.class), mConnectionCallback, null);
        return view;

    }  // oncreate method ends here

    private void initView(View v) {
        textView      = v.findViewById(R.id.textView);
        recyclerView  = v.findViewById(R.id.recyclerView);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position)  {
        songUtils.playSong(position,songs);
    }



} // fragment ends here




