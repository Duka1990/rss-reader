package hu.possible.demo.rssreader.utils;

import android.content.Context;
import android.content.Intent;

import hu.possible.demo.rssreader.R;

public class AndroidUtils {

    public static void share(Context context, String subject, String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.dialog_share_title)));
    }

}
