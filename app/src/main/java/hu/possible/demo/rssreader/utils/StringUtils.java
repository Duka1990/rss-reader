package hu.possible.demo.rssreader.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isValidUrl(String url) {
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(url.toLowerCase());

        return matcher.matches();
    }

    public static Spanned fromHtml(String content) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Html.fromHtml(content);
        } else {
            return Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY);
        }
    }

}
