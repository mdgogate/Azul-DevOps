package com.sdp.appazul.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class GothamMedium extends TextView {

    public GothamMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GothamMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GothamMedium(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Gotham-Medium.ttf");
        setTypeface(tf);
    }

}
