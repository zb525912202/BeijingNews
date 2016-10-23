package com.atguigu.beijingnews.utils;

import android.graphics.Bitmap;

import org.xutils.cache.LruCache;

/**
 * 作者：尚硅谷-杨光福 on 2016/8/26 11:52
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：Java之软引用&弱引用&虚引用
 */
public class MemoryCacheUtils {

    /**
     * 集合
     */
    private LruCache<String,Bitmap> lruCache;

    public MemoryCacheUtils(){
        //使用了系统分配给应用程序的八分之一内存来作为缓存大小
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/1024/8);
        lruCache = new LruCache<String,Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
//                return super.sizeOf(key, value);
                return (value.getRowBytes() * value.getHeight())/1024;
            }
        };
    }

    /**
     * 根据url从内存中获取图片
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmapFromUrl(String imageUrl) {
        return lruCache.get(imageUrl);
    }

    /**
     * 根据url保存图片到lruCache集合中
     * @param imageUrl 图片路径
     * @param bitmap 图片
     */
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        lruCache.put(imageUrl,bitmap);
    }
}
