package com.instagramy.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@Entity
public class DrawableResource implements Serializable {

    // In our impl it's the drawable url hash code.
    @NonNull
    @PrimaryKey
    private int key;

    private Drawable drawable;

    public DrawableResource() {
    }

    public DrawableResource(@NonNull int key, Drawable drawable) {
        this.key = key;
        this.drawable = drawable;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public static class Converters {
        @TypeConverter
        public static byte[] drableToByteArray(Drawable drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        @TypeConverter
        public static Drawable byteArrayToDrable(byte[] bytes) {
            return new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
        }
    }

}
