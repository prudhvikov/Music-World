package com.prudhvi.musicworld.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.prudhvi.musicworld.Activity.AllSongs;
import com.prudhvi.musicworld.Helpers.MediaStyleHelper;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.SharedPrefs;
import com.prudhvi.musicworld.Utils.SongUtils;
import com.prudhvi.musicworld.Utils.Utilities;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

//public class MusicPlayerServices extends
//        MediaBrowserServiceCompat
//        implements
//        MediaPlayer.OnCompletionListener,
//        AudioManager.OnAudioFocusChangeListener,
//        MediaPlayer.OnPreparedListener {
//
//
//    public static final String ACTION_CLOSE = "com.example.musicworld.action.CLOSE";
//    public static final String ACTION_PLAY = "com.example.musicworld.action.PLAY";
//    public static final String ACTION_PLAY_PAUSE = "com.example.musicworld.action.PLAY_PAUSE";
//    public static final String ACTION_TRACK_PREV = "com.example.musicworld.action.TRACK_PREV";
//    public static final String ACTION_TRACK_NEXT = "com.example.musicworld.action.TRACK_NEXT";
//
//
//    private final String TAG = "PlaybackServiceConsole";
//
//    private MediaPlayer mMediaPlayer;
//    private MediaSessionCompat mMediaSessionCompat;
//    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
//    private SharedPrefs sharedPrefsUtils;
//    private SongUtils songsUtils;
//
//    private MediaPlayer mMediaPlayer2;
//    private int currentMediaPlayer = 0; // 0 - mMediaPlayer; 1 - mMediaPlayer2
//    private int crossfadeDuration = 3000; // 3 seconds
//    private Handler mHandler;
//
//    private final int NOTIFICATION_ID = 34213134;
//
//
//    public MusicPlayerServices() {
//        super();
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        sharedPrefsUtils = new SharedPrefs(this);
//        songsUtils = new SongUtils(this);
//        mHandler = new Handler();
//
//
//        initMediaPlayer();
//        initMediaSession();
//        initNoisyReceiver();
//
//        initNotification();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//
//            MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
//            Log.d(TAG, "Intent received.");
//            String action = intent.getAction();
//            if (action != null) {
//                switch (action) {
//                    case ACTION_PLAY: {
//                        resetMediaPlayerPosition();
//                        processPlayRequest();
//                        break;
//                    }
//                    case ACTION_PLAY_PAUSE: {
//                        resetMediaPlayerPosition();
//                        processPlayPause();
//                        break;
//                    }
//                    case ACTION_TRACK_PREV: {
//                        processPrevRequest();
//                        break;
//                    }
//                    case ACTION_TRACK_NEXT: {
//                        processNextRequest();
//                        break;
//                    }
//                    case ACTION_CLOSE: {
//                        if (!sharedPrefsUtils.readSharedPrefBoolean("persistentNotificationPref", false)) {
//                            processCloseRequest();
//                        } else {
//                            processPauseRequest();
//                        }
//                        break;
//                    }
//                    default: {
//                        initNotification();
//                    }
//                }
//            } else {
//                initNotification();
//            }
//        }
//        return START_STICKY;
//    }
//
//    private MediaPlayer getCurrentMediaPlayer() {
//        return (currentMediaPlayer == 0) ? mMediaPlayer : mMediaPlayer2;
//    }
//
//    private void checkErrorInPrefs() {
//        if (songsUtils.getMusicId() > songsUtils.queue().size() - 1) {
//            songsUtils.setMusicId(0);
//        }
//    }
//
//    private void switchMediaPlayer() {
//        if (currentMediaPlayer == 0) {
//            currentMediaPlayer++; // output 1
//        } else if (currentMediaPlayer == 1) {
//            currentMediaPlayer--; // output 0
//        } else {
//            currentMediaPlayer = 0;
//        }
//    }
//
//    private void crossfade() {
//        mHandler.post(detectCrossFadeRunnable);
//    }
//
//    private void cancelCrossfade() {
//        mHandler.removeCallbacks(detectCrossFadeRunnable);
//    }
//
//    // Detects and starts cross-fading
//    public Runnable detectCrossFadeRunnable = new Runnable() {
//        @Override
//        public void run() {
//            //Check if we're in the last part of the current song.
//            try {
//                if (getCurrentMediaPlayer().isPlaying()) {
//                    if (getCurrentMediaPlayer().getCurrentPosition() >=
//                            (getCurrentMediaPlayer().getDuration() - crossfadeDuration)) {
//                        // Start cross-fading the songs
//                        switchMediaPlayer();
//                        mHandler.postDelayed(crossFadeRunnable, 100);
//                    } else {
//                        mHandler.postDelayed(detectCrossFadeRunnable, 1000);
//                    }
//                } else {
//                    mHandler.postDelayed(detectCrossFadeRunnable, 1000);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    // Volume
//    private float mFadeOutVolume = 1.0f;
//    private float mFadeInVolume = 0.0f;
//
//    public Runnable crossFadeRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            try {
//                int crossfadeStep = 10000;
//
//                if (currentMediaPlayer == 0) {
//
//                    mMediaPlayer.setVolume(mFadeInVolume, mFadeInVolume);
//                    mMediaPlayer2.setVolume(mFadeOutVolume, mFadeOutVolume);
//
//                    mMediaPlayer.seekTo(crossfadeStep);
//                    mMediaPlayer.start();
//                } else {
//
//                    mMediaPlayer2.setVolume(mFadeInVolume, mFadeInVolume);
//                    mMediaPlayer.setVolume(mFadeOutVolume, mFadeOutVolume);
//
//                    mMediaPlayer2.seekTo(crossfadeStep);
//                    mMediaPlayer2.start();
//                }
//
//                mFadeInVolume = mFadeInVolume + (1.0f / (((float) crossfadeDuration / 1000) * 10.0f));
//                mFadeOutVolume = mFadeOutVolume - (1.0f / (((float) crossfadeDuration / 1000) * 10.0f));
//
//                mHandler.postDelayed(crossFadeRunnable, 100);
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    };
//
//
//    private void saveData() {
//        int musicID = songsUtils.getMusicId();
//        sharedPrefsUtils.writeSharedPrefs("audio_session_id", getCurrentMediaPlayer().getAudioSessionId());
//        try {
//            sharedPrefsUtils.writeSharedPrefs("title", songsUtils.queue().get(musicID).getTitle());
//            sharedPrefsUtils.writeSharedPrefs("artist", songsUtils.queue().get(musicID).getArtist());
//            sharedPrefsUtils.writeSharedPrefs("album", songsUtils.queue().get(musicID).getAlbum());
//            sharedPrefsUtils.writeSharedPrefs("albumid", songsUtils.queue().get(musicID).getAlbumID());
//            sharedPrefsUtils.writeSharedPrefs("path", songsUtils.queue().get(musicID).getPath());
//            sharedPrefsUtils.writeSharedPrefs("name", songsUtils.queue().get(musicID).getName());
//            sharedPrefsUtils.writeSharedPrefs("duration", songsUtils.queue().get(musicID).getDuration());
//            sharedPrefsUtils.writeSharedPrefs("durationInMS", getCurrentMediaPlayer().getDuration());
//        } catch (Exception e) {
//            Log.d(TAG, "Unable to save song info in persistent storage. MusicID " + musicID);
//        }
//    }
//
//
//    private void processNextRequest() {
//        resetMediaPlayerPosition();
//
//        int musicID = songsUtils.getMusicId();
//
//        if (musicID + 1 != songsUtils.queue().size()) {
//            musicID++;
//        } else {
//            musicID = 0;
//        }
//        songsUtils.setMusicId(musicID);
//
//        if (successfullyRetrievedAudioFocus()) {
//            showPausedNotification();
//            return;
//        }
//
//        setMediaPlayer(songsUtils.queue().get(musicID).getPath());
//    }
//
//    private void processPrevRequest() {
//        resetMediaPlayerPosition();
//
//        if (getCurrentMediaPlayer().getCurrentPosition() < 5000) {
//            int musicID = songsUtils.getMusicId();
//            if (musicID > 0) {
//                musicID--;
//                songsUtils.setMusicId(musicID);
//                if (successfullyRetrievedAudioFocus()) {
//                    showPausedNotification();
//                    return;
//                }
//                setMediaPlayer(songsUtils.queue().get(musicID).getPath());
//            } else {
//                mMediaSessionCompat.getController().getTransportControls().seekTo(0);
//            }
//        } else {
//            mMediaSessionCompat.getController().getTransportControls().seekTo(0);
//        }
//    }
//
//    private void processCloseRequest() {
//        stopSelf();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        sharedPrefsUtils.writeSharedPrefs("positon", getCurrentMediaPlayer().getCurrentPosition());
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        assert audioManager != null;
//        audioManager.abandonAudioFocus(this);
//        unregisterReceiver(mNoisyReceiver);
//        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
//        mMediaSessionCompat.setActive(false);
//        mMediaSessionCompat.release();
//        getCurrentMediaPlayer().release();
//        stopForeground(true);
//        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
//    }
//
//    private void processPauseRequest() {
//        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING) {
//            mMediaSessionCompat.setActive(false);
//            getCurrentMediaPlayer().pause();
//            sharedPrefsUtils.writeSharedPrefs("position", getCurrentMediaPlayer().getCurrentPosition());
//            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
//            showPausedNotification();
//        }
//    }
//
//    private void processPlayRequest() {
//        if (successfullyRetrievedAudioFocus()) {
//            showPausedNotification();
//            return;
//        }
//
//        setMediaPlayer(songsUtils.queue().get(songsUtils.getMusicId()).getPath());
//    }
//
//    private void processPlayPause() {
//        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING) {
//            processPauseRequest();
//        } else if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PAUSED) {
//            if (successfullyRetrievedAudioFocus()) {
//                return;
//            }
//            mMediaSessionCompat.setActive(true);
//            getCurrentMediaPlayer().start();
//            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
//            showPlayingNotification();
//            autoPaused = false;
//        } else {
//            processPlayRequest();
//        }
//
//    }
//
//    private void setMetaData() {
//        int musicID = songsUtils.getMusicId();
//        MediaMetadataCompat mMediaMetadataCompat = new MediaMetadataCompat.Builder()
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songsUtils.queue().get(musicID).getTitle())
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songsUtils.queue().get(musicID).getAlbum())
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songsUtils.queue().get(musicID).getArtist())
//                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long) sharedPrefsUtils.readSharedPrefsInt("durationInMS", 0))
//                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, grabAlbumArt(songsUtils.queue().get(musicID).getAlbumID()))
//                .build();
//        mMediaSessionCompat.setMetadata(mMediaMetadataCompat);
//    }
//
//    private void setGraphics() {
//        saveData();
//        setMetaData();
//    }
//
//    private Bitmap grabAlbumArt(String albumID) {
//        Bitmap art = null;
//        try {
//            try {
//                final Uri sArtworkUri = Uri
//                        .parse("content://media/external/audio/albumart");
//
//                Uri uri = ContentUris.withAppendedId(sArtworkUri,
//                        Long.parseLong(albumID));
//
//                ParcelFileDescriptor pfd = getApplicationContext().getContentResolver()
//                        .openFileDescriptor(uri, "r");
//
//                if (pfd != null) {
//                    FileDescriptor fd = pfd.getFileDescriptor();
//                    art = BitmapFactory.decodeFileDescriptor(fd);
//                }
//            } catch (Exception ignored) {
//            }
//            if (art == null) {
//                return drawableToBitmap(ContextCompat.getDrawable(this, R.drawable.musicart));
//            } else {
//                return art;
//            }
//        } catch (Exception e) {
//            return drawableToBitmap(ContextCompat.getDrawable(MusicPlayerServices.this,
//                    R.drawable.musicart));
//        }
//    }
//
//    public Bitmap drawableToBitmap(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        }
//
//        int width = drawable.getIntrinsicWidth();
//        width = width > 0 ? width : 300;
//        int height = drawable.getIntrinsicHeight();
//        height = height > 0 ? height : 300;
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas);
//
//        return bitmap;
//    }
//
//
//    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
//        @Override
//        public void onPlayFromUri(Uri uri, Bundle extras) {
//            super.onPlayFromUri(uri, extras);
//        }
//
//        @Override
//        public void onPlay() {
//            super.onPlay();
//            processPlayPause();
//        }
//
//        @Override
//        public void onPause() {
//            super.onPause();
//            processPauseRequest();
//        }
//
//        @Override
//        public void onSkipToNext() {
//            super.onSkipToNext();
//            processNextRequest();
//        }
//
//        @Override
//        public void onSkipToPrevious() {
//            super.onSkipToPrevious();
//            processPrevRequest();
//        }
//
//        @Override
//        public void onRewind() {
//            super.onRewind();
//        }
//
//        @Override
//        public void onStop() {
//            super.onStop();
//            stopSelf();
//        }
//
//        @Override
//        public void onSetRepeatMode(int repeatMode) {
//            super.onSetRepeatMode(repeatMode);
//        }
//
//
//        @Override
//        public void onSeekTo(long pos) {
//            super.onSeekTo(pos);
//            getCurrentMediaPlayer().seekTo((int) (long) pos);
//            Log.d(TAG, "onSeekTo: " + pos);
//            setMediaPlaybackState(mPlaybackStateBuilder.build().getState());
//        }
//    };
//
//    private void setMediaPlaybackState(int state) {
//        mPlaybackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY
//                | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
//                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_STOP
//                | PlaybackStateCompat.ACTION_SEEK_TO);
//
//        mPlaybackStateBuilder.setState(state, getCurrentMediaPlayer().getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
//
//        mMediaSessionCompat.setPlaybackState(mPlaybackStateBuilder.build());
//    }
//
//    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (getCurrentMediaPlayer() != null && getCurrentMediaPlayer().isPlaying()) {
//                processPauseRequest();
//            }
//        }
//    };
//
//
//    private void createChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager
//                    mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//            String id = "channel_music_playback";
//            CharSequence name = "Media Playback";
//            String description = "Media playback controls";
//            int importance = NotificationManager.IMPORTANCE_LOW;
//            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//            // Configure the notification channel.
//            mChannel.setDescription(description);
//            mChannel.setShowBadge(false);
//            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            assert mNotificationManager != null;
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//    }
//
//    private void showPlayingNotification() {
//
//        createChannel();
//        NotificationCompat.Builder builder
//                = MediaStyleHelper.from(MusicPlayerServices.this, mMediaSessionCompat);
//
//        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_CLOSE), 0);
//
//        PendingIntent prevIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_PREV), 0);
//        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_PLAY_PAUSE), 0);
//        PendingIntent nextIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_NEXT), 0);
//
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_prev, "Previous", prevIntent));
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause, "Play", playPauseIntent));
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent));
//        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
//        builder.setSmallIcon(R.drawable.ic_music_notes_padded);
//        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
//        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AllSongs.class), 0));
//        builder.setDeleteIntent(pCloseIntent);
//        builder.setShowWhen(false);
//        startForeground(NOTIFICATION_ID, builder.build());
//    }
//
//    private void showPausedNotification() {
//
//        createChannel();
//        NotificationCompat.Builder builder
//                = MediaStyleHelper.from(MusicPlayerServices.this, mMediaSessionCompat);
//
//        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_CLOSE), 0);
//
//        PendingIntent prevIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_PREV), 0);
//        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_PLAY_PAUSE), 0);
//        PendingIntent nextIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_NEXT), 0);
//
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_prev, "Previous", prevIntent));
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play", playPauseIntent));
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent));
//        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
//        builder.setSmallIcon(R.drawable.ic_music_notes_padded);
//        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
//        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AllSongs.class), 0));
//        builder.setDeleteIntent(pCloseIntent);
//        builder.setShowWhen(false);
//        startForeground(NOTIFICATION_ID, builder.build());
//        if (!sharedPrefsUtils.readSharedPrefBoolean("persistentNotificationPref", false)) {
//            stopForeground(false);
//        }
//    }
//
//    private void initNotification() {
//        createChannel();
//        NotificationCompat.Builder builder
//                = MediaStyleHelper.from(MusicPlayerServices.this, mMediaSessionCompat);
//
//        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_CLOSE), 0);
//
//        PendingIntent prevIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_PREV), 0);
//        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_PLAY_PAUSE), 0);
//        PendingIntent nextIntent = PendingIntent.getService(this, 0,
//                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_NEXT), 0);
//
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_prev, "Previous", prevIntent));
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play", playPauseIntent));
//        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent));
//        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
//        builder.setSmallIcon(R.drawable.ic_music_notes_padded);
//        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
//        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AllSongs.class), 0));
//        builder.setDeleteIntent(pCloseIntent);
//        builder.setShowWhen(false);
//        startForeground(NOTIFICATION_ID, builder.build());
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        getCurrentMediaPlayer().reset();
//        resetMediaPlayerPosition();
//        if (!sharedPrefsUtils.readSharedPrefBoolean("repeat", false)) {
//            processNextRequest();
//        } else {
//            setMediaPlayer((songsUtils.queue().get(songsUtils.getMusicId()).getPath()));
//        }
//    }
//
//    void resetMediaPlayerPosition() {
//        sharedPrefsUtils.writeSharedPrefs("position", 0);
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
//
////        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_NONE &&
////                sharedPrefsUtils.readSharedPrefsString("raw_path", "").equals(songsUtils.queue().get(songsUtils.getCurrentMusicID()).getPath())) {
////            mMediaSessionCompat.getController().getTransportControls().seekTo(sharedPrefsUtils.readSharedPrefsInt("song_position", 0));
////        }
//
//
//        setGraphics();
//
//        mMediaSessionCompat.setActive(true);
//        autoPaused = false;
//        getCurrentMediaPlayer().start();
//        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
//        showPlayingNotification();
//    }
//
//    private void initMediaPlayer() {
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer.setVolume(1.0f, 1.0f);
//        mMediaPlayer.setOnCompletionListener(this);
//        mMediaPlayer.setOnPreparedListener(this);
//        mMediaPlayer2 = new MediaPlayer();
//        mMediaPlayer2.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
//        mMediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mMediaPlayer2.setVolume(1.0f, 1.0f);
//        mMediaPlayer2.setOnCompletionListener(this);
//        mMediaPlayer2.setOnPreparedListener(this);
//        mMediaPlayer2.setAudioSessionId(mMediaPlayer.getAudioSessionId());
//        sharedPrefsUtils.writeSharedPrefs("audio_session_id", mMediaPlayer.getAudioSessionId());
//
//    }
//
//    private void setMediaPlayer(String path) {
//        getCurrentMediaPlayer().reset();
//        File file = new File(path);
//        if (file.exists()) {
//            try {
//                getCurrentMediaPlayer().setDataSource(path);
//            } catch (IOException e) {
//                processNextRequest();
//                e.printStackTrace();
//            }
//
//            try {
//                getCurrentMediaPlayer().prepare();
//            } catch (IOException ignored) {
//            }
//        } else {
//            processNextRequest();
////            (new Utilities(this)).showShortToast("Error finding music file");
//        }
//    }
//
//    private void initMediaSession() {
//        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
//        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);
//
//        mMediaSessionCompat.setCallback(mMediaSessionCallback);
//        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
//
//        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
//        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
//        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);
//
//        mPlaybackStateBuilder = new PlaybackStateCompat.Builder();
//
//        setSessionToken(mMediaSessionCompat.getSessionToken());
//
//        setMetaData();
//    }
//
//    private void initNoisyReceiver() {
//        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//        registerReceiver(mNoisyReceiver, filter);
//    }
//
//    boolean autoPaused = false;
//
//    @Override
//    public void onAudioFocusChange(int focusChange) {
//        switch (focusChange) {
//            case AudioManager.AUDIOFOCUS_LOSS: {
//                if (getCurrentMediaPlayer().isPlaying()) {
//                    processPauseRequest();
//                    autoPaused = true;
//                }
//                break;
//            }
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
//                if (getCurrentMediaPlayer().isPlaying()) {
//                    processPauseRequest();
//                    autoPaused = true;
//                }
//                break;
//            }
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
//                if (getCurrentMediaPlayer() != null) {
//                    getCurrentMediaPlayer().setVolume(0.3f, 0.3f);
//                }
//                break;
//            }
//            case AudioManager.AUDIOFOCUS_GAIN: {
//                if (getCurrentMediaPlayer() != null) {
//                    if (!getCurrentMediaPlayer().isPlaying() && autoPaused) {
//                        processPlayPause();
//                    }
//                    getCurrentMediaPlayer().setVolume(1.0f, 1.0f);
//                }
//                break;
//            }
//        }
//    }
//
//    private boolean successfullyRetrievedAudioFocus() {
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        assert audioManager != null;
//        int result = audioManager.requestAudioFocus(this,
//                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//
//        if (result != AudioManager.AUDIOFOCUS_GAIN) {
//        }
//
//        return result != AudioManager.AUDIOFOCUS_GAIN;
//    }
//
//
//    @Nullable
//    @Override
//    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
//        if (TextUtils.equals(clientPackageName, getPackageName())) {
//            return new BrowserRoot(getString(R.string.app_name), null);
//        }
//
//        return null;
//    }
//
//    //Not important for general audio service, required for class
//    @Override
//    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
//        result.sendResult(null);
//    }
//}



/** original code **/

public class MusicPlayerServices extends MediaBrowserServiceCompat
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener{

    private SongUtils songUtils;
    public MediaPlayer mediaPlayer;
    private final int NOTIFICATION_ID = 34213134;
    private static final String LOG_TAG = "MUSIC PLAYER SERVICE";
    int musicID = 0;
    Bitmap albumArt;
    Notification notification;
    SharedPrefs sharedPrefs;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    public static final String ACTION_CLOSE = "com.example.musicworld.action.CLOSE";
    public static final String ACTION_PLAY = "com.example.musicworld.action.PLAY";
    public static final String ACTION_PLAY_PAUSE = "com.example.musicworld.action.PLAY_PAUSE";
    public static final String ACTION_TRACK_PREV = "com.example.musicworld.action.TRACK_PREV";
    public static final String ACTION_TRACK_NEXT = "com.example.musicworld.action.TRACK_NEXT";

    @Override
    public void onCreate() {
        super.onCreate();
        songUtils = new SongUtils(getApplicationContext());
        sharedPrefs  = new SharedPrefs(getApplicationContext());
        initMediaPlayer();
        intiMediaSession();
        initNotification();
        noisyReceiver();
    }

    private void intiMediaSession() {

        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);

        mediaSession.setCallback(callback);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mediaSession.setMediaButtonReceiver(pendingIntent);

        stateBuilder = new PlaybackStateCompat.Builder();

        setSessionToken(mediaSession.getSessionToken());

        setMetaData();
    }

    private void setMetaData() {
        int id = songUtils.getMusicId();
        albumArt = Utilities.getBitmap(getApplicationContext(),Long.parseLong(songUtils.queue().get(id).getAlbumID()));
        if (albumArt == null) albumArt = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.musicart);
        MediaMetadataCompat mMediaMetadataCompat = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songUtils.queue().get(id).getTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songUtils.queue().get(id).getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songUtils.queue().get(id).getArtist())
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, (long) sharedPrefs.readSharedPrefsInt("durationInMS", 0))
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .build();
        mediaSession.setMetadata(mMediaMetadataCompat);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){

            MediaButtonReceiver.handleIntent(mediaSession, intent);
            String action = intent.getAction();
            if (action != null){
                switch (action){
                    case ACTION_PLAY:
                        setMediaPlayerPosition();
                        playSong();
                        break;
                    case ACTION_PLAY_PAUSE:
                        setMediaPlayerPosition();
                        playPause();
                        break;
                    case ACTION_CLOSE:
                        closeNotification();
                        break;
                    case ACTION_TRACK_NEXT:
                        nextSong();
                        break;
                    case ACTION_TRACK_PREV:
                        prevSong();
                        break;
                    default:
                        initNotification();
                }
            }else{
                initNotification();
            }
        }

        return START_STICKY;
    }

    private void setMediaPlayerPosition() {
        sharedPrefs.writeSharedPrefs("position",0);
    }

    private void prevSong() {

        if (musicID > 0){
            musicID -= 1;
        }else{
            musicID = 0;
        }

        if (successfullyRetrievedAudioFocus()) {
            showPauseNotification();
            return;
        }
        songUtils.setMusicId(musicID);
        setMediaPlayer();
    }

    private void nextSong() {

        if (musicID + 1 != songUtils.queue().size()) {
            musicID+= 1;
        } else {
            musicID = 0;
        }

        if (successfullyRetrievedAudioFocus()) {
            showPauseNotification();
            return;
        }
        songUtils.setMusicId(musicID);
        setMediaPlayer();

    }

    private void pauseSong() {
        if (stateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING) {
            mediaSession.setActive(false);
            mediaPlayer.pause();
            sharedPrefs.writeSharedPrefs("position", mediaPlayer.getCurrentPosition());
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showPauseNotification();
        }
    }

    private void setMediaPlayer() {
        try {
            int id = songUtils.getMusicId();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songUtils.queue().get(id).getPath());
            mediaPlayer.prepare();
        }catch (Exception e){

        }
    }


    private void playPause() {

        if (stateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING){
            stopMusic();
        }else if(stateBuilder.build().getState() == PlaybackStateCompat.STATE_PAUSED) {
            if (successfullyRetrievedAudioFocus()) {
                return;
            }
            mediaSession.setActive(true);
            mediaPlayer.start();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification();
        }else{
            playSong();
        }

    }

    private void stopMusic() {
        mediaSession.setActive(false);
        mediaPlayer.pause();
        sharedPrefs.writeSharedPrefs("position", mediaPlayer.getCurrentPosition());
        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        showPauseNotification();
    }

    private void playSong() {

        if (successfullyRetrievedAudioFocus()) {
            showPauseNotification();
            return;
        }
        setMediaPlayer();
    }

    private void showPauseNotification() {
        createChannel();

        NotificationCompat.Builder builder
                = MediaStyleHelper.from(MusicPlayerServices.this, mediaSession);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_CLOSE), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_PREV), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_PLAY_PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.ic_prev, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AllSongs.class), 0));
//        builder.setDeleteIntent(pCloseIntent);
        builder.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_STOP));
        builder.setShowWhen(false);
        startForeground(NOTIFICATION_ID, builder.build());
        if (!sharedPrefs.readSharedPrefBoolean("persistentNotificationPref", false)) {
            stopForeground(false);
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }



    private void initNotification() {
        createChannel();
        NotificationCompat.Builder builder
                = MediaStyleHelper.from(MusicPlayerServices.this, mediaSession);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_CLOSE), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_PREV), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_PLAY_PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.ic_prev, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AllSongs.class), 0));
//        builder.setDeleteIntent(pCloseIntent);
        builder.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_STOP));
        builder.setShowWhen(false);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager
                    mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "channel_music_playback";
            CharSequence name = "Media Playback";
            String description = "Media playback controls";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.setShowBadge(false);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }



    @Override
    public void onPrepared(MediaPlayer mp) {

        if (stateBuilder.build().getState() == PlaybackStateCompat.STATE_NONE &&
                sharedPrefs.readSharedPrefsString("path", "").equals(songUtils.queue().get(songUtils.getMusicId()).getPath())) {
            mediaSession.getController().getTransportControls().seekTo(sharedPrefs.readSharedPrefsInt("position", 0));
        }

        saveData();
        setMetaData();
        mediaSession.setActive(true);

        mediaPlayer.start();
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
        showPlayingNotification();
    }


    private void closeNotification() {
            stopSelf();
    }



    private void showPlayingNotification() {
        createChannel();
        NotificationCompat.Builder builder
                = MediaStyleHelper.from(MusicPlayerServices.this, mediaSession);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_CLOSE), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_PREV), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_PLAY_PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayerServices.class)).setAction(MusicPlayerServices.ACTION_TRACK_NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.ic_prev, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mediaSession.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, AllSongs.class), 0));
//        builder.setDeleteIntent(pCloseIntent);
        builder.setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_STOP));
        builder.setShowWhen(false);
        startForeground(NOTIFICATION_ID, builder.build());

    }


    MediaSessionCompat.Callback callback = new
            MediaSessionCompat.Callback() {
                @Override
                public void onPlay() {
                    playPause();
                }

                @Override
                public void onStop() {
                    super.onStop();
                    closeNotification();
                }

                @Override
                public void onPause() {

                }

                @Override
                public void onSkipToNext() {
                    super.onSkipToNext();
                    nextSong();
                }

                @Override
                public void onSkipToPrevious() {
                    super.onSkipToPrevious();
                    prevSong();
                }

                @Override
                public void onSeekTo(long pos) {
                    super.onSeekTo(pos);
                    mediaPlayer.seekTo((int) (long) pos);
                    setMediaPlaybackState(stateBuilder.build().getState());
                }



            };





    private void setMediaPlaybackState(int state) {
        stateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS | PlaybackStateCompat.ACTION_STOP
                | PlaybackStateCompat.ACTION_SEEK_TO);


        stateBuilder.setState(state, mediaPlayer.getCurrentPosition(), 1.0f, SystemClock.elapsedRealtime());
        mediaSession.setPlaybackState(stateBuilder.build());
    }

    private void saveData() {
        int musicID = songUtils.getMusicId();
//        sharedPrefsUtils.writeSharedPrefs("audio_session_id", getCurrentMediaPlayer().getAudioSessionId());
        try {
            sharedPrefs.writeSharedPrefs("title", songUtils.queue().get(musicID).getTitle());
            sharedPrefs.writeSharedPrefs("artist", songUtils.queue().get(musicID).getArtist());
            sharedPrefs.writeSharedPrefs("album", songUtils.queue().get(musicID).getAlbum());
            sharedPrefs.writeSharedPrefs("albumid", songUtils.queue().get(musicID).getAlbumID());
            sharedPrefs.writeSharedPrefs("path", songUtils.queue().get(musicID).getPath());
            sharedPrefs.writeSharedPrefs("name", songUtils.queue().get(musicID).getName());
            sharedPrefs.writeSharedPrefs("duration", songUtils.queue().get(musicID).getDuration());
            sharedPrefs.writeSharedPrefs("durationInMS", mediaPlayer.getDuration());
        } catch (Exception e) {
//            Log.d(TAG, "Unable to save song info in persistent storage. MusicID " + musicID);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPrefs.writeSharedPrefs("position", mediaPlayer.getCurrentPosition());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(_noisyReceiver);
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
        mediaSession.setActive(false);
        mediaSession.release();
        mediaPlayer.release();
        stopForeground(true);
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.reset();
        setMediaPlayerPosition();
        boolean re = sharedPrefs.readSharedPrefBoolean("repeat",false);

        if (re){
            setMediaPlayer();
        }else{
            nextSong();
        }

    }

    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        assert audioManager != null;
        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result != AudioManager.AUDIOFOCUS_GAIN;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if (mediaPlayer.isPlaying()) {
                    pauseSong();
//                    autoPaused = true;
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                if (mediaPlayer.isPlaying()) {
                    pauseSong();
//                    autoPaused = true;
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        playPause();
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    private BroadcastReceiver _noisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                pauseSong();
            }
        }
    };

    private void noisyReceiver() {
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(_noisyReceiver, filter);
    }

} // player service ending...
