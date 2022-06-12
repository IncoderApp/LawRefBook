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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import app.incoder.lawrefbook.model.Classify;
import lombok.Data;

/**
 * Libraries
 *
 * @author : Jerry xu
 * @since : 2022/5/2 13:18
 */
@Data
@Entity(tableName = "libraries")
public class Libraries {

    @PrimaryKey(autoGenerate = true)
    private int id;
    /**
     * {@link Classify#getName()}
     */
    @ColumnInfo(name = "classify")
    private String classify;
    @ColumnInfo(name = "tag")
    private String tag;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "snippets_content")
    private String snippetsContent;
    @ColumnInfo(name = "snippets_index")
    private Integer snippetsIndex;
    @ColumnInfo(name = "laws_id")
    private String lawsId;
    @ColumnInfo(name = "article_path")
    private String articlePath;
    @ColumnInfo(name = "article_folder")
    private String articleFolder;
    @ColumnInfo(name = "create_time")
    private String createTime;

}
