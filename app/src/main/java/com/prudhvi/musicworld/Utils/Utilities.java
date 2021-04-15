package com.prudhvi.musicworld.Utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.FileDescriptor;

public class Utilities {

    Context ctx;

    public Utilities(Context ctx) {
        this.ctx = ctx;
    }

    public static Bitmap getBitmap(Context context, long albumId) {
        Bitmap albumArtBitMap = null;
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(albumArtUri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                albumArtBitMap = BitmapFactory.decodeFileDescriptor(fd, null, options);
                pfd = null;
                fd = null;
            }
        } catch (Exception e) {
        }
        return albumArtBitMap;
    }

    public void showShortToast(String string) {
        Toast.makeText(ctx, string, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String string) {
        Toast.makeText(ctx, string, Toast.LENGTH_LONG).show();
    }


}
