package com.prudhvi.musicworld.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prudhvi.musicworld.Model.Song;

import java.util.ArrayList;

public class Favorites {

    private String TEXT_TYPE = " TEXT";
    private String COMMA_SEP = ",";
    public static final String DB_NAME = "Favs.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "Favorites";
    public static final String TITLE = "title";
    public static final String ALBUM = "album";
    public static final String ARTIST = "artist";
    public static final String DURATION = "duration";
    public static final String PATH = "path";
    public static final String NAME = "name";
    public static final String ALBUMID = "albumid";
    public static final String COLUMN_NAME_ID = "id";

    private String[] ALL_KEYS = new String[]
            {COLUMN_NAME_ID, TITLE, PATH, ARTIST, ALBUM, NAME, DURATION, ALBUMID};


    private String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE + COMMA_SEP +
                    ARTIST + TEXT_TYPE + COMMA_SEP +
                    ALBUM + TEXT_TYPE + COMMA_SEP +
                    NAME + TEXT_TYPE + COMMA_SEP +
                    DURATION + TEXT_TYPE + COMMA_SEP +
                    ALBUMID + TEXT_TYPE +
                    ");";


    private String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private SQLiteDatabase database;
    private FavoritesHelper favoritesHelper;
    Context context;

    public Favorites(Context context) {
           this.context = context;
           favoritesHelper = new FavoritesHelper(context);
    }

    public Favorites open(){
           database = favoritesHelper.getWritableDatabase();
           return this;
    }

    public Favorites close() {
        database.close();
        return this;
    }

    public void deleteRow(String name) {
        String where = PATH + "=" + "'"+name+"'";
        database.delete(TABLE_NAME, where, null);
    }

    public ArrayList<Song> getFavorites(){

        Cursor c = database.query(TABLE_NAME, ALL_KEYS, null, null, null, null, COLUMN_NAME_ID+" DESC");

        ArrayList<Song> favorites = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Song song = new Song( c.getString(1),
                                      c.getString(4),
                                      c.getString(3),
                                      c.getString(6),
                                      c.getString(2),
                                      c.getString(5),
                                      c.getString(7) );

                favorites.add(song);
            } while (c.moveToNext());
        }
        c.close();

        return favorites;
    }

    public long addRow(Song row) {

        ContentValues values = new ContentValues();
        values.put(TITLE, row.getTitle());
        values.put(PATH, row.getPath());
        values.put(ARTIST, row.getArtist());
        values.put(ALBUM, row.getAlbum());
        values.put(NAME, row.getName());
        values.put(DURATION, row.getDuration());
        values.put(ALBUMID, row.getAlbumID());

        return database.insert(TABLE_NAME,"NULL", values);
    }


    public class FavoritesHelper extends SQLiteOpenHelper {

        public FavoritesHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

    } // database helper ending here

} // favorites class ends here


