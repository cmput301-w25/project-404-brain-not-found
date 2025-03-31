package com.example.cmput301_team_project.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * The MapsUtils class is a utility class for managing Moods locations.
 *
 * Deals with creating the Mood posts emoji marker on the map.
 */
public class MapsUtils {
    /**
     * Creates the Emoji marker for the Mood post to display on the map. Marker consists of the
     * Mood emoji and a triangular pin underneath it.
     *
     * @param emoji The emoji of the Mood post.
     * @param color The color of the Mood post.
     * @param size The size of the Marker.
     * @return A {@link BitmapDescriptor} consisting of the custom marker.
     */
    public static BitmapDescriptor getEmojiMarker(String emoji, int color, int size) {
        int markerWidth = (int) (size * 1.5);
        int markerHeight = size * 2;
        int emojiSize = size;

        Bitmap bitmap = Bitmap.createBitmap(markerWidth, markerHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setTextSize(emojiSize);
        paint.setTextAlign(Paint.Align.CENTER);

        Path path = new Path();
        path.moveTo(markerWidth / 2f, markerHeight);
        path.lineTo(markerWidth / 4f + 10, markerHeight - (float) size / 3);
        path.lineTo(markerWidth * 3 / 4f - 10, markerHeight - (float) size / 3);
        path.close();

        Paint pointerPaint = new Paint();
        pointerPaint.setColor(color);
        pointerPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, pointerPaint);

        Rect bounds = new Rect();
        paint.getTextBounds(emoji, 0, emoji.length(), bounds);
        canvas.drawText(emoji, markerWidth / 2f, (markerHeight - 50) / 2f - bounds.exactCenterY() + 35, paint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
