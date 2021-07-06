package com.sdp.appazul.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {

    private FontUtils() {
    }

    public static Typeface robotoMedium(Context context) {
        Typeface typeface;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        return typeface;
    }
}
