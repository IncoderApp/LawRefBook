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
 * Extended
 *
 * @author : Jerry xu
 * @since : 2022/5/1 14:10
 */
@lombok.NoArgsConstructor
@lombok.Data
public class Extended implements Serializable {

    /**
     * 官方文章地址
     */
    private String officialUrl;
    /**
     * 字数统计
     */
    private String wordsCount;
    /**
     * 施行日期
     */
    private String enforcementDate;
    /**
     * 公布日期
     */
    private String releaseDate;
    /**
     * 制定机关
     */
    private String formulationOrgan;
    /**
     * 时效性
     * <ol>
     *     <li>尚未生效</li>
     *     <li>有效</li>
     *     <li>已修改</li>
     *     <li>已废止</li>
     * </ol>
     */
    private String timeliness;
    /**
     * 法律性质
     */
    private String legalNature;
    private List<String> correctHistory;
}
