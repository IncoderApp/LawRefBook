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

    public int way3(String str, String M) {
        return str.split(M).length - 1;
    }
}