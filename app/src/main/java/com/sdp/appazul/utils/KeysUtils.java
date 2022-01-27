package com.sdp.appazul.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import com.sdp.appazul.globals.Constants;
import com.sdp.appazul.globals.KeyConstants;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Calendar;
import javax.security.auth.x500.X500Principal;

public class KeysUtils {

    private KeysUtils() {
    }

    public static void createNewKeys(String alias, Context context) {
        try {
            KeyStore mKeyStore = KeyStore.getInstance(Constants.KEY_STORE);
            mKeyStore.load(null);
            if (!mKeyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance(Constants.RSA_ALGO, Constants.KEY_STORE);
                generator.initialize(spec);
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            Log.e("Exception : ", Log.getStackTraceString(e));
        }
    }


    public static String getRandomAlphanumDigitKey(int capacity) {
        StringBuilder sb = new StringBuilder(capacity);
        String randomKey = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < 32; i++)
            sb.append(randomKey.charAt(rnd.nextInt(randomKey.length())));
        return sb.toString();
    }


    public static String getEMSgData(Context context) {
        String emString = "";
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(Constants.EM_SG_DATA)
                    .setSubject(new X500Principal("CN=BPD, O=BPD Authority"))
                    .setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            generator.initialize(spec);
            generator.generateKeyPair();
            PublicKey publicKey = keyStore.getCertificate(Constants.EM_SG_DATA).getPublicKey();
            emString = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        } catch (Exception ex) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(ex));
        }
        return emString;
    }

}
