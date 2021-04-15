package com.prudhvi.musicworld.Fragments;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.prudhvi.musicworld.Activity.NowPlaying;
import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Services.MusicPlayerServices;
import com.prudhvi.musicworld.Utils.SongUtils;

import static android.support.v4.media.session.PlaybackStateCompat.STATE_NONE;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING;
import static android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED;

public class MusicDockFragment extends Fragment {


    TextView title,albumName;
    ImageView albumArt,play;
    SeekBar seekBar;
    SongUtils songUtils;
    ConstraintLayout nextActivity;
    PlaybackStateCompat mLastPlaybackState = null;
    Drawable d;

    //    private final Runnable mUpdateProgressTask = new Runnable() {
//        @Override
//        public void run() {
//            updateProgress();
//        }
//    };
//
//    private final ScheduledExecutorService mExecutorService =
//            Executors.newSingleThreadScheduledExecutor();
//    private ScheduledFuture<?> mScheduleFuture;
//    private final Handler mHandler = new Handler();
    private MediaBrowserCompat mediaBrowser;
    MediaControllerCompat mediaController = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_music_dock, container, false);
        songUtils = new SongUtils(getActivity());
        title  = v.findViewById(R.id.title);
        albumName  = v.findViewById(R.id.albumName);
        albumArt  = v.findViewById(R.id.albumArt);
        play  = v.findViewById(R.id.play);
        nextActivity = v.findViewById(R.id.constraintLayout);

        nextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   startActivity(new Intent(getActivity(), NowPlaying.class));
            }
        });
//        seekBar = v.findViewById(R.id.seekBar);

        mediaBrowser = new MediaBrowserCompat(getActivity(), new ComponentName(getActivity(), MusicPlayerServices.class), connectionCallbacks, null); // optional Bundle

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   MediaControllerCompat.getMediaController(getActivity()).getTransportControls().play();

            }
        });

//        getActivity().runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                if(mLastPlaybackState != null){
//                    ();
//                }
//                mHandler.postDelayed(this, 1000);
//            }
//        });


        return v;
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
        if (MediaControllerCompat.getMediaController(getActivity()) != null) {
            MediaControllerCompat.getMediaController(getActivity()).unregisterCallback(controllerCallback);
        }

    }


    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        connectToSession(mediaBrowser.getSessionToken());
                        ContextCompat.startForegroundService(getActivity(), new Intent(getActivity(),MusicPlayerServices.class));
                    } catch (RemoteException e) {
                    }
                }
            };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                getActivity(), token);

        if (mediaController.getMetadata() == null) {
            return;
        }

        MediaControllerCompat.setMediaController(getActivity(), mediaController);
        mediaController.registerCallback(controllerCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        updateMediaDescription(mediaController.getMetadata());
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata);
        }

    }


//    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
//            new MediaBrowserCompat.ConnectionCallback() {
//                @Override
//                public void onConnected() {
//
//                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();
//
//                    MediaControllerCompat mediaController =
//                            null;
//                    try {
//                        mediaController = new MediaControllerCompat(getActivity(), token);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//
//                    MediaControllerCompat.setMediaController(getActivity(), mediaController);
//
//                    buildTransportControls();
//
//                    ContextCompat.startForegroundService(getActivity(), new Intent(getActivity(),MusicPlayerServices.class));
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

//    MediaControllerCompat.Callback controllerCallback =
//            new MediaControllerCompat.Callback() {
//                @Override
//                public void onMetadataChanged(MediaMetadataCompat metadata) {
//                    updateMediaDescription(metadata);
////                    updateDuration(metadata);
//                }
//                @Override
//                public void onPlaybackStateChanged(PlaybackStateCompat state) {
//                    updatePlaybackState(state);
//                }
//            };

    private final MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata);
            }
        }
    };

    private void updateMediaDescription(MediaMetadataCompat metadata) {
        String _title = metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE).toString();
        String _albumName = metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM).toString();

        if (_title.length() > 20){
            _title = _title.substring(0,20);
        }

        if (_albumName.length() > 20){
            _albumName = _albumName.substring(0,20);
        }

        title.setText(_title);
        albumName.setText(_albumName);

//        title.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE).toString().substring(0,20));
//        albumName.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM).toString().substring(0,20));
        d = new BitmapDrawable(getResources(), metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
        albumArt.setImageDrawable(d);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {

        if (state == null) return;

        mLastPlaybackState = state;

        switch (state.getState()) {
            case STATE_PLAYING:
//                scheduleSeekbarUpdate();
                play.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_pause));
                break;
            case STATE_PAUSED:
//                stopSeekbarUpdate();
                play.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play));
                break;
            case STATE_NONE:
                play.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play));
                break;
            case STATE_STOPPED:
//                stopSeekbarUpdate();
                play.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_play));
                break;
            default:
//                Log.d(TAG, "Unhandled state " + state.getState());

        }
    }

//    void buildTransportControls()
//    {
//            MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
//            MediaMetadataCompat metadata = mediaController.getMetadata();
//            PlaybackStateCompat pbState = mediaController.getPlaybackState();
//            updateMediaDescription(metadata);
//            updatePlaybackState(pbState);
//            mediaController.registerCallback(controllerCallback);
//    }

//    private void updateProgress() {
//        if (mLastPlaybackState == null) return;
//        long currentPosition = mLastPlaybackState.getPosition();
//        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
//            long timeDelta = SystemClock.elapsedRealtime() -
//                    mLastPlaybackState.getLastPositionUpdateTime();
//            currentPosition += (long) timeDelta * mLastPlaybackState.getPlaybackSpeed();
//        }
//        seekBar.setProgress((int) currentPosition);
//    }
//
//    private void updateDuration(MediaMetadataCompat metadata) {
//        if (metadata == null) {
//            return;
//        }
//        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
//        seekBar.setMax(duration);
//    }
//
//    private void scheduleSeekbarUpdate() {
//        stopSeekbarUpdate();
//        if (!mExecutorService.isShutdown()) {
//            long PROGRESS_UPDATE_INTERNAL = 1000;
//            long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
//            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            mHandler.post(mUpdateProgressTask);
//                        }
//                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
//                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
//        }
//    }
//
//    private void stopSeekbarUpdate() {
//        if (mScheduleFuture != null) {
//            mScheduleFuture.cancel(false);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopSeekbarUpdate();
//        mExecutorService.shutdown();
    }
} // fragment ends here

