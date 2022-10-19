package com.example.exercise1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;

public class Utils {
    @NonNull
    public static byte[] getBytes(@NonNull Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
}
