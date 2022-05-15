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

package app.incoder.lawrefbook.model;

import java.io.Serializable;
import java.util.List;

/**
 * Lawre Model
 *
 * @author : Jerry xu
 * @since : 2022/4/16 13:54
 */
@lombok.NoArgsConstructor
@lombok.Data
public class Lawre implements Serializable {

    private String category;
    private String folder;
    private List<LawsBean> laws;
    private String id;
    private List<String> links;
    private Boolean isSubFolder;
    private String group;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class LawsBean implements Serializable {
        private String name;
        private String filename;
        private String id;
        private String level;
    }
}
