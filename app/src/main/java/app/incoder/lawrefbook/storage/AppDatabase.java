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

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameColumn;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;

/**
 * Single Room Database
 *
 * @author : Jerry xu
 * @since : 2022/5/2 13:18
 */
@Database(entities = {Libraries.class}, version = 2,
        autoMigrations = {@AutoMigration(from = 1, to = 2, spec = AppDatabase.Libraries1To2AutoMigration.class)}
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    /**
     * getLibrariesDAO
     *
     * @return LibrariesDao
     */
    public abstract LibrariesDao getLibrariesDAO();

    static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "lawre_room")
//                    .createFromAsset("database/db.sqlite3")
                    // sql log
//                    .setJournalMode(JournalMode.TRUNCATE)
                    // 破坏式迁移
//                    .fallbackToDestructiveMigration()
                    // 迁移策略
//                    .addMigrations(MIGRATION1_2)
                    .build();
        }
        return INSTANCE;
    }

    @RenameColumn(tableName = "libraries", fromColumnName = "category", toColumnName = "classify")
    static class Libraries1To2AutoMigration implements AutoMigrationSpec {
    }

    /*static final Migration MIGRATION1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Libraries ADD COLUMN classify");
//            database.execSQL("alter table 'product' add 'enable' integer not null default '1'");
        }
    };*/

}
