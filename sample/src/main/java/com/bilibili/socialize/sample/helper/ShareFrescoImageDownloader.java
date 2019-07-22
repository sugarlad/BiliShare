/*
 * Copyright (C) 2015 Bilibili <jungly.ik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bilibili.socialize.sample.helper;

import com.bilibili.socialize.share.download.AbsImageDownloader;
import com.bilibili.socialize.share.util.FileUtil;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;

import java.io.File;
import java.io.IOException;

/**
 * @author Jungly
 * @email jungly.ik@gmail.com
 * @since 2016/2/7
 */
public class ShareFrescoImageDownloader extends AbsImageDownloader {

    @Override
    protected void downloadDirectly(final String imageUrl, final String filePath, final OnImageDownloadListener listener) {
        if (listener != null)
            listener.onStart();

        final ImageRequest request = ImageRequest.fromUri(imageUrl);
        DataSource<CloseableReference<CloseableImage>> dataSource =
                Fresco.getImagePipeline().fetchDecodedImage(request, null);
        dataSource.subscribe(new BaseDataSubscriber<CloseableReference<CloseableImage>>() {

            @Override
            protected void onNewResultImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                CloseableReference<CloseableImage> result = dataSource.getResult();
                if (result != null) {
                    ImageRequest imageRequest = ImageRequest.fromUri(imageUrl);
                    CacheKey cacheKey = DefaultCacheKeyFactory.getInstance()
                            .getEncodedCacheKey(imageRequest, null);
                    BinaryResource resource = Fresco.getImagePipelineFactory()
                            .getMainFileCache()
                            .getResource(cacheKey);
                    if (resource instanceof FileBinaryResource) {
                        File cacheFile = ((FileBinaryResource) resource).getFile();
                        try {
                            FileUtil.copyFile(cacheFile, new File(filePath));
                            if (listener != null)
                                listener.onSuccess(filePath);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (listener != null)
                    listener.onFailed(imageUrl);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                if (listener != null)
                    listener.onFailed(imageUrl);
            }

        }, UiThreadImmediateExecutorService.getInstance());
    }

}
