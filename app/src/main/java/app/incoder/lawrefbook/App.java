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

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * App
 *
 * @author : Jerry xu
 * @since : 2022/6/5 09:37
 */
public class App extends Application {

    public static void packDataBase(Context context) {
        // internal
        @SuppressLint("SdCardPath")
        String internalPath = "/data/data/app.incoder.lawrefbook/databases";
        String sqliteName = "/db.sqlite3";
        // external
        String originPath = "Laws" + sqliteName;

        // check internal SQLite is exist
        if (!(new File(internalPath + sqliteName)).exists()) {
            File f = new File(internalPath);
            // if databases category is not exist
            if (!f.exists()) {
                if (f.mkdir()) {
                    System.out.println("create databases");
                }
            }
            try {
                InputStream is = context.getAssets().open(originPath);
                OutputStream os = new FileOutputStream(internalPath + sqliteName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        packDataBase(getApplicationContext());
    }

}
