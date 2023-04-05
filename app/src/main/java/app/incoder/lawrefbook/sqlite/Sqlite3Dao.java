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

package app.incoder.lawrefbook.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.incoder.lawrefbook.storage.Category;
import app.incoder.lawrefbook.storage.Law;

/**
 * Sqlite3Dao
 *
 * @author : Jerry xu
 * @since : 2022/6/5 18:16
 */
public class Sqlite3Dao {

    private volatile SQLiteDatabase mLite;

    /**
     * 获取单例
     *
     * @return Sqlite3Dao
     */
    public static Sqlite3Dao getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 在访问数据库时创建单例
     */
    private static class SingletonHolder {
        private static final Sqlite3Dao INSTANCE = new Sqlite3Dao();
    }

    public Sqlite3Dao() {

    }

    public SQLiteDatabase getSqlite(Context context) {
        if (mLite == null) {
            synchronized (Sqlite3Dao.class) {
                if (mLite == null) {
                    mLite = new Sqlite3Helper(context).getReadableDatabase();
                }
            }
        }
        return mLite;
    }

    public static List<Category> categoryList(Context context) {
        return getCategory(Sqlite3Dao.getInstance().getSqlite(context));
    }

    public static List<Law> lawList(Context context, Integer categoryId) {
        return getLaw(Sqlite3Dao.getInstance().getSqlite(context), categoryId);
    }

    private static List<Category> getCategory(SQLiteDatabase sqlite) {
        Cursor category = sqlite.rawQuery("SELECT * FROM category ORDER BY `order`", null);
        List<Category> result = new ArrayList<>();
        if (null != category) {
            if (category.moveToFirst()) {
                do {
                    int id = category.getInt(category.getColumnIndexOrThrow("id"));
                    String name = category.getString(category.getColumnIndexOrThrow("name"));
                    String folder = category.getString(category.getColumnIndexOrThrow("folder"));
                    Integer subFolder = category.getInt(category.getColumnIndexOrThrow("isSubFolder"));
                    String group = category.getString(category.getColumnIndexOrThrow("group"));
                    Integer order = category.getInt(category.getColumnIndexOrThrow("order"));
                    Category roomCategory = Category.builder()
                            .id(id)
                            .name(name)
                            .folder(folder)
                            .isSubFolder(subFolder)
                            .group(group)
                            .order(order)
                            .build();
                    result.add(roomCategory);
                } while (category.moveToNext());
            }
            category.close();
        }
        return result;
    }

    private static List<Law> getLaw(SQLiteDatabase sqlite, Integer categoryIds) {
        Cursor law;
        if (categoryIds != null) {
            law = sqlite.rawQuery("SELECT * FROM law WHERE category_id = ? ORDER BY `order`", new String[]{categoryIds.toString()});
        } else {
            law = sqlite.rawQuery("SELECT * FROM law ORDER BY `order`", null);
        }
        List<Law> result = new ArrayList<>();
        if (null != law) {
            if (law.moveToFirst()) {
                do {
                    String id = law.getString(law.getColumnIndexOrThrow("id"));
                    String level = law.getString(law.getColumnIndexOrThrow("level"));
                    String name = law.getString(law.getColumnIndexOrThrow("name"));
                    String filename = law.getString(law.getColumnIndexOrThrow("filename"));
                    String publish = law.getString(law.getColumnIndexOrThrow("publish"));
                    String expired = law.getString(law.getColumnIndexOrThrow("expired"));
                    Integer categoryId = law.getInt(law.getColumnIndexOrThrow("category_id"));
                    Integer order = law.getInt(law.getColumnIndexOrThrow("order"));
                    String subtitle = law.getString(law.getColumnIndexOrThrow("subtitle"));
                    String validFrom = law.getString(law.getColumnIndexOrThrow("valid_from"));
                    Law roomLaw = Law.builder()
                            .id(id)
                            .level(level)
                            .name(name)
                            .filename(filename)
                            .publish(publish)
                            .expired(expired)
                            .categoryId(categoryId)
                            .order(order)
                            .subtitle(subtitle)
                            .validFrom(validFrom)
                            .build();
                    result.add(roomLaw);
                } while (law.moveToNext());
            }
            law.close();
        }
        return result;
    }

}
