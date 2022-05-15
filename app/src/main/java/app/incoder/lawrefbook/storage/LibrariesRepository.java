/*
 * Copyright (C) 2021 The Jerry xu Open Source Project
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

package app.incoder.lawrefbook.storage;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Libraries Repository
 *
 * @author : Jerry xu
 * @since : 2022/5/2 13:18
 */
public class LibrariesRepository {

    private final LiveData<List<Libraries>> mLibrariesLive;
    private final LibrariesDao mDao;

    /**
     * thread full parameters
     * corePoolSize：5
     * maximumPoolSize：10
     * keepAliveTime：survival 60 minutes
     * workQueue：LinkedBlockingQueue
     * threadFactory：defaultThreadFactory
     * handlerPolicy：AbortPolicy
     */
    private static final ThreadPoolExecutor POOL_EXECUTOR =
            new ThreadPoolExecutor(5,
                    10
                    , 60
                    , TimeUnit.MINUTES
                    , new LinkedBlockingQueue<>()
                    , Executors.defaultThreadFactory()
                    , new ThreadPoolExecutor.AbortPolicy());


    LibrariesRepository(Context context) {
        LibrariesDatabase database = LibrariesDatabase.getInstance(context.getApplicationContext());
        mDao = database.getLibrariesDAO();
        mLibrariesLive = mDao.queryAll();
    }

    public LiveData<List<Libraries>> getAllLibrariesLive() {
        return mLibrariesLive;
    }

    public LiveData<List<Libraries>> getFavorite(String articleId, String category) {
        return mDao.getFavorite(articleId, category);
    }

    void insertLibraries(Libraries... libraries) {
        POOL_EXECUTOR.submit(() -> mDao.insertLibraries(libraries));
    }

    void updateLibraries(Libraries... libraries) {
        POOL_EXECUTOR.submit(() -> mDao.updateLibraries(libraries));
    }

    void deleteLibraries(Libraries... libraries) {
        POOL_EXECUTOR.submit(() -> mDao.deleteLibraries(libraries));
    }

    void deleteAll() {
        POOL_EXECUTOR.submit(mDao::deleteAllLibraries);
    }

}
