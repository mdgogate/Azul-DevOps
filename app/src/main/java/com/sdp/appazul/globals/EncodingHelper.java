package com.sdp.appazul.globals;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class EncodingHelper {

    Context context;

    public EncodingHelper(Context context) {
        this.context = context;
    }

    public String encodeSpanish(String encodedString) {
        Log.d("EncodingHelper", "encodeSpanish: " + context.getClass());
        String decodedString = "";
        try {
            String modifiedList = new String(encodedString.getBytes(Constants.UTF_FORMAT), "ISO-8859-1");
            //This if condition is for "n"
            //¿ = \u00BF
            //ñ = \u0148
            //á = \u0227
            //é = \u00E9
            if (modifiedList.contains("ï¿½")) {
                String data = "\u0148";
                byte[] bute = null;
                bute = data.getBytes();
                String asd = new String(bute, Constants.UTF_FORMAT);
                decodedString = modifiedList.replace("ï¿½", asd);
                return decodedString;
            } else {
                return modifiedList;
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("TAG", "encodeSpanish: " + e.getLocalizedMessage());
        }
        return "";
    }


    public static String getEncodedString(String encodedString) {
        return new String(encodedString.getBytes(StandardCharsets.UTF_8));
    }
}
