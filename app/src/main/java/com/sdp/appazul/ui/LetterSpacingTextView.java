package com.sdp.appazul.ui;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class LetterSpacingTextView extends androidx.appcompat.widget.AppCompatTextView {

    private float spacing = Spacing.NORMAL;
    private CharSequence originalText = "";

    public LetterSpacingTextView(Context context) {
        super(context);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public LetterSpacingTextView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("unused")
    public float getSpacing() {
        return this.spacing;
    }

    @SuppressWarnings("unused")
    public void setSpacing(float spacing) {
        this.spacing = spacing;
        applySpacing();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        applySpacing();
    }

    @Override
    public CharSequence getText() {
        return originalText;
    }

    private void applySpacing() {
        if (this == null || this.originalText == null) return;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if(i+1 < originalText.length())
                builder.append("\u00A0");
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2)
                finalText.setSpan(new ScaleXSpan((spacing+1)/10), i, i+1, SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }

    public static class Spacing {
        private Spacing() {
        }

        public final static float NORMAL = 0;
    }

}
