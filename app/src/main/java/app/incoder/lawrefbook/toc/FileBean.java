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

package app.incoder.lawrefbook.toc;

/**
 * FileBean
 *
 * @author : Jerry xu
 * @since : 2022/5/3 08:53
 */
@lombok.Data
@lombok.Builder
public class FileBean {

    @TreeNodeId
    private int id;
    @TreeNodePid
    private int parentId;
    @TreeNodeLabel
    private String name;
    private int position;

//    public FileBean(int id, int parentId, String name) {
//        super();
//        this.id = id;
//        this.parentId = parentId;
//        this.name = name;
//    }
}
