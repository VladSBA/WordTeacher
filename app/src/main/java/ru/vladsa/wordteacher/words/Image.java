package ru.vladsa.wordteacher.words;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ru.vladsa.wordteacher.MainActivity;

public class Image implements Serializable {
    private final static String LOG_TAG = MainActivity.LOG_TAG + " (Image)";

    private final byte[] byteArray;

    private final int width;
    private final int height;

    private final String name;

    public Image(Bitmap bitmap) {
        Log.d(LOG_TAG, "Creating image...");

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        name = bitmap.getConfig().name();

        int size = bitmap.getRowBytes() * bitmap.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteArray = byteBuffer.array();

        Log.d(LOG_TAG, "Image has been created.");
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public Bitmap getBitmap() {
        Log.d(LOG_TAG, "Creating bitmap...");

        if (byteArray == null) {
            Log.d(LOG_TAG, "Byte array is null.");
            return null;
        }

        Bitmap.Config config = Bitmap.Config.valueOf(name);
        Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, config);
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        bitmap_tmp.copyPixelsFromBuffer(buffer);

        Log.d(LOG_TAG, "Bitmap has been created.");

        return bitmap_tmp;
    }
}
