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

package app.incoder.lawrefbook;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.incoder.lawrefbook.model.Article;
import app.incoder.lawrefbook.model.Content;
import app.incoder.lawrefbook.model.Extended;
import app.incoder.lawrefbook.model.Lawre;
import app.incoder.lawrefbook.model.Toc;
import app.incoder.lawrefbook.model.Type;

/**
 * LawRefBookRepository
 *
 * @author : Jerry xu
 * @since : 2022/4/30 09:06
 */
public class LawRefBookRepository {

    public static String getContext(String fileName, Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName), StandardCharsets.UTF_8));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<Lawre> getData(String category, Context context) {
        String json = getContext("Laws/data.json", context);
        JSONArray jsonArray;
        List<Lawre> jetPackModelList = new ArrayList<>();
        try {
            JSONObject keyBean = new JSONObject(json);
            jsonArray = (JSONArray) keyBean.get(category);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Lawre bean = new Lawre();
                bean.setCategory(object.getString("category"));
                bean.setFolder(object.getString("folder"));
                bean.setId(object.getString("id"));
                bean.setGroup(object.getString("group"));
                bean.setIsSubFolder(object.getBoolean("isSubFolder"));
                JSONArray laws = object.getJSONArray("laws");
                List<Lawre.LawsBean> lawsBeanList = new ArrayList<>(laws.length());
                for (int j = 0; j < laws.length(); j++) {
                    JSONObject lawsBeanObject = laws.getJSONObject(i);
                    Lawre.LawsBean lawsBean = new Lawre.LawsBean();
                    lawsBean.setLevel(lawsBeanObject.getString("level"));
                    lawsBean.setName(lawsBeanObject.getString("name"));
                    lawsBean.setId(lawsBeanObject.getString("id"));
                    lawsBeanList.add(lawsBean);
                }
                bean.setLaws(lawsBeanList);
                JSONArray links = object.getJSONArray("links");
                List<String> stringList = new ArrayList<>(links.length());
                for (int k = 0; k < links.length(); k++) {
                    String string = links.getString(k);
                    stringList.add(string);
                }
                bean.setLinks(stringList);
                jetPackModelList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jetPackModelList;
    }

    public static List<Lawre> getData(Context context) {
        String json = getContext("Laws/data.json", context);
        List<Lawre> jetPackModelList = new ArrayList<>();
        try {
            JSONArray keyBean = new JSONArray(json);
            for (int i = 0; i < keyBean.length(); i++) {
                JSONObject object = keyBean.getJSONObject(i);
                Lawre bean = new Lawre();
                bean.setCategory(object.getString("category"));
                bean.setFolder(object.getString("folder"));
                bean.setId(object.getString("id"));
                if (!object.isNull("group")) {
                    bean.setGroup(object.getString("group"));
                }
                if (!object.isNull("isSubFolder")) {
                    bean.setIsSubFolder(object.getBoolean("isSubFolder"));
                }
                if (!object.isNull("laws")) {
                    JSONArray laws = object.getJSONArray("laws");
                    List<Lawre.LawsBean> lawsBeanList = new ArrayList<>(laws.length());
                    for (int j = 0; j < laws.length(); j++) {
                        JSONObject lawsBeanObject = laws.getJSONObject(j);
                        Lawre.LawsBean lawsBean = new Lawre.LawsBean();
                        lawsBean.setLevel(lawsBeanObject.getString("level"));
                        lawsBean.setName(lawsBeanObject.getString("name"));
                        if (!lawsBeanObject.isNull("filename")) {
                            lawsBean.setFilename(lawsBeanObject.getString("filename"));
                        }
                        lawsBean.setId(lawsBeanObject.getString("id"));
                        lawsBeanList.add(lawsBean);
                    }
                    bean.setLaws(lawsBeanList);
                }
                if (!object.isNull("links")) {
                    JSONArray links = object.getJSONArray("links");
                    List<String> stringList = new ArrayList<>(links.length());
                    for (int k = 0; k < links.length(); k++) {
                        String string = links.getString(k);
                        stringList.add(string);
                    }
                    bean.setLinks(stringList);
                }
                jetPackModelList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jetPackModelList;
    }

    public static List<String> getCatalog(Context context) {
        AssetManager assetManager = context.getAssets();
        List<String> catalogList = new ArrayList<>();
        try {
            String[] laws = assetManager.list("Laws");
            List<String> list = Arrays.asList(laws);
            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i);
                if (name.matches("[\u4E00-\u9FA5]+")) {
                    catalogList.add(name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return catalogList;
    }

    public static Article getArticle(Context context, String path) {
        Article article = new Article();
        Extended extended = new Extended();
        List<Toc> tocList = new ArrayList<>();
        List<String> history = new ArrayList<>();
        List<Content> articleContent = new ArrayList<>();
        int count = 0;
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("Laws/" + path), StandardCharsets.UTF_8));
            String line;
            boolean body = false;
            int tocIndex = 0;
            int tocId = 0;
            // key = realLevel, value = parentId
            Map<Integer, Integer> indexMap = new HashMap<>(8);
            // 记录上一次标题的 parentId，初始化默认没有，即定义为 -1
            Integer compareLevelParentId = -1;
            StringBuilder sb = new StringBuilder();
            while ((line = bf.readLine()) != null) {
                line = line.trim();
                if ("".equals(line)) {
                    continue;
                }
                if ("<!-- INFO END -->".equals(line)) {
                    body = true;
                    continue;
                }
                Content content = new Content();
                if (body) {
                    if (line.matches("^#+")) {
                        // 行内容 ## 的标题，丢弃
                        continue;
                    }
                    if (line.matches("^#+ .*")) {
                        tocIndex++;
                        tocId++;
                        String headline = line.replaceAll("^#+ ", "");
                        int level = line.split("#").length;
                        Toc toc = new Toc();
                        toc.setId(tocId);
                        int realLevel = level - 2;
                        Integer currentLevelParentId = indexMap.get(realLevel);
                        // 利用 map 特性记录最新 top 的 level 和 id
                        if (!compareLevelParentId.equals(currentLevelParentId)) {
                            // 第一层级，清除记录
                            if (realLevel == 1) {
                                indexMap.clear();
                            }
                            if (currentLevelParentId == null || compareLevelParentId < currentLevelParentId) {
                                indexMap.put(realLevel, tocId - 1);
                                compareLevelParentId = tocId - 1;
                            } else {
                                indexMap.put(realLevel, currentLevelParentId);
                                compareLevelParentId = currentLevelParentId;
                            }
                        }
                        Integer realLevelParentId = indexMap.get(realLevel);
                        toc.setParentId(realLevelParentId != null ? realLevelParentId : -1);
                        toc.setPosition(tocIndex);
                        toc.setTitle(headline);
                        toc.setTitleLevel(realLevel);
                        tocList.add(toc);
                        if (headline.matches("^(第[一二三四五六七八九十零百千万]).*?") && realLevel == 1) {
                            content.setType(Type.SECTION_TYPE.getCode());
                        } else if (realLevel > 1) {
                            content.setType(Type.NODE_TYPE.getCode());
                        }
                        content.setRule(headline);
                        articleContent.add(content);
                    } else {
                        if (line.matches("^$")) {
                            // empty line
                            continue;
                        } else if (line.matches("(第[一二三四五六七八九十零百千万]*条)( *)([\\s\\S]*)")) {
                            content.setType(Type.CONTENT_TYPE.getCode());
                            if (sb.length() > 0) {
                                content.setRule(sb.toString());
                                tocIndex++;
                                articleContent.add(content);
                                // clear sb
                                sb.delete(0, sb.length());
                            }
                            sb.append(line);
                        } else {
                            sb.append("\n").append(line);
                        }
                        // 预解析，查看是否为最后一行内容
                        if (bf.readLine() == null) {
                            Content lastContent = new Content();
                            lastContent.setType(Type.CONTENT_TYPE.getCode());
                            lastContent.setRule(sb.toString());
                            tocIndex++;
                            articleContent.add(lastContent);
                        }
                    }
                } else {
                    if (line.matches("^#+ .*")) {
                        if (article.getTitle() != null && article.getTitle().trim().length() > 0) {
                            String temp = line.replaceAll("^#+ ", "");
                            article.setTitle(article.getTitle() + temp);
                        } else {
                            article.setTitle(line.replaceAll("^#+ ", ""));
                        }
                    } else {
                        history.add(line);
                    }
                }
                count = count + line.length();
            }
            extended.setWordsCount(count + "");
            extended.setCorrectHistory(history);
            article.setContents(articleContent);
            article.setToc(tocList);
            article.setInfo(extended);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return article;
    }


}