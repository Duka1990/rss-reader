package hu.possible.demo.rssreader.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hu.possible.demo.rssreader.models.Enclosure;
import hu.possible.demo.rssreader.models.Feed;
import hu.possible.demo.rssreader.models.Image;
import hu.possible.demo.rssreader.models.Item;
import hu.possible.demo.rssreader.models.RssSource;
import timber.log.Timber;

public class ModelHelpers {

    public static String resolveTitleFromRssSource(RssSource rssSource) {
        Feed feed = rssSource.getFeed();

        if (feed != null && feed.getChannel() != null && !TextUtils.isEmpty(feed.getChannel().getTitle())) {
            return feed.getChannel().getTitle().trim();
        } else {
            return rssSource.getUrl();
        }
    }

    public static String resolveDescriptionFromRssSource(RssSource rssSource) {
        Feed feed = rssSource.getFeed();

        if (feed != null && feed.getChannel() != null && !TextUtils.isEmpty(feed.getChannel().getDescription())) {
            return feed.getChannel().getDescription().trim();
        } else {
            return null;
        }
    }

    public static String resolveImageUrlFromItem(Item item) {
        List<Image> images = item.getImages();
        List<Enclosure> enclosures = item.getEnclosures();

        if (images != null && images.size() > 0) {
            return images.get(0).getUrl();
        }

        if (enclosures != null && enclosures.size() > 0) {
            for (Enclosure enclosure : enclosures) {
                if (enclosure.getType().startsWith("image/")) {
                    return enclosure.getUrl();
                }
            }
        }

        return null;
    }

    public static String resolveTitleFromItem(Item item) {
        if (!TextUtils.isEmpty(item.getTitle())) {
            return item.getTitle().trim();
        }

        return null;
    }

    public static String resolveDescriptionFromItem(Item item) {
        if (!TextUtils.isEmpty(item.getDescription())) {
            return item.getDescription().trim();
        }

        return null;
    }

    public static String resolveFormattedPublicationDateFromItem(Item item) {
        Date pubDate = resolvePublicationDateFromItem(item);

        if (pubDate != null) {
            SimpleDateFormat displayedDateFormat = new SimpleDateFormat("yyyy. MM. dd. HH:mm:ss", Locale.ENGLISH);

            return displayedDateFormat.format(new Date(pubDate.getTime()));
        }

        return null;
    }

    public static Date resolvePublicationDateFromItem(Item item) {
        if (!TextUtils.isEmpty(item.getPubDate())) {
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

            try {
                return dateFormat.parse(item.getPubDate());
            } catch (ParseException e) {
                Timber.d(e);

                return null;
            }
        } else {
            return null;
        }
    }

}
