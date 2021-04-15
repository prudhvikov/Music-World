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

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    private ArrayList<Song> albumArrayList;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView albumName;
        public ImageView albumArt;

        public MyViewHolder(View view) {
            super(view);
            albumName = (TextView) view.findViewById(R.id.albumName);
            albumArt  = (ImageView) view.findViewById(R.id.albumArt);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public AlbumsAdapter(ArrayList<Song> albumList,Context context,OnItemClickListener listener) {
        this.albumArrayList = albumList;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_albums, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Song album = albumArrayList.get(position);

        String albumName = album.getAlbum();
        if (albumName.length() > 15){
            albumName = albumName.substring(0,15);
        }
        holder.albumName.setText(albumName);

        Bitmap bitmap = Utilities.getBitmap(mContext,Long.parseLong(album.getAlbumID()));

        if (bitmap == null) holder.albumArt.setImageResource(R.drawable.musicart);
        else holder.albumArt.setImageBitmap(bitmap);

    }


    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }

}
