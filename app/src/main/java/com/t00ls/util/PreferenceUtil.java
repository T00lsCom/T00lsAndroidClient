package com.t00ls.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by 123 on 2018/3/28.
 */

public class PreferenceUtil {

    public static void storePreference(String title, String name, String content, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(title, Context.MODE_PRIVATE).edit();
        editor.putString(name, content);
        editor.apply();
    }

    public static String readPreference(String title, String name, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(title, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, null);
    }

    public static void setObject(String title, String key, Object object, Context context) {
        SharedPreferences sp = context.getSharedPreferences(title, Context.MODE_PRIVATE);

        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        try {
            //然后通过将字对象进行64转码，写入key值为key的sp中
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.apply();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T getObject(String title, String key, Class<T> clazz, Context context) {
        SharedPreferences sp = context.getSharedPreferences(title, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer;
            try {
                buffer = Base64.decode(objectVal, Base64.DEFAULT);
            } catch (NullPointerException e) {
                buffer = org.apache.commons.codec.binary.Base64.decodeBase64(objectVal);
            }

            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void clearPreference(String title, String name , Context context) {
        SharedPreferences sp = context.getSharedPreferences(title, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, null);
        editor.apply();
    }
}
