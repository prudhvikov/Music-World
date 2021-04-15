package com.prudhvi.musicworld.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;

import java.util.ArrayList;


public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.MyViewHolder> {

    private ArrayList<Song> songList;
    private Context mContext;
    private OnItemClickListener listener;


    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title,artist,serialNumber;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            artist = (TextView) view.findViewById(R.id.artist);
            serialNumber = (TextView) view.findViewById(R.id.serial_number);
//            imageView = (ImageView) view.findViewById(R.id.albumArt);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public MasterAdapter(ArrayList<Song> songList,Context context,OnItemClickListener listener) {
        this.songList = songList;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.master_songs, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Song song = songList.get(position);
        int pos = position + 1;
        if (pos < 10){
            holder.serialNumber.setText("0"+pos);
        }else{
            holder.serialNumber.setText(pos+"");
        }
        if (song.getTitle().length() > 18) holder.title.setText(song.getTitle().substring(0,18));
        else holder.title.setText(song.getTitle());
        if (song.getArtist().length() > 18) holder.artist.setText(song.getArtist().substring(0,18));
        else holder.artist.setText(song.getArtist());

//        Bitmap bitmap = Utilities.getBitmap(mContext,Long.parseLong(song.getAlbumID()));

//        if (bitmap != null) holder.imageView.setImageBitmap(bitmap);
//        else holder.imageView.setImageResource(R.drawable.musicart);
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

}