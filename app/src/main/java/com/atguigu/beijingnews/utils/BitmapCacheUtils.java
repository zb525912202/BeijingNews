package com.atguigu.beijingnews.utils;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * 作者：尚硅谷-杨光福 on 2016/8/26 10:14
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：图片三级缓存的工具类
 */
public class BitmapCacheUtils {

    /**
     * 网络缓存工具类
     */
    private NetCacheUtils netCacheUtils;

    /**
     * 本地缓存工具类
     */

    private LocalCacheUtils localCacheUtils;

    /**
     内存缓存工具类
     */
    private MemoryCacheUtils memoryCacheUtils;

    public BitmapCacheUtils(Handler handler) {
        memoryCacheUtils = new MemoryCacheUtils();
        localCacheUtils = new LocalCacheUtils(memoryCacheUtils);
        netCacheUtils = new NetCacheUtils(handler,localCacheUtils,memoryCacheUtils);

    }


    /**
     * 三级缓存设计步骤：
     *   * 从内存中取图片
     *   * 从本地文件中取图片
     *        向内存中保持一份
     *   * 请求网络图片，获取图片，显示到控件上,Hanlder,postion
     *      * 向内存存一份
     *      * 向本地文件中存一份
     *
     * @param imageUrl
     * @param position
     * @return
     */
    public Bitmap getBitmap(String imageUrl, int position) {
        //1.从内存中取图片
        if (memoryCacheUtils != null) {
            Bitmap bitmap = memoryCacheUtils.getBitmapFromUrl(imageUrl);
            if (bitmap != null) {
                LogUtil.e("内存加载图片成功=="+position);
                return bitmap;
            }
        }

        //2.从本地文件中取图片
        if (localCacheUtils != null) {
            Bitmap bitmap = localCacheUtils.getBitmapFromUrl(imageUrl);
            if (bitmap != null) {
                LogUtil.e("本地加载图片成功=="+position);
                return bitmap;
            }
        }

        //3.请求网络图片
        netCacheUtils.getBitmapFomNet(imageUrl, position);
        return null;
    }
}
