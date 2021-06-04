package com.azul.app.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.azul.app.android.globals.KeyConstants;

import java.util.Objects;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class KeyboardUtils {
    Context context;

    public KeyboardUtils(Context context) {
        this.context = context;
    }

    /**
     * Hide keyboard on outside click.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void hideKeyboardOnOutsideTouch(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideSoftKeyboard((Activity) context);
                return false;
            });
        }

        // If a layout container, iterate over children and seed recursion.
        hideKeyboard(view);
    }
    // If a layout container, iterate over children and seed recursion.
    private void hideKeyboard(View view){
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                hideKeyboardOnOutsideTouch(innerView);
            }
        }
    }

    /**
     * Hide keyboard on outside click.
     */
    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }

    }
}
