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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void matchString() {
        String content = "## 第一章  基本规定";
        boolean title = content.matches("^# .*");
        boolean matches = content.matches("^#+ .*");
        System.out.println(way3(content, "#"));
        System.out.println(title);
        System.out.println(matches);

        String s = content.replaceAll("^#+ ", "");
        System.out.println(s);

        String path = "人生若只如初见,何事秋风悲画扇.";
        String n = path.replaceAll("若.*风", "");
        System.out.println("替换后的内容:" + n);
    }

    @Test
    public void date() {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        System.out.println(f.format(new Date()));
    }

    @Test
    public void contentIndex() {
        String content = "在刑法第三十七条后增加一条，作为第三十七条之一:“因利用职业便利实施犯罪，或者实施违背职业要求的特定义务的犯罪被判处刑罚的，人民法院可以根据犯罪情况和预防再犯罪的需要，禁止其自刑罚执行完毕之日或者假释之日起从事相关职业，期限为三年至五年。";
        String search = "一条";
        searchAllIndex(content, search);
    }

    @Test
    public void lawLine() {
        String content = "第一条 为了保护民事主体的合法权益，调整民事关系，维护社会和经济秩序，适应中国特色社会主义发展要求，弘扬社会主义核心价值观，根据宪法，制定本法。";
        // (第.+?条)( *)([\s\S]*)
        // ^(第[一二三四五六七八九十零百千万]条).*?
        if (content.matches("(第[一二三四五六七八九十零百千万]*条)( *)([\\s\\S]*)")) {
            System.out.println(content);
        }
    }

    private void searchAllIndex(String content, String key) {
        // 第一个出现的索引位置
        int a = content.indexOf(key);
        while (a != -1) {
            // 从这个索引往后开始第一个出现的位置
            System.out.println("位置" + a);
            a = content.indexOf(key, a + 1);
        }
    }

    public int way3(String str, String M) {
        return str.split(M).length - 1;
    }
}