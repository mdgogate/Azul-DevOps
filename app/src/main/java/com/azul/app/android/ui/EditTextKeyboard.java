package com.azul.app.android.ui;

/*
 * @author Siddhatech
 *
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class EditTextKeyboard extends AppCompatEditText {

    private BackPressedListener mOnImeBack;

    public EditTextKeyboard(Context context) {
        super(context);
    }

    public EditTextKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextKeyboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && mOnImeBack != null) {
            mOnImeBack.onImeBack(this);
        }
        return super.dispatchKeyEvent(event);
    }

    @SuppressWarnings("unused")
    public void setBackPressedListener(BackPressedListener listener) {
        mOnImeBack = listener;
    }

    public interface BackPressedListener {
        void onImeBack(EditTextKeyboard editText);
    }
}