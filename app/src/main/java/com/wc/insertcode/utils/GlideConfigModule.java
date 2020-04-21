package com.wc.insertcode.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class GlideConfigModule extends AppGlideModule {
    public final static String TAG = "GlideConfigModule";
    private final static int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    private final static int MAX_CACHE_MEMORY_SIZE = MAX_HEAP_SIZE / 4;
    private final static int MAX_CACHE_DISK_SIZE = 50 * 1024 * 1024;

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        super.applyOptions(context, builder);
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, MAX_CACHE_DISK_SIZE));
        builder.setMemoryCache(new LruResourceCache(MAX_CACHE_MEMORY_SIZE));
        builder.setBitmapPool(new LruBitmapPool(MAX_CACHE_MEMORY_SIZE));
    }
}
