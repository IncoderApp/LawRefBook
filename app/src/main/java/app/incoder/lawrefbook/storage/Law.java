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

import lombok.Builder;
import lombok.Data;

/**
 * Law
 *
 * <pre>
 * CREATE TABLE "law" ("id" TEXT NOT NULL PRIMARY KEY, "level" TEXT NOT NULL, "name" TEXT NOT NULL, "filename" TEXT, "publish" DATE, "expired" INTEGER NOT NULL, "category_id" INTEGER NOT NULL, "order" integer, "subtitle" TEXT, valid_from DATE, FOREIGN KEY ("category_id") REFERENCES "category" ("id"));
 *
 * CREATE INDEX "law_category_id" ON "law" ("category_id");
 *
 * CREATE INDEX "law_name" ON "law" ("name");
 * </pre>
 *
 * @author : Jerry xu
 * @since : 2022/6/5 08:17
 */
@Data
@Builder
public class Law {

    private String id;
    private String level;
    private String name;
    private String filename;
    private String publish;
    private String expired;
    private Integer categoryId;
    private Integer order;
    private String subtitle;
    private String validFrom;

}
