package com.prudhvi.musicworld.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;


import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.Utilities;

import java.util.ArrayList;

public class AllsongsAdapter extends RecyclerView.Adapter<AllsongsAdapter.MyViewHolder> {

    private ArrayList<Song> songList;
    private Context mContext;
    private OnItemClickListener listener;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title,artist;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.albumName);
            imageView = (ImageView) view.findViewById(R.id.albumArt);
            imageView.setClipToOutline(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
               listener.onItemClick(getAdapterPosition());
        }
    }

    public AllsongsAdapter(ArrayList<Song> songList,Context context,OnItemClickListener listener) {
        this.songList = songList;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_songs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Song song = songList.get(position);

        if (song.getTitle().length() > 25) holder.title.setText(song.getTitle().substring(0,25));
        else  holder.title.setText(song.getTitle());
        if (song.getArtist().length() > 25) holder.artist.setText(song.getArtist().substring(0,25));
        else holder.artist.setText(song.getArtist());

        Bitmap bitmap = Utilities.getBitmap(mContext,Long.parseLong(song.getAlbumID()));

        if (bitmap != null) holder.imageView.setImageBitmap(bitmap);
        else holder.imageView.setImageResource(R.drawable.musicart);
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

}
