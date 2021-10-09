/*
 * Copyright (C) 2014-2016  福建星网视易信息系统有限公司
 * All rights reserved by  福建星网视易信息系统有限公司
 *
 *  Modification History:
 *  Date            Author      Version     Description
 *  -----------------------------------------------
 *  2015-3-16       "weixiong"      1.0     [修订说明]
 *
 */

package com.qiezitv.common;

import android.content.Context;

import com.qiezitv.R;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * [ImageLoader工具类, 提供默认的ImageLoaderConfiguration和DisplayImageOptions]
 */
public final class ImageLoaderUtil {

    private static DisplayImageOptions sOptions;

    private ImageLoaderUtil() {
    }

    public static void init() {
        sOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_oneyuan) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_oneyuan)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_oneyuan)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
//                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .build();//构建完成
    }

    /**
     * [初始化ImageLoader配置]
     *
     * @param context context
     */
    public static void initImageLoaderConfiguration(Context context) {
        // ImageLoaderConfiguration config =
        // getDefaultImageLoaderConfiguration(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
//                .memoryCacheExtraOptions(2800, 1280)
                // max width, max height，即保存的每个缓存文件的最大长宽
                // Can slow ImageLoader, use it carefully (Better don't use
                // it)/设置缓存的详细信息，最好不要设置这个
                .threadPoolSize(3)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // You can pass your own memory cache
                // implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 缓存的文件数量
                // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs().build();// 开始构建
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getOptions() {
        return sOptions;
    }

    public static void clearMemoryCache() {
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
    }
}
