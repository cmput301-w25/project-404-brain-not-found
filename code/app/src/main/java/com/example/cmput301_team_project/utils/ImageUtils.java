package com.example.cmput301_team_project.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Utility class for image-related operations.
 */
public class ImageUtils {

    /**
     * Decodes a Base64 encoded string into a Bitmap.
     * This method is commonly used to convert encoded image data back into image form for UI display.
     *
     * @param base64Str The Base64 encoded string representing the bitmap data.
     * @return A Bitmap derived from the decoded bytes of the Base64 string, or null if decoding fails.
     */
    public static Bitmap decodeBase64(String base64Str) {
        if(base64Str == null) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64Str, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}

