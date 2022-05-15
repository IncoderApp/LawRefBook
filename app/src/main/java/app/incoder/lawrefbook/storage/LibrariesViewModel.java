/*
 * Copyright (C) 2022 The Jerry xu Open Source Project
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

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * LibrariesViewModel
 *
 * @author : Jerry xu
 * @since : 2022/5/2 13:18
 */
public class LibrariesViewModel extends AndroidViewModel {

    private final LibrariesRepository mLibrariesRepository;

    public LibrariesViewModel(@NonNull Application application) {
        super(application);
        mLibrariesRepository = new LibrariesRepository(getApplication());
    }

    public LiveData<List<Libraries>> getAllLibrariesLive() {
        return mLibrariesRepository.getAllLibrariesLive();
    }

    public LiveData<List<Libraries>> getFavorite(String articleId, String category) {
        return mLibrariesRepository.getFavorite(articleId, category);
    }

    public void insertAction(Libraries... encapsulations) {
        mLibrariesRepository.insertLibraries(encapsulations);
    }

    public void updateLibraries(Libraries... libraries) {
        mLibrariesRepository.updateLibraries(libraries);
    }

    public void delete(Libraries... libraries) {
        mLibrariesRepository.deleteLibraries(libraries);
    }

    void deleteLibraries() {
        mLibrariesRepository.deleteAll();
    }

}
