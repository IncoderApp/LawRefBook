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

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Room Action interface
 *
 * @author : Jerry xu
 * @since : 2022/5/2 13:18
 */
@Dao
interface LibrariesDao {

    /**
     * insertLibraries
     *
     * @param libraries libraries
     */
    @Insert
    void insertLibraries(Libraries... libraries);

    /**
     * updateLibraries
     *
     * @param libraries libraries
     */
    @Update
    void updateLibraries(Libraries... libraries);

    /**
     * deleteLibraries
     *
     * @param libraries libraries
     */
    @Delete
    void deleteLibraries(Libraries... libraries);

    /**
     * deleteAllLibraries
     */
    @Query("DELETE FROM libraries")
    void deleteAllLibraries();

    /**
     * deleteSequenceRecord
     */
    @Query("DELETE FROM sqlite_sequence WHERE name = 'libraries'")
    void deleteSequenceRecord();

    /**
     * deleteSequence
     */
    @Query("DELETE FROM sqlite_sequence")
    void deleteSequence();

    /**
     * queryLibraries
     *
     * @param paraName paraName
     * @param classify classify
     * @return LiveData
     */
    @Query("SELECT * FROM libraries WHERE name LIKE :paraName AND classify = :classify")
    LiveData<List<Libraries>> queryLibraries(String paraName, String classify);

    /**
     * queryAll
     *
     * @return LiveData
     */
    @Query("SELECT * FROM libraries")
    LiveData<List<Libraries>> queryAll();

    /**
     * getFavorite
     *
     * @param articleId articleId
     * @param classify  classify
     * @return LiveData
     */
    @Query("SELECT * FROM libraries WHERE laws_id = :articleId AND classify = :classify")
    LiveData<List<Libraries>> getFavorite(String articleId, String classify);
}
