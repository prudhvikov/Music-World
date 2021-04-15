package com.prudhvi.musicworld.Utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.Services.MusicPlayerServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SongUtils {

    Context context;
    private SharedPrefs sharedPrefs;

    private static ArrayList<Song> mainList = new ArrayList<>();
    private static ArrayList<Song> albums = new ArrayList<>();
    private static ArrayList<Song> artists = new ArrayList<>();

    private static ArrayList<Song> queue = new ArrayList<>();

    public SongUtils(Context context) {
        this.context = context;
        sharedPrefs  = new SharedPrefs(context);
    }

    public ArrayList<Song> getArtists() {
        grabEmpty();
        return  artists;
    }

    public ArrayList<Song> getArtistSongs(String s){

        ArrayList<Song> artistSongs = new ArrayList<>();
        ArrayList<Song> list       = new ArrayList<>(mainList);

        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getArtist().toString().equals(s)){
                artistSongs.add(list.get(i));
            }
        }
        return artistSongs;

    }


    public ArrayList<Song> getAlbums() {
        grabEmpty();
        return  albums;
    }

    public ArrayList<Song> getAlbumSongs(String s){

           ArrayList<Song> albumSongs = new ArrayList<>();
           ArrayList<Song> list       = new ArrayList<>(mainList);

           for (int i = 0; i < list.size(); i++){
               if (list.get(i).getAlbum().toString().equals(s)){
                   albumSongs.add(list.get(i));
               }
           }
           return albumSongs;

    }

    public ArrayList<Song> allSongs() {
        grabEmpty();
        ArrayList<Song> song = new ArrayList<>(mainList);
        Collections.sort(song, new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });
        return song;
    }

    public void grabEmpty() {
        if (mainList.isEmpty()) {
            grabData();
        }
    }

    public void setMusicId(int id){
           sharedPrefs.writeSharedPrefs("musicId",id);
    }

    public int getMusicId(){
           int musicId = sharedPrefs.readSharedPrefsInt("musicId",0);
           return musicId;
    }

    /** grab data **/

    private void grabData() {
        Cursor cursor;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        cursor = context.getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
//                    long id          = cursor.getLong(cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID));
                    String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    int currentDuration = Math.round(Integer.parseInt(duration));
                    String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String albumID = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    TimeZone tz = TimeZone.getTimeZone("UTC");
                    SimpleDateFormat df = new SimpleDateFormat("mm:ss", Locale.getDefault());
                    df.setTimeZone(tz);
                    String time = String.valueOf(df.format(currentDuration));

                    Song s = new Song(title,albumName,artistName,time,path,songName,albumID);
                    mainList.add(s);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        }


        /** albums data **/
        ArrayList<Song> allSongList = new ArrayList<>(mainList);
        ArrayList<Song> list = new ArrayList<>();

        for (int i = 0; i < allSongList.size(); i++){
            Song s = allSongList.get(i);

            if (list.size() > 0){
                int index = 0;
                for (int j = 0; j < list.size(); j ++){
                    if (s.getAlbum().toString().equals(list.get(j).getAlbum().toString())){
                        index = 1;
                    }
                }
                if (index == 0){
                    list.add(s);
                }
            }else{
                list.add(s);
            }
        }

        albums.addAll(list);


        ArrayList<Song> artistList = new ArrayList<>(mainList);
        ArrayList<Song> aList = new ArrayList<>();

        for (int i = 0; i < artistList.size(); i++){
            Song s = artistList.get(i);

            if (aList.size() > 0){
                int index = 0;
                for (int j = 0; j < aList.size(); j ++){
                    if (s.getArtist().toString().equals(aList.get(j).getArtist().toString())){
//                        if(_artists.containsKey(s)){
//                            _artists.put(s, _artists.get(s) + 1);
//                        }
                        index = 1;
                    }
                }
                if (index == 0){
                    aList.add(s);
//                    _artists.put(s, 0);

                }
            }else{
                aList.add(s);
//                _artists.put(s, 0);
            }
        }

        artists.addAll(aList);

    } // grabdata ending here


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playSong(int position, ArrayList<Song> songList){
           sharedPrefs.writeSharedPrefs("musicId",position);
           replaceQuery(songList);
           Intent intent = new Intent(MusicPlayerServices.ACTION_PLAY);
           ContextCompat.startForegroundService(context,createExplicitFromImplicitIntent(intent));
    } // playsong function ends here


    public void replaceQuery(ArrayList<Song> songList) {
            if ( !songList.isEmpty() && songList != null ) clearQueue();
            queue.addAll(songList);
    }


    public ArrayList<Song> queue() {
        if (queue.isEmpty()) {
            ArrayList<Song> list = new ArrayList<>(mainList);
//            Collections.reverse(list);
            replaceQuery(list);
        }
        return (queue);
    }

    private void clearQueue() {
            queue.clear();
    }

    private Intent createExplicitFromImplicitIntent(Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }


}// songutils class ending