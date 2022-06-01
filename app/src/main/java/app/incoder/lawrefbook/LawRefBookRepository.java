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
import java.util.LinkedHashMap;
import java.util.List;

import app.incoder.lawrefbook.model.Article;
import app.incoder.lawrefbook.model.Extended;
import app.incoder.lawrefbook.model.Lawre;
import app.incoder.lawrefbook.model.Toc;

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
        List<String> content = new ArrayList<>();
        LinkedHashMap<Integer, String> catalog = new LinkedHashMap<>();
        int count = 0;
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("Laws/" + path), StandardCharsets.UTF_8));
            String line;
            boolean body = false;
            int tocIndex = 0;
            while ((line = bf.readLine()) != null) {
                if ("".equals(line)) {
                    continue;
                }
                if ("<!-- INFO END -->".equals(line)) {
                    body = true;
                    continue;
                }
                if (body) {
                    tocIndex++;
                    if (line.matches("^#+ .*")) {
                        String headline = line.replaceAll("^#+ ", "");
                        int level = (line.split("#").length - 1);
                        Toc toc = new Toc();
                        toc.setPosition(tocIndex);
                        toc.setTitle(headline);
                        toc.setTitleLevel(level);
                        tocList.add(toc);
                        content.add(headline);
                    } else {
                        content.add(line);
                    }
                } else {
                    if (line.matches("^#+ .*")) {
                        article.setTitle(line.replaceAll("^#+ ", ""));
                    } else {
                        history.add(line);
                    }
                }
                count = count + line.length();
            }
            extended.setWordsCount(count + "");
            extended.setCorrectHistory(history);
            article.setContent(content);
            article.setToc(tocList);
            article.setInfo(extended);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return article;
    }


}