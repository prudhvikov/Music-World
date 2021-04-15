package com.prudhvi.musicworld.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;

import java.util.ArrayList;


public class AlbumSongAdapter extends RecyclerView.Adapter<AlbumSongAdapter.MyViewHolder> {

    private ArrayList<Song> albumSongs;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView songName;

        public MyViewHolder(View view) {
            super(view);
            songName = (TextView) view.findViewById(R.id.songName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public AlbumSongAdapter(ArrayList<Song> albumSongs,Context context,OnItemClickListener listener) {
        this.albumSongs = albumSongs;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_songs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Song song = albumSongs.get(position);
        holder.songName.setText(song.getTitle());
    }


    @Override
    public int getItemCount() {
        return albumSongs.size();
    }

}