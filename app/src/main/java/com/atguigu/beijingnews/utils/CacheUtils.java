package com.atguigu.beijingnews.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 作用：缓存软件的一些参数和数据
 */
public class CacheUtils {
    /**
     * 得到缓存值
     *
     * @param context 上下文
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 保存软件参数
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 缓存文本数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context, String key, String value) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ///mnt/sdcard/beijingnews/files/llkskljskljklsjklsllsl
            try {
                String fileName = MD5Encoder.encode(key);//llkskljskljklsjklsllsl

                ///mnt/sdcard/beijingnews/files/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files", fileName);

                File parentFile = file.getParentFile();//mnt/sdcard/beijingnews/files
                if (!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }


                if (!file.exists()) {
                    file.createNewFile();
                }
                //保存文本数据
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(value.getBytes());
                fileOutputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("文本数据缓存失败");
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
            sp.edit().putString(key, value).commit();
        }

    }

    /**
     * 获取缓存的文本信息
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key) {
        String result = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String fileName = MD5Encoder.encode(key);//llkskljskljklsjklsllsl

                ///mnt/sdcard/beijingnews/files/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews/files", fileName);


                if (file.exists()) {

                    FileInputStream is = new FileInputStream(file);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) != -1) {
                        stream.write(buffer, 0, length);
                    }

                    is.close();
                    stream.close();

                    result = stream.toString();


                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片获取失败");
            }
        } else {
            SharedPreferences sp = context.getSharedPreferences("atguigu", Context.MODE_PRIVATE);
            result = sp.getString(key, "");
        }
        return result;
    }
}
