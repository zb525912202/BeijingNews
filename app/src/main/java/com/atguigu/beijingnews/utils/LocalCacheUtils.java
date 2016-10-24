package com.atguigu.beijingnews.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 作用：本地缓存工具类
 */
public class LocalCacheUtils {
    private final MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
    }

    /**
     * 根据Url获取图片
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmapFromUrl(String imageUrl) {
        //判断sdcard是否挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //保存图片在/mnt/sdcard/beijingnews/http://192.168.21.165:8080/xsxxxx.png
            //保存图片在/mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
            try {
                String fileName = MD5Encoder.encode(imageUrl);//llkskljskljklsjklsllsl

                ///mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);




                if(file.exists()){

                    FileInputStream is = new FileInputStream(file);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    if(bitmap != null){
                        memoryCacheUtils.putBitmap(imageUrl,bitmap);
                        LogUtil.e("把从本地保持到内存中");
                    }
                    return  bitmap;

                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片获取失败");
            }
        }

        return null;
    }

    /**
     * 根据Url保存图片
     * @param imageUrl url
     * @param bitmap 图片
     */
    public void putBitmap(String imageUrl, Bitmap bitmap) {

        //判断sdcard是否挂载
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //保存图片在/mnt/sdcard/beijingnews/http://192.168.21.165:8080/xsxxxx.png
            //保存图片在/mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
            try {
                String fileName = MD5Encoder.encode(imageUrl);//llkskljskljklsjklsllsl

                ///mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory()+"/beijingnews",fileName);

                File parentFile =  file.getParentFile();//mnt/sdcard/beijingnews
                if(!parentFile.exists()){
                    //创建目录
                    parentFile.mkdirs();
                }


                if(!file.exists()){
                    file.createNewFile();
                }
                //保存图片
                bitmap.compress(Bitmap.CompressFormat.PNG,100,new FileOutputStream(file));

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地缓存失败");
            }
        }


    }
}
