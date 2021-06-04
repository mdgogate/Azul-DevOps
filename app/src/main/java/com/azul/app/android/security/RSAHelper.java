package com.azul.app.android.security;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import com.azul.app.android.globals.Constants;
import com.azul.app.android.globals.KeyConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSAHelper {

    private static final int TAG_LENGTH_BIT = 128;
    private static final String IV = "010203040506";

    public static String rsaEncrypt(String data) {
        try {
            KeyStore mKeyStore = KeyStore.getInstance(Constants.KEY_STORE);
            mKeyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(Constants.SSC_ALIAS, null);
            // Encrypt the text
            Cipher inputCipher = Cipher.getInstance(KeyConstants.TRANSFORMATION);
            inputCipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());
            byte[] vals = inputCipher.doFinal(data.getBytes());
            return (Base64.encodeToString(vals, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return "";
    }

    public static byte[] decryptAESserverKey(String encryptedData) {

        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(Constants.EM_SG_DATA, null);
            final Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(Base64.decode(encryptedData.getBytes(), 0));
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return null;
    }

    public static String decryptDataUsingDynamicKey(String encryptedText, byte[] dynamicKey) {
        try {

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec skeySpec = new SecretKeySpec(dynamicKey, 0, dynamicKey.length, "AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new GCMParameterSpec(TAG_LENGTH_BIT, IV.getBytes()));
            byte[] decryptedText = cipher.doFinal(Base64.decode(encryptedText, 0));
            return new String(decryptedText);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return null;
    }

    public static String rsaDecrypt(String data) {
        try {
            KeyStore mKeyStore = KeyStore.getInstance(Constants.KEY_STORE);
            mKeyStore.load(null);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) mKeyStore.getEntry(Constants.SSC_ALIAS, null);
            Cipher output = Cipher.getInstance(KeyConstants.TRANSFORMATION);
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
            byte[] data1 = output.doFinal(Base64.decode(data, 0));
            return new String(data1);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return "";
    }

    /**
     * Encrypts parameters
     */
    public static String ecryptRSA(Context context, String message) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA/None/OAEPWithSHA-1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(context));
            return Base64.encodeToString(cipher.doFinal(message.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));
        }
        return null;
    }
    public static String encryptAES(String data,byte[] dynamicKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec skeySpec = new SecretKeySpec(dynamicKey,"AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new GCMParameterSpec(TAG_LENGTH_BIT, IV.getBytes()));
            return Base64.encodeToString(cipher.doFinal(data.getBytes()),Base64.NO_WRAP);
        } catch (Exception e) {
            Log.e(KeyConstants.EXCEPTION_LABEL, Log.getStackTraceString(e));

        }
        return "";
    }
    /**
     * Get Public key
     */
    public static PublicKey getPublicKey(Context context)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.decode(readPublicKeyFromFile(context).getBytes(), Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Log.i("TAG", "Public Key ::: " + keyFactory.generatePublic(spec));
        return keyFactory.generatePublic(spec);
    }

    /**
     * Read public key from File
     */
    private static String readPublicKeyFromFile(Context context) throws IOException {
        AssetManager am = context.getAssets();
        InputStream is = am.open("files/public.pem");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null)
            lines.add(line);

        // removes the first and last lines of the file (comments)
        if (lines.size() > 1 && lines.get(0).startsWith("-----") && lines.get(lines.size() - 1).startsWith("-----")) {
            lines.remove(0);
            lines.remove(lines.size() - 1);
        }
        // concats the remaining lines to a single String
        StringBuilder sb = new StringBuilder();
        for (String aLine : lines)
            sb.append(aLine);
        return sb.toString();
    }
}
