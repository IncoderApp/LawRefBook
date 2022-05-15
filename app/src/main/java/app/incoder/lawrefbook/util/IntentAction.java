package app.incoder.lawrefbook.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * system intent action
 *
 * @author : Jerry xu
 * @since : 2022/5/15 23:43
 */
public class IntentAction {

    public static void sendEmail(Context context, String title, String content, String address) {
        // 调用系统发邮件
        Uri uri = Uri.parse("mailto:" + address);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(emailIntent, "选择邮箱"));
    }
}
