package com.devpoint.realpros.mobibank;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StringEn {
    static String key="ThisisAsecretkey";
    static SecureRandom rnd=new SecureRandom();
    static IvParameterSpec iv=new IvParameterSpec(rnd.generateSeed(16));
    public static void main(String[] args) {
    }
    public static  String encrypt(String value){
        String encrypt_msg=null;

        try {
            byte[]raw=key.getBytes(Charset.forName("UTF-8"));
            SecretKeySpec skey=new SecretKeySpec(raw,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            encrypt_msg= android.util.Base64.encodeToString(cipher.doFinal(value.getBytes()),0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt_msg;
    }
    public static  String decrypt(String encrypted){
        String original=null;
        try{
            byte[]raw=key.getBytes(Charset.forName("UTF-8"));
            SecretKeySpec skey=new SecretKeySpec(raw,"AES");
            Cipher cipher=Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[]decodevalue=android.util.Base64.decode(encrypted,0);
            byte[]decvalue=cipher.doFinal(decodevalue);
            original=new String(decvalue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return original;

    }

}
