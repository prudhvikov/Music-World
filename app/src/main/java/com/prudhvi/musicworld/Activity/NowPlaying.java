package com.prudhvi.musicworld.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.prudhvi.musicworld.Database.Favorites;
import com.prudhvi.musicworld.Model.Song;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Services.MusicPlayerServices;
import com.prudhvi.musicworld.Utils.SharedPrefs;
import com.prudhvi.musicworld.Utils.SongUtils;
import com.prudhvi.musicworld.Utils.Utilities;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;

public class NowPlaying extends AppCompatActivity {

    MediaControllerCompat mediaController = null;
    private MediaBrowserCompat mediaBrowser;
    PlaybackStateCompat mLastPlaybackState = null;

    SharedPrefs sharedPrefs;
    ImageView prev, play, next, albumArt, shuffle, repeat, _favorite;
    Favorites favorites;
    TextView songName;
    ArrayList<Song> favSongs;
    SeekBar seekBar;
    String path;
    Toolbar toolbar;
    TextView startTimer, endTimer;
    SongUtils songUtils;

    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private final Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        mediaBrowser = new MediaBrowserCompat(NowPlaying.this, new ComponentName(NowPlaying.this, MusicPlayerServices.class), connectionCallback, null);
        songUtils = new SongUtils(this);
        initView();

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefs = new SharedPrefs(this);
        favorites = new Favorites(this);
        favorites.open();

        _favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeItFavorite();
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean re = sharedPrefs.readSharedPrefBoolean("repeat", false);

                if (re) {
                    repeat.setColorFilter(null);
                    sharedPrefs.writeSharedPrefBoolean("repeat", false);
                    new Utilities(NowPlaying.this).showShortToast("Repeat Mode Off");
                } else {
                    repeat.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    sharedPrefs.writeSharedPrefBoolean("repeat", true);
                    new Utilities(NowPlaying.this).showShortToast("Repeat Mode On");
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(NowPlaying.this).getTransportControls().play();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(NowPlaying.this).getTransportControls().skipToNext();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaControllerCompat.getMediaController(NowPlaying.this).getTransportControls().skipToPrevious();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shufflePlay();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MediaControllerCompat.getMediaController(NowPlaying.this).getTransportControls().seekTo(seekBar.getProgress());
                scheduleSeekbarUpdate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });

    } // oncreate method ends here

    private void makeItFavorite() {
        int f = setFavoriteIcon();
        if (f == 1) {
            favorites.deleteRow(path);
            _favorite.setImageResource(R.drawable.ic_heart);
            new Utilities(this).showShortToast("Song Removed From Favorites");
        } else {

            String __path = sharedPrefs.readSharedPrefsString("path","");
            Song e;

            if (__path.length() == 0){
                String _title = songUtils.queue().get(0).getTitle();
                String _path = songUtils.queue().get(0).getPath();
                String _artist = songUtils.queue().get(0).getArtist();
                String _album = songUtils.queue().get(0).getAlbum();
                String _name = songUtils.queue().get(0).getName();
                String _duration = songUtils.queue().get(0).getDuration();
                String _albumId = songUtils.queue().get(0).getAlbumID();
                e = new Song(_title, _album, _artist, _duration, _path, _name, _albumId);
            }else {
                String _title = sharedPrefs.readSharedPrefsString("title", "");
                String _path = sharedPrefs.readSharedPrefsString("path", "");
                String _artist = sharedPrefs.readSharedPrefsString("artist", "");
                String _album = sharedPrefs.readSharedPrefsString("album", "");
                String _name = sharedPrefs.readSharedPrefsString("name", "");
                String _duration = sharedPrefs.readSharedPrefsString("duration", "");
                String _albumId = sharedPrefs.readSharedPrefsString("albumid", "");
                e = new Song(_title, _album, _artist, _duration, _path, _name, _albumId);
            }

            favorites.addRow(e);
            new Utilities(this).showShortToast("Song Added To The Favorites");
            _favorite.setImageResource(R.drawable.ic__heart);
        }
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        prev = findViewById(R.id.prev);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);
        albumArt = findViewById(R.id.albumArt);
        seekBar = findViewById(R.id.seekbar);
        songName = findViewById(R.id.title);
        _favorite = findViewById(R.id.favorites);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
        startTimer = findViewById(R.id.startTimer);
        endTimer = findViewById(R.id.endTimer);
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        connectToSession(mediaBrowser.getSessionToken());
                        ContextCompat.startForegroundService(NowPlaying.this,
                                new Intent(NowPlaying.this, MusicPlayerServices.class));
                    } catch (RemoteException e) {
                    }
                }
            };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(NowPlaying.this, token);
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }
        MediaControllerCompat.setMediaController(this, mediaController);
        mediaController.registerCallback(controllerCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata);
            updateDuration(metadata);
        }
        updateProgress();
        if (state != null && (state.getState() == PlaybackStateCompat.STATE_PLAYING ||
                state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekbarUpdate();
        }
    }

    MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    updateMediaDescription(metadata);
                    updateDuration(metadata);

                }

                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    updatePlaybackState(state);
                }
            };

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        endTimer.setText(getTimeString(Long.parseLong(String.valueOf(duration))));
        seekBar.setMax(duration);
    }


    private void updatePlaybackState(PlaybackStateCompat state) {

        if (state == null) return;


        mLastPlaybackState = state;

        switch (state.getState()) {
            case STATE_PLAYING:
                scheduleSeekbarUpdate();
                play.setImageDrawable(ContextCompat.getDrawable(NowPlaying.this, R.drawable.ic_pause));
                break;
            case STATE_PAUSED:
                stopSeekbarUpdate();
                play.setImageDrawable(ContextCompat.getDrawable(NowPlaying.this, R.drawable.ic_play));
                break;
            case PlaybackStateCompat.STATE_NONE:
            case PlaybackStateCompat.STATE_STOPPED:
                stopSeekbarUpdate();
                play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_play));
                break;
            case STATE_BUFFERING:
//                stopSeekbarUpdate();
                break;
            default:
//                Log.d(TAG, "Unhandled state " + state.getState());

        }
    }

//    void buildTransportControls() {
//        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(NowPlaying.this);
//        MediaMetadataCompat metadata = mediaController.getMetadata();
//        PlaybackStateCompat pbState = mediaController.getPlaybackState();
//        updateMediaDescription(metadata);
//        updatePlaybackState(pbState);
//        mediaController.registerCallback(controllerCallback);
//    }

    private void updateMediaDescription(MediaMetadataCompat metadata) {

        if (metadata == null) {
            return;
        }
        getSupportActionBar().setTitle(metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
        String _albumName = metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM).toString();

        if (_albumName.length() > 25) songName.setText(_albumName.substring(0, 25));
        else songName.setText(_albumName);

        albumArt.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));

        setFavoriteIcon();

        int fav = setFavoriteIcon();

        if (fav == 1) {
            _favorite.setImageResource(R.drawable.ic__heart);
        } else {
            _favorite.setImageResource(R.drawable.ic_heart);
        }

    }

    private int setFavoriteIcon() {
        path = sharedPrefs.readSharedPrefsString("path", "");
        favSongs = favorites.getFavorites();

        boolean rep = sharedPrefs.readSharedPrefBoolean("repeat", false);

        if (rep) {
            repeat.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }

        int j = 0;

        for (int i = 0; i < favSongs.size(); i++) {
            if (path.equals(favSongs.get(i).getPath())) {
                j = 1;
                return j;
            }
        }
        return j;
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSeekbarUpdate();
        mExecutorService.shutdown();
    }


    private void updateProgress() {

        if (mLastPlaybackState == null) {
            return;
        }

        long currentPosition = mLastPlaybackState.getPosition();
        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }
        seekBar.setProgress((int) currentPosition);
        startTimer.setText(getTimeString(currentPosition));
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void scheduleSeekbarUpdate() {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            long PROGRESS_UPDATE_INTERNAL = 1000;
            long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }


    public void shufflePlay() {

        (new Utilities(NowPlaying.this)).showShortToast("Shuffling the list");
//        int musicID = sharedPrefs.readSharedPrefsInt("musicID", 0);
//        ArrayList<Song> arrayList = new ArrayList<Song>(songUtils.queue());
//
//        if (arrayList.size() <= 1) {
//            (new Utilities(NowPlaying.this)).showShortToast("Nothing to shuffle");
//        } else {
//            Song currentSong = arrayList.get(musicID);
//            arrayList.remove(musicID);
//            Collections.shuffle(arrayList);
//            arrayList.add(0, currentSong);
//            songUtils.replaceQuery(arrayList);
//            sharedPrefs.writeSharedPrefs("musicID", 0);
//            (new Utilities(NowPlaying.this)).showShortToast("Shuffling the list");
//
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mediaBrowser != null) {
            mediaBrowser.connect();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mediaBrowser != null) {
            mediaBrowser.disconnect();
        }
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(controllerCallback);
        }
    }


//    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
//            new MediaBrowserCompat.ConnectionCallback() {
//                @Override
//                public void onConnected() {
//
//                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
//
//
//
//                    MediaControllerCompat mediaController =
//                            null;
//                    try {
//                        mediaController = new MediaControllerCompat(NowPlaying.this, token);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//
//                    MediaControllerCompat.setMediaController(NowPlaying.this, mediaController);
//
//                    buildTransportControls();
//
//                    ContextCompat.startForegroundService(NowPlaying.this, new Intent(NowPlaying.this, MusicPlayerServices.class));
//                }
//
//                @Override
//                public void onConnectionSuspended() {
//                    // The Service has crashed. Disable transport controls until it automatically reconnects
//                }
//
//                @Override
//                public void onConnectionFailed() {
//                    // The Service has refused our connection
//                }
//            };
//
//    MediaControllerCompat.Callback controllerCallback =
//            new MediaControllerCompat.Callback() {
//                @Override
//                public void onMetadataChanged(MediaMetadataCompat metadata) {
//                    updateMediaDescription(metadata);
//                    updateDuration(metadata);
//                }
//
//                @Override
//                public void onPlaybackStateChanged(PlaybackStateCompat state) {
//                    updatePlaybackState(state);
//                }
//            };


//    private void updateDuration(MediaMetadataCompat metadata) {
//        if (metadata == null) {
//            return;
//        }
//        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
//        seekBar.setMax(duration);
//    }


}  // activity ends here
