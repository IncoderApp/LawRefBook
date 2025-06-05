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

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * Category
 * <pre>
 * CREATE TABLE "category" ("id" INTEGER NOT NULL PRIMARY KEY, "name" TEXT NOT NULL, "folder" TEXT NOT NULL, "isSubFolder" INTEGER NOT NULL, "group" TEXT, "order" integer);
 * </pre>
 *
 * @author : Jerry xu
 * @since : 2022/6/5 08:17
 */
@Data
@Builder
public class Category implements Serializable {

    private String id;
    private String name;
    private String folder;
    private Integer isSubFolder;
    private String group;
    private Integer order;
}
