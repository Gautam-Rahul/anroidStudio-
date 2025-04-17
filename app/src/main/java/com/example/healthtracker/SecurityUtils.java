package com.example.healthtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecurityUtils {
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "HealthTrackerKey";
    private static final String SHARED_PREFS_NAME = "EncryptedPrefs";

    public static void encryptAndSaveData(Context context, String key, String value) {
        try {
            String encryptedValue = encrypt(value);
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(key, encryptedValue);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decryptAndGetData(Context context, String key) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            String encryptedValue = prefs.getString(key, null);
            if (encryptedValue != null) {
                return decrypt(encryptedValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String encrypt(String value) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey key = getOrCreateKey();
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encrypted, Base64.DEFAULT) + ":" + Base64.encodeToString(iv, Base64.DEFAULT);
    }

    private static String decrypt(String encryptedValue) throws Exception {
        String[] parts = encryptedValue.split(":");
        byte[] encrypted = Base64.decode(parts[0], Base64.DEFAULT);
        byte[] iv = Base64.decode(parts[1], Base64.DEFAULT);
        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKey key = getOrCreateKey();
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    private static SecretKey getOrCreateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
            keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            return keyGenerator.generateKey();
        }

        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
    }

    public static void deleteAllData(Context context) {
        try {
            // Delete encrypted preferences
            context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
            
            // Delete the encryption key
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            keyStore.deleteEntry(KEY_ALIAS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 