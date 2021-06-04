package com.azul.app.android.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class RobotoMediumTextView extends androidx.appcompat.widget.AppCompatTextView {
    public RobotoMediumTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RobotoMediumTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoMediumTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Medium.ttf");
        setTypeface(tf);
    }

}
